package lostark.todo.domain.friend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.domain.admin.dto.AdminFriendResponse;
import lostark.todo.domain.friend.dto.UpdateFriendSettingRequest;
import lostark.todo.domain.character.repository.CharacterRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import lostark.todo.domain.friend.enums.FriendshipPair;
import lostark.todo.domain.friend.dto.FriendFindCharacterResponse;
import lostark.todo.domain.character.dto.CharacterResponse;
import lostark.todo.domain.friend.dto.FriendsResponse;
import lostark.todo.domain.character.entity.Character;
import lostark.todo.domain.friend.entity.FriendSettings;
import lostark.todo.domain.friend.entity.Friends;
import lostark.todo.domain.friend.repository.FriendsRepository;
import lostark.todo.domain.member.entity.Member;
import lostark.todo.domain.friend.enums.FriendRequestCategory;
import lostark.todo.domain.friend.enums.FriendStatus;
import lostark.todo.global.exhandler.exceptions.ConditionNotMetException;
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
    private final CharacterRepository characterRepository;

    @Transactional(readOnly = true)
    public List<FriendsResponse> get(long memberId) {
        Map<Long, FriendshipPair> friendshipPairs = friendsRepository.findFriendshipPairs(memberId);

        return friendshipPairs.values().stream()
                .map(pair -> {
                    Friends toFriend = pair.toFriend();
                    Friends fromFriend = pair.fromFriend();

                    String areWeFriend = determineFriendshipStatus(
                            toFriend.isAreWeFriend(),
                            fromFriend.isAreWeFriend()
                    );

                    List<CharacterResponse> characterResponseList = fromFriend.getMember().toDtoList();

                    return FriendsResponse.builder()
                            .friendId(toFriend.getId())
                            .friendUsername(fromFriend.getMember().getUsername())
                            .areWeFriend(areWeFriend)
                            .nickName(getMainCharacterName(fromFriend.getMember()))
                            .ordering(toFriend.getOrdering())
                            .characterList(characterResponseList)
                            .toFriendSettings(toFriend.getFriendSettings())
                            .fromFriendSettings(fromFriend.getFriendSettings())
                            .build();
                })
                .collect(Collectors.toList());
    }

    private String determineFriendshipStatus(boolean toFriends, boolean fromFriends) {
        if (toFriends && fromFriends) return "깐부";
        if (toFriends) return "깐부 요청 진행중";
        if (fromFriends) return "깐부 요청 받음";
        return "요청 거부";
    }

    public void addFriendsRequest(Member toMember, Member fromMember) {
        Friends friends = friendsRepository.findByMemberAndFromMember(toMember, fromMember.getId());
        if (friends != null) {
            throw new ConditionNotMetException("먼저 기존 요청을 삭제하여 주십시오.");
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

    private String getMainCharacterName(Member member) {
        return member.getMainCharacterName() != null ? member.getMainCharacterName() : member.getCharacters().get(0).getCharacterName();
    }

    public FriendSettings updateSetting(UpdateFriendSettingRequest request ) {
        Friends friends = friendsRepository.findById(request.getId()).orElseThrow(() -> new ConditionNotMetException("없는 데이터 입니다."));
        friends.getFriendSettings().update(request.getName(), request.isValue());
        return friends.getFriendSettings();
    }

    public boolean deleteByMember(Member member) {
        long result = friendsRepository.deleteByMember(member);
        return result != 0;
    }

    public Friends findByFriendUsername(String friendUsername, String username) {
        return friendsRepository.findByFriendUsername(friendUsername, username).orElseThrow(() -> new ConditionNotMetException("등록되지 않은 정보입니다."));
    }

    @Transactional
    public void deleteById(Member member, long friendId) {
        Friends friends = friendsRepository.findByMemberAndId(member, friendId)
                .orElseThrow(() -> new ConditionNotMetException("없는 깐부 입니다."));
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
        List<Character> characterList = characterRepository.getCharacter(characterName);

        if (characterList.isEmpty()) {
            throw new ConditionNotMetException(CHARACTER_NOT_FOUND);
        }

        return characterList.stream()
                .filter(character -> !character.getMember().getUsername().equals(username)) //본인 제외
                .map(character -> {
                    Member member = character.getMember();
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
            if (toMemberEntity != null) {
                friendsRepository.deleteByIdSafe(toMemberEntity.getId());
            }
            if (fromMemberEntity != null) {
                friendsRepository.deleteByIdSafe(fromMemberEntity.getId());
            }
        } else {
            throw new ConditionNotMetException("지원하지 않는 친구 요청 카테고리입니다: " + category);
        }
    }

    // =============== Admin Methods ===============

    @Transactional(readOnly = true)
    public Page<AdminFriendResponse> getFriendsForAdmin(Pageable pageable) {
        Page<Friends> friends = friendsRepository.findAll(
                PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                        Sort.by(Sort.Direction.DESC, "createdDate")));

        return friends.map(AdminFriendResponse::from);
    }

    @Transactional
    public void deleteByAdmin(Long friendId) {
        Friends friend = friendsRepository.findById(friendId)
                .orElseThrow(() -> new ConditionNotMetException("깐부 관계가 존재하지 않습니다. ID: " + friendId));

        // 양쪽 관계 모두 삭제
        friendsRepository.deleteByMemberFriend(friend.getMember().getId(), friend.getFromMember());
        friendsRepository.deleteByMemberFriend(friend.getFromMember(), friend.getMember().getId());
    }
}
