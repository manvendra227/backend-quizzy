package com.example.demo.Collection;

import com.example.demo.Collection.User;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Calendar;
import java.util.Date;

@Data
@Document
@NoArgsConstructor
public class PasswordResetToken {

    //Expiration time 10 miutes
    private static final int EXPIRATION_TIME = 10;

    @Id
    private ObjectId objectId;
    private String token;
    private Date expirationTime;
    @Indexed(unique = true)
    private String userId;
    private String emailID;


    public PasswordResetToken(User user, String token) {
        super();
        this.token = token;
        this.expirationTime = calculateExpirationDate(EXPIRATION_TIME);
        this.userId = user.getUserId();
        this.emailID = user.getEmailID();
    }

    public PasswordResetToken(String token) {
        super();
        this.token = token;
        this.expirationTime = calculateExpirationDate(EXPIRATION_TIME);
    }

    private Date calculateExpirationDate(int expirationTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(new Date().getTime());
        calendar.add(Calendar.MINUTE, expirationTime);
        return new Date(calendar.getTime().getTime());
    }
}
