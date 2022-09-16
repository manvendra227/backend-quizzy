package com.example.demo.Services;

import com.example.demo.Collection.Quiz;
import com.example.demo.Exceptions.GeneralException;
import com.example.demo.Model.QuizShortModel;
import com.example.demo.Repository.QuizRepository;
import com.example.demo.Exceptions.QuizNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class QuizServiceImpl implements QuizService {
    @Autowired
    private QuizRepository quizRepository;
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public String saveQuiz(Quiz quiz) {
        return quizRepository.save(quiz).getQuizID();
    }

    @Override
    public Quiz findQuizById(String quizID) throws QuizNotFoundException {
        Optional<Quiz> quiz = quizRepository.findById(quizID);
        if (!quiz.isPresent()) {
            throw new QuizNotFoundException("Quiz does not exists");
        }
        return quiz.get();
    }

    @Override
    public void deleteQuizById(String quizID) {
        quizRepository.deleteById(quizID);
    }

    @Override
    public Quiz updateQuiz(String quizID, Quiz quiz) {
        Quiz tempQuiz = quizRepository.findById(quizID).get();
        quizRepository.save(tempQuiz);
        return tempQuiz;
    }

    @Override
    public List<QuizShortModel> fetchByRating(Double minRating, Double maxRating) {
        List<Quiz> quiz = quizRepository.findQuizByRatingBetween(minRating, maxRating);
        return quiz.stream()
                .map(quiz1 -> new QuizShortModel(
                        quiz1.getQuizID(),
                        quiz1.getTitle(),
                        quiz1.getDescription(),
                        quiz1.getDifficulty(),
                        quiz1.getAuthorID(),
                        quiz1.getAuthorName(),
                        quiz1.getTimesPlayed(),
                        quiz1.getAvgRating(),
                        quiz1.getTimestamp()))
                .sorted(Comparator.comparing(QuizShortModel::getAvgRating).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public Page<QuizShortModel> searchQuiz(String title, String desc, String author, Pageable pageable) {
        Query query = new Query().with(pageable);
        List<Criteria> criteria = new ArrayList<>();

        if (title != null && !title.isEmpty()) {
            criteria.add(Criteria.where("title").regex(title, "i"));
        }
        if (desc != null && !desc.isEmpty()) {
            criteria.add(Criteria.where("description").regex(desc, "i"));
        }
        if (author != null && !author.isEmpty()) {
            criteria.add(Criteria.where("authorName").regex(author, "i"));
        }

        if (!criteria.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(criteria.toArray(new Criteria[0])));
        }

        List<Quiz> quizzes = mongoTemplate.find(query, Quiz.class);
        List<QuizShortModel> quiz = quizzes.stream().map(quiz1 -> new QuizShortModel(
                quiz1.getQuizID(),
                quiz1.getTitle(),
                quiz1.getDescription(),
                quiz1.getDifficulty(),
                quiz1.getAuthorID(),
                quiz1.getAuthorName(),
                quiz1.getTimesPlayed(),
                quiz1.getAvgRating(),
                quiz1.getTimestamp())).toList();

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), quiz.size());
        return new PageImpl<>(quiz.subList(start, end), pageable, quiz.size());
    }

    @Override
    public Page<QuizShortModel> search(String searchKey, Pageable pageable) throws GeneralException {
        Query query = new Query();
        List<Criteria> criteria = new ArrayList<>();


        if (searchKey != null && !searchKey.isEmpty()) {

            criteria.add(Criteria.where("title").regex(searchKey, "i"));

            criteria.add(Criteria.where("tags").regex(searchKey, "i"));

            StringTokenizer st1 = new StringTokenizer(searchKey);
            while (st1.hasMoreTokens()) {
                criteria.add(Criteria.where("tags").regex(st1.nextToken(), "i"));

            }
            StringTokenizer st2 = new StringTokenizer(searchKey);
            while (st2.hasMoreTokens()) {
                criteria.add(Criteria.where("title").regex(st2.nextToken(), "i"));

            }
        } else throw new GeneralException("SearchWord empty");

        query.addCriteria(new Criteria().orOperator(criteria.toArray(new Criteria[0])));

        List<Quiz> quizzes = mongoTemplate.find(query, Quiz.class);

        List<QuizShortModel> quiz = quizzes.stream().map(quiz1 -> new QuizShortModel(
                quiz1.getQuizID(),
                quiz1.getTitle(),
                quiz1.getDescription(),
                quiz1.getDifficulty(),
                quiz1.getAuthorID(),
                quiz1.getAuthorName(),
                quiz1.getTimesPlayed(),
                quiz1.getAvgRating(),
                quiz1.getTimestamp())).toList();

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), quiz.size());
        return new PageImpl<>(quiz.subList(start, end), pageable, quiz.size());
    }

    @Override
    public List<QuizShortModel> fetchUserUploads(String authorId) {
        List<Quiz> quiz = quizRepository.findByAuthorID(authorId);
        return quiz.stream()
                .map(quiz1 -> new QuizShortModel(
                        quiz1.getQuizID(),
                        quiz1.getTitle(),
                        quiz1.getDescription(),
                        quiz1.getDifficulty(),
                        quiz1.getAuthorID(),
                        quiz1.getAuthorName(),
                        quiz1.getTimesPlayed(),
                        quiz1.getAvgRating(),
                        quiz1.getTimestamp()))
                .sorted(Comparator.comparing(QuizShortModel::getTimestamp))
                .collect(Collectors.toList());
    }

    @Override
    public Page<QuizShortModel> loadQuiz(Pageable pageable) {
        List<Quiz> found=quizRepository.findAll();

        List<QuizShortModel> quiz=found.stream()
                .map(quiz1 -> new QuizShortModel(
                        quiz1.getQuizID(),
                        quiz1.getTitle(),
                        quiz1.getDescription(),
                        quiz1.getDifficulty(),
                        quiz1.getAuthorID(),
                        quiz1.getAuthorName(),
                        quiz1.getTimesPlayed(),
                        quiz1.getAvgRating(),
                        quiz1.getTimestamp()))
                .sorted(Comparator.comparing(QuizShortModel::getTimesPlayed).reversed())
                .collect(Collectors.toList());

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), quiz.size());
        return new PageImpl<>(quiz.subList(start, end), pageable, quiz.size());
    }


}
