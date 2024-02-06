package lostark.todo.controller.apiV3.auth;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.auth.ResponseDto;
import lostark.todo.controller.dto.mailDto.MailCheckDto;
import lostark.todo.controller.dto.mailDto.MailRequestDto;
import lostark.todo.service.MailService;
import lostark.todo.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/v3/mail")
@Api(tags = {"이메일 인증"})
public class MailController {

    private final MailService mailService;
    private final MemberService memberService;

    @ApiOperation(value = "이메일 인증번호 전송",
            response = Integer.class)
    @PostMapping("")
    public ResponseEntity<?> sendMail(@RequestBody @Valid MailRequestDto mailRequestDto) {
        if (memberService.existByUsername(mailRequestDto.getMail())) {
            throw new IllegalArgumentException("이미 가입된 회원입니다.");
        }
        int number = mailService.sendMail(mailRequestDto.getMail());
        log.info("인증번호 전송이 정상처리 되었습니다. email={}, number={}", mailRequestDto.getMail(), number);
        ResponseDto responseDto = new ResponseDto(true, "인증번호 전송이 정상처리 되었습니다.");
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @ApiOperation(value = "이메일 인증번호 인증",
            notes = "3분이내 인증번호가 일치해야 true 리턴",
            response = ResponseDto.class)
    @PostMapping("/auth")
    public ResponseEntity<?> authMail(@RequestBody MailCheckDto mailCheckDto) {
        boolean auth = mailService.checkMail(mailCheckDto);
        if (auth) {
            log.info("이메일 인증번호 성공, email={}", mailCheckDto.getMail());
            return new ResponseEntity<>(new ResponseDto(true, "이메일 인증번호 성공"), HttpStatus.OK);
        } else {
            log.warn("인증번호가 일치하지 않거나 만료되었습니다, email={}", mailCheckDto.getMail());
            return new ResponseEntity<>(new ResponseDto(false, "인증번호가 일치하지 않거나 만료되었습니다."), HttpStatus.OK);
        }
    }
}
