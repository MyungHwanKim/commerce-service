package com.zerobase.user.client.service;

import com.zerobase.user.client.MailgunClient;
import com.zerobase.user.client.mailgun.SendMailForm;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class EmailSendServiceTest {

    @Autowired
    private MailgunClient mailgunClient;

    @Test
    void EmailTest() {
        //given
        SendMailForm form = SendMailForm.builder()
                .from("code.kmh9250@gmail.com")
                .to("kmh9250@naver.com")
                .subject("Test email form zerobase")
                .text("my test")
                .build();

        // when
        String response = mailgunClient.sendEmail(form).getBody();

        // then
        System.out.println(response);
    }
}