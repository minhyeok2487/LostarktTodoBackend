package lostark.todo.domain.logs.service;

import lombok.RequiredArgsConstructor;
import lostark.todo.domain.logs.dto.GetLogsProfitRequest;
import lostark.todo.domain.logs.dto.LogProfitResponse;
import lostark.todo.domain.logs.dto.LogsSearchParams;
import lostark.todo.domain.logs.dto.LogsSearchResponse;
import lostark.todo.domain.logs.entity.Logs;
import lostark.todo.domain.logs.repository.LogsRepository;
import lostark.todo.domain.member.entity.Member;
import lostark.todo.domain.member.repository.MemberRepository;
import lostark.todo.global.dto.CursorResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LogService {
    private final LogsRepository repository;
    private final MemberRepository memberRepository;

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

    @Transactional(readOnly = true)
    public CursorResponse<LogsSearchResponse> search(String username, LogsSearchParams params) {
        PageRequest pageRequest = PageRequest.of(0, 100);
        Member member = memberRepository.get(username);
        return repository.search(member.getId(), params, pageRequest);
    }

    @Transactional
    public List<LogProfitResponse> getProfit(String username, GetLogsProfitRequest request) {
        Member member = memberRepository.get(username);
        return repository.getProfit(member.getId(), request);
    }
}