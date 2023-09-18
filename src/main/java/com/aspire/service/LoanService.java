package com.aspire.service;

import com.aspire.exceptions.UserException;
import com.aspire.model.LoanDetails;
import com.aspire.model.UserData;

import java.util.List;

public interface LoanService {

    public LoanDetails createLoan(UserData user, LoanDetails loanDetails);

    public boolean approveOrRejectLoan(Integer loanId, UserData user, String status);

    public List<LoanDetails> getLoanDetails(UserData user);

    public String reassignLoan(Integer loanId) throws UserException;

    public String loanPayment(Double amount, Integer loanId) throws UserException;
}
