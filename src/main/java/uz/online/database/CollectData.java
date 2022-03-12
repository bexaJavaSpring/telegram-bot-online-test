package uz.online.database;



import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import uz.online.config.CollectService;
import uz.online.model.TestHistory;
import uz.online.model.Subject;
import uz.online.model.User;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;

import static uz.online.DB.*;

public class CollectData implements CollectService {

    @Override
    public void collectSubject() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (Reader reader = new FileReader(subjectFile)) {
            List<Subject> list = gson.fromJson(reader, new TypeToken<List<Subject>>() {
            }.getType());
            subjectList.addAll(list);

        } catch (IOException e) {
        }

    }

    @Override
    public void collectUser() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (Reader reader = new FileReader(userFile)) {
            List<User> list = gson.fromJson(reader, new TypeToken<List<User>>() {
            }.getType());
            userList.addAll(list);

        } catch (IOException e) {
        }
    }

    @Override
    public void collectHistory() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (Reader reader = new FileReader(historyFile)) {
            List<TestHistory> list = gson.fromJson(reader, new TypeToken<List<TestHistory>>() {
            }.getType());
            testHistoryList.addAll(list);

        } catch (IOException e) {
        }
    }
}
