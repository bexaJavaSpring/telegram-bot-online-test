package uz.online.service.crud;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import uz.online.model.Answer;
import uz.online.model.Subject;
import uz.online.model.Test;
import uz.online.model.User;

import java.util.ArrayList;
import java.util.List;

import static uz.online.DB.subjectList;


public class TestCrud {
    public ReplyKeyboard getSubjectButton(User user) {
        //INLINE
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> inlineButtons = new ArrayList<>();
        List<InlineKeyboardButton> buttonList = new ArrayList<>();
        inlineButtons.add(buttonList);
        inlineKeyboardMarkup.setKeyboard(inlineButtons);


        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> rowList = new ArrayList<>();

        keyboardMarkup.setKeyboard(rowList);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(true);

        switch (user.getState()) {

            case TEST_MENU:
                KeyboardRow row4 = new KeyboardRow();
                KeyboardRow row5 = new KeyboardRow();
                KeyboardRow row6 = new KeyboardRow();
                row4.add("TEST CREATE");
                row5.add("TEST DELETE");
                row6.add("BACK");
                rowList.add(row4);
                rowList.add(row5);
                rowList.add(row6);
                break;
            case TEST_CREATE:
            case TEST_DELETE:
                List<InlineKeyboardButton> inlineRow = new ArrayList<>();
                List<InlineKeyboardButton> inlinePrev = new ArrayList<>();
                for (int i = 0; i < subjectList.size(); i++) {
                    Subject value = subjectList.get(i);

                    InlineKeyboardButton buttonN = new InlineKeyboardButton();
                    buttonN.setText(value.getName());
                    buttonN.setCallbackData(value.getName());
                    inlineRow.add(buttonN);

                    if (i % 2 == 0) {
                        inlineButtons.add(inlineRow);
                    } else {
                        inlineRow = new ArrayList<>();
                    }
                }
                InlineKeyboardButton buttonH = new InlineKeyboardButton();
                buttonH.setText("Back");
                buttonH.setCallbackData("Back");
                inlinePrev.add(buttonH);

                inlineButtons.add(inlinePrev);
                return inlineKeyboardMarkup;
            case ANSWER_LIST:
                List<InlineKeyboardButton> inlineRow2 = new ArrayList<>();
                String[] option = {"A", "B", "C", "D"};

                for (int K = 0; K < user.getTest().getAnswers().size(); K++) {
                    Answer answer = user.getTest().getAnswers().get(K);
                    InlineKeyboardButton buttonN = new InlineKeyboardButton();
                    buttonN.setText(option[K]);
                    buttonN.setCallbackData("" + answer.getId());
                    inlineRow2.add(buttonN);

                    if (K % 2 == 0) {
                        inlineButtons.add(inlineRow2);
                    } else {
                        inlineRow2 = new ArrayList<>();
                    }
                }
                return inlineKeyboardMarkup;
            case TEST_LIST:
                List<InlineKeyboardButton> inlineRow3 = new ArrayList<>();


                int i=1;
                for (int K = 0; K < user.getSubject().getTests().size(); K++) {
                    Test test = user.getSubject().getTests().get(K);
                    InlineKeyboardButton buttonN = new InlineKeyboardButton();
                    buttonN.setText("" + i);
                    buttonN.setCallbackData("" + test.getId());
                    inlineRow3.add(buttonN);

                    if (K % 2 == 0) {
                        inlineButtons.add(inlineRow3);
                    } else {
                        inlineRow3 = new ArrayList<>();
                    }
                    i++;
                }
                return inlineKeyboardMarkup;
        }
        return keyboardMarkup;
    }
}
