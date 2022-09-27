package com.example.demo.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AttemptModelQuiz {
    private String userId;
    private String username;
    private Double score;
    private Long time;
    private Double feedback;
    private Date startTime;
}
