package uz.online.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data



public class UserAnswer {

    private Integer id;
    private User user;
    private Answer answer;
    private Subject subject;
    private LocalDateTime createdAt = LocalDateTime.now();

    public UserAnswer(User user, Answer answer, Subject subject) {
        this.user = user;
        this.answer = answer;
        this.subject = subject;
    }

    public UserAnswer(User user, Answer answer, Subject subject, LocalDateTime createdAt) {
        this.user = user;
        this.answer = answer;
        this.subject = subject;
        this.createdAt = createdAt;
    }
}
