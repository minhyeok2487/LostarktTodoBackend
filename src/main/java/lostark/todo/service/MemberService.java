package lostark.todo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.adminDto.DashboardResponse;
import lostark.todo.controller.dto.characterDto.CharacterSortDto;
import lostark.todo.controller.dto.memberDto.MemberLoginDto;
import lostark.todo.controller.dto.memberDto.MemberRequestDto;
import lostark.todo.controller.dtoV2.admin.SearchAdminMemberRequest;
import lostark.todo.controller.dtoV2.admin.SearchAdminMemberResponse;
import lostark.todo.controller.dtoV2.member.EditProvider;
import lostark.todo.domain.Role;
import lostark.todo.domain.character.Character;
import lostark.todo.domain.member.Member;
import lostark.todo.domain.member.MemberRepository;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static lostark.todo.constants.ErrorMessages.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    // 회원 - 캐릭터 조인 조회 - Test Code X
    @Transactional(readOnly = true)
    public Member get(String username) {
        return memberRepository.get(username).orElseThrow(() -> new NoSuchElementException(MEMER_NOT_FOUND));
    }

    // 1차 회원가입 - Test Code 작성완료
    @Transactional
    public Member createMember(String mail, String password) {
        if (memberRepository.existsByUsername(mail)) {
            throw new IllegalArgumentException(EMAIL_ALREADY_EXISTS);
        }

        Member member = Member.builder()
                .username(mail)
                .password(passwordEncoder.encode(password))
                .characters(new ArrayList<>())
                .authProvider("none")
                .role(Role.USER)
                .build();

        return memberRepository.save(member);
    }

    // 대표 캐릭터 변경 - Test Code 작성완료
    @Transactional
    public void editMainCharacter(String username, String mainCharacter) {
        Member member = get(username);
        boolean characterExists = member.getCharacters().stream()
                .map(Character::getCharacterName)
                .anyMatch(characterName -> characterName.equals(mainCharacter));

        if (characterExists) {
            member.editMainCharacter(mainCharacter);
        } else {
            throw new IllegalArgumentException(MEMBER_CHARACTER_NOT_FOUND);
        }
    }


    // 유저 전환(소셜 로그인 -> 일반 로그인)
    @Transactional
    public void editProvider(String username, EditProvider editProvider) {
        Member member = get(username);
        if (member.getAuthProvider().equals("none")) {
            throw new IllegalArgumentException("소셜 로그인으로 가입된 회원이 아닙니다.");
        }
        member.changeAuthToNone(passwordEncoder.encode(editProvider.getPassword()));
    }

    /////////////////////////////////////////////////////////////////////////////////////////

    public Member login(MemberLoginDto memberloginDto) {
        String username = memberloginDto.getUsername();
        Member member = findMember(username);
        if (passwordEncoder.matches(memberloginDto.getPassword(), member.getPassword())) {
            return member;
        } else {
            throw new IllegalArgumentException("이메일 또는 패스워드가 일치하지 않습니다.");
        }
    }

    /**
     * 회원 찾기(캐릭터 리스트와 함께)
     */
    public Member findMember(String username) {
        return memberRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("등록되지 않은 회원입니다."));
    }

    public Member findMember(long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("등록되지 않은 회원입니다."));
    }

    /**
     * 회원가입 캐릭터 추가
     */
    @Transactional
    public void createCharacter(String username, MemberRequestDto dto, List<Character> characterList) {
        Member member = get(username);
        characterList.stream().map(character -> member.addCharacter(character)).collect(Collectors.toList());
        member.setApiKey(dto.getApiKey());
        member.setMainCharacter(dto.getCharacterName());
    }


    public List<Member> findAll() {
        return memberRepository.findAll();
    }


    public Member updateSort(String username, List<CharacterSortDto> characterSortDtoList) {
        Member member = findMember(username);
        List<Character> beforeCharacterList = member.getCharacters();
        beforeCharacterList.stream().peek(
                        character -> characterSortDtoList.stream()
                                .filter(characterSortDto -> character.getCharacterName().equals(characterSortDto.getCharacterName()))
                                .findFirst()
                                .ifPresent(characterSortDto -> character.setSortNumber(characterSortDto.getSortNumber())))
                .collect(Collectors.toList());

        return member;
    }

    /**
     * 회원 API KEY 업데이트
     */
    public void updateApiKey(Member member, String apiKey) {
        member.setApiKey(apiKey);
    }

    public boolean existByUsername(String username) {
        return memberRepository.existsByUsername(username);
    }

    @Transactional(readOnly = true)
    public List<DashboardResponse> searchMemberDashBoard(int limit) {
        return memberRepository.searchMemberDashBoard(limit);
    }

    @Transactional(readOnly = true)
    public PageImpl<SearchAdminMemberResponse> searchAdminMember(SearchAdminMemberRequest request, PageRequest pageRequest) {
        return memberRepository.searchAdminMember(request, pageRequest);
    }

    @Transactional
    public void removeMember(String name) {
        Member member = get(name);
        memberRepository.delete(member);
    }

    @Transactional
    public void updatePassword(String mail, String newPassword) {
        Member member = get(mail);
        member.updatePassword(passwordEncoder.encode(newPassword));
    }
}
