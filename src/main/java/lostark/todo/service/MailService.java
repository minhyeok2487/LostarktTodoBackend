package lostark.todo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.domain.redis.Mail;
import lostark.todo.domain.redis.RedisRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailService {

    private final JavaMailSender javaMailSender;

    private final RedisRepository repository;
    private static int number;

    @Value("${spring.mail.username}")
    private String senderEmail;

    public static void createNumber() {
        number = (int)(Math.random() * (90000)) + 100000;
    }

    public MimeMessage createMail(String mail){
        createNumber();
        MimeMessage message = javaMailSender.createMimeMessage();

        try {
            message.setFrom(senderEmail);
            message.setRecipients(MimeMessage.RecipientType.TO, mail);
            message.setSubject("이메일 인증");
            String body = "";
            body += "<h1>LoaTodo에 가입해주셔서 감사합니다.</h3>";
            body += "<h3>" + "인증 번호입니다." + "</h3>";
            body += "<p>" + "3분내로 입력해주세요." + "</p>";
            body += "<h4> 인증번호 : " + number + "</h4>";
            message.setText(body,"UTF-8", "html");
        } catch (MessagingException e) {
            log.error(e.toString());
        }
        return message;
    }

    public int sendMail(String mail){
        MimeMessage message = createMail(mail);
        javaMailSender.send(message);
        saveRedis(mail, number);
        return number;
    }

    public void saveRedis(String mail, int number) {
        Mail email = new Mail(mail, number);
        repository.save(email);
    }
}
