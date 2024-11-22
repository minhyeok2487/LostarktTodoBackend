package lostark.todo.domainV2.board.community.service;

import lombok.RequiredArgsConstructor;
import lostark.todo.domainV2.board.community.repository.FollowRepository;
import lostark.todo.domainV2.member.entity.Member;
import lostark.todo.domainV2.member.repository.MemberRepository;
import lostark.todo.domainV2.board.community.dto.FollowResponse;
import lostark.todo.domainV2.board.community.dto.FollowingUpdateRequest;
import lostark.todo.domainV2.board.community.entity.Follow;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class FollowService {

    private final FollowRepository followRepository;
    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public List<FollowResponse> search(String username) {
        List<FollowResponse> result = new ArrayList<>();
        for (Follow search : followRepository.search(username)) {
            result.add(new FollowResponse(search));
        }
        return result;
    }

    @Transactional
    public void update(String username, FollowingUpdateRequest request) {
        Member follower = memberRepository.get(username);
        Member following = memberRepository.get(request.getFollowing());
        followRepository.get(follower, following)
                .ifPresentOrElse(
                        followRepository::delete,  // 존재하면 삭제
                        () -> followRepository.save(Follow.builder()  // 없으면 생성
                                .follower(follower)
                                .following(following)
                                .build())
                );
    }
}
