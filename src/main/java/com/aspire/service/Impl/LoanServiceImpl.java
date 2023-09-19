package com.aspire.service.Impl;

import com.aspire.exceptions.UserException;
import com.aspire.model.LoanDetails;
import com.aspire.model.UserData;
import com.aspire.repository.LoanRepository;
import com.aspire.service.LoanService;
import com.aspire.service.RepaymentService;
import com.aspire.service.UserService;
import com.aspire.utils.Constant;
import com.aspire.utils.LoanStage;
import com.aspire.utils.ResponseCodes;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class LoanServiceImpl implements LoanService {

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private RepaymentService repaymentService;

    @Autowired
    private UserService userService;


    @Override
    public LoanDetails createLoan(UserData user, LoanDetails loanDetails) {

        loanDetails.setUserId(user.getId());
        loanDetails.setInterestRate(Constant.INTEREST_RATE);
        loanDetails.setStatus(LoanStage.PENDING.getValue());
        loanDetails.setRePaymentFreq(Constant.WEEKLY);
        loanDetails.setRemainingAmount(loanDetails.getAmount());
        log.info("Fetching Admin user ..");
        UserData adminUser = userService.fetchAdminUser();

        if (adminUser != null) {
            log.info("Admin user found with id {}", adminUser.getId());
            loanDetails.setAdminId(adminUser.getId());
        }
        log.info("Admin user not found");
        LoanDetails detailsInDb = loanRepository.save(loanDetails);
        log.info("Calling RepaymentService to create the rePayment details");
        repaymentService.createRepayment(loanDetails);

        return detailsInDb;
    }

    @Override
    @Transactional
    public boolean approveOrRejectLoan(Integer loanId, UserData user, String status) {

        log.info("Started checking for approval..");
        Optional<LoanDetails> optional = loanRepository.findById(loanId);

        LoanDetails loanDetails = null;

        if(optional.isPresent()) {
           loanDetails = optional.get();
        }

        if (loanDetails != null && (user.getId() == loanDetails.getAdminId())) {

            log.info("Checking Loan Details ...");
            Integer isUpdated = loanRepository.updateStatus(status.toUpperCase(), loanDetails.getId());

            if (isUpdated == 1) {
                log.info("Loan status changed by Admin User ....");
                return true;
            }
        }
        log.info("Not found any loan with loanId {} ", loanId);
        return false;
    }

    @Override
    public List<LoanDetails> getLoanDetails(UserData user) {

        if (Constant.ROLE_USER.equalsIgnoreCase(user.getRole())) {
            log.info("Found the Role is ROLE_USER");
            List<LoanDetails> loanDetails = loanRepository.findAllByUserId(user.getId());
            return loanDetails;

        } else if (Constant.ROLE_ADMIN.equalsIgnoreCase(user.getRole())) {
            log.info("Found the Role is ROLE_ADMIN");
            List<LoanDetails> loanDetails = loanRepository.findAll();
            return loanDetails;
        }
        return null;
    }

    /**
     * @param loanId
     * @throws UserException
     */
    @Override
    @Transactional
    public String reassignLoan(Integer loanId) throws UserException {

        UserData user = userService.loginUser();

        if (user != null) {

            Integer isUpdated = loanRepository.updateLoanAssignee(user.getId(), loanId);

            if(isUpdated == 1) {
                log.info(ResponseCodes.ASSIGNED_UPDATED.getValue() + " for loanId {} ", loanId);
                return ResponseCodes.ASSIGNED_UPDATED.getValue();
            }
        }

        throw new BadCredentialsException("Please login to reassign the loan");
    }

    @Override
    @Transactional
    public String loanPayment(Double amount, Integer loanId) throws UserException {

        UserData user = userService.loginUser();

        try {
            if (user != null) {
                Optional<LoanDetails> optional = loanRepository.findById(loanId);
                LoanDetails loanDetails;

                if (optional.isPresent()) {

                    loanDetails = optional.get();

                    final String loanStatus = loanDetails.getStatus().toUpperCase();

                    log.info("loanStatus : {} ", loanStatus );
                    String resultMsg = "";

                    switch (loanStatus) {

                        case "PENDING": {
                            resultMsg = ResponseCodes.REPAYMENT_PENDING.getValue();
                            break;
                        }
                        case "APPROVED": {
                            Pair<String,Double> isLoanCompleted = repaymentService.rePaymentPaid(amount,loanId,loanDetails.getRemainingAmount());

                            if(ResponseCodes.LOAN_COMPLETED.getValue().equalsIgnoreCase(isLoanCompleted.getFirst())) {
                                loanRepository.updateStatus(LoanStage.PAID.getValue(), loanId);
                            }
                            loanRepository.updateRemainingAmount(isLoanCompleted.getSecond(),loanId);

                            resultMsg = isLoanCompleted.getFirst();
                            break;
                        }
                        case "PAID": {
                            resultMsg = ResponseCodes.REPAYMENT_PAID.getValue();
                            break;
                        }
                        case "REJECTED": {
                            resultMsg = ResponseCodes.REPAYMENT_REJECTED.getValue();
                            break;
                        }
                        case "CANCELLED": {
                            resultMsg = ResponseCodes.REPAYMENT_CANCELLED.getValue();
                            break;
                        }
                    }
                    log.info(resultMsg + "for loanId: {} ", loanId);
                    return resultMsg;
                }

                log.info(ResponseCodes.NOT_HAVE_LOAN.getValue());
                return ResponseCodes.NOT_HAVE_LOAN.getValue();
            }
        } catch (Exception e) {
            log.error("Exception has been occured while paying loan amount ", e);
            throw e;
        }
        throw new BadCredentialsException("Please login to paid the loan");
    }
}
