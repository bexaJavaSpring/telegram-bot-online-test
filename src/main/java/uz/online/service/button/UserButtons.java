package uz.online.service.button;



import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import uz.online.config.ButtonService;
import uz.online.model.*;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static uz.online.DB.subjectList;
import static uz.online.DB.testHistoryList;
import static uz.online.config.BotConfig.step;

public class UserButtons implements ButtonService {

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
                row1.add("\uD83D\uDCCA Results");
                row1.add("\uD83D\uDDD2 Take a Test");
                rowList.add(row1);
            }
            break;
            case 2: {
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
                buttonH.setText("⏏️Menu");
                buttonH.setCallbackData("Menu");
                inlinePrev.add(buttonH);

                inlineButtons.add(inlinePrev);
                return inlineKeyboardMarkup;
            }
            case 3: {
                KeyboardRow row4 = new KeyboardRow();
                row4.add("✅ Confirm");
                row4.add("✖️Cancel");
                rowList.add(row4);
            }
            break;
            case 4: {
                List<InlineKeyboardButton> inlineRow = new ArrayList<>();
                for (int i = 0; i < step; i++) {
                    String[] option = {"A", "B", "C", "D"};
                    List<Test> tests = user.getSubject().getTests();
                    Test test1 = tests.get(user.getTestStep());

                    for (int K = 0; K < test1.getAnswers().size(); K++) {
                        Answer answer = test1.getAnswers().get(K);
                        InlineKeyboardButton buttonN = new InlineKeyboardButton();
                        buttonN.setText(option[K]);
                        buttonN.setCallbackData("" + answer.getId());
                        inlineRow.add(buttonN);

                        if (K % 2 == 0) {
                            inlineButtons.add(inlineRow);
                        } else {
                            inlineRow = new ArrayList<>();
                        }
                    }
                }
                return inlineKeyboardMarkup;
            }
            case 5: {
                KeyboardRow row4 = new KeyboardRow();
                row4.add("Menu");
                rowList.add(row4);
            }
            break;
            case 6: {
                KeyboardRow row4 = new KeyboardRow();
                KeyboardRow row5 = new KeyboardRow();
                row4.add("Download All Time Stats");
                row4.add("Download Answer Sheet");
                row5.add("Menu");
                rowList.add(row4);
                rowList.add(row5);
            }
            break;
            case 7: {
                List<TestHistory> collect = testHistoryList.stream().filter(testHistory -> testHistory.getUser().getId().longValue() == user.getId()).collect(Collectors.toList());
                List<InlineKeyboardButton> inlineRow = new ArrayList<>();
                List<InlineKeyboardButton> inlinePrev = new ArrayList<>();
                for (int i = 0; i < collect.size(); i++) {
                    TestHistory value = collect.get(i);

                    InlineKeyboardButton buttonN = new InlineKeyboardButton();
                    buttonN.setText("" + value.getResult() + " % Date " + value.getCreatedAt().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
                    buttonN.setCallbackData(value.getId().toString());
                    inlineRow.add(buttonN);

                    if (i % 2 == 0) {
                        inlineButtons.add(inlineRow);
                    } else {
                        inlineRow = new ArrayList<>();
                    }
                }
                InlineKeyboardButton buttonH = new InlineKeyboardButton();
                buttonH.setText("⏏️Menu");
                buttonH.setCallbackData("Menu");
                inlinePrev.add(buttonH);

                inlineButtons.add(inlinePrev);
                return inlineKeyboardMarkup;
            }
            case 10: {
                KeyboardRow row1 = new KeyboardRow();
                row1.add("Resend Code");
                rowList.add(row1);
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
