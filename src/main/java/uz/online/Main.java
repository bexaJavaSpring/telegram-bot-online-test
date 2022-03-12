package uz.online;



import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import uz.online.database.JsonConfig;
import uz.online.model.BotState;
import uz.online.model.User;
import uz.online.service.App;

import static uz.online.DB.userFile;
import static uz.online.DB.userList;

public class Main {

    static DB db = new DB();

    public static void main(String[] args) {
        db.migration();
        try {
            TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
            api.registerBot(new App());
            System.out.println("Bot started");
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }

}
