package lostark.todo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.auth.AuthSignupDto;
import lostark.todo.controller.dto.mailDto.MailCheckDto;
import lostark.todo.domain.authEmail.AuthMail;
import lostark.todo.domain.authEmail.AuthMailRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.transaction.Transactional;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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

    public int sendMail(String email){
        MimeMessage message = createMail(email);
        javaMailSender.send(message);
        emailRepository.save(new AuthMail(email, number));
        return number;
    }

    public boolean isAuth(AuthSignupDto authSignupDto) {
        return emailRepository.findAllByMail(authSignupDto.getMail()).stream()
                .anyMatch(mail -> mail.getNumber() == authSignupDto.getNumber() && mail.isAuth());
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
}
