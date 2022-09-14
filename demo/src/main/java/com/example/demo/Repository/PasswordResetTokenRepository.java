package com.example.demo.Repository;

import com.example.demo.Collection.PasswordResetToken;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PasswordResetTokenRepository extends MongoRepository<PasswordResetToken, ObjectId> {
    PasswordResetToken findByToken(String token);

    PasswordResetToken findByUserId(String userId);
}
