package lostark.todo.domain.admin.api;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lostark.todo.domain.admin.dto.AdminCharacterResponse;
import lostark.todo.domain.admin.dto.AdminCharacterSearchRequest;
import lostark.todo.domain.admin.dto.AdminCharacterUpdateRequest;
import lostark.todo.domain.character.entity.Character;
import lostark.todo.domain.character.service.CharacterService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/api/v1/characters")
@RequiredArgsConstructor
public class AdminCharacterApi {

    private final CharacterService characterService;

    @ApiOperation(value = "어드민 캐릭터 목록 조회 API",
            response = AdminCharacterResponse.class)
    @GetMapping
    public ResponseEntity<?> search(AdminCharacterSearchRequest request,
                                    @RequestParam(required = false, defaultValue = "1") int page,
                                    @RequestParam(required = false, defaultValue = "25") int limit) {
        PageRequest pageRequest = PageRequest.of(page - 1, limit);
        Page<AdminCharacterResponse> characterList = characterService.searchAdminCharacter(request, pageRequest);
        return new ResponseEntity<>(characterList, HttpStatus.OK);
    }

    @ApiOperation(value = "어드민 캐릭터 상세 조회 API",
            response = AdminCharacterResponse.class)
    @GetMapping("/{characterId}")
    public ResponseEntity<?> getDetail(@PathVariable Long characterId) {
        Character character = characterService.getByIdForAdmin(characterId);
        return new ResponseEntity<>(AdminCharacterResponse.from(character), HttpStatus.OK);
    }

    @ApiOperation(value = "어드민 캐릭터 정보 수정 API",
            response = AdminCharacterResponse.class)
    @PutMapping("/{characterId}")
    public ResponseEntity<?> update(@PathVariable Long characterId,
                                    @RequestBody AdminCharacterUpdateRequest request) {
        Character updatedCharacter = characterService.updateByAdmin(characterId, request);
        return new ResponseEntity<>(AdminCharacterResponse.from(updatedCharacter), HttpStatus.OK);
    }

    @ApiOperation(value = "어드민 캐릭터 삭제 API")
    @DeleteMapping("/{characterId}")
    public ResponseEntity<?> delete(@PathVariable Long characterId,
                                    @RequestParam(defaultValue = "false") boolean hardDelete) {
        characterService.deleteByAdmin(characterId, hardDelete);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
