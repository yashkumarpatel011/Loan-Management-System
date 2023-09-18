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
            }

            resultSet.add(RepaymentDetails.builder()
                    .amount(amountPerPayment)
                    .loanId(loanId)
                    .scheduledOn(currentDate)
                    .status(LoanStage.PENDING.getValue())
                    .build());
        }
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

            double remainingAmount = totalAmount - amount;
            int remainingduration = repaymentDetailsList.size() - indexOfRepayment;

            double revisedReinstallationAmount = Math.round((remainingAmount/remainingduration) * 10.0)/10.0;

            for (int i = indexOfRepayment ; i < repaymentDetailsList.size(); i++) {

                repaymentObject = repaymentDetailsList.get(i);
                repaymentObject.setAmount(revisedReinstallationAmount);
            }

            if(remainingAmount <= 0.0) {

                if(repaymentDetailsList.size() > indexOfRepayment) {

                    for (int i = indexOfRepayment ; i < repaymentDetailsList.size() ; i++) {
                        repaymentObject = repaymentDetailsList.get(i);
                        repaymentObject.setStatus(LoanStage.NOT_REQUIRED.getValue());
                    }
                }

                return Pair.of(ResponseCodes.LOAN_COMPLETED.getValue(),remainingAmount);
            } else {
                return Pair.of(ResponseCodes.SCHEDULED_PAYMENT_COMPLETED.getValue(),remainingAmount);
            }

        } else {
            log.info("Amount is less than the scheduled Repayment");
            return Pair.of(ResponseCodes.LESS_AMOUNT.getValue(),totalAmount);
        }



/*
        while (amount > 0.0) {

            log.info("********** Amount before process *********" + amount);

            RepaymentDetails repaymentDetails = repaymentDetailsList.get(indexOfRepayment);
            double amountOfRepaymentPerInst = repaymentDetails.getAmount();

            if (amount >= amountOfRepaymentPerInst) {
                repaymentDetails.setStatus(LoanStage.COMPLETED.getValue());
                repaymentDetails.setPaidAmount(amountOfRepaymentPerInst);
                amount -=amountOfRepaymentPerInst;
                repaymentInstallationCount++;
                amount = Math.round(amount * 10.0) / 10.0;
            } else {

                repaymentDetails.setPaidAmount(amount);
                amountOfRepaymentPerInst -= amount;
                repaymentDetails.setAmount(amountOfRepaymentPerInst);
                amount = 0.0;
            }
            indexOfRepayment++;

            log.info("****** Amount after process ******** " + amount);
        }

        if (repaymentDetailsList.size() == repaymentInstallationCount) {
            return true;
        }
        return false;
        */
    }
}

