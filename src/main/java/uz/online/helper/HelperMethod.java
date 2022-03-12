package uz.online.helper;



import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.online.database.JsonConfig;
import uz.online.model.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

import static uz.online.DB.*;
import static uz.online.config.BotConfig.step;

public class HelperMethod {

    public User findUser(Update update) {
        JsonConfig<User> jsonConfig = new JsonConfig<>();
        for (User user : userList)
            if (user.getId().longValue() == update.getMessage().getFrom().getId().longValue()) return user;

        User newUser = new User(update.getMessage().getFrom().getId(), 0, 0, false);
        newUser.setState(BotState.DEFAULT);
        userList.add(newUser);
        jsonConfig.writeJson(userFile, userList);
        return newUser;

    }

    public User findUserCallBack(Update update) {
        JsonConfig<User> jsonConfig = new JsonConfig<>();
        for (User user : userList)
            if (user.getId().longValue() == update.getCallbackQuery().getFrom().getId().longValue()) return user;

        User newUser = new User(update.getCallbackQuery().getFrom().getId(), 0, 0, false);
        newUser.setState(BotState.DEFAULT);
        userList.add(newUser);
        jsonConfig.writeJson(userFile, userList);
        return newUser;
    }

    public User changeRound(User currentUser, int lastRound, int currentRound) {
        JsonConfig<User> jsonConfig = new JsonConfig<>();
        User userFind = null;
        for (User user : userList) {
            if (user.getId().longValue() == currentUser.getId().longValue()) {
                user.setLastRound(lastRound);
                user.setCurrentRound(currentRound);
                userFind = user;
                break;
            }
        }

        jsonConfig.writeJson(userFile, userList);
        return userFind;
    }

    public User changeUserData(User currentUser, Contact contact) {
        JsonConfig<User> jsonConfig = new JsonConfig<>();
        User userFind = null;
        for (User user : userList) {
            if (user.getId().longValue() == currentUser.getId().longValue()) {
                user.setFirstName(contact.getFirstName());
                user.setLastName(contact.getLastName());
                user.setPhoneNumber(contact.getPhoneNumber());
                userFind = user;
                break;
            }
        }

        jsonConfig.writeJson(userFile, userList);
        return userFind;
    }

    public User setSubject(User currentUser, Subject subject) {
        JsonConfig<User> jsonConfig = new JsonConfig<>();
        User userFind = null;
        for (User user : userList) {
            if (user.getId().longValue() == currentUser.getId().longValue()) {
                user.setSubject(subject);
                userFind = user;
                break;
            }
        }

        jsonConfig.writeJson(userFile, userList);
        return userFind;
    }

    public User setTestStep(User currentUser) {
        JsonConfig<User> jsonConfig = new JsonConfig<>();
        User userFind = null;
        for (User user : userList) {
            if (user.getId().longValue() == currentUser.getId().longValue()) {
                user.setTestStep(user.getTestStep() + 1);
                userFind = user;
                break;
            }
        }

        jsonConfig.writeJson(userFile, userList);
        return userFind;
    }


    public User setSubjectNull(User currentUser) {
        JsonConfig<User> jsonConfig = new JsonConfig<>();
        User userFind = null;
        for (User user : userList) {
            if (user.getId().longValue() == currentUser.getId().longValue()) {
                user.setSubject(null);
                user.setTestStep(0);
                userFind = user;
                break;
            }
        }

        jsonConfig.writeJson(userFile, userList);
        return userFind;
    }

    public String timeDifference(LocalDateTime dateTime, LocalDateTime dateTime2) throws ParseException {

        String time1 = dateTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        String time2 = dateTime2.format(DateTimeFormatter.ofPattern("HH:mm:ss"));

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");


        Date date1 = simpleDateFormat.parse(time1);
        Date date2 = simpleDateFormat.parse(time2);


        long differenceInMilliSeconds = Math.abs(date2.getTime() - date1.getTime());

        long differenceInHours = (differenceInMilliSeconds / (60 * 60 * 1000)) % 24;

        // Calculating the difference in Minutes
        long differenceInMinutes = (differenceInMilliSeconds / (60 * 1000)) % 60;

        long differenceInSeconds = (differenceInMilliSeconds / 1000) % 60;

        if (differenceInHours > 0) {
            return ("Difference is " + differenceInHours + " hours " + differenceInMinutes + " minutes " + differenceInSeconds + " Seconds. ");
        } else if (differenceInMinutes > 0) {
            return (differenceInMinutes + " minutes " + differenceInSeconds + " Seconds. ");
        } else {
            return (differenceInSeconds + " Seconds. ");
        }
    }

    public String testOption(User currentUser) {

        String test = "";
        if (currentUser.getTestStep() < currentUser.getSubject().getTests().size()) {
            for (int i = 0; i < step; i++) {
                String[] option = {"A", "B", "C", "D"};
                List<Test> tests = currentUser.getSubject().getTests();
                Test test1 = tests.get(currentUser.getTestStep());
                test += test1.getName() + "\n\n";
                for (int k = 0; k < test1.getAnswers().size(); k++) {
                    Answer answer = test1.getAnswers().get(k);
                    test += option[k] + " ) " + answer.getBody() + "\n";
                }
            }
        } else {
            test += "Please Submit test";
        }
        return test;
    }

