package com.example.demo.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AttemptSaveModel {

    private String userId;
    private String quizId;
    private Double score;
    private Date startTime;
    private Date endTime;
    private Double feedback;
    private Integer newQuestions;
    private Integer newCorrect;
}
