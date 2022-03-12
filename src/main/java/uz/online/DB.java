package uz.online;




import uz.online.database.CollectData;
import uz.online.database.JsonConfig;
import uz.online.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


import static uz.online.model.BotState.DEFAULT;

public class DB {

    static CollectData collectData = new CollectData();
    public static Subject subject = new Subject();

    public static String subjectFile = "src/main/resources/subjects.json";
    public static String userFile = "src/main/resources/users.json";
    public static String testFile = "src/main/resources/tests.json";
    public static String answerFile = "src/main/resources/answers.json";
    public static String historyFile = "src/main/resources/history.json";
    public static String userAnswerFile = "src/main/resources/userAnswer.json";
    public static String userExcel = "src/main/resources/results/user.xlsx";

    public static List<Subject> subjectList = new ArrayList<>();
    public static List<User> userList = new ArrayList<>();
    public static List<Test> testList = new ArrayList<>();
    public static List<Answer> answerList = new ArrayList<>();
    public static List<TestHistory> testHistoryList = new ArrayList<>();
    public static List<UserAnswer> userAnswerList = new ArrayList<>();


    public void migration() {

        collectData.collectSubject();
        collectData.collectUser();
        collectData.collectHistory();
        JsonConfig<User> jsonConfig = new JsonConfig<>();
        for (User user : userList) {
            user.setCurrentRound(0);
            user.setLastRound(0);
            user.setSubject(null);
            user.setState(DEFAULT);
        }
        jsonConfig.writeJson(userFile,userList);
        System.out.println("RUNNING DATABASE");
    }

}
