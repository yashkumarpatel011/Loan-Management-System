package com.aspire.controller;

import com.aspire.exceptions.UserException;
import com.aspire.model.LoanDetails;
import com.aspire.model.UserData;
import com.aspire.service.LoanService;
import com.aspire.service.UserService;
import com.aspire.utils.LoanStage;
import com.aspire.utils.ResponseCodes;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
public class LoanDetailsController {

    @Autowired
    private UserService userService;

    @Autowired
    private LoanService loanService;

    @PostMapping("/loan/create")
    public ResponseEntity<LoanDetails> createLoan(@RequestBody LoanDetails loanDetails) throws UserException {

        log.info("Started Creating Loan ");
        UserData user =  userService.loginUser();

        if (user != null) {

            LoanDetails loanDetail = loanService.createLoan(user, loanDetails);
            log.info("Loan has been created successfully");

            return new ResponseEntity<LoanDetails>(loanDetail, HttpStatus.CREATED);
        }

        throw new BadCredentialsException(ResponseCodes.LOGIN_BEFORE_LOAN_CREATION.getValue());
    }

    @GetMapping("/loan/status/{status}")
    public ResponseEntity<String> approveOrRejectLoan(@PathVariable(value = "status") String status,
                                                  @RequestParam(value = "loanId", required = true) Integer loanId) throws UserException {

        if (Strings.isBlank(status) ||
                !(LoanStage.APPROVED.getValue().equalsIgnoreCase(status) || LoanStage.REJECTED.getValue().equalsIgnoreCase(status))) {
            log.info(ResponseCodes.LOAN_STATUS_NOT_VALID.getValue() + " for loanId {} ", loanId);
            return new ResponseEntity<>(ResponseCodes.LOAN_STATUS_NOT_VALID.getValue(), HttpStatus.BAD_REQUEST);
        }

        log.info("Started approving Loan for loanId {}", loanId);

        try {
            UserData user =  userService.loginUser();
            if (user != null) {
                boolean isApproved = loanService.approveOrRejectLoan(loanId,user,status);

                if (isApproved) {
                    log.info(ResponseCodes.LOAN_APPROVED.getValue() + " for loanId {} ", loanId);
                    return new ResponseEntity<>(ResponseCodes.LOAN_APPROVED.getValue(), HttpStatus.OK);
                }
                log.info(ResponseCodes.LOAN_ASSIGNED_DIFF_ADMIN.getValue() + " for loanId {} ", loanId);
                return new ResponseEntity<>(ResponseCodes.LOAN_ASSIGNED_DIFF_ADMIN.getValue(), HttpStatus.BAD_REQUEST);
            }
            log.info(ResponseCodes.LOGIN_BEFORE_LOAN_APPROVED.getValue() + " for loanId {} ", loanId);
            throw new BadCredentialsException(ResponseCodes.LOGIN_BEFORE_LOAN_APPROVED.getValue());
        } catch (Exception e) {
            log.error("Exception while approving the Loan for loanId {} ", loanId,e);
            throw e;
        }
    }

    @GetMapping("/loan/get")
    public ResponseEntity<List<LoanDetails>> getLoanDetails(@RequestParam(value = "loanId", required = false) Integer loanId) throws UserException {
        log.info("Fetching the Loan Details for loanId {} ",loanId);
        try {
            UserData user =  userService.loginUser();
            if (user != null) {

                List<LoanDetails> loanDetailsList = loanService.getLoanDetails(user);

                if(loanDetailsList == null) {
                    throw new BadCredentialsException(ResponseCodes.GET_LOAN_PERMISSION_DENIED.getValue());
                }
                log.info("Loan Details Fetched successfully");
                return new ResponseEntity<>(loanDetailsList,HttpStatus.OK);
            }
            throw new BadCredentialsException(ResponseCodes.LOGIN_BEFORE_GET_LOAN.getValue());
        } catch (Exception e) {
            log.error("Exception while fetching the loanDetails ", e);
            throw e;
        }
    }

    @PatchMapping("/loan/reassign")
    public ResponseEntity<String> reAssignLoan(@RequestParam("loanId") Integer loanId) throws UserException {
        log.info("Reassigning Loan for loanId {}", loanId);
        try {
           String msg =  loanService.reassignLoan(loanId);
           log.info(msg);
           return new ResponseEntity<>(msg, HttpStatus.OK);

        } catch (Exception e) {
            log.error("Exception during loan reassignment for loanId {}", loanId, e);
            throw e;
        }
    }

    @PatchMapping("/loan/paid")
    public ResponseEntity<String> rePaymentPaid(@RequestParam(value = "amount", required = true) Double amount ,
                                                @RequestParam(value = "loanId", required = true) Integer loanId ) throws UserException {
        log.info("Starting Paying Loan");
        try {
            String result = loanService.loanPayment(amount, loanId);
            log.info(result);
            return new ResponseEntity<>(result,HttpStatus.OK);

        } catch (Exception e) {
            log.error("Exception during the loan repayment for loanId {}", loanId, e);
            throw e;
        }
    }
}
