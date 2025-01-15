package lostark.todo.domain.friend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.friendsDto.UpdateFriendSettingRequest;
import lostark.todo.domain.character.repository.CharacterRepository;
import lostark.todo.domain.member.repository.MemberRepository;
import lostark.todo.domain.friend.dto.FriendFindCharacterResponse;
import lostark.todo.controller.dtoV2.character.CharacterResponse;
import lostark.todo.controller.dtoV2.firend.FriendsResponse;
import lostark.todo.domain.character.entity.Character;
import lostark.todo.domain.friend.entity.FriendSettings;
import lostark.todo.domain.friend.entity.Friends;
import lostark.todo.domain.friend.repository.FriendsRepository;
import lostark.todo.domain.member.entity.Member;
import lostark.todo.domain.friend.enums.FriendRequestCategory;
import lostark.todo.domain.friend.enums.FriendStatus;
import lostark.todo.global.utils.GlobalMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static lostark.todo.global.exhandler.ErrorMessageConstants.CHARACTER_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class FriendsService {

    private final FriendsRepository friendsRepository;
    private final MemberRepository memberRepository;
    private final CharacterRepository characterRepository;

    public void addFriendsRequest(Member toMember, Member fromMember) {
        Friends friends = friendsRepository.findByMemberAndFromMember(toMember, fromMember.getId());
        if (friends != null) {
            throw new IllegalStateException("먼저 기존 요청을 삭제하여 주십시오.");
        }
        Friends toFriends = Friends.builder()
                .member(toMember)
                .fromMember(fromMember.getId())
                .areWeFriend(true)
                .friendSettings(new FriendSettings())
                .ordering(0)
                .build();
        Friends fromFriends = Friends.builder()
                .member(fromMember)
                .fromMember(toMember.getId())
                .areWeFriend(false)
                .friendSettings(new FriendSettings())
                .ordering(0)
                .build();
        friendsRepository.save(toFriends);
        friendsRepository.save(fromFriends);
    }

    @Transactional(readOnly = true)
    public List<FriendsResponse> getFriendListV2(long memberId) {
        List<FriendsResponse> returnDtoList = new ArrayList<>();
        List<Friends> friendList = friendsRepository.getFriendList(memberId);
        List<Friends> byMember = friendList.stream().filter(friend -> friend.getMember().getId() == memberId).distinct().collect(Collectors.toList());
        List<Friends> fromMember = friendList.stream().filter(friend -> friend.getFromMember() == memberId).distinct().collect(Collectors.toList());
        for (Friends friends : byMember) {
            boolean toFriends = friends.isAreWeFriend();
            for (Friends fromFriend : fromMember) {
                if (friends.getFromMember() == fromFriend.getMember().getId()) {
                    boolean fromFriends = fromFriend.isAreWeFriend();
                    String areWeFriend = "";
                    if (toFriends && fromFriends) {
                        areWeFriend = "깐부";
                    } else if (toFriends) {
                        areWeFriend = "깐부 요청 진행중";
                    } else if (fromFriends) {
                        areWeFriend = "깐부 요청 받음";
                    } else {
                        areWeFriend = "요청 거부";
                    }

                    //캐릭터 리스트
                    List<CharacterResponse> characterResponseList = fromFriend.getMember().getCharacters().stream()
                            .filter(character -> character.getSettings().isShowCharacter() & !character.isDeleted())
                            .map(CharacterResponse::toDto)
                            .sorted(Comparator
                                    .comparingInt(CharacterResponse::getSortNumber)
                                    .thenComparing(Comparator.comparingDouble(CharacterResponse::getItemLevel).reversed()))
                            .collect(Collectors.toList());

                    FriendsResponse friendsReturnDto = FriendsResponse.builder()
                            .friendId(friends.getId())
                            .friendUsername(fromFriend.getMember().getUsername())
                            .areWeFriend(areWeFriend)
                            .nickName(getMainCharacterName(fromFriend.getMember()))
                            .ordering(friends.getOrdering())
                            .characterList(characterResponseList)
                            .toFriendSettings(friends.getFriendSettings())
                            .fromFriendSettings(fromFriend.getFriendSettings())
                            .build();

                    returnDtoList.add(friendsReturnDto);
                }
            }
        }
        return returnDtoList;
    }

    private String getMainCharacterName(Member member) {
        return member.getMainCharacterName() != null ? member.getMainCharacterName() : member.getCharacters().get(0).getCharacterName();
    }

    public FriendSettings updateSetting(UpdateFriendSettingRequest request ) {
        Friends friends = friendsRepository.findById(request.getId()).orElseThrow(() -> new IllegalArgumentException("없는 데이터 입니다."));
        friends.getFriendSettings().update(request.getName(), request.isValue());
        return friends.getFriendSettings();
    }

    public boolean deleteByMember(Member member) {
        long result = friendsRepository.deleteByMember(member);
        if (result != 0) {
            return true;
        } else {
            return false;
        }
    }

    public Character findFriendCharacter(String friendUsername, long characterId) {
        return friendsRepository.findFriendCharacter(friendUsername, characterId);
    }

    public Friends findByFriendUsername(String friendUsername, String username) {
        return friendsRepository.findByFriendUsername(friendUsername, username).orElseThrow(() -> new IllegalArgumentException("등록되지 않은 정보입니다."));
    }

    @Transactional
    public void deleteById(Member member, long friendId) {
        Friends friends = friendsRepository.findByMemberAndId(member, friendId)
                .orElseThrow(() -> new IllegalArgumentException("없는 깐부 입니다."));
        friendsRepository.deleteByMemberFriend(friends.getMember().getId(), friends.getFromMember());
        friendsRepository.deleteByMemberFriend(friends.getFromMember(), friends.getMember().getId());
    }

    @Transactional
    public void updateSort(Member member, List<Long> friendIdList) {
        GlobalMethod.compareLists(member.getFriends().stream().map(Friends::getId).toList(),
                friendIdList, "깐부 리스트가 일치하지 않습니다.");
        Map<Long, Integer> idOrderingMap = new HashMap<>();
        int start = 1;
        for (Long param : friendIdList) {
            idOrderingMap.put(param, start++);
        }
        friendsRepository.updateSort(idOrderingMap);
    }

    @Transactional(readOnly = true)
    public List<FriendFindCharacterResponse> findCharacter(String username, String characterName) {
        Member member = memberRepository.get(username);
        List<Character> characterList = characterRepository.getCharacter(characterName);

        if (characterList.isEmpty()) {
            throw new IllegalArgumentException(CHARACTER_NOT_FOUND);
        }

        return characterList.stream()
                .filter(character -> !character.getMember().equals(member)) //본인 제외
                .map(character -> {
                    FriendStatus friendDaoFriend = friendsRepository.isFriend(member.getId(), character.getMember().getId());
                    return FriendFindCharacterResponse.builder()
                            .id(character.getMember().getId())
                            .username(character.getMember().getUsername())
                            .characterName(characterName)
                            .characterListSize(character.getMember().getCharacters().size())
                            .areWeFriend(friendDaoFriend.getType())
                            .build();
                })
                .collect(Collectors.toList());
    }

    public void updateFriendsRequestV2(Member toMember, Member fromMember, FriendRequestCategory category) {
        if (category.equals(FriendRequestCategory.OK)) {
            friendsRepository.findByMemberAndFromMember(toMember, fromMember.getId()).setAreWeFriend(true);
        } else if (category.equals(FriendRequestCategory.REJECT)) {
            friendsRepository.findByMemberAndFromMember(fromMember, toMember.getId()).setAreWeFriend(false);
        } else if (category.equals(FriendRequestCategory.DELETE)) {
            Friends toMemberEntity = friendsRepository.findByMemberAndFromMember(toMember, fromMember.getId());
            Friends fromMemberEntity = friendsRepository.findByMemberAndFromMember(fromMember, toMember.getId());
            friendsRepository.delete(toMemberEntity);
            friendsRepository.delete(fromMemberEntity);
        } else {
            throw new RuntimeException();
        }
    }
}
