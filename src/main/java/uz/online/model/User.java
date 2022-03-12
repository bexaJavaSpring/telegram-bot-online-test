package uz.online.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Data



public class User {

    private Long id;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String username;
    private int lastRound;
    private int currentRound;
    private boolean isAuth;
    private int testStep=0;
    private Subject subject;
    private BotState state;
    private String tempCode;
    private Test test;


    public User(Long id, int lastRound, int currentRound, boolean isAuth) {
        this.id = id;
        this.lastRound = lastRound;
        this.currentRound = currentRound;
        this.isAuth = isAuth;
    }

}
