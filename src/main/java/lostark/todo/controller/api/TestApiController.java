package lostark.todo.controller.api;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.domain.market.Market;
import lostark.todo.domain.member.Member;
import lostark.todo.service.MarketService;
import lostark.todo.service.MemberService;
import lostark.todo.service.lostarkApi.LostarkApiService;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/news")
@Api(tags = {"Test API"})
public class TestApiController {

    private final LostarkApiService lostarkApiService;
    private final MemberService memberService;
    @GetMapping("/events")
    public ResponseEntity test(@AuthenticationPrincipal String username) throws IOException, ParseException {
        String apiKey = memberService.findMember(username).getApiKey();
        String link = "https://developer-lostark.game.onstove.com/news/events";
        InputStreamReader inputStreamReader = lostarkApiService.lostarkGetApi(link, apiKey);
        JSONParser parser = new JSONParser();
        JSONArray jsonArray = (JSONArray) parser.parse(inputStreamReader);
        return new ResponseEntity(jsonArray, HttpStatus.OK);
    }

}
