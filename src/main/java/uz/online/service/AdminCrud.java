package uz.online.service;



import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.online.helper.DocGeneratorService;
import uz.online.helper.HelperMethod;
import uz.online.model.BotState;
import uz.online.model.User;
import uz.online.service.button.AdminButtons;
import uz.online.service.crud.SubjectCrud;
import uz.online.service.crud.TestCrud;


public class AdminCrud {

    static HelperMethod helperMethod = new HelperMethod();
    static AdminCrud adminCrud = new AdminCrud();
    static AdminButtons adminButtons = new AdminButtons();
    static DocGeneratorService docGeneratorService = new DocGeneratorService();
    static SubjectCrud subjectCrud = new SubjectCrud();
    static TestCrud testCrud = new TestCrud();

    public SendMessage adminCrudMenu(User currentUser, Update update, Message message) {

        SendMessage sendMessage = new SendMessage();
        switch (currentUser.getState()) {
            case CRUD_MENU: {
                if (message.getText().equalsIgnoreCase("MENU")) {
                    currentUser = helperMethod.changeRound(currentUser, 3, 1);
                    sendMessage.setText("Menu");
                    sendMessage.setReplyMarkup(adminButtons.getreplyKeyboard(currentUser));
                    currentUser = helperMethod.changeState(currentUser, BotState.DEFAULT);
                    currentUser = helperMethod.changeRound(currentUser, 1, 2);
                } else if (message.getText().equalsIgnoreCase("\uD83D\uDEE0 SUBJECT CRUD")) {
                    sendMessage.setText("SUBJECT MENU");
                    currentUser = helperMethod.changeState(currentUser, BotState.SUBJECT_MENU);
                    sendMessage.setReplyMarkup(subjectCrud.getSubjectButton(currentUser));
                } else if (message.getText().equalsIgnoreCase("\uD83D\uDEE0 TEST, ANSWER CRUD")) {
                    currentUser = helperMethod.changeState(currentUser, BotState.TEST_MENU);
                    sendMessage.setText("Test Menu");
                    sendMessage.setReplyMarkup(testCrud.getSubjectButton(currentUser));
                }
            }
            break;
        }
        return sendMessage;

    }

}
