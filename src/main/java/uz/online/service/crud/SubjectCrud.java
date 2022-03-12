package uz.online.service.crud;


import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import uz.online.model.Subject;
import uz.online.model.User;

import java.util.ArrayList;
import java.util.List;

import static uz.online.DB.subjectList;

public class SubjectCrud {


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
            case SUBJECT_MENU: {
                KeyboardRow row4 = new KeyboardRow();
                KeyboardRow row5 = new KeyboardRow();
                KeyboardRow row6 = new KeyboardRow();
                row4.add("SUBJECT CREATE");
                row4.add("SUBJECT UPDATE");
                row5.add("SUBJECT DELETE");
                row5.add("SUBJECT LIST");
                row6.add("BACK");
                rowList.add(row4);
                rowList.add(row5);
                rowList.add(row6);
            }
            break;
            case SUBJECT_DELETE:
            case SUBJECT_UPDATE:
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
        }
        return keyboardMarkup;
    }


}
