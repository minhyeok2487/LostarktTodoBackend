package lostark.todo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {
    private static final List<String> users = new ArrayList<>();

    public List<String> addUser(String username) {
        if (!users.contains(username)) {
            users.add(username);
        }
        return users;
    }

}
