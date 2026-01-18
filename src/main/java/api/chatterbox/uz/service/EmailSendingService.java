package api.chatterbox.uz.service;

import api.chatterbox.uz.enums.AppLanguage;
import api.chatterbox.uz.enums.SmsType;
import api.chatterbox.uz.exps.AppBadException;
import api.chatterbox.uz.util.JwtUtil;
import api.chatterbox.uz.util.RandomUtil;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class EmailSendingService {
    private Integer emailLimit = (Integer) 3;
    @Value("${spring.mail.username}")
    private String fromAccount;

    @Value("${server.domain}")
    private String serverDomain;
    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private EmailHistoryService emailHistoryService;

    public void sendRegistrationEmail(String email, Integer profileId, AppLanguage lang) {
        String subject = "Complete Registration";
        String body = "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>Title</title>\n" +
                "    <style>\n" +
                "        a {\n" +
                "            padding: 10px 30px;\n" +
                "            display: inline-block;\n" +
                "        }\n" +
                "        .button-link  {\n" +
                "            text-decoration: none;\n" +
                "            color: white;\n" +
                "            background-color: indianred;\n" +
                "        }\n" +
                "        .button-link:hover {\n" +
                "            background-color: #dd4444;\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "\n" +
                "<h1>Complete Registration</h1>\n" +
                "\n" +
                "<p>\n" +
                "    Greetings, hope you are doing well!\n" +
                "</p>\n" +
                "<p>\n" +
                "    Please click the button below for completing the registration:\n" +
                "    <a class=\"button-link\" href=\"%s/api/v1/auth/registration/email-verification/%s?lang=%s\" target=\"_blank\">Click here</a>\n" +
                "</p>\n" +
                "</body>\n" +
                "</html>";
        body = String.format(body, serverDomain, JwtUtil.encode(profileId), lang.name());
        System.out.println(JwtUtil.encode(profileId));
        sendMimeEmail(email, subject, body);
    }

    public void sendResetPasswordEmail(String email, AppLanguage lang) {
        String subject = "Reset Password Confirmation";
        String code = RandomUtil.getRandomSmsCode();
        String body = "How are you mazgi?! Here is your confirmation code to reset password. Your code: " + code;
        checkAndSendMimeEmail(email, subject, body, code);
    }

    public void sendChangeUsernameEmail(String email, AppLanguage lang) {
        String subject = "Username change confirmation";
        String code = RandomUtil.getRandomSmsCode();
        String body = "How are you mazgi?! Here is your confirmation code to change username. Your code: " + code;
        checkAndSendMimeEmail(email, subject, body, code);
    }

    private void checkAndSendMimeEmail(String email, String subject, String body, String code) {
        // check
        Long count = emailHistoryService.getEmailCount(email);
        if (count >= emailLimit) {
            System.out.println(" --- Email limit reached. Email: " + email);
            throw new AppBadException("Email limit reached.");
        }
        // send
        sendMimeEmail(email, subject, body);

        // create
        emailHistoryService.create(email, code, SmsType.RESET_PASSWORD);
    }

    private void sendMimeEmail(String email, String subject, String body) {
        try {
            MimeMessage msg = javaMailSender.createMimeMessage();
            msg.setFrom(fromAccount);

            MimeMessageHelper helper = new MimeMessageHelper(msg, true);
            helper.setTo(email);
            helper.setSubject(subject);
            helper.setText(body, true);
            CompletableFuture.runAsync(() -> {
                javaMailSender.send(msg);
            });
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendSimpleEmail(String email, String subject, String body) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(fromAccount);
        msg.setTo(email);
        msg.setSubject(subject);
        msg.setText(body);
        javaMailSender.send(msg);
    }
}
