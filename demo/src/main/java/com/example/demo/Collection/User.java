package com.example.demo.Collection;

import com.example.demo.Collection.extras.Gender;
import com.example.demo.Collection.extras.Status;
import com.example.demo.Collection.extras.UserPersonal;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

@Data
@Document(collection = "user")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Validated
@ToString
//@CompoundIndex()
public class User {

    @Id
    private String userId;

    @NotNull(message = "Name cannot be null")
    @NotEmpty(message = "Name cannot be empty")
    private String name;

    @Indexed(unique = true)
    @Email
    private String emailID;

    @NotNull(message = "Password cannot be null")
    @NotEmpty(message = "Password cannot be empty")
    @Size(max = 100, min = 6)
    private String password;

    private boolean enabled;
    private Gender gender;
    private String date_of_birth;
    private Status status;
    private MongoProperties.Gridfs profilePic;
    private boolean isVerified;
    private Date timestamp;
    private UserPersonal userPersonal;
}
