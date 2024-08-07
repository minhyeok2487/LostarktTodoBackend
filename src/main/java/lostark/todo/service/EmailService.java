package lostark.todo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.mailDto.MailCheckDto;
import lostark.todo.domain.authEmail.AuthMail;
import lostark.todo.domain.authEmail.AuthMailRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.transaction.Transactional;
import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final AuthMailRepository emailRepository;
    private static int number;

    @Value("${spring.mail.username}")
    private String senderEmail;

    public static void createNumber() {
        number = (int)(Math.random() * (90000)) + 100000;
    }

    public MimeMessage createSignUpMail(String email){
        createNumber();
        MimeMessage message = javaMailSender.createMimeMessage();

        try {
            message.setFrom(senderEmail);
            message.setRecipients(MimeMessage.RecipientType.TO, email);
            message.setSubject("[LOA TODO] 이메일 인증");
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

    public int sendSignUpMail(String email){
        MimeMessage message = createSignUpMail(email);
        javaMailSender.send(message);
        emailRepository.save(new AuthMail(email, number));
        return number;
    }

    public void isAuth(String email, Integer number) {
        boolean auth = emailRepository.findAllByMail(email).stream()
                .anyMatch(mail -> mail.getNumber().equals(number) && mail.isAuth());
        if (!auth) {
            throw new IllegalStateException("이메일 인증이 실패하였습니다.");
        }
    }

    public void deleteAll(String mail) {
        emailRepository.deleteAllByMail(mail);
    }

    public boolean checkMail(MailCheckDto mailCheckDto) {
        AuthMail authMail = emailRepository.findByMailAndNumber(mailCheckDto.getMail(), mailCheckDto.getNumber())
                .orElseThrow(() -> new IllegalStateException("유효하지 않은 인증번호 입니다."));
        if (authMail.getNumber() == mailCheckDto.getNumber()
                && Duration.between(authMail.getCreatedDate(), LocalDateTime.now()).toMinutes() <= 3) {
            authMail.setAuth(true);
            return true;
        }
        return false;
    }

    public MimeMessage createResetPasswordMail(String email) {
        createNumber();
        MimeMessage message = javaMailSender.createMimeMessage();

        try {
            message.setFrom(senderEmail);
            message.setRecipients(MimeMessage.RecipientType.TO, email);
            message.setSubject("[LOA TODO] 이메일 인증");
            String body = "";
            body += "<h1>LoaTodo에 이용해주셔서 감사합니다.</h3>";
            body += "<h3>" + "인증 번호입니다." + "</h3>";
            body += "<p>" + "3분내로 입력해주세요." + "</p>";
            body += "<h4> 인증번호 : " + number + "</h4>";
            message.setText(body,"UTF-8", "html");
        } catch (MessagingException e) {
            log.error(e.toString());
        }
        return message;
    }


    public void sendResetPasswordMail(String email) {
        MimeMessage message = createResetPasswordMail(email);
        javaMailSender.send(message);
        emailRepository.save(new AuthMail(email, number));
    }
}
