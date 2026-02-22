package lostark.todo.domain.lostark.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.domain.lostark.service.ArmoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/armory")
public class ArmoryApi {

    private final ArmoryService armoryService;

    @GetMapping
    public ResponseEntity<?> getArmory(
            @AuthenticationPrincipal String username,
            @RequestParam String characterName) {
        return ResponseEntity.ok(armoryService.getArmory(username, characterName));
    }

    @GetMapping("/siblings")
    public ResponseEntity<?> getSiblings(
            @AuthenticationPrincipal String username,
            @RequestParam String characterName) {
        return ResponseEntity.ok(armoryService.getSiblings(username, characterName));
    }
}
