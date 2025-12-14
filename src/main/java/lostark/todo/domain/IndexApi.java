package lostark.todo.domain;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.domain.member.service.MemberService;
import lostark.todo.global.config.TokenProvider;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/")
public class IndexApi {

    private final TokenProvider tokenProvider;

    @GetMapping("/")
    public ResponseEntity<?> index() {
        String token = tokenProvider.createToken("rldnjs4578@gmail.com");
        log.info("token = "+token);
        return new ResponseEntity<>("ok", HttpStatus.OK);
    }
}
