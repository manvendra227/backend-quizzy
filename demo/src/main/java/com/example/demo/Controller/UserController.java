package com.example.demo.Controller;

import com.example.demo.Collection.User;
import com.example.demo.Collection.VerificationToken;
import com.example.demo.Collection.extras.UserPersonal;
import com.example.demo.Email.SendEmailService;
import com.example.demo.Event.RegistrationCompleteEvent;
import com.example.demo.Model.LoginModel;
import com.example.demo.Model.PasswordModel;
import com.example.demo.Model.UserModel;
import com.example.demo.Services.UserService;
import com.example.demo.Exceptions.GeneralException;
import com.example.demo.Exceptions.UserNotFoundException;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

@RestController
@Validated
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private SendEmailService sendEmailService;
    @Autowired
    private ApplicationEventPublisher Publisher;

    @GetMapping("/login")
    public String loginUser(@RequestBody @NotNull LoginModel loginModel) throws GeneralException, UserNotFoundException {
        if (loginModel.getEmailID() == null || loginModel.getPassword() == null) {
            throw new GeneralException("Email or Password should not be null [Email:" + loginModel.getEmailID() + ", Password:" + loginModel.getPassword() + "]");
        }
        if (!loginModel.getPassword().isEmpty() && !loginModel.getEmailID().isEmpty() && !loginModel.getEmailID().isBlank() && !loginModel.getPassword().isBlank())
            return userService.loginUser(loginModel);
        throw new GeneralException("Email-ID or Password should not be blank");
    }

    @PostMapping
    public String saveUser(@RequestBody UserModel userModel, final HttpServletRequest request) throws GeneralException {
        String check = CheckUserModel(userModel);
        if (check.equalsIgnoreCase("Success")) {
            User user = userService.saveUser(userModel);
            Publisher.publishEvent(new RegistrationCompleteEvent(user, applicationUrl(request)));
            return user.getUserId();
        }
        return check;
    }

    @GetMapping("/verifyRegistration")
    public String verifyRegistration(@RequestParam("token") String token) {
        String result = userService.validateVerificationToken(token);
        if (result.equalsIgnoreCase("valid")) {
            return "User Verified Successfully";
        }
        return "Bad User";
    }

    @GetMapping("/resendVerifyToken")
    public String resendVerificationToken(@RequestParam("token") String oldToken,
                                          HttpServletRequest request) {
        VerificationToken verificationToken
                = userService.generateNewVerificationToken(oldToken);
        resendVerificationTokenMail(verificationToken.getEmailID(), applicationUrl(request), verificationToken);
        return "Verification Link Sent";
    }

    @PostMapping("/resetPassword")
    public String resetPassword(@RequestBody PasswordModel passwordModel, HttpServletRequest request) throws GeneralException {
        if (passwordModel.getEmailId() == null || passwordModel.getNewPassword() == null) {
            throw new GeneralException("Email or Password should not be null [Email:" + passwordModel.getEmailId() + ", Password:" + passwordModel.getNewPassword() + "]");
        }
        User user = userService.findUserByEmail(passwordModel.getEmailId());
        String url = "";
        if (user != null) {
            String token = UUID.randomUUID().toString();
            userService.createPasswordResetTokenForUser(user, token);
            url = passwordResetTokenMail(user.getEmailID(), applicationUrl(request), token);
        } else {
            throw new GeneralException("No such user exists with email : " + passwordModel.getEmailId());
        }
        return url;
    }

    @PostMapping("/savePassword")
    public String savePassword(@RequestParam("token") String token,
                               @RequestBody PasswordModel passwordModel) {
        String result = userService.validatePasswordResetToken(token);

        if (!result.equalsIgnoreCase("valid")) {
            if (result.equalsIgnoreCase("Invalid"))
                return "Token does not exists/Invalid Token";
            return "TokenExpired";
        }

        Optional<String> userID = userService.getUserByPasswordResetToken(token);
        if (userID.isPresent()) {
            userService.changePassword(userID.get(), passwordModel.getNewPassword());
            return "Password Reset Successfully";
        } else {
            return "Invalid Token";
        }
    }

    @PostMapping("/changePassword")
    public String changeUserPassword(@RequestBody @NotNull PasswordModel passwordModel) {
        Optional<User> user = Optional.ofNullable(userService.findUserByEmail(passwordModel.getEmailId()));
        if (user.isPresent()) {
            if (!userService.checkIfValidOldPassword(user.get(), passwordModel.getOldPassword())) {
                return "Invalid Old Password";
            }
            //Save New password
            userService.changePassword(user.get().getUserId(), passwordModel.getNewPassword());
            return "New password saved successfully";
        }
        return "User Not Found";
    }

    @GetMapping("/{id}")
    public User findUserByID(@PathVariable("id") String userID) throws UserNotFoundException {
        return userService.findUserById(userID);
    }

    @DeleteMapping("/{id}")
    public void deleteUserById(@PathVariable("id") String userID) {
        userService.deleteUserById(userID);
    }

    @PutMapping("/{id}")
    public User updateUser(@PathVariable("id") String userID, @RequestBody User user) {
        return userService.updateUser(userID, user);
    }

    @PutMapping("/userpersonal/{id}")
    public UserPersonal updateUserAfterQuiz(@PathVariable("id") String userID, @RequestParam int newQuestions, @RequestParam int correctNewQuestions) {
        return userService.updateUserAfterQuiz(userID, newQuestions, correctNewQuestions);
    }

    @PutMapping("/userpersonal/wishlist/{id}")
    public List<String> updateWishlist(@PathVariable("id") String userID, @RequestParam String quizID) {
        return userService.updateUserWishList(userID, quizID);
    }

    @PutMapping("/userpersonal/searchlist/{id}")
    public List<String> updateSearchList(@PathVariable("id") String userID, @RequestParam String searchTag) {
        return userService.updateUserSearchList(userID, searchTag);
    }


    //Not Routes
    private String applicationUrl(HttpServletRequest request) {
        return "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
    }

    private void resendVerificationTokenMail(String emailId, String applicationUrl, VerificationToken verificationToken) {
        String url = applicationUrl + "/resendVerifyToken?token=" + verificationToken;
        sendEmailService.sendSimpleEmail(emailId, url, "VerificationLink-Resent");
    }

    private String passwordResetTokenMail(String emailId, String applicationUrl, String token) {
        String url = applicationUrl + "/user/savePassword?token=" + token;
        sendEmailService.sendSimpleEmail(emailId, url, "Password reset mail ");
        return url;
    }

    private String CheckUserModel(UserModel userModel) throws GeneralException {
        Pattern p = Pattern.compile("^(.+)@(\\S+)$");
        if (userModel.getName().isEmpty() || userModel.getName().isBlank())
            throw new GeneralException("Name should not be blank");
        else if (userModel.getEmailId().isBlank() || userModel.getEmailId().isEmpty())
            throw new GeneralException("Email-ID should not be blank");
        else if (!p.matcher(userModel.getEmailId()).find())
            throw new GeneralException("Email-ID does not follow email pattern");
        else if (userModel.getPassword().isBlank() || userModel.getPassword().isEmpty())
            throw new GeneralException("Password should not be blank");
        else if (userModel.getPassword().length() <= 7)
            throw new GeneralException("Password should be atleast 8 characters long");
        else return "Success";
    }

}
