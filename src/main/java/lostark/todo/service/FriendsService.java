package lostark.todo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.characterDto.CharacterDto;
import lostark.todo.controller.dto.friendsDto.FriendsReturnDto;
import lostark.todo.controller.dto.homeDto.HomeFriendsDto;
import lostark.todo.domain.character.Character;
import lostark.todo.domain.friends.FriendSettings;
import lostark.todo.domain.friends.Friends;
import lostark.todo.domain.friends.FriendsRepository;
import lostark.todo.domain.member.Member;
import lostark.todo.domain.todoV2.TodoV2;
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

    public List<Friends> findAllByFromMember(Member member) {
        return friendsRepository.findAllByFromMember(member.getId());
    }

    public void addFriendsRequest(Member toMember, Member fromMember) {
        Friends friends = friendsRepository.findByMemberAndFromMember(toMember, fromMember.getId());
        if(friends != null) {
            throw new IllegalStateException("먼저 기존 요청을 삭제하여 주십시오.");
        }
        Friends toFriends = Friends.builder()
                .member(toMember)
                .fromMember(fromMember.getId())
                .areWeFriend(true)
                .friendSettings(new FriendSettings())
                .build();
        Friends fromFriends = Friends.builder()
                .member(fromMember)
                .fromMember(toMember.getId())
                .areWeFriend(false)
                .friendSettings(new FriendSettings())
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
                return "요청 거부";
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
                            .toFriendSettings(friends.getFriendSettings())
                            .fromFriendSettings(fromFriend.getFriendSettings())
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
        } else if(category.equals("delete")) {
            Friends toMemberEntity = friendsRepository.findByMemberAndFromMember(toMember, fromMember.getId());
            Friends fromMemberEntity = friendsRepository.findByMemberAndFromMember(fromMember, toMember.getId());
            friendsRepository.delete(toMemberEntity);
            friendsRepository.delete(fromMemberEntity);
        } else {
            throw new IllegalStateException("잘못된 요청입니다.");
        }
    }

    public FriendSettings updateSetting(long id, String name, boolean value) {
        Friends friends = friendsRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("없는 데이터 입니다."));
        friends.getFriendSettings().update(name, value);
        return friends.getFriendSettings();
    }

    public boolean checkSetting(Member fromMember, Member toMember, String content) {
        Friends memberAndFromMember = friendsRepository.findByMemberAndFromMember(fromMember, toMember.getId());
        if(content.equals("dayContent")){
            return memberAndFromMember.getFriendSettings().isCheckDayTodo();
        }
        if(content.equals("raid")){
            return memberAndFromMember.getFriendSettings().isCheckRaid();
        }
        if(content.equals("weekTodo")){
            return memberAndFromMember.getFriendSettings().isCheckWeekTodo();
        }
        if(content.equals("setting")){
            return memberAndFromMember.getFriendSettings().isSetting();
        }
        return false;
    }

    public boolean deleteByMember(Member member) {
        int result = friendsRepository.deleteByMember(member, member.getId());
        if (result != 0) {
            return true;
        } else {
            return false;
        }
    }

    public List<HomeFriendsDto> calculateFriendsWeekTotalGold(Member member) {
        List<HomeFriendsDto> homeFriendsDtoList = new ArrayList<>();
        List<Friends> byFromMember = friendsRepository.findAllByFromMember(member.getId());
        for (Friends friends : byFromMember) {
            List<Character> characters = friends.getMember().getCharacters();
            double gold = calculateWeekTotalGold(characters);
            homeFriendsDtoList.add(
                    HomeFriendsDto.builder()
                    .characterName(friends.getMember().getCharacters().get(0).getCharacterName())
                    .gold(gold)
                    .build());
        }
        return homeFriendsDtoList;
    }

    public double calculateWeekTotalGold(List<Character> characterList) {
        double weekTotalGold = 0;
        for (Character character : characterList) {
            if (!character.getTodoV2List().isEmpty()) {
                for (TodoV2 todoV2 : character.getTodoV2List()) {
                    if (todoV2.isChecked()) {
                        weekTotalGold += todoV2.getGold();
                    }
                }
            }
        }
        return weekTotalGold;
    }
}
