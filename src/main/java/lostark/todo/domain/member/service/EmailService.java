package lostark.todo.domain.member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.domain.member.dto.MailCheckRequest;
import lostark.todo.domain.member.entity.AuthMail;
import lostark.todo.domain.member.entity.Member;
import lostark.todo.domain.member.repository.AuthMailRepository;
import lostark.todo.domain.member.repository.MemberRepository;
import lostark.todo.global.dto.GlobalResponseDto;
import lostark.todo.global.exhandler.exceptions.ConditionNotMetException;
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
    private final MemberRepository memberRepository;
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

    public GlobalResponseDto sendSignUpMail(String email){
        if (memberRepository.existsByUsername(email)) {
            throw new ConditionNotMetException("이미 가입된 회원입니다.");
        }
        MimeMessage message = createSignUpMail(email);
        javaMailSender.send(message);
        emailRepository.save(new AuthMail(email, number));
        return new GlobalResponseDto(true, "인증번호 전송이 정상처리 되었습니다.");
    }

    public GlobalResponseDto checkMail(MailCheckRequest mailCheckRequest) {
        AuthMail authMail = emailRepository.findByMailAndNumber(mailCheckRequest.getMail(), mailCheckRequest.getNumber())
                .orElseThrow(() -> new ConditionNotMetException("유효하지 않은 인증번호 입니다."));
        if (authMail.getNumber() == mailCheckRequest.getNumber()
                && Duration.between(authMail.getCreatedDate(), LocalDateTime.now()).toMinutes() <= 3) {
            authMail.setAuth(true);
            return new GlobalResponseDto(true, "이메일 인증번호 성공");
        }
        return new GlobalResponseDto(false, "인증번호가 일치하지 않거나 만료되었습니다.");
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
        Member member = memberRepository.get(email);
        if (!member.getAuthProvider().equals("none")) {
            throw new ConditionNotMetException("SNS 가입자는 비밀번호 변경이 불가능 합니다.");
        }
        MimeMessage message = createResetPasswordMail(email);
        javaMailSender.send(message);
        emailRepository.save(new AuthMail(email, number));
    }
}
