package lostark.todo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.characterDto.CharacterDto;
import lostark.todo.controller.dto.friendsDto.FriendsReturnDto;
import lostark.todo.domain.friends.FriendSettings;
import lostark.todo.domain.friends.Friends;
import lostark.todo.domain.friends.FriendsRepository;
import lostark.todo.domain.logs.Logs;
import lostark.todo.domain.logs.LogsRepository;
import lostark.todo.domain.member.Member;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class LogsService {

    private final LogsRepository logsRepository;

    public void save(Logs logs) {
        logsRepository.save(logs);
    }

}
