package com.aspire.service.Impl;

import jakarta.mail.internet.MimeMessage;
import jakarta.websocket.Session;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmailServiceTest {

    @InjectMocks
    EmailService emailService;

    @Mock
    private JavaMailSender javaMailSender;

    @Mock
    private MimeMessage message;

    @Test
    public void testMailService() throws Exception {

        ArgumentCaptor<MimeMessage> mimeMessageArgumentCaptor = ArgumentCaptor.forClass(MimeMessage.class);
        when(javaMailSender.createMimeMessage()).thenReturn(message);

        doNothing().when(javaMailSender).send(message);

        emailService.sendOtpMessage("test@gmail.com","email subject", "msg sent");

        verify(javaMailSender, times(1)).send((mimeMessageArgumentCaptor.capture()));
        assertNull(mimeMessageArgumentCaptor.getValue().getSubject());
    }
}
