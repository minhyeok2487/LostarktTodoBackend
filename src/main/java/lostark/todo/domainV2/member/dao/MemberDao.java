package lostark.todo.domainV2.member.dao;

import lombok.RequiredArgsConstructor;
import lostark.todo.domain.member.Member;
import lostark.todo.domain.member.MemberRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

import static lostark.todo.constants.ErrorMessages.MEMER_NOT_FOUND;

@RequiredArgsConstructor
@Repository
public class MemberDao {

    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public Member get(String username) {
        return memberRepository.get(username).orElseThrow(() -> new NoSuchElementException(MEMER_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public Member getAll(String username) {
        return memberRepository.getAll(username).orElseThrow(() -> new NoSuchElementException(MEMER_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public Member get(Long id) {
        return memberRepository.get(id).orElseThrow(() -> new NoSuchElementException(MEMER_NOT_FOUND));
    }
}
