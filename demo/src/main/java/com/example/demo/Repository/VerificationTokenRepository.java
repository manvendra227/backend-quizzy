package com.example.demo.Repository;

import com.example.demo.Collection.VerificationToken;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VerificationTokenRepository extends MongoRepository<VerificationToken, ObjectId> {
    VerificationToken findByToken(String token);
}
