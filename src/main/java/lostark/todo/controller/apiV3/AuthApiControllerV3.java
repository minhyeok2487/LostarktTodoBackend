package lostark.todo.controller.apiV3;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.auth.AuthSignupDto;
import lostark.todo.domain.character.Character;
import lostark.todo.service.*;
import lostark.todo.service.lostarkApi.LostarkApiService;
import lostark.todo.service.lostarkApi.LostarkCharacterService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/v3/auth")
@Api(tags = {"인증 API V3"})
public class AuthApiControllerV3 {

    private final CharacterService characterService;
    private final MarketService marketService;
    private final ContentService contentService;
    private final MemberService memberService;
    private final LostarkCharacterService lostarkCharacterService;
    private final LostarkApiService lostarkApiService;
    private final LogsService logsService;
    private final ConcurrentHashMap<String, Boolean> usernameLocks;

}
