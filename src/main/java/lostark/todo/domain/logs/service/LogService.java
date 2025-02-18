package lostark.todo.domain.logs.service;

import lombok.RequiredArgsConstructor;
import lostark.todo.domain.logs.dto.GetLogsProfitRequest;
import lostark.todo.domain.logs.dto.LogProfitResponse;
import lostark.todo.domain.logs.entity.Logs;
import lostark.todo.domain.logs.repository.LogsRepository;
import lostark.todo.domain.member.entity.Member;
import lostark.todo.domain.member.repository.MemberRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
    public List<Logs> search(int size, String username) {
        Member member = memberRepository.get(username);
        Page<Logs> allLogs = repository.findAllByMemberId(PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "createdDate")), member.getId());
        return allLogs.getContent();
    }

    @Transactional
    public List<LogProfitResponse> getProfit(String username, GetLogsProfitRequest request) {
        Member member = memberRepository.get(username);
        return repository.getProfit(member.getId(), request);
    }
}