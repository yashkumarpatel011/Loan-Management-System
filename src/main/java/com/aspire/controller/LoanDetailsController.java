package com.aspire.controller;

import com.aspire.exceptions.UserException;
import com.aspire.model.LoanDetails;
import com.aspire.model.UserData;
import com.aspire.service.LoanService;
import com.aspire.service.UserService;
import com.aspire.utils.LoanStage;
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

        log.info("Started  Creating Loan ");
        UserData user =  userService.loginUser();

        if (user != null) {

            LoanDetails loanDetail = loanService.createLoan(user, loanDetails);

            return new ResponseEntity<LoanDetails>(loanDetail, HttpStatus.CREATED);
        }

        throw new BadCredentialsException("Please login before creating Loan");
    }

    @GetMapping("/loan/status/{status}")
    public ResponseEntity<String> approveOrRejectLoan(@PathVariable(value = "status") String status,
                                                  @RequestParam(value = "loanId", required = true) Integer loanId) throws UserException {

        log.info("Started approving Loan ");

        if (Strings.isBlank(status) ||
                !(LoanStage.APPROVED.getValue().equalsIgnoreCase(status) || LoanStage.REJECTED.getValue().equalsIgnoreCase(status))) {
            return new ResponseEntity<>("Loan Status is not accepted", HttpStatus.BAD_REQUEST);
        }

        try {
            UserData user =  userService.loginUser();
            if (user != null) {
                boolean isApproved = loanService.approveOrRejectLoan(loanId,user,status);

                if (isApproved) {
                    return new ResponseEntity<>("Loan has been approved", HttpStatus.OK);
                }
                return new ResponseEntity<>("Unable to approve the loan because loan assigned to different admin", HttpStatus.BAD_REQUEST);
            }
            throw new BadCredentialsException("Please login before approving Loan");
        } catch (Exception e) {
            throw e;
        }
    }

    @GetMapping("/loan/get")
    public ResponseEntity<List<LoanDetails>> getLoanDetails(@RequestParam(value = "loanId", required = false) Integer loanId) throws UserException {

        try {
            UserData user =  userService.loginUser();
            if (user != null) {

                List<LoanDetails> loanDetailsList = loanService.getLoanDetails(user);

                if(loanDetailsList == null) {
                    throw new BadCredentialsException("You don't have any permission to view the loan details");
                }
                return new ResponseEntity<List<LoanDetails>>(loanDetailsList,HttpStatus.OK);
            }
            throw new BadCredentialsException("Please login before fetching Loan Detials");
        } catch (Exception e) {
            throw e;
        }
    }

    @PatchMapping("/loan/reassign")
    public ResponseEntity<String> reAssignLoan(@RequestParam("loanId") Integer loanId) throws UserException {

        try {
           String msg =  loanService.reassignLoan(loanId);
           return new ResponseEntity<>(msg, HttpStatus.OK);

        } catch (Exception e) {
            throw e;
        }
    }

    @PatchMapping("/loan/paid")
    public ResponseEntity<String> rePaymentPaid(@RequestParam(value = "amount", required = true) Double amount ,
                                                @RequestParam(value = "loanId", required = true) Integer loanId ) throws UserException {
        try {
            String result = loanService.loanPayment(amount, loanId);
            return new ResponseEntity<>(result,HttpStatus.OK);

        } catch (Exception e) {
            throw e;
        }
    }
}
