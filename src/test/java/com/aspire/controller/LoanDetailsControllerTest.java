package com.aspire.controller;

import com.aspire.exceptions.UserException;
import com.aspire.model.LoanDetails;
import com.aspire.model.UserData;
import com.aspire.service.LoanService;
import com.aspire.service.UserService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class LoanDetailsControllerTest {

    @InjectMocks
    LoanDetailsController loanDetailsController;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Mock
    private UserService userService;

    @Mock
    private LoanService loanService;

    UserData userData = UserData.builder().id(1)
            .fullName("Yash Patel").email("yash@gmail.com")
            .role("ROLE_USER").password("yash@123").build();

    LoanDetails loanDetails = LoanDetails.builder().id(1)
            .userId(1).amount(1000.0).duration(3).rePaymentFreq("WEEKLY").interestRate(8.7)
            .status("pending").adminId(1).build();

    @BeforeEach
    public void before(){
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @Test
    public void testCreateLoan() throws Exception {

        Mockito.when(userService.loginUser()).thenReturn(userData);
        Mockito.when(loanService.createLoan(Mockito.any(),Mockito.any())).thenReturn(loanDetails);
        ResponseEntity<LoanDetails> responseEntity = loanDetailsController.createLoan(loanDetails);

        assertEquals(HttpStatus.CREATED.value(), responseEntity.getStatusCodeValue());
    }

    @Test
    public void testGetLoanDetails() throws Exception {

        Mockito.when(userService.loginUser()).thenReturn(userData);
        Mockito.when(loanService.getLoanDetails(Mockito.any())).thenReturn(Arrays.asList(loanDetails));
        ResponseEntity<List<LoanDetails>> responseEntity = loanDetailsController.getLoanDetails(1);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void testApproveOrRejectLoan() throws Exception {

        Mockito.when(userService.loginUser()).thenReturn(userData);
        Mockito.when(loanService.approveOrRejectLoan(Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(true);
        ResponseEntity<String> responseEntity = loanDetailsController.approveOrRejectLoan("ACCEPTED",1);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void testApproveFalse() throws Exception {

        Mockito.when(userService.loginUser()).thenReturn(userData);
        Mockito.when(loanService.approveOrRejectLoan(Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(false);
        ResponseEntity<String> responseEntity = loanDetailsController.approveOrRejectLoan("ACCEPTED",1);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    public void testWithoutApproveOrReject() throws Exception {

        ResponseEntity<String> responseEntity = loanDetailsController.approveOrRejectLoan("APPROVED",1);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    public void testReassignLoan() throws Exception {

        Mockito.when(loanService.reassignLoan(Mockito.any())).thenReturn("success");
        ResponseEntity<String> responseEntity = loanDetailsController.reAssignLoan(1);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void testRePaymentPaid() throws Exception {

        Mockito.when(loanService.loanPayment(Mockito.any(),Mockito.any())).thenReturn("success");
        ResponseEntity<String> responseEntity = loanDetailsController.rePaymentPaid(200.0,1);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }
}
