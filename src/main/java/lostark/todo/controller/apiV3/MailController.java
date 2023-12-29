package lostark.todo.controller.apiV3;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.mailDto.MailCheckDto;
import lostark.todo.controller.dto.mailDto.MailRequestDto;
import lostark.todo.controller.dto.noticesDto.NoticesDto;
import lostark.todo.domain.member.Member;
import lostark.todo.domain.redis.Mail;
import lostark.todo.domain.redis.RedisRepository;
import lostark.todo.exhandler.exceptions.CustomIllegalArgumentException;
import lostark.todo.service.MailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/v3/mail")
@Api(tags = {"이메일 인증"})
public class MailController {

    private final MailService mailService;
    private final RedisRepository repository;

    @PostMapping("")
    public ResponseEntity<?> sendMail(@RequestBody MailRequestDto mailRequestDto){
        return new ResponseEntity<>(mailService.sendMail(mailRequestDto.getMail()), HttpStatus.OK);
    }

    @PutMapping("")
    public ResponseEntity<?> checkMail(@RequestBody MailCheckDto mailCheckDto) {
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
            return new ResponseEntity<>(true, HttpStatus.OK);
        } else {
            throw new CustomIllegalArgumentException("메일 인증번호 체크 에러",
                    "인증번호가 일치하지 않습니다.", tempMember);
        }
    }
}
