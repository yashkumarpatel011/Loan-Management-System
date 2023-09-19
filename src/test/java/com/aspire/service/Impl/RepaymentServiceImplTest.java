package com.aspire.service.Impl;

import com.aspire.model.LoanDetails;
import com.aspire.model.RepaymentDetails;
import com.aspire.repository.RepaymentRepository;
import com.aspire.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class RepaymentServiceImplTest {

    @InjectMocks
    RepaymentServiceImpl repaymentService;

    @Mock
    private RepaymentRepository repaymentRepository;

    @Mock
    private UserService userService;

    @Test
    public void testCreateRepayment() throws Exception {

        Mockito.when(repaymentRepository.saveAll(Mockito.any())).thenReturn(new ArrayList<>());

        LoanDetails loanDetails = LoanDetails.builder().id(1)
                .userId(1).amount(1000.0).duration(3).rePaymentFreq("WEEKLY").interestRate(8.7)
                .status("pending").adminId(2).build();

        repaymentService.createRepayment(loanDetails);


    }
}
