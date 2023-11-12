package lostark.todo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.characterDto.CharacterCheckDto;
import lostark.todo.controller.dto.characterDto.CharacterDto;
import lostark.todo.controller.dto.characterDto.CharacterSortDto;
import lostark.todo.controller.dto.friendsDto.FriendsReturnDto;
import lostark.todo.domain.character.Character;
import lostark.todo.domain.friends.Friends;
import lostark.todo.domain.friends.FriendsRepository;
import lostark.todo.domain.member.Member;
import lostark.todo.domain.member.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class FriendsService {

    private final FriendsRepository friendsRepository;

    public void addFriendsRequest(Member toMember, Member fromMember) {
        Friends toFriends = Friends.builder()
                .member(toMember)
                .fromMember(fromMember.getId())
                .areWeFriend(true)
                .build();
        Friends fromFriends = Friends.builder()
                .member(fromMember)
                .fromMember(toMember.getId())
                .areWeFriend(false)
                .build();
        friendsRepository.save(toFriends);
        friendsRepository.save(fromFriends);
    }

    public String findFriends(Member toMember, Member fromMember) {
        if(friendsRepository.existsByMemberAndFromMember(toMember, fromMember.getId())) {
            boolean toFriends = friendsRepository.findByMemberAndFromMember(toMember, fromMember.getId()).isAreWeFriend();
            boolean fromFriends = friendsRepository.findByMemberAndFromMember(fromMember, toMember.getId()).isAreWeFriend();
            if(toFriends && fromFriends) {
                return "깐부";
            }
            else if(toFriends) {
                return "깐부 요청 진행중";
            }
            else if(fromFriends) {
                return "깐부 요청 받음";
            }
            else {
                return "깐부 요청";
            }
        }
        return "깐부 요청";
    }

    public List<FriendsReturnDto> findFriends(Member member) {
        List<FriendsReturnDto> returnDtoList = new ArrayList<>();
        List<Friends> byMember = friendsRepository.findAllByMember(member);
        List<Friends> fromMember = friendsRepository.findAllByFromMember(member.getId());
        for (Friends friends : byMember) {
            boolean toFriends = friends.isAreWeFriend();
            for (Friends fromFriend : fromMember) {
                if(friends.getFromMember() == fromFriend.getMember().getId()) {
                    boolean fromFriends = fromFriend.isAreWeFriend();
                    String areWeFriend = "";
                    if(toFriends && fromFriends) {
                        areWeFriend = "깐부";
                    }
                    else if(toFriends) {
                        areWeFriend = "깐부 요청 진행중";
                    }
                    else if(fromFriends) {
                        areWeFriend = "깐부 요청 받음";
                    }
                    else {
                        areWeFriend = "요청 거부";
                    }

                    //캐릭터 리스트
                    List<CharacterDto> characterDtoList = fromFriend.getMember().getCharacters().stream()
                            .filter(character -> character.getSettings().isShowCharacter())
                            .map(character -> new CharacterDto().toDtoV2(character))
                            .collect(Collectors.toList());

                    // characterResponseDtoList를 character.getSortnumber 오름차순으로 정렬
                    characterDtoList.sort(Comparator
                            .comparingInt(CharacterDto::getSortNumber)
                            .thenComparing(Comparator.comparingDouble(CharacterDto::getItemLevel).reversed())
                    );

                    FriendsReturnDto friendsReturnDto = FriendsReturnDto.builder()
                            .id(friends.getId())
                            .friendUsername(fromFriend.getMember().getUsername())
                            .areWeFriend(areWeFriend)
                            .nickName(fromFriend.getMember().getCharacters().get(0).getCharacterName())
                            .characterList(characterDtoList)
                            .build();

                    returnDtoList.add(friendsReturnDto);
                }
            }
        }
        return returnDtoList;
    }

    public void updateFriendsRequest(Member toMember, Member fromMember, String category) {
        if(category.equals("ok")) {
            friendsRepository.findByMemberAndFromMember(toMember, fromMember.getId()).setAreWeFriend(true);
        } else if(category.equals("reject")) {
            friendsRepository.findByMemberAndFromMember(fromMember, toMember.getId()).setAreWeFriend(false);
        } else {
            throw new IllegalStateException("잘못된 요청입니다.");
        }
    }
}
