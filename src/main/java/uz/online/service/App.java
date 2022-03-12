package uz.online.service;


import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import uz.online.config.BotConfig;
import uz.online.database.JsonConfig;
import uz.online.helper.DocGeneratorService;
import uz.online.helper.HelperMethod;
import uz.online.model.*;
import uz.online.service.button.AdminButtons;
import uz.online.service.button.UserButtons;
import uz.online.service.crud.SubjectCrud;
import uz.online.service.crud.TestCrud;
import uz.online.sms.SendSms;

import java.io.File;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static uz.online.DB.*;

public class App extends TelegramLongPollingBot implements BotConfig {

    static UserButtons userButtons = new UserButtons();
    static SendSms sendSms = new SendSms();
    static HelperMethod helperMethod = new HelperMethod();
    static DocGeneratorService docGeneratorService = new DocGeneratorService();
    static AdminButtons adminButtons = new AdminButtons();
    static AdminCrud adminCrud = new AdminCrud();
    static SendSms sms = new SendSms();
    static SubjectCrud subjectCrud = new SubjectCrud();
    static TestCrud testCrud = new TestCrud();

    @Override
    public String getBotUsername() {
        return BOT_USERNAME;
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }

    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage()) {

            User currentUser = helperMethod.findUser(update);
            Long chatId = update.getMessage().getChatId();

            Message message = update.getMessage();
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId.toString());

            if (currentUser.getId().longValue() == adminId.longValue()) {

                switch (currentUser.getState()) {
                    case DEFAULT: {
                        defaultAdmin(currentUser, chatId, message, sendMessage);
                    }
                    break;
                    case CRUD_MENU: {
                        try {
                            SendMessage sendMessage1 = adminCrud.adminCrudMenu(currentUser, update, message);
                            sendMessage1.setChatId(chatId.toString());
                            execute(sendMessage1);
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                    case SUBJECT_MENU: {
                        if (message.getText().equalsIgnoreCase("BACK")) {
                            currentUser = helperMethod.changeState(currentUser, BotState.CRUD_MENU);
                            currentUser = helperMethod.changeRound(currentUser, 1, 3);
                            sendMessage.setText("CRUD MENU");
                            sendMessage.setReplyMarkup(adminButtons.getreplyKeyboard(currentUser));
                        } else if (message.getText().equalsIgnoreCase("SUBJECT CREATE")) {
                            sendMessage.setText("Enter Subject Name");
                            currentUser = helperMethod.changeState(currentUser, BotState.SUBJECT_CREATE);
                        } else if (message.getText().equalsIgnoreCase("SUBJECT LIST")) {
                            StringBuilder subjectsList = helperMethod.subjects();
                            sendMessage.setText("<b>Subjects</b>\n" + subjectsList);
                            sendMessage.setParseMode(ParseMode.HTML);
                            sendMessage.setReplyMarkup(subjectCrud.getSubjectButton(currentUser));
                        } else if (message.getText().equalsIgnoreCase("SUBJECT DELETE")) {
                            currentUser = helperMethod.changeState(currentUser, BotState.SUBJECT_DELETE);
                            sendMessage.setText("Choose Subject");
                            sendMessage.setReplyMarkup(subjectCrud.getSubjectButton(currentUser));
                        } else if (message.getText().equalsIgnoreCase("SUBJECT UPDATE")) {
                            currentUser = helperMethod.changeState(currentUser, BotState.SUBJECT_UPDATE);
                            sendMessage.setText("Choose Subject");
                            sendMessage.setReplyMarkup(subjectCrud.getSubjectButton(currentUser));
                        }
                        try {
                            execute(sendMessage);
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                    case SUBJECT_CREATE: {
                        if (subject.getName() == null) {
                            subject.setName(message.getText());
                            sendMessage.setText("Enter point each answer");
                        } else {
                            double mark = Double.parseDouble(message.getText());
                            subject.setMark(mark);
                            sendMessage.setText("Successfully created");
                            currentUser = helperMethod.changeState(currentUser, BotState.SUBJECT_MENU);
                            sendMessage.setReplyMarkup(subjectCrud.getSubjectButton(currentUser));
                            subjectList.add(subject);
                            JsonConfig<Subject> subjectJsonConfig = new JsonConfig<>();
                            subjectJsonConfig.writeJson(subjectFile, subjectList);
                            subject = new Subject();
                        }
                        try {
                            execute(sendMessage);
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                    case SUBJECT_UPDATE: {
                        for (Subject subject1 : subjectList) {
                            if (subject1.getId().equals(currentUser.getSubject().getId())) {
                                subject1.setName(message.getText());
                                break;
                            }
                        }
                        JsonConfig<Subject> subjectJsonConfig = new JsonConfig<>();
                        subjectJsonConfig.writeJson(subjectFile, subjectList);
                        sendMessage.setText("Successfully updated");
                        currentUser = helperMethod.changeState(currentUser, BotState.SUBJECT_MENU);
                        sendMessage.setReplyMarkup(subjectCrud.getSubjectButton(currentUser));
                        try {
                            execute(sendMessage);
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                    case TEST_MENU:
                        if (message.getText().equalsIgnoreCase("BACK")) {
                            currentUser = helperMethod.changeState(currentUser, BotState.CRUD_MENU);
                            currentUser = helperMethod.changeRound(currentUser, 1, 3);
                            sendMessage.setText("CRUD MENU");
                            sendMessage.setReplyMarkup(adminButtons.getreplyKeyboard(currentUser));
                        } else if (message.getText().equalsIgnoreCase("TEST CREATE")) {
                            currentUser = helperMethod.changeState(currentUser, BotState.TEST_CREATE);
                            sendMessage.setText("Choose subject for create a test");
                            sendMessage.setReplyMarkup(testCrud.getSubjectButton(currentUser));
                        } else if (message.getText().equalsIgnoreCase("TEST DELETE")) {
                            currentUser = helperMethod.changeState(currentUser, BotState.TEST_DELETE);
                            sendMessage.setText("Choose subject for delete a test");
                            sendMessage.setReplyMarkup(testCrud.getSubjectButton(currentUser));
                        }
                        try {
                            execute(sendMessage);
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                        break;
                    case TEST_CREATE: {
                        Test test = new Test();
                        test.setName(message.getText());
                        currentUser = helperMethod.setTest(currentUser, test);
                        currentUser = helperMethod.changeState(currentUser, BotState.ANSWER_CREATE);
                        sendMessage.setText("Enter option");
                        try {
                            execute(sendMessage);
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                    case ANSWER_CREATE: {
                        boolean falg = false;
                        currentUser = helperMethod.setAnswer(currentUser, message.getText());
                        int size = 1;
                        if (currentUser.getTest().getAnswers().size() > 0)
                            size = currentUser.getTest().getAnswers().size();

                        if (size == 4) {
                            String answer = "" + currentUser.getTest().getName() + "\n";
                            String[] option = {"A", "B", "C", "D"};
                            for (int i = 0; i < currentUser.getTest().getAnswers().size(); i++) {
                                Answer answer1 = currentUser.getTest().getAnswers().get(i);
                                answer += option[i] + " ) " + answer1.getBody() + "\n";
                            }
                            sendMessage.setText("Subject " + currentUser.getSubject().getName() + "\nChoose correct answer\n\nTest : \n" + answer);
                            currentUser = helperMethod.changeState(currentUser, BotState.ANSWER_LIST);
                            sendMessage.setReplyMarkup(testCrud.getSubjectButton(currentUser));
                        } else {
                            sendMessage.setText("Enter option");
                        }
                        try {
                            execute(sendMessage);
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }


                    }
                    break;

                }
            } else {
                allRounds(currentUser, chatId, message, sendMessage);
                sendMessage.setChatId(chatId.toString());

                try {
                    execute(sendMessage);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }


        } else if (update.hasCallbackQuery()) {

            User currentUser = helperMethod.findUserCallBack(update);
            if (currentUser.getId().longValue() == adminId.longValue()) {
                adminCallBack(update);
            } else {
                callBack(update);
            }

        }


    }

    private void adminCallBack(Update update) {
        User currentUser = helperMethod.findUserCallBack(update);
        CallbackQuery callbackQuery = update.getCallbackQuery();
        Long chatId = callbackQuery.getMessage().getChatId();

        SendMessage sendMessage = new SendMessage();
        EditMessageText new_message = new EditMessageText();
        DeleteMessage deleteMessage = new DeleteMessage();

        deleteMessage.setChatId(chatId.toString());
        sendMessage.setChatId(chatId.toString());
        new_message.setChatId(chatId.toString());

        String data = callbackQuery.getData();
        switch (currentUser.getState()) {
            case SUBJECT_DELETE: {
                if (data.equalsIgnoreCase("Back")) {
                    sendMessage.setText("Subject Menu");
                    currentUser = helperMethod.changeState(currentUser, BotState.SUBJECT_MENU);
                    sendMessage.setReplyMarkup(subjectCrud.getSubjectButton(currentUser));
                } else {
                    Subject subject = null;
                    for (Subject subject1 : subjectList) {
                        if (subject1.getName().equalsIgnoreCase(data)) {
                            subject = subject1;
                            break;
                        }
                    }
                    subjectList.remove(subject);
                    JsonConfig<Subject> subjectJsonConfig = new JsonConfig<>();
                    subjectJsonConfig.writeJson(subjectFile, subjectList);
                    deleteMessage.setMessageId(callbackQuery.getMessage().getMessageId());
                    sendMessage.setText("Successfully Deleted");
                    currentUser = helperMethod.changeState(currentUser, BotState.SUBJECT_MENU);
                    sendMessage.setReplyMarkup(subjectCrud.getSubjectButton(currentUser));
                    try {
                        execute(deleteMessage);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }
            }
            break;
            case SUBJECT_UPDATE: {
                if (data.equalsIgnoreCase("Back")) {
                    sendMessage.setText("Subject Menu");
                    currentUser = helperMethod.changeState(currentUser, BotState.SUBJECT_MENU);
                    sendMessage.setReplyMarkup(subjectCrud.getSubjectButton(currentUser));
                    deleteMessage.setMessageId(callbackQuery.getMessage().getMessageId());
                    try {
                        execute(deleteMessage);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                } else {
                    Subject subject = subjectList.stream().filter(subject1 -> subject1.getName().equalsIgnoreCase(data)).findFirst().orElse(null);

                    currentUser = helperMethod.setSubject(currentUser, subject);
                    sendMessage.setText("Enter new subject name");
                    deleteMessage.setMessageId(callbackQuery.getMessage().getMessageId());
                    try {
                        execute(deleteMessage);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                    currentUser = helperMethod.changeState(currentUser, BotState.SUBJECT_UPDATE);
                }
            }
            break;
            case TEST_CREATE: {
                if (data.equalsIgnoreCase("Back")) {
                    sendMessage.setText("Test Menu");
                    currentUser = helperMethod.changeState(currentUser, BotState.TEST_MENU);
                    sendMessage.setReplyMarkup(testCrud.getSubjectButton(currentUser));
                    deleteMessage.setMessageId(callbackQuery.getMessage().getMessageId());
                    try {
                        execute(deleteMessage);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                } else {
                    Subject selectedSubject = subjectList.stream().filter(subject1 -> subject1.getName().equalsIgnoreCase(data)).findFirst().orElse(null);
                    currentUser = helperMethod.setSubject(currentUser, selectedSubject);
                    sendMessage.setText("Enter question");
                    deleteMessage.setMessageId(callbackQuery.getMessage().getMessageId());
                    try {
                        execute(deleteMessage);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }

            }
            break;
            case TEST_DELETE: {
                if (data.equalsIgnoreCase("Back")) {
                    sendMessage.setText("Subject Menu");
                    currentUser = helperMethod.changeState(currentUser, BotState.SUBJECT_MENU);
                    sendMessage.setReplyMarkup(subjectCrud.getSubjectButton(currentUser));
                } else {
                    for (Subject subject1 : subjectList) {
                        if (subject1.getName().equalsIgnoreCase(data)) {
                            currentUser = helperMethod.setSubject(currentUser, subject1);
                            currentUser = helperMethod.changeState(currentUser, BotState.TEST_LIST);
                            int i = 1;
                            String tests = "";
                            for (Test test : currentUser.getSubject().getTests()) {
                                tests += (i++) + ". " + test.getName() + "\n";
                            }
                            sendMessage.setText("Choose test" + "\n" + tests);
                            sendMessage.setReplyMarkup(testCrud.getSubjectButton(currentUser));
                            break;
                        }
                    }

                    deleteMessage.setMessageId(callbackQuery.getMessage().getMessageId());
                    try {
                        execute(deleteMessage);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }
            }
            break;
            case TEST_LIST:
                currentUser = helperMethod.removeTest(currentUser, data);
                sendMessage.setText("Deleted");
                currentUser = helperMethod.changeState(currentUser, BotState.TEST_MENU);
                sendMessage.setReplyMarkup(testCrud.getSubjectButton(currentUser));
                deleteMessage.setMessageId(callbackQuery.getMessage().getMessageId());
                try {
                    execute(deleteMessage);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
                break;
            case ANSWER_LIST:
                for (Answer answer : currentUser.getTest().getAnswers()) {
                    String id = "" + answer.getId();
                    if (id.equalsIgnoreCase(data)) {
                        currentUser = helperMethod.setCorrectAnswer(currentUser, answer);
                        break;
                    }
                }
                deleteMessage.setMessageId(callbackQuery.getMessage().getMessageId());
                try {
                    execute(deleteMessage);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
                sendMessage.setText("TEST MENU");
                currentUser = helperMethod.addTest(currentUser);
                currentUser = helperMethod.changeState(currentUser, BotState.TEST_MENU);
                sendMessage.setReplyMarkup(testCrud.getSubjectButton(currentUser));
                break;
        }
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void defaultAdmin(User currentUser, Long chatId, Message message, SendMessage sendMessage) {
        switch (currentUser.getCurrentRound()) {
            case 0:
                if (message.getText().equalsIgnoreCase("/start")) {
                    sendMessage.setText("WELCOME ONLINE TEST BOT");
                    sendMessage.setReplyMarkup(adminButtons.getreplyKeyboard(currentUser));
                    currentUser = helperMethod.changeRound(currentUser, 0, 1);
                }
                break;
            case 1:
                if (message.hasContact()) {
                    currentUser = helperMethod.changeUserData(currentUser, message.getContact());
//                                twillioSms(currentUser, sendMessage);
                }
//                            } else if (sendMessage.getText().equalsIgnoreCase(currentUser.getTempCode())) {
//                            }else{
//                                sendMessage.setText("Confirmation failed");
//                            }
                sendMessage.setText("Menu");
                sendMessage.setReplyMarkup(adminButtons.getreplyKeyboard(currentUser));
                currentUser = helperMethod.changeRound(currentUser, 1, 2);
                break;
            case 2: {
                if (message.getText().equalsIgnoreCase("\uD83D\uDCCA Download Results")) {
                    sendMessage.setText("Results");
                    SendDocument sendDocument = new SendDocument();
                    File file = docGeneratorService.pdfGenerator(currentUser);
                    InputFile inputFile = new InputFile(file);
                    sendDocument.setDocument(inputFile);
                    sendDocument.setCaption("All Data");
                    sendDocument.setChatId(chatId.toString());
                    currentUser = helperMethod.changeRound(currentUser, 2, 1);
                    sendMessage.setReplyMarkup(adminButtons.getreplyKeyboard(currentUser));
                    currentUser = helperMethod.changeRound(currentUser, 1, 2);
                    try {
                        execute(sendDocument);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                } else if (message.getText().equalsIgnoreCase("\uD83D\uDDD2 Download UserList")) {
                    sendMessage.setText("User List");
                    SendDocument sendDocument = new SendDocument();
                    File file = docGeneratorService.excelConventor(currentUser);
                    InputFile inputFile = new InputFile(file);
                    sendDocument.setDocument(inputFile);
                    sendDocument.setCaption("All Data");
                    sendDocument.setChatId(chatId.toString());
                    currentUser = helperMethod.changeRound(currentUser, 2, 1);
                    sendMessage.setReplyMarkup(adminButtons.getreplyKeyboard(currentUser));
                    currentUser = helperMethod.changeRound(currentUser, 1, 2);
                    try {
                        execute(sendDocument);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                } else if (message.getText().equalsIgnoreCase("\uD83D\uDEE0 CRUD MENU")) {
                    sendMessage.setText("CRUD MENU");
                    currentUser = helperMethod.changeRound(currentUser, 2, 3);
                    currentUser = helperMethod.changeState(currentUser, BotState.CRUD_MENU);
                    sendMessage.setReplyMarkup(adminButtons.getreplyKeyboard(currentUser));
                }
            }
            break;
            default:
                sendMessage.setText("SERVER ERROR");
                break;
        }
        try {

            execute(sendMessage);

        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void twillioSms(User currentUser, SendMessage sendMessage) {
        int code = sms.twillioApi();
        currentUser = helperMethod.addCode(currentUser, code);
        sendMessage.setText("Enter verification code we will send your phone");
    }

    private void allRounds(User currentUser, Long chatId, Message message, SendMessage sendMessage) {

        switch (currentUser.getCurrentRound()) {
            case 10: {
                if (message.getText().equalsIgnoreCase("Resend Code")) {
                    verifyCode(currentUser, sendMessage);
                    currentUser = helperMethod.changeRound(currentUser, 10, 1);
                }
            }
            break;
            case 0:
                if (message.getText().equalsIgnoreCase("/start")) {
                    sendMessage.setText("WELCOME TO MY ONLINE TEST BOT  "+currentUser.getUsername());
                    sendMessage.setText("Call center: (93) 620-75-16");
                    sendMessage.setReplyMarkup(userButtons.getreplyKeyboard(currentUser));
                    currentUser = helperMethod.changeRound(currentUser, 0, 1);
                }
                break;
            case 1:
                if (message.hasContact()) {
                    currentUser = helperMethod.changeUserData(currentUser, message.getContact());
                    verifyCode(currentUser, sendMessage);
                } else if (currentUser.isAuth() || message.getText().equalsIgnoreCase(currentUser.getTempCode())) {
                    sendMessage.setText("Menu");
                    sendMessage.setReplyMarkup(userButtons.getreplyKeyboard(currentUser));
                    currentUser = helperMethod.changeRoundConfirm(currentUser, 1, 2);
                } else {
                    sendMessage.setText("Confirmation Failed");
                    currentUser = helperMethod.changeRound(currentUser, 1, 10);
                    sendMessage.setReplyMarkup(userButtons.getreplyKeyboard(currentUser));
                }
                break;
            case 2:
                if (message.getText().equalsIgnoreCase("\uD83D\uDDD2 Take a Test")) {
                    sendMessage.setText("Choose a subject");
                    sendMessage.setReplyMarkup(userButtons.getreplyKeyboard(currentUser));
                    currentUser = helperMethod.changeRound(currentUser, 2, 3);
                } else if (message.getText().equalsIgnoreCase("\uD83D\uDCCA Results")) {
                    sendMessage.setText("Choose option");
                    currentUser = helperMethod.changeRound(currentUser, 2, 6);
                    sendMessage.setReplyMarkup(userButtons.getreplyKeyboard(currentUser));
                } else {
                    sendMessage.setText("UNAUTHORIZED");
                }
                break;
            case 3:
                if (message.getText().equalsIgnoreCase("✅ Confirm")) {

                    sendMessage.setText("Starting test....");
                    String test = helperMethod.testOption(currentUser);
                    sendMessage.setText(test);
                    sendMessage.setParseMode(ParseMode.MARKDOWN);
                    currentUser = helperMethod.changeRound(currentUser, 3, 4);
                    sendMessage.setReplyMarkup(userButtons.getreplyKeyboard(currentUser));

                } else if (message.getText().equalsIgnoreCase("✖️Cancel")) {
                    currentUser = helperMethod.changeRound(currentUser, 3, 2);
                    currentUser = helperMethod.setSubject(currentUser, null);
                    sendMessage.setText("Choose a subject, Test canceled");
                    sendMessage.setReplyMarkup(userButtons.getreplyKeyboard(currentUser));
                } else {
                    sendMessage.setText("UNAUTHORIZED");
                }
                break;
            case 5: {
                if (message.getText().equalsIgnoreCase("Menu")) {
                    sendMessage.setText("Menu");
                    currentUser = helperMethod.changeRound(currentUser, 5, 1);
                    sendMessage.setReplyMarkup(userButtons.getreplyKeyboard(currentUser));
                } else {
                    sendMessage.setText("UNAUTHORIZED");
                }
            }
            case 6: {
                if (message.getText().equalsIgnoreCase("Download All Time Stats")) {
                    User finalCurrentUser = currentUser;
                    long count = testHistoryList.stream().filter(testHistory -> finalCurrentUser.getId().longValue() == testHistory.getUser().getId().longValue()).count();
                    if (count > 0) {
                        SendDocument sendDocument = new SendDocument();
                        File file = docGeneratorService.pdfGenerator(currentUser);
                        InputFile inputFile = new InputFile(file);
                        sendDocument.setDocument(inputFile);
                        sendDocument.setCaption("Stats " + currentUser.getFirstName());
                        sendDocument.setChatId(chatId.toString());

                        sendMessage.setText("You can analyze your result");
                        sendMessage.setReplyMarkup(userButtons.getreplyKeyboard(currentUser));
                        try {
                            execute(sendDocument);
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                        file.delete();
                    } else {
                        sendMessage.setText("No Data Yet");
                        sendMessage.setReplyMarkup(userButtons.getreplyKeyboard(currentUser));
                    }
                } else if (message.getText().equalsIgnoreCase("Download Answer Sheet")) {
                    User finalCurrentUser = currentUser;
                    long count = testHistoryList.stream().filter(testHistory -> finalCurrentUser.getId().longValue() == testHistory.getUser().getId().longValue()).count();
                    if (count > 0) {
                        currentUser = helperMethod.changeRound(currentUser, 6, 7);
                        sendMessage.setText("Choose one result");
                        sendMessage.setReplyMarkup(userButtons.getreplyKeyboard(currentUser));
                    } else {
                        sendMessage.setText("No Data Yet");
                        sendMessage.setReplyMarkup(userButtons.getreplyKeyboard(currentUser));
                    }
                } else if (message.getText().equalsIgnoreCase("Menu")) {
                    currentUser = helperMethod.changeRound(currentUser, 6, 1);
                    sendMessage.setText("Menu");
                    sendMessage.setReplyMarkup(userButtons.getreplyKeyboard(currentUser));
                    currentUser = helperMethod.changeRound(currentUser, 1, 2);
                }
            }
            break;
            default:
                sendMessage.setText("SERVER ERROR");
                break;
        }
    }

    private void verifyCode(User currentUser, SendMessage sendMessage) {
        int code = sms.codeGenerate();
        currentUser = helperMethod.addCode(currentUser, code);
        sendMessage.setText("Verification code <b>" + code + "</b>\nEnter confirmation code");
        sendMessage.setParseMode(ParseMode.HTML);
    }

    private void callBack(Update update) {
        User currentUser = helperMethod.findUserCallBack(update);
        CallbackQuery callbackQuery = update.getCallbackQuery();
        Long chatId = callbackQuery.getMessage().getChatId();

        SendMessage sendMessage = new SendMessage();
        EditMessageText new_message = new EditMessageText();
        DeleteMessage deleteMessage = new DeleteMessage();

        sendMessage.setChatId(chatId.toString());
        new_message.setChatId(chatId.toString());

        String data = callbackQuery.getData();
        if (data.equalsIgnoreCase("Menu")) {
            currentUser = helperMethod.changeRound(currentUser, 2, 1);
            sendMessage.setText("Menu");
            sendMessage.setReplyMarkup(userButtons.getreplyKeyboard(currentUser));
            currentUser = helperMethod.changeRound(currentUser, 1, 2);
            try {
                execute(sendMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        } else if (currentUser.getCurrentRound() == 7) {
            downloadAnswerSheet(currentUser, data, sendMessage, chatId);
        } else {
            passTest(currentUser, callbackQuery, chatId, sendMessage, new_message, deleteMessage, data);
        }
    }

    private void passTest(User currentUser, CallbackQuery callbackQuery, Long chatId, SendMessage sendMessage, EditMessageText new_message, DeleteMessage deleteMessage, String data) {
        boolean isAnswered = false;
        boolean lastQuestion = false;

        if (currentUser.getSubject() != null && currentUser.getTestStep() < currentUser.getSubject().getTests().size()) {
            Test test = currentUser.getSubject().getTests().get(currentUser.getTestStep());
            int last = currentUser.getSubject().getTests().size() - 1;
            if (test.equals(currentUser.getSubject().getTests().get(last))) lastQuestion = true;
            for (Answer answer : test.getAnswers()) {
                String id = "" + answer.getId();
                if (id.equals(data)) {
                    UserAnswer userAnswer = new UserAnswer(currentUser, answer, currentUser.getSubject(), LocalDateTime.now());
                    userAnswerList.add(userAnswer);
                    isAnswered = true;
                    break;
                }
            }
        }

        if (isAnswered && lastQuestion) {
            double result = 0;
            double total = 0;
            boolean flag = false;
            LocalDateTime createdAt = null;
            String resultCollect = "";
            int i = 1;
            User finalCurrentUser = currentUser;
            List<UserAnswer> collect = userAnswerList.stream().filter(userAnswer -> userAnswer.getUser().getId().longValue() == finalCurrentUser.getId().longValue()).collect(Collectors.toList());

            for (UserAnswer userAnswer : collect) {
                if (!flag) {
                    createdAt = userAnswer.getCreatedAt();
                    flag = true;
                }
                if (userAnswer.getAnswer().isCorrect()) {
                    userAnswer.getSubject().setTests(null);
                    result += 1;
                    resultCollect += "✅ TEST " + i + "\n";
                } else {
                    resultCollect += "❌️ TEST " + i + "\n";
                }
                i++;
            }

            total = result * 100 / (i - 1);

            String dataResult = "SUBJECT   " + currentUser.getSubject().getName() + "\n\n" + resultCollect + "\n\n" + "<b>RESULT : " + total + " %</b>";
            deleteMessage.setChatId(chatId.toString());
            deleteMessage.setMessageId(callbackQuery.getMessage().getMessageId());

            String duration = "";
            try {
                duration = helperMethod.timeDifference(LocalDateTime.now(), createdAt);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            TestHistory testHistory = new TestHistory();
            currentUser = helperMethod.setSubjectAnswr(currentUser);
            testHistory.setSubject(currentUser.getSubject());
            currentUser = helperMethod.setSubjectNull(currentUser);
            testHistory.setAnswerList(collect);
            testHistory.setDuration(duration);
            testHistory.setResult(total);
            testHistory.setUser(currentUser);
            testHistoryList.add(testHistory);
            JsonConfig<TestHistory> historyJsonConfig = new JsonConfig<>();
            historyJsonConfig.writeJson(historyFile, testHistoryList);
            currentUser = helperMethod.changeRound(currentUser, 4, 5);

            sendMessage.setText("" + dataResult);
            sendMessage.setParseMode(ParseMode.HTML);
            sendMessage.setReplyMarkup(userButtons.getreplyKeyboard(currentUser));

            currentUser = helperMethod.changeRound(currentUser, 5, 1);
            userAnswerList.removeAll(collect);
            collect.clear();

        } else if (isAnswered) {

            currentUser = helperMethod.setTestStep(currentUser);
            String test = "" + helperMethod.testOption(currentUser);

            new_message.setText(test);
            new_message.setParseMode(ParseMode.MARKDOWN);
            new_message.setMessageId(callbackQuery.getMessage().getMessageId());
            new_message.setReplyMarkup((InlineKeyboardMarkup) userButtons.getreplyKeyboard(currentUser));

        } else if (!isAnswered && data.equalsIgnoreCase("Menu")) {
            currentUser = helperMethod.changeRound(currentUser, 2, 1);
            sendMessage.setText("Menu");
            sendMessage.setReplyMarkup(userButtons.getreplyKeyboard(currentUser));
            currentUser = helperMethod.changeRound(currentUser, 1, 2);
        } else if (!isAnswered) {
            Subject subjectSelect = null;
            for (Subject subject : subjectList) {
                if (subject.getName().equalsIgnoreCase(data)) {
                    subjectSelect = subject;
                    break;
                }
            }
            sendMessage.setText("Do you really want to start test !");
            sendMessage.setReplyMarkup(userButtons.getreplyKeyboard(currentUser));
            currentUser = helperMethod.setSubject(currentUser, subjectSelect);
        }


        try {
            if (lastQuestion) {
                execute(deleteMessage);
                execute(sendMessage);
            } else if (isAnswered && !lastQuestion) {
                execute(new_message);
            } else {
                execute(sendMessage);
            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void downloadAnswerSheet(User currentUser, String data, SendMessage sendMessage, Long chatId) {
        TestHistory selectedTest = null;
        for (TestHistory testHistory : testHistoryList) {
            String id = "" + testHistory.getId();
            if (id.equalsIgnoreCase(data)) {
                selectedTest = testHistory;
                break;
            }
        }

        sendMessage.setText("Answer Sheet");
        SendDocument sendDocument = new SendDocument();
        sendDocument.setChatId(chatId.toString());
        File file = docGeneratorService.answerSheet(currentUser, selectedTest);
        InputFile inputFile = new InputFile(file);
        sendDocument.setDocument(inputFile);

        try {
            execute(sendMessage);
            execute(sendDocument);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }


    }


}
