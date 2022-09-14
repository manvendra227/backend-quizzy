package com.example.demo.Collection.extras;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@Builder
public class questionFormat {

    @NotEmpty
    private String question;
    private List<String> options;
    @NotEmpty
    private String answer;
    private String explanation;
}
