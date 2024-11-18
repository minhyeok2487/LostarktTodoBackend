package lostark.todo.controller.apiV3.auth;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.auth.ResponseDto;
import lostark.todo.controller.dto.mailDto.MailCheckDto;
import lostark.todo.controller.dto.mailDto.MailRequestDto;
import lostark.todo.domain.member.Member;
import lostark.todo.service.EmailService;
import lostark.todo.domainV2.member.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/v3/mail")
@Api(tags = {"이메일 API"})
public class EmailController {

    private final EmailService emailService;
    private final MemberService memberService;

    @ApiOperation(value = "회원가입 이메일 인증번호 전송",
            response = Integer.class)
    @PostMapping("")
    public ResponseEntity<?> sendSignUpMail(@RequestBody @Valid MailRequestDto mailRequestDto) {
        if (memberService.existByUsername(mailRequestDto.getMail())) {
            throw new IllegalArgumentException("이미 가입된 회원입니다.");
        }
        int number = emailService.sendSignUpMail(mailRequestDto.getMail());
        ResponseDto responseDto = new ResponseDto(true, "인증번호 전송이 정상처리 되었습니다.");
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @ApiOperation(value = "비밀번호 변경 인증번호 전송")
    @PostMapping("/password")
    public ResponseEntity<?> sendResetPasswordMail(@RequestBody @Valid MailRequestDto mailRequestDto) {
        Member member = memberService.get(mailRequestDto.getMail());
        if (!member.getAuthProvider().equals("none")) {
            throw new IllegalStateException("SNS 가입자는 비밀번호 변경이 불가능 합니다.");
        }
        emailService.sendResetPasswordMail(mailRequestDto.getMail());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(value = "이메일 인증번호 인증",
            notes = "3분이내 인증번호가 일치해야 true 리턴",
            response = ResponseDto.class)
    @PostMapping("/auth")
    public ResponseEntity<?> authMail(@RequestBody MailCheckDto mailCheckDto) {
        boolean auth = emailService.checkMail(mailCheckDto);
        if (auth) {
            return new ResponseEntity<>(new ResponseDto(true, "이메일 인증번호 성공"), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ResponseDto(false, "인증번호가 일치하지 않거나 만료되었습니다."), HttpStatus.OK);
        }
    }
}
