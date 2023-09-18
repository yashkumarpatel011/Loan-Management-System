package com.aspire.controller;

import com.aspire.model.UserData;
import com.aspire.repository.UserRepository;
import com.aspire.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.skyscreamer.jsonassert.JSONAssert;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@WebMvcTest(value = UserController.class)
@WithMockUser
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    UserData userData = UserData.builder().id(1)
            .fullName("Yash Patel").email("yash@gmail.com")
            .role("ROLE_USER").password("yash@123").build();

    String exampleJson = "{\n" +
            "    \"id\": 1,\n" +
            "    \"fullName\": \"Yash Patel\",\n" +
            "    \"password\": \"yash@123\",\n" +
            "    \"email\": \"yash@gmail.com\",\n" +
            "    \"role\": \"ROLE_USER\"\n" +
            "}";

    @Test
    public void testLoggedInCustomerDetailsHandler() throws Exception {

        Mockito.when(userRepository.findByEmail(Mockito.any())).thenReturn(userData);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(
                "/signIn").accept(
                MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        System.out.println(result.getResponse());
        String expected = "{\n" +
                "    \"fullName\": \"Yash Patel\",\n" +
                "    \"password\": \"yash@123\",\n" +
                "    \"email\": \"yash@gmail.com\",\n" +
                "    \"role\": \"ROLE_USER\"\n" +
                "}";


        JSONAssert.assertEquals(expected, result.getResponse()
                .getContentAsString(), false);
    }

    @Test
    public void testNotLogginedIn() throws Exception {
        Mockito.when(userRepository.findByEmail(Mockito.any())).thenReturn(null);
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(
                "/signIn").accept(
                MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertThrows(BadCredentialsException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                throw new BadCredentialsException("Invalid Username or password");
            }
        });
    }


    @Test
    public void testLoginUser() throws Exception {

        Mockito.when(userService.loginUser()).thenReturn(userData);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(
                "/logged-in/user").accept(
                MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        String expected = "Welcome to Yash's API World  : Yash Patel role: ROLE_USER";

        assertEquals(expected,result.getResponse().getContentAsString());

    }

    @Test
    public void testNotLoggin() throws Exception {
        Mockito.when(userRepository.findByEmail(Mockito.any())).thenReturn(null);
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(
                "/logged-in/user").accept(
                MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertThrows(BadCredentialsException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                throw new BadCredentialsException("Invalid credentials");
            }
        });
    }

    @Test
    public void testSignup() throws Exception {
        Mockito.when(passwordEncoder.encode(Mockito.any())).thenReturn("yash@123");

        Mockito.when(userService.registerUser(Mockito.any())).thenReturn(userData);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/app/signup")
                .accept(MediaType.APPLICATION_JSON).content(exampleJson);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        MockHttpServletResponse response = result.getResponse();

        assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus());
    }
}
