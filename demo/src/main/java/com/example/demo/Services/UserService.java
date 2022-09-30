package com.example.demo.Services;

import com.example.demo.Collection.Photo;
import com.example.demo.Collection.User;
import com.example.demo.Collection.VerificationToken;
import com.example.demo.Model.UserModel;
import com.example.demo.Exceptions.GeneralException;
import com.example.demo.Exceptions.UserNotFoundException;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface UserService {

    User saveUser(UserModel userModel) throws GeneralException;

    User findUserById(String userID) throws UserNotFoundException;

    String deleteUserById(String id, String password);

    User updateUser(String userID, User user);

    List<String> updateUserWishList(String userID, String quizID);

    List<String> updateUserSearchList(String userID, String searchTag);

    void saveVerificationTokenForUser(User user, String token);

    String validateVerificationToken(String token);

    VerificationToken generateNewVerificationToken(String oldToken);

    User findUserByEmail(String emailId);

    void createPasswordResetTokenForUser(User user, String token);

    String validatePasswordResetToken(String token);

    Optional<String> getUserByPasswordResetToken(String token);

    void changePassword(String userID, String newPassword);

    boolean checkIfValidOldPassword(User user, String oldPassword);

    String loginUser(String emailID, String password) throws UserNotFoundException, GeneralException;

    List<String> fetchSearchList(String emailId);

    String saveUserPhoto(String originalFilename, MultipartFile photo, String emailId) throws IOException;

    Photo getPhoto(String emailId) throws Exception;
}
