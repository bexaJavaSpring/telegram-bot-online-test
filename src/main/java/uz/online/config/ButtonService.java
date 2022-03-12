package uz.online.config;


import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import uz.online.model.User;

public interface ButtonService {

    ReplyKeyboard getreplyKeyboard(User user);
}
