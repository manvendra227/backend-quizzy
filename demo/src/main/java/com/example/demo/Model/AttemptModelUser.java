package com.example.demo.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AttemptModelUser {
    private String quizID;
    private String title;
    private Double score;
    private Long time;
    private Double feedback;
}
