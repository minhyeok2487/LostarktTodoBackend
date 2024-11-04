package lostark.todo.domainV2.board.community.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lostark.todo.domain.member.Member;
import lostark.todo.domainV2.board.community.entity.Follow;
import lostark.todo.domainV2.character.entity.Character;

@Data
public class FollowResponse {

    private long followId;

    @ApiModelProperty(notes = "팔로우 사용자 id")
    private long following;

    private String characterImageUrl;

    public FollowResponse(Follow follow) {
        this.followId = follow.getId();
        this.following = follow.getFollowing().getId();
        this.characterImageUrl = getCharacterImageUrl(follow.getFollowing());
    }

    public String getCharacterImageUrl(Member member) {
        for (Character character : member.getCharacters()) {
            if (character.getCharacterName().equals(member.getMainCharacterName())) {
                return character.getCharacterImage();
            }
        }
        return null;
    }
}
