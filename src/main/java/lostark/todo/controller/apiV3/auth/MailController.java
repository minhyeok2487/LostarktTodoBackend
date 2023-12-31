package lostark.todo.controller.apiV3.auth;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.auth.AuthResponseDto;
import lostark.todo.controller.dto.mailDto.MailCheckDto;
import lostark.todo.controller.dto.mailDto.MailRequestDto;
import lostark.todo.domain.redis.Mail;
import lostark.todo.domain.redis.RedisRepository;
import lostark.todo.service.MailService;
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

    @ApiOperation(value = "이메일 인증번호 전송",
            response = Integer.class)
    @PostMapping("")
    public ResponseEntity<?> sendMail(@RequestBody MailRequestDto mailRequestDto) {
        int number = mailService.sendMail(mailRequestDto.getMail());
        log.info("인증번호 전송이 정상처리 되었습니다. email={}, number={}", mailRequestDto.getMail(), number);
        return new ResponseEntity<>("인증번호 전송이 정상처리 되었습니다.", HttpStatus.OK);
    }

    @ApiOperation(value = "이메일 인증번호 체크",
            notes = "3분이내 인증번호가 일치해야 true 리턴",
            response = AuthResponseDto.class)
    @PostMapping("/auth")
    public ResponseEntity<?> authMail(@RequestBody MailCheckDto mailCheckDto) {
        boolean auth = false;
        List<Mail> allByMail = repository.findAllByMail(mailCheckDto.getMail());
        for (Mail mail : allByMail) {
            if (mail.getNumber() == mailCheckDto.getNumber() && Duration.between(mail.getRegDate(), LocalDateTime.now()).toMinutes() <= 3) {
                auth = true;
                mail.setCheck(true); //안됨
            }
        }
        if (auth) {
            log.info("이메일 인증번호 성공, email={}", mailCheckDto.getMail());
            return new ResponseEntity<>(new AuthResponseDto(true, "이메일 인증번호 성공"), HttpStatus.OK);
        } else {
            log.warn("인증번호가 일치하지 않거나 만료되었습니다, email={}", mailCheckDto.getMail());
            return new ResponseEntity<>(new AuthResponseDto(false, "인증번호가 일치하지 않거나 만료되었습니다."), HttpStatus.BAD_REQUEST);
        }
    }
}
