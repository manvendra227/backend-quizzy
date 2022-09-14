package com.example.demo.Services;

import com.example.demo.Collection.Attempt;
import com.example.demo.Exceptions.GeneralException;
import com.example.demo.Model.AttemptModelQuiz;
import com.example.demo.Model.AttemptModelQuizUser;
import com.example.demo.Model.AttemptModelUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AttemptService {
    void saveAttempt(Attempt attempt) throws GeneralException;

    Page<AttemptModelUser> fetchPastAttemptsOfUser(String userId, Pageable pageable);

    Page<AttemptModelQuiz> fetchPastAttemptsOnQuiz(String quizId, Pageable pageable);

    List<AttemptModelQuiz> findTop3(String quizId);

    List<AttemptModelQuizUser> fetchPastAttemptsOnQuizByUser(String quizId, String userId);

    void deleteAllAttempt(String quizId, String userId);
}
