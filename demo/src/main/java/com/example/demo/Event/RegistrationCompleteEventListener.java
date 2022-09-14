package com.example.demo.Event;

import com.example.demo.Collection.User;
import com.example.demo.Email.SendEmailService;
import com.example.demo.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RegistrationCompleteEventListener implements ApplicationListener<RegistrationCompleteEvent> {
    @Autowired
    private UserService userService;
    @Autowired
    private SendEmailService sendEmailService;
    @Override
    public void onApplicationEvent(RegistrationCompleteEvent event) {

        //Creating verification token and link
        User user=event.getUser();
        String token= UUID.randomUUID().toString();
        userService.saveVerificationTokenForUser(user,token);

        //Sending Mail to user
        String url =
                event.getApplicationUrl()
                        + "/user/verifyRegistration?token="
                        + token;
        sendEmailService.sendSimpleEmail(user.getEmailID(),url,"VerificationLink");
    }


}
