package com.example.demo.Services;

import com.example.demo.Collection.PasswordResetToken;
import com.example.demo.Collection.Photo;
import com.example.demo.Collection.User;
import com.example.demo.Collection.VerificationToken;
import com.example.demo.Collection.extras.UserPersonal;
import com.example.demo.Model.UserModel;
import com.example.demo.Repository.*;
import com.example.demo.Exceptions.GeneralException;
import com.example.demo.Exceptions.UserNotFoundException;
import com.google.common.hash.Hashing;
import org.bson.BsonBinarySubType;
import org.bson.types.Binary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AttemptRepository attemptRepository;
    @Autowired
    private VerificationTokenRepository verificationTokenRepository;
    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private PhotoRepository photoRepository;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private PasswordEncoder passwordEncoder;


    @Override
    public String loginUser(String emailId, String password) {
        Optional<User> user = Optional.ofNullable(userRepository.findByEmailID(emailId));
        if (user.isEmpty()) {
            return "User does not exists with following email-ID : " + emailId + ", Signup to make an account";
        }
        boolean check = passwordEncoder.matches(password, user.get().getPassword());
        if (user.get().isEnabled()) {
            if (check) {
                return "Login Success | " + user.get().getUserId() + " | " + user.get().getName();
            }
            return "Password did not match";
        }
        return "User email :" + emailId + " Not verified,Check mail and verify ID";
    }


    @Override
    public User saveUser(UserModel userModel) throws GeneralException {
        Optional<User> found = Optional.ofNullable(userRepository.findByEmailID(userModel.getEmailId()));
        if (found.isEmpty()) {
            String id = "user" + userModel.getEmailId();
            String hashed = Hashing.sha256()
                    .hashString(id, StandardCharsets.UTF_8)
                    .toString();
            User user = new User();
            user.setUserId(hashed);
            user.setName(userModel.getName().trim());
            user.setEmailID(userModel.getEmailId());
            user.setPassword(passwordEncoder.encode(userModel.getPassword()));
            user.setGender(userModel.getGender());
            user.setStatus(userModel.getStatus());
            user.setDate_of_birth(userModel.getDob());
            user.setVerified(false);
            user.setTimestamp(Calendar.getInstance().getTime());
            user.setUserPersonal(new UserPersonal(0, 0, new ArrayList<>(), new ArrayList<>()));
            userRepository.save(user);
            return user;
        } else throw new GeneralException("User Already exists with Email :" + userModel.getEmailId());
    }


    @Override
    public void saveVerificationTokenForUser(User user, String token) {
        VerificationToken verificationToken = new VerificationToken(user, token);
        verificationTokenRepository.save(verificationToken);
    }

    @Override
    public String validateVerificationToken(String token) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token);

        if (verificationToken == null) {
            return "invalid";
        }

        User user = userRepository.findById(verificationToken.getUserId()).get();
        Calendar cal = Calendar.getInstance();

        if ((verificationToken.getExpirationTime().getTime() - cal.getTime().getTime()) <= 0) {
            verificationTokenRepository.delete(verificationToken);
            return "expired";
        }

        user.setEnabled(true);
        userRepository.save(user);
        verificationTokenRepository.delete(verificationToken);
        return "valid";
    }

    @Override
    public VerificationToken generateNewVerificationToken(String oldToken) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(oldToken);
        verificationToken.setToken(UUID.randomUUID().toString());
        verificationTokenRepository.save(verificationToken);
        return verificationToken;
    }

    @Override
    public User findUserByEmail(String emailId) {
        return userRepository.findByEmailID(emailId);
    }


    @Override
    public void createPasswordResetTokenForUser(User user, String token) {
        Optional<PasswordResetToken> found = Optional.ofNullable(passwordResetTokenRepository.findByUserId(user.getUserId()));

        found.ifPresent(passwordResetToken -> passwordResetTokenRepository.delete(passwordResetToken));
        PasswordResetToken passwordResetToken = new PasswordResetToken(user, token);
        passwordResetTokenRepository.save(passwordResetToken);

    }

    @Override
    public String validatePasswordResetToken(String token) {
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(token);

        if (passwordResetToken == null) {
            return "invalid";
        }
        Calendar cal = Calendar.getInstance();

        if ((passwordResetToken.getExpirationTime().getTime() - cal.getTime().getTime()) <= 0) {
            passwordResetTokenRepository.delete(passwordResetToken);
            return "expired";
        }

        passwordResetTokenRepository.delete(passwordResetToken);
        return "valid";
    }

    @Override
    public Optional<String> getUserByPasswordResetToken(String token) {
        return Optional.ofNullable(passwordResetTokenRepository.findByToken(token).getUserId());
    }

    @Override
    public void changePassword(String userID, String newPassword) {
        User user = userRepository.findById(userID).get();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    public boolean checkIfValidOldPassword(User user, String oldPassword) {
        return passwordEncoder.matches(oldPassword, user.getPassword());
    }


    @Override
    public User findUserById(String userID) throws UserNotFoundException {
        Optional<User> user = userRepository.findById(userID);
        if (!user.isPresent()) {
            throw new UserNotFoundException("User Not Available");
        }
        return user.get();
    }

    @Override
    public String deleteUserById(String id, String password) {
        Optional<User> found = userRepository.findById(id);

        if (found.isPresent() && (passwordEncoder.matches(found.get().getPassword(), password))) {
            userRepository.deleteById(id);
            attemptRepository.deleteAllByUserId(id);
            return "Delete Success";
        }
        return "Could not delete";
    }

    @Override
    public User updateUser(String userID, User user) {
        User tempUser = userRepository.findById(userID).get();
        return userRepository.save(tempUser);
    }

//    @Override
//    public UserPersonal updateUserAfterQuiz(String userID, int newQuestions, int correctNewQuestions) {
//        User tempUser = userRepository.findById(userID).get();
//        UserPersonal userPersonal = tempUser.getUserPersonal();
//        userPersonal.setQuestionsSolved(userPersonal.getQuestionsSolved() + newQuestions);
//        userPersonal.setQuestionsCorrect(userPersonal.getQuestionsCorrect() + correctNewQuestions);
//        userRepository.save(tempUser);
//        return userPersonal;
//    }


    @Override
    public List<String> updateUserWishList(String userID, String quizID) {
        User tempUser = userRepository.findById(userID).get();
        tempUser.getUserPersonal().getWishlist().add(quizID);
        userRepository.save(tempUser);
        return tempUser.getUserPersonal().getWishlist();
    }

    @Override
    public List<String> updateUserSearchList(String userID, String searchTag) {
        User tempUser = userRepository.findById(userID).get();
        tempUser.getUserPersonal().getSearch().add(searchTag);
        userRepository.save(tempUser);
        return tempUser.getUserPersonal().getSearch();
    }

    @Override
    public List<String> fetchSearchList(String emailId) {
        Optional<User> user = Optional.ofNullable(userRepository.findByEmailID(emailId));
        return user.map(value -> value.getUserPersonal().getSearch()).orElse(null);
    }

    @Override
    public String saveUserPhoto(String originalFilename, MultipartFile photo, String emailId) throws IOException {

        Optional<User> found = Optional.ofNullable(userRepository.findByEmailID(emailId));
        if (found.isPresent()) {
            Photo _photo = new Photo();
            _photo.setEmailId(emailId);
            _photo.setPhotoName(originalFilename);
            _photo.setPhoto(new Binary(BsonBinarySubType.BINARY, photo.getBytes()));
            photoRepository.save(_photo);

            String url = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/user/photo/download/")
                    .path(_photo.getEmailId())
                    .toUriString();
            found.get().setPhoto(url);
            userRepository.save(found.get());

            return "success";
        }
        return "failed";
    }

    @Override
    public Photo getPhoto(String emailId) throws Exception {
        return photoRepository.findById(emailId)
                .orElseThrow(()->new Exception("File not found with id = "+emailId));
    }


}
