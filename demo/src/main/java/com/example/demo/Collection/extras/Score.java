package com.example.demo.Collection.extras;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotNull;
import java.nio.DoubleBuffer;

@Data
@Builder
@ToString
public class Score {

    private Double maxScore;
    private Double passingScore;
    private Double onCorrect;
    private Double onWrong;
}
