package lostark.todo.controller.apiV3;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.domain.redis.User;
import lostark.todo.domain.redis.UserRepository;
import lostark.todo.service.NotificationService;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@Slf4j
@RequiredArgsConstructor
public class NotificationController {

    private final UserRepository userRepository;

    /*로그인 시 SSE 연결*/
    @GetMapping(value = "/v3/notification", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<Integer>> getUserCount(@RequestParam(value = "username", required = false) String username) {
        Flux<ServerSentEvent<Integer>> flux = Flux.interval(Duration.ofSeconds(1)).map(sequence -> {
            List<User> users = new ArrayList<>((Collection) userRepository.findAll());
            LocalDateTime now = LocalDateTime.now();
            if (username != null) {
                boolean exist = false;
                for (User user : users) {
                    if (user.getUsername().equals(username)) {
                        user.setDate(now);
                        userRepository.save(user);
                        exist = true;
                        break;
                    }
                }
                if (!exist) {
                    userRepository.save(new User(username));
                }
            }

            Iterator<User> iterator = users.iterator();
            while (iterator.hasNext()) {
                User user = iterator.next();
                if (Duration.between(user.getDate(), now).toMinutes() >= 3) {
                    iterator.remove();
                    userRepository.delete(user);
                }
            }

            return ServerSentEvent.<Integer>builder().data(users.size()).build();
        });


        return flux;
    }
}

