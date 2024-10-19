package lostark.todo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.characterDto.CharacterDto;
import lostark.todo.domainV2.friend.dao.FriendDao;
import lostark.todo.domainV2.friend.dto.FriendFindCharacterResponse;
import lostark.todo.controller.dto.friendsDto.FriendsReturnDto;
import lostark.todo.controller.dtoV2.character.CharacterResponse;
import lostark.todo.controller.dtoV2.firend.FriendsResponse;
import lostark.todo.domainV2.character.dao.CharacterDao;
import lostark.todo.domainV2.character.entity.Character;
import lostark.todo.domain.friends.FriendSettings;
import lostark.todo.domain.friends.Friends;
import lostark.todo.domain.friends.FriendsRepository;
import lostark.todo.domain.member.Member;
import lostark.todo.domainV2.friend.enums.FriendStatus;
import lostark.todo.domainV2.member.dao.MemberDao;
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
    private final FriendDao friendDao;
    private final MemberDao memberDao;
    private final CharacterDao characterDao;

    public List<Friends> findAllByFromMember(Member member) {
        return friendsRepository.findAllByFromMember(member.getId());
    }

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

    public String isFriend(Member toMember, Member fromMember) {
        if (friendsRepository.existsByMemberAndFromMember(toMember, fromMember.getId())) {
            boolean toFriends = friendsRepository.findByMemberAndFromMember(toMember, fromMember.getId()).isAreWeFriend();
            boolean fromFriends = friendsRepository.findByMemberAndFromMember(fromMember, toMember.getId()).isAreWeFriend();
            if (toFriends && fromFriends) {
                return "깐부";
            } else if (toFriends) {
                return "깐부 요청 진행중";
            } else if (fromFriends) {
                return "깐부 요청 받음";
            } else {
                return "요청 거부";
            }
        }
        return "깐부 요청";
    }

    public List<FriendsReturnDto> isFriend(Member member) {
        List<FriendsReturnDto> returnDtoList = new ArrayList<>();
        List<Friends> byMember = friendsRepository.findAllByMember(member);
        List<Friends> fromMember = friendsRepository.findAllByFromMember(member.getId());
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
                            .filter(character -> character.getSettings().isShowCharacter())
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

    public void updateFriendsRequest(Member toMember, Member fromMember, String category) {
        if (category.equals("ok")) {
            friendsRepository.findByMemberAndFromMember(toMember, fromMember.getId()).setAreWeFriend(true);
        } else if (category.equals("reject")) {
            friendsRepository.findByMemberAndFromMember(fromMember, toMember.getId()).setAreWeFriend(false);
        } else if (category.equals("delete")) {
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
        Member member = memberDao.get(username);
        List<Character> characterList = characterDao.getCharacter(characterName);

        if (characterList.isEmpty()) {
            throw new IllegalArgumentException(CHARACTER_NOT_FOUND);
        }

        return characterList.stream()
                .filter(character -> !character.getMember().equals(member)) //본인 제외
                .map(character -> {
                    FriendStatus friendDaoFriend = friendDao.isFriend(member.getId(), character.getMember().getId());
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
}
