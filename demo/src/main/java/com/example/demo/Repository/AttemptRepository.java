package com.example.demo.Repository;

import com.example.demo.Collection.Attempt;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttemptRepository extends MongoRepository<Attempt, ObjectId> {
    List<Attempt> findByUserId(String userId);

    List<Attempt> findByQuizId(String quizId);

    List<Attempt> findByQuizIdAndUserId(String quizId, String userId);

    void deleteByQuizId(String quizId);

    void deleteByUserId(String userId);
}
