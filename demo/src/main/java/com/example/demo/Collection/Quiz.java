package com.example.demo.Collection;

import com.example.demo.Collection.extras.Difficulty;
import com.example.demo.Collection.extras.Questions;
import com.example.demo.Collection.extras.QuizType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;

@Data
@Builder
@Document(collection = "quiz")
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
public class Quiz {

    @Id
    private String quizID;

    @NotEmpty
    @NotNull
    @Size(min = 10,max = 80)
    private String title;
    @NotNull
    private String description;
    private Difficulty difficulty;
    private QuizType quizType;
    @NotNull
    private Questions questions;
    @NotNull
    @NotEmpty
    private String authorID;
    @NotEmpty
    private String authorName;
    private List<String> tags;

    private int time;
    private Date timestamp;
    private Boolean isImported;
    private int timesPlayed;
    private double avgRating;

}
