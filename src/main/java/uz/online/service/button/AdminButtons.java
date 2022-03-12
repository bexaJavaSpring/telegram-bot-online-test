package uz.online.service.button;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import uz.online.config.ButtonService;
import uz.online.model.User;

import java.util.ArrayList;
import java.util.List;




public class AdminButtons implements ButtonService {

    @Override
    public ReplyKeyboard getreplyKeyboard(User user) {

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

        switch (user.getCurrentRound()) {
            case 0: {
                KeyboardRow row1 = new KeyboardRow();
                KeyboardButton shareButton = new KeyboardButton();
                shareButton.setText("Share contact");
                shareButton.setRequestContact(true);
                row1.add(shareButton);
                rowList.add(row1);
            }
            break;
            case 1: {
                KeyboardRow row1 = new KeyboardRow();
                row1.add("\uD83D\uDCCA Download Results");
                row1.add("\uD83D\uDDD2 Download UserList");
                row1.add("\uD83D\uDEE0 CRUD MENU");
                rowList.add(row1);
            }
            break;
            case 3: {
                KeyboardRow row1 = new KeyboardRow();
                KeyboardRow row2 = new KeyboardRow();
                row1.add("\uD83D\uDEE0 SUBJECT CRUD");
                row1.add("\uD83D\uDEE0 TEST, ANSWER CRUD");
                row2.add("MENU");
                rowList.add(row1);
                rowList.add(row2);
            }
            break;
            case 4: {
                KeyboardRow row1 = new KeyboardRow();
                KeyboardRow row2 = new KeyboardRow();
                KeyboardRow row3 = new KeyboardRow();
                row1.add("\uD83D\uDEE0 SUBJECT CREATE");
                row1.add("\uD83D\uDEE0 SUBJECT UPDATE");
                row2.add("\uD83D\uDEE0 SUBJECT DELETE");
                row2.add("\uD83D\uDEE0 SUBJECT LIST");
                row3.add("MENU");
                rowList.add(row1);
                rowList.add(row2);
                rowList.add(row3);
            }
            break;
            default:
                KeyboardRow row4 = new KeyboardRow();
                row4.add("◀️ Back");
                row4.add("⏏️ Menu");
                rowList.add(row4);
                break;
        }

        return keyboardMarkup;
    }
}
