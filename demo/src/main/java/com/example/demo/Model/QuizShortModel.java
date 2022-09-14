package com.example.demo.Model;

import com.example.demo.Collection.extras.Difficulty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizShortModel {
    private String quizId;
    private String title;
    private String description;
    private Difficulty difficulty;
    private String authorID;
    private String authorName;
    private int timesPlayed;
    private double avgRating;
    private Date timestamp;
}
