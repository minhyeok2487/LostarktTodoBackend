package lostark.todo.controller.dto.characterDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lostark.todo.domainV2.character.entity.Character;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CharacterChallengeResponseDto {

    private boolean challengeGuardian;

    private boolean challengeAbyss;

    public CharacterChallengeResponseDto toDto(Character character) {
        return CharacterChallengeResponseDto.builder()
                .challengeAbyss(character.isChallengeAbyss())
                .challengeGuardian(character.isChallengeGuardian())
                .build();
    }
}
