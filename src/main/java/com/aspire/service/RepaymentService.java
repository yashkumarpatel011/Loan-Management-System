package com.aspire.service;

import com.aspire.exceptions.UserException;
import com.aspire.model.LoanDetails;
import com.aspire.model.RepaymentDetails;
import org.springframework.data.util.Pair;

public interface RepaymentService {

    public void createRepayment(LoanDetails details);

    public Pair<String,Double> rePaymentPaid(Double amount, Integer loanId, Double totalAmount) throws UserException;
}
