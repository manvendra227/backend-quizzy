package com.example.demo.Collection;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.validation.annotation.Validated;

import java.util.Date;

@Data
@Document(collection = "attempts")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Validated
@ToString
public class Attempt {

    private String userId;
    private String quizId;
    private Double score;
    private Date startTime;
    private Date endTime;
    private Double feedback;
}
