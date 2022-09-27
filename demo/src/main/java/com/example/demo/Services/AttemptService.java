package com.example.demo.Services;

import com.example.demo.Exceptions.GeneralException;
import com.example.demo.Model.AttemptModelQuiz;
import com.example.demo.Model.AttemptModelQuizUser;
import com.example.demo.Model.AttemptModelUser;
import com.example.demo.Model.AttemptSaveModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AttemptService {
    void saveAttempt(AttemptSaveModel attempt) throws GeneralException;

    List<AttemptModelUser> fetchPastAttemptsOfUser(String userId);

    List<AttemptModelQuiz> fetchPastAttemptsOnQuiz(String quizId);

    List<AttemptModelQuiz> findTop3(String quizId);

    List<AttemptModelQuizUser> fetchPastAttemptsOnQuizByUser(String quizId, String userId);

    void deleteAllAttempt(String quizId, String userId);
}
