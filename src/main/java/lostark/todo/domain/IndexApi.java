package lostark.todo.domain;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @GetMapping("/")
    public ResponseEntity<?> index() {
        return new ResponseEntity<>("ok", HttpStatus.OK);
    }
}
