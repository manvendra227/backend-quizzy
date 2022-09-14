package com.example.demo.Services;

import com.example.demo.Collection.Attempt;
import com.example.demo.Collection.Quiz;
import com.example.demo.Exceptions.GeneralException;
import com.example.demo.Model.AttemptModelQuiz;
import com.example.demo.Model.AttemptModelQuizUser;
import com.example.demo.Model.AttemptModelUser;
import com.example.demo.Repository.AttemptRepository;
import com.example.demo.Repository.QuizRepository;
import com.example.demo.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
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
    public void saveAttempt(Attempt attempt) throws GeneralException {
        Optional<Quiz> quiz = quizRepository.findById(attempt.getQuizId());
        if (quiz.isEmpty()) throw new GeneralException("Quiz Does not exists with given id");
        attemptRepository.save(attempt);
    }

    @Override
    public void deleteAllAttempt(String quizId, String userId) {
        if (quizId!=null && !quizId.isEmpty()){
            attemptRepository.deleteByQuizId(quizId);
        }
        if(userId!=null && !userId.isEmpty()){
            attemptRepository.deleteByUserId(userId);
        }
    }

    @Override
    public Page<AttemptModelUser> fetchPastAttemptsOfUser(String userId, Pageable pageable) {

        List<Attempt> found = attemptRepository.findByUserId(userId);
        List<AttemptModelUser> attempts = found.stream().map(attempt -> new AttemptModelUser(
                attempt.getQuizId(),
                quizRepository.findById(attempt.getQuizId()).get().getTitle(),
                attempt.getScore(),
                attempt.getEndTime().getTime() - attempt.getStartTime().getTime(),
                attempt.getFeedback()
        )).toList();
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), attempts.size());
        return new PageImpl<>(attempts.subList(start, end), pageable, attempts.size());
    }

    @Override
    public Page<AttemptModelQuiz> fetchPastAttemptsOnQuiz(String quizId, Pageable pageable) {

        List<Attempt> found = attemptRepository.findByQuizId(quizId);
        List<AttemptModelQuiz> attempts = found.stream().map(attempt -> new AttemptModelQuiz(
                attempt.getUserId(),
                userRepository.findById(attempt.getUserId()).get().getName(),
                attempt.getScore(),
                attempt.getEndTime().getTime() - attempt.getStartTime().getTime(),
                attempt.getFeedback())).toList();
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), attempts.size());
        return new PageImpl<>(attempts.subList(start, end), pageable, attempts.size());
    }

    @Override
    public List<AttemptModelQuiz> findTop3(String quizId) {

        List<Attempt> found = attemptRepository.findByQuizId(quizId);
        return found.stream().map(attempt -> new AttemptModelQuiz(
                attempt.getUserId(),
                userRepository.findById(attempt.getUserId()).get().getName(),
                attempt.getScore(),
                attempt.getEndTime().getTime() - attempt.getStartTime().getTime(),
                attempt.getFeedback())).sorted(Comparator.comparing(AttemptModelQuiz::getScore).reversed()).limit(3).toList();
    }

    @Override
    public List<AttemptModelQuizUser> fetchPastAttemptsOnQuizByUser(String quizId, String userId) {
        List<Attempt> found = attemptRepository.findByQuizIdAndUserId(quizId, userId);
        return found.stream().map(attempt -> new AttemptModelQuizUser(
                        attempt.getStartTime(),
                        attempt.getScore(),
                        attempt.getEndTime().getTime() - attempt.getStartTime().getTime()))
                .sorted(Comparator.comparing(AttemptModelQuizUser::getTimestamp)).collect(Collectors.toList());
    }

}
