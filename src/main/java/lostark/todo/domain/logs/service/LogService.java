package lostark.todo.domain.logs.service;

import lombok.RequiredArgsConstructor;
import lostark.todo.domain.logs.entity.Logs;
import lostark.todo.domain.logs.repository.LogsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LogService {
    private final LogsRepository repository;

    @Transactional
    public void saveLog(Logs logs) {
        repository.save(logs);
    }

    @Transactional
    public void deleteLog(Logs logs) {
        repository.deleteLogsByLogs(logs);
    }

    @Transactional
    public void deleteMoreRewardLogs(long memberId, long characterId, String weekCategory) {
        repository.deleteMoreRewardLogs(memberId, characterId, weekCategory);
    }
}