package uz.online.sms;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import uz.online.config.BotConfig;
import uz.online.config.SmsService;


public class SendSms implements BotConfig, SmsService {

    //SEND SMS
    @Override
    public int twillioApi() {

        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

        int code = codeGenerate();

        Message message = Message.creator(new PhoneNumber("+998936207516"), new PhoneNumber("+16672135275"), "Confirmation code " + code).create();

        System.out.println(message.getSid());

        return code;
    }

    @Override
    public int codeGenerate() {
        int min = 1000;
        int max = 9999;
        int code = (int) (Math.random() * (max - min + 1) + min);

        return code;
    }
}
