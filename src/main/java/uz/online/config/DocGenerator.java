package uz.online.config;



import uz.online.model.TestHistory;
import uz.online.model.User;

import java.io.File;

public interface DocGenerator {
    File pdfGenerator(User user);

    File answerSheet(User currentUser,TestHistory selectedTest);
}
