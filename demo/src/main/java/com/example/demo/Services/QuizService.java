package com.example.demo.Services;

import com.example.demo.Collection.Quiz;
import com.example.demo.Exceptions.GeneralException;
import com.example.demo.Exceptions.QuizNotFoundException;
import com.example.demo.Model.QuizShortModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface QuizService {
    String saveQuiz(Quiz quiz);

    Quiz findQuizById(String quizID) throws QuizNotFoundException;

    void deleteQuizById(String quizID);

    Quiz updateQuiz(String quizID, Quiz quiz);

    List<QuizShortModel> fetchByRating(Double minRating, Double maxRating);

    Page<QuizShortModel> searchQuiz(String title, String desc, String author, Pageable pageable);

    Page<QuizShortModel> search(String searchKey, Pageable pageable) throws GeneralException;

    List<QuizShortModel> fetchUserUploads(String authorID);

    Page<QuizShortModel> loadQuiz(Pageable pageable);
}
