package lostark.todo.domain.member.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.domain.member.dto.MailCheckRequest;
import lostark.todo.domain.member.dto.SendMailRequest;
import lostark.todo.domain.member.service.EmailService;
import lostark.todo.global.dto.GlobalResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/mail")
@Api(tags = {"이메일 API"})
public class EmailApi {

    private final EmailService emailService;

    @ApiOperation(value = "회원가입 이메일 인증번호 전송",
            response = GlobalResponseDto.class)
    @PostMapping("")
    public ResponseEntity<?> sendSignUpMail(@RequestBody @Valid SendMailRequest sendMailRequest) {
        return new ResponseEntity<>(emailService.sendSignUpMail(sendMailRequest.getMail()), HttpStatus.OK);
    }

    @ApiOperation(value = "비밀번호 변경 인증번호 전송")
    @PostMapping("/password")
    public ResponseEntity<?> sendResetPasswordMail(@RequestBody @Valid SendMailRequest sendMailRequest) {
        emailService.sendResetPasswordMail(sendMailRequest.getMail());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(value = "이메일 인증번호 인증",
            notes = "3분이내 인증번호가 일치해야 true 리턴",
            response = GlobalResponseDto.class)
    @PostMapping("/auth")
    public ResponseEntity<?> authMail(@RequestBody MailCheckRequest mailCheckRequest) {
        return new ResponseEntity<>(emailService.checkMail(mailCheckRequest), HttpStatus.OK);
    }
}
