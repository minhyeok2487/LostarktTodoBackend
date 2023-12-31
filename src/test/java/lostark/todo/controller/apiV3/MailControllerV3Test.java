package lostark.todo.controller.apiV3;

import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.mailDto.MailCheckDto;
import lostark.todo.controller.dto.mailDto.MailRequestDto;
import lostark.todo.domain.member.Member;
import lostark.todo.domain.redis.Mail;
import lostark.todo.domain.redis.RedisRepository;
import lostark.todo.exhandler.exceptions.CustomIllegalArgumentException;
import lostark.todo.service.MailService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
@Slf4j
public class MailControllerV3Test {

    @Autowired
    MailService mailService;

    @Autowired
    RedisRepository repository;

    /*메일 전송 메소드*/
    public int sendMail(MailRequestDto mailRequestDto) {
        return mailService.sendMail(mailRequestDto.getMail());
    }

    /*메일 인증번호 체크 메소드*/
    public String checkMail(MailCheckDto mailCheckDto) {
        Member tempMember = Member.builder().username(mailCheckDto.getMail()).build();
        List<Mail> mailList = repository.findAllByMail(mailCheckDto.getMail());
        boolean check = false;
        if (mailList.isEmpty()) {
            throw new CustomIllegalArgumentException("메일 인증번호 체크 에러",
                    "진행된 메일 인증번호 체크가 없습니다.", tempMember);
        } else {
            for (Mail mail : mailList) {
                if (mail.getNumber() == mailCheckDto.getNumber()) {
                    LocalDateTime now = LocalDateTime.now();
                    LocalDateTime regDate = mail.getRegDate();
                    Duration duration = Duration.between(regDate, now);
                    if (duration.toMinutes() < 3) {
                        log.info("이메일 인증번호 체크 성공");
                        check = true;
                    } else {
                        throw new CustomIllegalArgumentException("메일 인증번호 체크 에러",
                                "만료된 인증번호 입니다.", tempMember);
                    }
                }
            }
        }

        if (check) {
            return "체크 성공";
        } else {
            throw new CustomIllegalArgumentException("메일 인증번호 체크 에러",
                    "인증번호가 일치하지 않습니다.", tempMember);
        }
    }

//    @Test
//    @DisplayName("메일 인증번호 체크 테스트")
//    void checkMailTest() {
//        String mail = "repeater2487@naver.com";
//        MailRequestDto mailRequestDto = new MailRequestDto(mail);
//
//        int number = sendMail(mailRequestDto);
//
//        MailCheckDto mailCheckDto = new MailCheckDto(mail, number);
//        checkMail(mailCheckDto);
//    }
//
//    @Test
//    @DisplayName("메일 인증번호 체크 실패 - 시간 만료")
//    void checkMailTestExpired() {
//        String mail = "repeater2487@naver.com";
//        MailRequestDto mailRequestDto = new MailRequestDto(mail);
//
//        int number = sendMail(mailRequestDto);
//
//        try {
//            Thread.sleep(190000);
//            MailCheckDto mailCheckDto = new MailCheckDto(mail, number);
//            checkMail(mailCheckDto);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//    }
}
