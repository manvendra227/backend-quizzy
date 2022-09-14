package com.example.demo.Repository;

import com.example.demo.Collection.Quiz;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizRepository extends MongoRepository<Quiz,String> {

    //{fields={address:0}}
    @Query(value = "{'avgRating':{ $gt:?0,$lte:?1}}",fields = "{questions: 0,quizPersonal: 0}")
    List<Quiz> findQuizByRatingBetween(Double min,Double max);


    List<Quiz> findByAuthorID(String authorID);
}
