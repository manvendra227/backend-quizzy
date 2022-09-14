package com.example.demo.Collection.extras;

import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserPersonal {

    @NotNull
    private int questionsSolved;
    @NotNull
    private int questionsCorrect;
    private List<String> wishlist;
    private List<String> search;

}
