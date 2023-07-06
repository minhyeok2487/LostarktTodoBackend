package lostark.todo.controller.lostarkApi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dtos.MemberFindDto;
import lostark.todo.domain.character.Character;
import lostark.todo.domain.market.Market;
import lostark.todo.domain.member.Member;
import lostark.todo.service.lostarkApi.LostarkCharacterService;
import lostark.todo.service.lostarkApi.LostarkMarketService;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/lostarkApi/market")
public class LostarkMarketApiController {

    private final LostarkMarketService marketService;

    @GetMapping("/{categoryCode}")
    public ResponseEntity getMarketCategoryCode(@PathVariable int categoryCode, HttpServletRequest request) throws Exception {
        Long memberId = Long.parseLong(request.getHeader("memberId"));
        List<Market> marketList = marketService.getMarketData(categoryCode, memberId);
        return new ResponseEntity(marketList, HttpStatus.OK);
    }


}
