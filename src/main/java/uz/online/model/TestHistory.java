package uz.online.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data



public class TestHistory {


    private Integer id;
    private User user;
    private List<UserAnswer> answerList=new ArrayList<>();
    private double result;
    private Subject subject;
    private String duration;
    private LocalDateTime createdAt = LocalDateTime.now();

    public TestHistory(User user, List<UserAnswer> answerList, double result) {
        this.user = user;
        this.answerList = answerList;
        this.result = result;
    }

    public TestHistory(User user, List<UserAnswer> answerList, double result, String duration) {
        this.user = user;
        this.answerList = answerList;
        this.result = result;
        this.duration = duration;
    }
}
