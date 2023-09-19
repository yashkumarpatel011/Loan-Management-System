package com.aspire.service.Impl;

import com.aspire.exceptions.UserException;
import com.aspire.model.LoanDetails;
import com.aspire.model.RepaymentDetails;
import com.aspire.repository.RepaymentRepository;
import com.aspire.service.RepaymentService;
import com.aspire.service.UserService;
import com.aspire.utils.Constant;
import com.aspire.utils.LoanStage;
import com.aspire.utils.ResponseCodes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class RepaymentServiceImpl implements RepaymentService {

    @Autowired
    private RepaymentRepository repaymentRepository;

    @Autowired
    private UserService userService;

    @Override
    public void createRepayment(LoanDetails details) {

        final double amount = details.getAmount();
        final int duration = details.getDuration();
        final String paymentFreq = details.getRePaymentFreq();
        final double amountPerPayment = amount/duration;
        final int loanId = details.getId();

        LocalDate currentDate = LocalDate.now();

        List<RepaymentDetails> resultSet = new ArrayList<>();

        for (int i =0 ; i<duration ; i++) {

            if(Constant.WEEKLY.equalsIgnoreCase(paymentFreq)) {
                currentDate = currentDate.plus(1, ChronoUnit.WEEKS);
            } else if (Constant.MONTHLY.equalsIgnoreCase(paymentFreq)) {
                currentDate = currentDate.plus(1,ChronoUnit.MONTHS);
            } else {
                currentDate = currentDate.plus(1, ChronoUnit.YEARS);
            }

            resultSet.add(RepaymentDetails.builder()
                    .amount(amountPerPayment)
                    .loanId(loanId)
                    .scheduledOn(currentDate)
                    .status(LoanStage.PENDING.getValue())
                    .build());

            log.info("Added in the resultSet");
        }
        log.info("Data Saved successfully ");
        repaymentRepository.saveAll(resultSet);
    }

    /**
     * @param amount
     * @throws UserException
     */
    @Override
    public Pair<String,Double> rePaymentPaid(Double amount, Integer loanId, Double totalAmount) throws UserException {

        List<RepaymentDetails> repaymentDetailsList = repaymentRepository.findByLoanIdAndStatus(loanId, LoanStage.PENDING.getValue());
        int indexOfRepayment = 0;

        RepaymentDetails repaymentObject = repaymentDetailsList.get(indexOfRepayment);
        double scheduledRepayment = repaymentObject.getAmount();

        if (amount >= scheduledRepayment) {
            repaymentObject.setStatus(LoanStage.PAID.getValue());
            repaymentObject.setPaidAmount(amount);
            indexOfRepayment++;

            log.info("ScheduledPayment Paid Successfully");

            double remainingAmount = totalAmount - amount;
            int remainingduration = repaymentDetailsList.size() - indexOfRepayment;

            log.info("Remaining Amount: {}" , remainingAmount);
            log.info("Remaining Duration: {}" , remainingduration);

            double revisedReinstallationAmount = Math.round((remainingAmount/remainingduration) * 10.0)/10.0;

            for (int i = indexOfRepayment ; i < repaymentDetailsList.size(); i++) {

                repaymentObject = repaymentDetailsList.get(i);
                repaymentObject.setAmount(revisedReinstallationAmount);
            }

            if(remainingAmount <= 0.0) {
                log.info("Remaining amount is zero or negative");
                if(repaymentDetailsList.size() > indexOfRepayment) {

                    for (int i = indexOfRepayment ; i < repaymentDetailsList.size() ; i++) {
                        repaymentObject = repaymentDetailsList.get(i);
                        repaymentObject.setStatus(LoanStage.NOT_REQUIRED.getValue());
                    }
                }
                log.info("Set the Remaining Scheduled Payment as NOT_REQUIRED");
                log.info(ResponseCodes.LOAN_COMPLETED.getValue());
                return Pair.of(ResponseCodes.LOAN_COMPLETED.getValue(),remainingAmount);
            } else {
                log.info(ResponseCodes.SCHEDULED_PAYMENT_COMPLETED.getValue());
                return Pair.of(ResponseCodes.SCHEDULED_PAYMENT_COMPLETED.getValue(),remainingAmount);
            }
        } else {
            log.info("Amount is less than the scheduled Repayment");
            return Pair.of(ResponseCodes.LESS_AMOUNT.getValue(),totalAmount);
        }
    }
}

