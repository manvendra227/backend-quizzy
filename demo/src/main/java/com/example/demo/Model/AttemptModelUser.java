package com.example.demo.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AttemptModelUser {
    private String quizID;
    private String title;
    private Double score;
    private Double passingScore;
    private Long time;
    private Date startTime;

}
