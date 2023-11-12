package lostark.todo.controller.dto.friendsDto;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lostark.todo.controller.dto.characterDto.CharacterDto;
import lostark.todo.domain.member.Member;

import javax.persistence.*;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class FriendsReturnDto {

    private long id;

    private String friendUsername;

    private String areWeFriend;

    private String nickName;

    private List<CharacterDto> characterList;
}
