package lostark.todo.domainV2.character.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lostark.todo.controller.dtoV2.character.CharacterResponse;
import lostark.todo.domainV2.character.dto.BaseCharacterRequest;
import lostark.todo.domainV2.character.entity.Character;
import lostark.todo.global.friendPermisson.FriendPermissionType;
import lostark.todo.global.friendPermisson.UpdateCharacterMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.function.Consumer;

@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class CharacterResponseOperation {

    private String username;
    private String friendUsername;
    private BaseCharacterRequest request;
    private FriendPermissionType permissionType;
    private Consumer<Character> operation;
    private UpdateCharacterMethod updateCharacterMethod;

    public ResponseEntity<CharacterResponse> execute() {
        Character character = updateCharacterMethod.getUpdateCharacter(
                username, friendUsername, request.getCharacterId(), permissionType
        );
        operation.accept(character);
        return new ResponseEntity<>(CharacterResponse.toDto(character), HttpStatus.OK);
    }
}
