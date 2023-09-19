package com.aspire.service.Impl;

import com.aspire.model.UserData;
import com.aspire.repository.UserRepository;
import com.aspire.utils.Constant;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @InjectMocks
    UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    UserData adminData = UserData.builder().id(1)
            .fullName("Yash Patel").email("yash@gmail.com")
            .role("ROLE_ADMIN").password("yash@123").isVerified(true).build();

    UserData userData = UserData.builder().id(2)
            .fullName("Anil Patel").email("anil@gmail.com")
            .role("ROLE_USER").password("anil@123").isVerified(false).build();

    @Test
    public void testRegisterUser() throws Exception {

        Mockito.when(userRepository.save(Mockito.any())).thenReturn(userData);

        UserData result = userService.registerUser(userData);

        assertEquals(result.getFullName(), "Anil Patel");
    }

    @Test
    public void testValidateUser() throws Exception {

        userData.setVerified(true);
        Mockito.when(userRepository.save(Mockito.any())).thenReturn(userData);

        UserData testUser = UserData.builder().id(2)
                .fullName("Yash Patel").email("yash@gmail.com")
                .role("ROLE_USER").password("Yash@123").isVerified(false).build();

        userService.validateUser(testUser);

        assertEquals(testUser.isVerified(),true);

    }

    @Test
    public void fetchAdminUser() throws Exception {

        Mockito.when(userRepository.findByRole(Mockito.any())).thenReturn(Arrays.asList(adminData));

        UserData result = userService.fetchAdminUser();

        assertEquals(result.getRole(),Constant.ROLE_ADMIN);
    }

    @Test
    public void testFetchUser() {

        Mockito.when(userRepository.findByEmail(Mockito.any())).thenReturn(userData);

        UserData testUser = userService.fetchUser("anil@gmail.com");

        assertEquals(testUser.getRole(), Constant.ROLE_USER);
    }
}
