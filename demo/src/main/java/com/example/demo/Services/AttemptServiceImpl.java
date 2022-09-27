package com.example.demo.Services;

import com.example.demo.Collection.Attempt;
import com.example.demo.Collection.Quiz;
import com.example.demo.Collection.User;
import com.example.demo.Collection.extras.UserPersonal;
import com.example.demo.Exceptions.GeneralException;
import com.example.demo.Model.AttemptModelQuiz;
import com.example.demo.Model.AttemptModelQuizUser;
import com.example.demo.Model.AttemptModelUser;
import com.example.demo.Model.AttemptSaveModel;
import com.example.demo.Repository.AttemptRepository;
import com.example.demo.Repository.QuizRepository;
import com.example.demo.Repository.UserRepository;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class AttemptServiceImpl implements AttemptService {

    @Autowired
    private AttemptRepository attemptRepository;
    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void saveAttempt(AttemptSaveModel attemptSaveModel) throws GeneralException {
        Optional<Quiz> quiz = quizRepository.findById(attemptSaveModel.getQuizId());
        if (quiz.isEmpty()) throw new GeneralException("Quiz Does not exists with given id");
        Attempt attempt = new Attempt();
        attempt.setUserId(attemptSaveModel.getUserId());
        attempt.setQuizId(attemptSaveModel.getQuizId());
        attempt.setScore(attemptSaveModel.getScore());
        attempt.setStartTime(attemptSaveModel.getStartTime());
        attempt.setEndTime(attemptSaveModel.getEndTime());
        attempt.setFeedback(attemptSaveModel.getFeedback());
        attemptRepository.save(attempt);

        Double rating = quiz.get().getAvgRating();
        int timesRated = quiz.get().getTimesPlayed();
        double newRating = ((rating * timesRated) + attemptSaveModel.getFeedback()) / (timesRated + 1);
        quiz.get().setTimesPlayed(quiz.get().getTimesPlayed() + 1);
        quiz.get().setAvgRating(newRating);
        quizRepository.save(quiz.get());


        Optional<User> user = userRepository.findById(attemptSaveModel.getUserId());
        if (user.isPresent()) {
            UserPersonal userPersonal = user.get().getUserPersonal();
            userPersonal.setQuestionsSolved(userPersonal.getQuestionsSolved() + attemptSaveModel.getNewQuestions());
            userPersonal.setQuestionsCorrect(userPersonal.getQuestionsCorrect() + attemptSaveModel.getNewCorrect());
            userRepository.save(user.get());
        }
    }

    @Override
    public void deleteAllAttempt(String quizId, String userId) {
        if (quizId != null && !quizId.isEmpty()) {
            attemptRepository.deleteByQuizId(quizId);
        }
        if (userId != null && !userId.isEmpty()) {
            attemptRepository.deleteByUserId(userId);
        }
    }

    @Override
    public List<AttemptModelUser> fetchPastAttemptsOfUser(String userId) {

        List<Attempt> found = attemptRepository.findByUserId(userId);

        List<AttemptModelUser> attempts = found.stream().map(attempt -> new AttemptModelUser(
                attempt.getQuizId(),
                quizRepository.findById(attempt.getQuizId()).get().getTitle(),
                attempt.getScore(),
                (quizRepository.findById(attempt.getQuizId()).get().getQuestions().getScore().getPassingScore()),
                attempt.getEndTime().getTime() - attempt.getStartTime().getTime(),
                attempt.getStartTime())).limit(50).sorted(Comparator.comparing(AttemptModelUser::getStartTime).reversed()).toList();
        return attempts;
    }

    @Override
    public List<AttemptModelQuiz> fetchPastAttemptsOnQuiz(String quizId) {

        List<Attempt> found = attemptRepository.findByQuizId(quizId);
        List<AttemptModelQuiz> attempts = found.stream().map(attempt -> new AttemptModelQuiz(
                attempt.getUserId(),
                userRepository.findById(attempt.getUserId()).get().getName(),
                attempt.getScore(),
                attempt.getEndTime().getTime() - attempt.getStartTime().getTime(),
                attempt.getFeedback(),
                attempt.getStartTime()
        )).limit(25).sorted(Comparator.comparing(AttemptModelQuiz::getStartTime).reversed()).toList();
        return attempts;
    }

    @Override
    public List<AttemptModelQuiz> findTop3(String quizId) {

        List<Attempt> found = attemptRepository.findByQuizId(quizId);
        return found.stream().map(attempt -> new AttemptModelQuiz(
                attempt.getUserId(),
                userRepository.findById(attempt.getUserId()).get().getName(),
                attempt.getScore(),
                attempt.getEndTime().getTime() - attempt.getStartTime().getTime(),
                attempt.getFeedback(),
                attempt.getStartTime())).sorted(Comparator.comparing(AttemptModelQuiz::getScore).reversed()).limit(3).toList();
    }

    @Override
    public List<AttemptModelQuizUser> fetchPastAttemptsOnQuizByUser(String quizId, String userId) {
        List<Attempt> found = attemptRepository.findByQuizIdAndUserId(quizId, userId);
        return found.stream().map(attempt -> new AttemptModelQuizUser(
                        attempt.getStartTime(),
                        attempt.getScore(),
                        attempt.getEndTime().getTime() - attempt.getStartTime().getTime()))
                .sorted(Comparator.comparing(AttemptModelQuizUser::getTimestamp).reversed()).collect(Collectors.toList());
    }

}
