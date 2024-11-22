package lostark.todo.domainV2.board.community.service;

import lombok.RequiredArgsConstructor;
import lostark.todo.domainV2.member.entity.Member;
import lostark.todo.domainV2.member.repository.MemberRepository;
import lostark.todo.domainV2.board.community.dao.FollowDao;
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

    private final FollowDao followDao;
    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public List<FollowResponse> search(String username) {
        List<FollowResponse> result = new ArrayList<>();
        for (Follow search : followDao.search(username)) {
            result.add(new FollowResponse(search));
        }
        return result;
    }

    @Transactional
    public void update(String username, FollowingUpdateRequest request) {
        Member follower = memberRepository.get(username);
        Member following = memberRepository.get(request.getFollowing());
        followDao.update(follower, following);
    }
}
