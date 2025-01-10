package lostark.todo.domain.member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.admin.dto.DashboardResponse;
import lostark.todo.controller.dto.memberDto.SaveCharacterRequest;
import lostark.todo.controller.dtoV2.admin.SearchAdminMemberRequest;
import lostark.todo.controller.dtoV2.admin.SearchAdminMemberResponse;
import lostark.todo.controller.dtoV2.auth.ResetPasswordRequest;
import lostark.todo.domain.member.dto.SaveAdsRequest;
import lostark.todo.domain.member.entity.Ads;
import lostark.todo.domain.member.repository.AdsRepository;
import lostark.todo.domain.member.repository.AuthMailRepository;
import lostark.todo.domain.util.market.entity.Market;
import lostark.todo.domain.util.market.repository.MarketRepository;
import lostark.todo.domain.character.entity.Character;
import lostark.todo.domain.member.entity.Member;
import lostark.todo.domain.member.repository.MemberRepository;
import lostark.todo.domain.lostark.client.LostarkCharacterApiClient;
import lostark.todo.domain.member.infra.MemberLockManager;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static lostark.todo.global.exhandler.ErrorMessageConstants.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MemberService {

    private final MemberLockManager memberLockManager;
    private final MemberRepository memberRepository;
    private final AuthMailRepository authMailRepository;
    private final PasswordEncoder passwordEncoder;
    private final LostarkCharacterApiClient lostarkCharacterApiClient;
    private final MarketRepository marketRepository;
    private final AdsRepository adsRepository;

    // 회원 - 캐릭터 조인 조회 - Test Code X
    @Transactional(readOnly = true)
    public Member get(String username) {
        return memberRepository.get(username);
    }

    @Transactional(readOnly = true)
    public Member get(Long id) {
        return memberRepository.get(id);
    }

    // 회원가입 후 캐릭터 추가
    @Transactional
    public void createCharacter(String username, SaveCharacterRequest request) {
        try (var ignored = memberLockManager.acquireLock(username)) {
            Member member = get(username);
            validateCreateCharacter(member);
            List<Character> characterList = createAndCalculateCharacters(request);
            member.createCharacter(characterList, request);
        }
    }

    private static void validateCreateCharacter(Member member) {
        if (!member.getCharacters().isEmpty()) {
            throw new IllegalStateException(CHARACTER_ALREADY_EXISTS);
        }
    }

    private List<Character> createAndCalculateCharacters(SaveCharacterRequest request) {
        // 대표캐릭터와 연동된 캐릭터 호출(api 검증)
        List<Character> characterList = lostarkCharacterApiClient.createCharacterList(
                request.getCharacterName(), request.getApiKey());

        // 재련재료 데이터 리스트로 거래소 데이터 호출
        Map<String, Market> contentResource = marketRepository.findLevelUpResource();

        // 일일숙제 예상 수익 계산(휴식 게이지 포함)
        return characterList.stream()
                .map(character -> character.calculateDayTodo(character, contentResource))
                .collect(Collectors.toList());
    }

    // 대표 캐릭터 변경
    @Transactional
    public void editMainCharacter(String username, String mainCharacter) {
        Member member = get(username);
        if (member.getCharacters().stream()
                .noneMatch(character -> character.getCharacterName().equals(mainCharacter))) {
            throw new IllegalArgumentException(MEMBER_CHARACTER_NOT_FOUND);
        }
        member.editMainCharacter(mainCharacter);
    }

    // 유저 전환(소셜 로그인 -> 일반 로그인) - Test Code 작성완료
    @Transactional
    public void editProvider(String username, String newPassword) {
        Member member = get(username);
        if (member.getAuthProvider().equals("none")) {
            throw new IllegalArgumentException(MEMBER_NOT_SOCIAL);
        }
        member.changeAuthToNone(passwordEncoder.encode(newPassword));
    }

    // 비밀번호 변경 - Test Code 작성 X
    @Transactional
    public void updatePassword(ResetPasswordRequest request) {
        authMailRepository.getAuthMail(request.getMail(), request.getNumber())
                .orElseThrow(() -> new IllegalStateException("이메일 인증이 실패하였습니다."));
        Member member = get(request.getMail());
        member.updatePassword(passwordEncoder.encode(request.getNewPassword()));
        authMailRepository.deleteAllByMail(request.getMail());
    }

    // 회원 API KEY 수정 - Test Code 작성 X
    public void editApiKey(Member member, String apiKey) {
        member.editApiKey(apiKey);
    }


    // Admin 일일 가입자 수 통계 호출 - Test Code 작성 X
    @Transactional(readOnly = true)
    public List<DashboardResponse> searchMemberDashBoard(int limit) {
        return memberRepository.searchMemberDashBoard(limit);
    }

    // Admin 회원 리스트 출력 - Test Code 작성 X
    @Transactional(readOnly = true)
    public PageImpl<SearchAdminMemberResponse> searchAdminMember(SearchAdminMemberRequest request, PageRequest pageRequest) {
        return memberRepository.searchAdminMember(request, pageRequest);
    }

    //광고 제거 기능 신청
    public void saveAds(String username, SaveAdsRequest request) {
        Member member = memberRepository.get(request.getMail());
        Ads ads = Ads.builder()
                .name(request.getName())
                .memberId(member.getId())
                .proposerEmail(username)
                .check(false)
                .build();
        adsRepository.save(ads);
    }
}
