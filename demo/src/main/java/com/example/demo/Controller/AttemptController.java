package com.example.demo.Controller;

import com.example.demo.Collection.Attempt;
import com.example.demo.Exceptions.GeneralException;
import com.example.demo.Model.AttemptModelQuiz;
import com.example.demo.Model.AttemptModelQuizUser;
import com.example.demo.Model.AttemptModelUser;
import com.example.demo.Model.AttemptSaveModel;
import com.example.demo.Services.AttemptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/attempt")
public class AttemptController {

    @Autowired
    private AttemptService attemptService;

    //Saves a attempt whenever quiz ends
    @PostMapping
    public void saveAttempt(@RequestBody AttemptSaveModel attemptSaveModel) throws GeneralException {
        attemptService.saveAttempt(attemptSaveModel);
    }

    //To refresh database by deleting attempts when quiz or user is deleted
    @DeleteMapping
    public void deleteAttempt(@RequestParam(required = false) String quizId,
                              @RequestParam(required = false) String userId) {
        attemptService.deleteAllAttempt(quizId, userId);
    }

    //All past attempts of a user
    @GetMapping("/userAttempts")
    public List<AttemptModelUser> fetchPastAttemptsOfUser(@RequestParam String userId) {
        return attemptService.fetchPastAttemptsOfUser(userId);
    }

    //All past attempts on a quiz
    @GetMapping("/quizAttempts")
    public List<AttemptModelQuiz> fetchPastAttemptsOnQuiz(@RequestParam String quizId) {

        return attemptService.fetchPastAttemptsOnQuiz(quizId);
    }

    //All past attempts by a user on a quiz
    @GetMapping("/userAttemptsOnQuiz")
    public List<AttemptModelQuizUser> fetchPastAttemptsOnQuizByUser(@RequestParam String quizId, @RequestParam String userId) {
        return attemptService.fetchPastAttemptsOnQuizByUser(quizId, userId);
    }

    //Best attempt on a quiz
    @GetMapping("/topScorers")
    public List<AttemptModelQuiz> findTop3Scorers(@RequestParam String quizId) {
        return attemptService.findTop3(quizId);
    }
}
