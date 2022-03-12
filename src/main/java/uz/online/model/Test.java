package uz.online.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data

public class Test {

    private Integer id;
    private String name;
    private Answer correctAnswer;
    private List<Answer> answers = new ArrayList<>();

    public Test(String name, Answer correctAnswer, List<Answer> answers) {
        this.name = name;
        this.correctAnswer = correctAnswer;
        this.answers = answers;
    }
}
