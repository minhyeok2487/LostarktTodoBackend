package lostark.todo.service;

import lombok.RequiredArgsConstructor;
import lostark.todo.domain.character.Character;
import lostark.todo.domain.member.Member;
import lostark.todo.domain.member.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;

    public Member signup(Member member) {
        return memberRepository.save(member);
    }


    public Member findUser(Long userId) throws Exception {
        return memberRepository.findById(userId).orElseThrow(() -> new Exception("존재하지않습니다"));
    }

    public Member findUser(String username) throws Exception {
        return memberRepository.findByUsername(username).orElseThrow(() -> new Exception("존재하지않습니다"));
    }
}
