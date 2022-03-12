package uz.online.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;


@AllArgsConstructor
@NoArgsConstructor
@Data


public class Answer {

    private Integer id;
    private String body ;
    private  boolean isCorrect;

    public Answer(String body, boolean isCorrect) {
        this.body = body;
        this.isCorrect = isCorrect;
    }
}