    public User changeState(User currentUser, BotState crudMenu) {

        JsonConfig<User> jsonConfig = new JsonConfig<>();
        User userFind = null;
        for (User user : userList) {
            if (user.getId().longValue() == currentUser.getId().longValue()) {
                user.setState(crudMenu);
                userFind = user;
                break;
            }
        }

        jsonConfig.writeJson(userFile, userList);
        return userFind;

    }

    public User setSubjectAnswr(User currentUser) {

        JsonConfig<User> jsonConfig = new JsonConfig<>();
        User userFind = null;
        for (User user : userList) {
            if (user.getId().longValue() == currentUser.getId().longValue()) {
                user.getSubject().setTests(null);
                userFind = user;
                break;
            }
        }

        jsonConfig.writeJson(userFile, userList);
        return userFind;

    }

    public User addCode(User currentUser, int code) {

        JsonConfig<User> jsonConfig = new JsonConfig<>();
        User userFind = null;
        for (User user : userList) {
            if (user.getId().longValue() == currentUser.getId().longValue()) {
                user.setTempCode("" + code);
                userFind = user;
                break;
            }
        }

        jsonConfig.writeJson(userFile, userList);
        return userFind;


    }

    public User changeRoundConfirm(User currentUser, int lastRound, int currentRound) {
        JsonConfig<User> jsonConfig = new JsonConfig<>();
        User userFind = null;
        for (User user : userList) {
            if (user.getId().longValue() == currentUser.getId().longValue()) {
                user.setLastRound(lastRound);
                user.setCurrentRound(currentRound);
                user.setAuth(true);
                userFind = user;
                break;
            }
        }

        jsonConfig.writeJson(userFile, userList);
        return userFind;
    }

    public StringBuilder subjects() {
        StringBuilder subjects = new StringBuilder();
        int i = 1;
        for (Subject subject1 : subjectList) {
            subjects.append("<b>" + (i++) + ". " + subject1.getName() + "</b>\n");
        }
        return subjects;
    }

    public User setTest(User currentUser, Test test) {

        JsonConfig<User> jsonConfig = new JsonConfig<>();
        User userFind = null;
        for (User user : userList) {
            if (user.getId().longValue() == currentUser.getId().longValue()) {

                user.setTest(test);
                userFind = user;
                break;
            }
        }

        jsonConfig.writeJson(userFile, userList);
        return userFind;

    }

    public User setAnswer(User currentUser, String text) {

        JsonConfig<User> jsonConfig = new JsonConfig<>();
        User userFind = null;
        for (User user : userList) {
            if (user.getId().longValue() == currentUser.getId().longValue()) {

                Answer answer = new Answer();
                answer.setBody(text);
                user.getTest().getAnswers().add(answer);

                userFind = user;
                break;
            }
        }

        jsonConfig.writeJson(userFile, userList);
        return userFind;

    }

    public User addTest(User currentUser) {
        JsonConfig<User> jsonConfig = new JsonConfig<>();
        JsonConfig<Subject> jsonConfig1 = new JsonConfig<>();
        User userFind = null;
        for (User user : userList) {
            if (user.getId().longValue() == currentUser.getId().longValue()) {
                for (Subject subject1 : subjectList) {
                    if (subject1.getId().equals(user.getSubject().getId())) {
                        int size = currentUser.getSubject().getTests().size() - 1;
                        subject1.getTests().add(user.getTest());
                        break;
                    }
                }
                userFind = user;
                break;
            }
        }

        jsonConfig.writeJson(userFile, userList);
        jsonConfig1.writeJson(subjectFile, subjectList);
        return userFind;
    }

    public User setCorrectAnswer(User currentUser, Answer answer) {

        JsonConfig<User> jsonConfig = new JsonConfig<>();
        User userFind = null;
        for (User user : userList) {
            if (user.getId().longValue() == currentUser.getId().longValue()) {

                for (Answer answer1 : user.getTest().getAnswers()) {
                    if (answer1.equals(answer)) {
                        answer1.setCorrect(true);
                        break;
                    }
                }


                userFind = user;
                break;
            }
        }

        jsonConfig.writeJson(userFile, userList);
        return userFind;

    }

    public User removeTest(User currentUser, String data) {
        JsonConfig<User> jsonConfig = new JsonConfig<>();
        JsonConfig<Subject> jsonConfig1 = new JsonConfig<>();
        User userFind = null;
        for (User user : userList) {
            if (user.getId().longValue() == currentUser.getId().longValue()) {

                for (Subject subject1 : subjectList) {
                    if (subject1.getId().equals(user.getSubject().getId())) {
                        Test extracted = extracted(data, subject1);
                        subject1.getTests().remove(extracted);
                        break;
                    }
                }

                userFind = user;
                break;
            }
        }

        jsonConfig.writeJson(userFile, userList);
        jsonConfig1.writeJson(subjectFile, subjectList);
        return userFind;
    }

    private Test extracted(String data, Subject subject1) {
        for (Test test : subject1.getTests()) {
            String id = ""+test.getId();
            if (id.equals(data)) {
                return test;
            }

        }
        return null;
    }
}
