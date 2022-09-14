package com.example.demo.Services;

import com.example.demo.Collection.User;
import com.example.demo.Collection.extras.UserPersonal;
import com.example.demo.Collection.VerificationToken;
import com.example.demo.Model.LoginModel;
import com.example.demo.Model.UserModel;
import com.example.demo.Exceptions.GeneralException;
import com.example.demo.Exceptions.UserNotFoundException;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User saveUser(UserModel userModel);

    User findUserById(String userID) throws UserNotFoundException;

    void deleteUserById(String id);

    User updateUser(String userID, User user);

    List<String> updateUserWishList(String userID, String quizID);

    List<String> updateUserSearchList(String userID, String searchTag);


    UserPersonal updateUserAfterQuiz(String userID, int newQuestions, int correctNewQuestions);

    void saveVerificationTokenForUser(User user, String token);

    String validateVerificationToken(String token);

    VerificationToken generateNewVerificationToken(String oldToken);

    User findUserByEmail(String emailId);

    void createPasswordResetTokenForUser(User user, String token);

    String validatePasswordResetToken(String token);

    Optional<String> getUserByPasswordResetToken(String token);

    void changePassword(String userID, String newPassword);

    boolean checkIfValidOldPassword(User user, String oldPassword);

    String loginUser(LoginModel loginModel) throws UserNotFoundException, GeneralException;
}
