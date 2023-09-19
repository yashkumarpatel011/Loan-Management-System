package com.aspire.service.Impl;

import com.aspire.model.LoanDetails;
import com.aspire.model.UserData;
import com.aspire.repository.LoanRepository;
import com.aspire.service.RepaymentService;
import com.aspire.service.UserService;
import com.aspire.utils.Constant;
import com.aspire.utils.ResponseCodes;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class LoanServiceImplTest {

    @InjectMocks
    LoanServiceImpl loanService;

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private RepaymentService repaymentService;

    @Mock
    private UserService userService;

    UserData userData = UserData.builder().id(1)
            .fullName("Yash Patel").email("yash@gmail.com")
            .role("ROLE_USER").password("yash@123").build();

    UserData adminData = UserData.builder().id(2)
            .fullName("Anil Patel").email("anil@gmail.com")
            .role("ROLE_ADMIN").password("anil@123").build();

    LoanDetails loanDetails = LoanDetails.builder().id(1)
            .userId(1).amount(1000.0).duration(3).rePaymentFreq("WEEKLY").interestRate(8.7)
            .status("pending").adminId(2).build();

    @Test
    public void testCreateLoan() throws Exception {

        Mockito.when(userService.fetchAdminUser()).thenReturn(adminData);
        Mockito.when(loanRepository.save(Mockito.any())).thenReturn(loanDetails);

        LoanDetails testDetails = LoanDetails.builder().amount(1000.0)
                .duration(3).rePaymentFreq("WEEKLY")
                .build();

        LoanDetails result = loanService.createLoan(userData,testDetails);

        assertEquals(result.getId(), 1);
        assertEquals(result.getAmount(),1000.0);
        assertEquals(result.getStatus(), "pending");
        assertEquals(result.getAdminId(),2);
    }

    @Test
    public void testApproveOrRejectLoan() throws Exception {

        Optional<LoanDetails> optional = Optional.of(loanDetails);

        Mockito.when(loanRepository.findById(Mockito.any())).thenReturn(optional);
        Mockito.when(loanRepository.updateStatus(Mockito.any(), Mockito.any())).thenReturn(1);

        boolean result = loanService.approveOrRejectLoan(1,adminData,"accepted");

        assertEquals(true,result);
    }

    @Test
    public void testApproveOrRejectLoanNegative() throws Exception {

        Optional<LoanDetails> optional = Optional.of(loanDetails);

        Mockito.when(loanRepository.findById(Mockito.any())).thenReturn(optional);
        Mockito.when(loanRepository.updateStatus(Mockito.any(), Mockito.any())).thenReturn(2);

        boolean result = loanService.approveOrRejectLoan(1,adminData,"accepted");

        assertEquals(false,result);
    }

    @Test
    public void testApproveOrRejectLoanUserNegative() throws Exception {

        Optional<LoanDetails> optional = Optional.empty();

        Mockito.when(loanRepository.findById(Mockito.any())).thenReturn(optional);

        boolean result = loanService.approveOrRejectLoan(1,adminData,"accepted");

        assertEquals(false,result);
    }

    @Test
    public void testGetLoanDetails() throws Exception {

        Mockito.when(loanRepository.findAllByUserId(Mockito.any())).thenReturn(Arrays.asList(loanDetails));

        List<LoanDetails> loanList =  loanService.getLoanDetails(userData);
        assertEquals(loanList.get(0).getStatus(),"pending");
    }

    @Test
    public void testGetAdminUser() throws Exception {

        LoanDetails tempLoan = LoanDetails.builder().id(2).adminId(1).userId(3).rePaymentFreq("WEEKLY").amount(1000.0).duration(3).status("pending").build();
        UserData userData1 = UserData.builder().id(2)
                .fullName("Yash Patel").email("yash@gmail.com")
                .role("ROLE_ADMIN").password("yash@123").build();
        Mockito.when(loanRepository.findAll()).thenReturn(Arrays.asList(loanDetails,tempLoan));

        List<LoanDetails> resultList = loanService.getLoanDetails(userData1);

        assertEquals(2, resultList.size());
    }

    @Test
    public void testCommonUser() {

        UserData userData1 = UserData.builder().id(2)
                .fullName("Yash Patel").email("yash@gmail.com")
                .role("ROLE_COMMON").password("yash@123").build();

        List<LoanDetails> resultList = loanService.getLoanDetails(userData1);

        assertNull(resultList);
    }

    @Test
    public void testReassignLoan() throws Exception {

        Mockito.when(userService.loginUser()).thenReturn(userData);
        Mockito.when(loanRepository.updateLoanAssignee(Mockito.any(), Mockito.any())).thenReturn(1);

        String result = loanService.reassignLoan(1);

        assertEquals(ResponseCodes.ASSIGNED_UPDATED.getValue(),result);
    }

    @Test
    public void testLoanRepayment() throws Exception {

        Optional<LoanDetails> optional = Optional.of(loanDetails);
        Mockito.when(userService.loginUser()).thenReturn(userData);
        Mockito.when(loanRepository.findById(Mockito.any())).thenReturn(optional);

        String result = loanService.loanPayment(3000.0,1);

        assertEquals(ResponseCodes.REPAYMENT_PENDING.getValue(), result);
    }

    @Test
    public void testLoanRepaymentPAID() throws Exception {

        LoanDetails loanDetails1 = LoanDetails.builder().id(1)
                .userId(1).amount(1000.0).duration(3).rePaymentFreq("WEEKLY").interestRate(8.7)
                .status("PAID").adminId(2).build();

        Optional<LoanDetails> optional = Optional.of(loanDetails1);
        Mockito.when(userService.loginUser()).thenReturn(userData);
        Mockito.when(loanRepository.findById(Mockito.any())).thenReturn(optional);

        String result = loanService.loanPayment(3000.0,1);

        assertEquals(ResponseCodes.REPAYMENT_PAID.getValue(), result);
    }

    @Test
    public void testLoanRepaymentAPPROVED() throws Exception {

        LoanDetails loanDetails1 = LoanDetails.builder().id(1)
                .userId(1).amount(1000.0).duration(3).rePaymentFreq("WEEKLY").interestRate(8.7)
                .status("REJECTED").adminId(2).build();

        Optional<LoanDetails> optional = Optional.of(loanDetails1);
        Mockito.when(userService.loginUser()).thenReturn(userData);
        Mockito.when(loanRepository.findById(Mockito.any())).thenReturn(optional);

        String result = loanService.loanPayment(3000.0,1);

        assertEquals(ResponseCodes.REPAYMENT_REJECTED.getValue(), result);
    }

    @Test
    public void testLoanRepaymentCANCELLED() throws Exception {

        LoanDetails loanDetails1 = LoanDetails.builder().id(1)
                .userId(1).amount(1000.0).duration(3).rePaymentFreq("WEEKLY").interestRate(8.7)
                .status("CANCELLED").adminId(2).build();

        Optional<LoanDetails> optional = Optional.of(loanDetails1);
        Mockito.when(userService.loginUser()).thenReturn(userData);
        Mockito.when(loanRepository.findById(Mockito.any())).thenReturn(optional);

        String result = loanService.loanPayment(3000.0,1);

        assertEquals(ResponseCodes.REPAYMENT_CANCELLED.getValue(), result);
    }

    @Test
    public void testNotHaveLoan() throws Exception {

        LoanDetails loanDetails1 = LoanDetails.builder().id(1)
                .userId(1).amount(1000.0).duration(3).rePaymentFreq("WEEKLY").interestRate(8.7)
                .status("CANCELLED").adminId(2).build();

        Optional<LoanDetails> optional = Optional.empty();
        Mockito.when(userService.loginUser()).thenReturn(userData);
        Mockito.when(loanRepository.findById(Mockito.any())).thenReturn(optional);

        String result = loanService.loanPayment(3000.0,1);

        assertEquals(ResponseCodes.NOT_HAVE_LOAN.getValue(), result);
    }
}
