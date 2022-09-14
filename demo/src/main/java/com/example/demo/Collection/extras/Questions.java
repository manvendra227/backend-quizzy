package com.example.demo.Collection.extras;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@ToString
public class Questions {

    @NotEmpty
    private int noOfQuestions;

    private List<questionFormat> question;
    @NotNull
    private Score score;

}
