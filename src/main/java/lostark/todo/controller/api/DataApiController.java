package lostark.todo.controller.api;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.domain.market.Market;
import lostark.todo.domain.member.Member;
import lostark.todo.service.MarketService;
import lostark.todo.service.MemberService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/db")
@Api(tags = {"데이터 API"})
public class DataApiController {

    private final MemberService memberService;
    private final MarketService marketService;
    @GetMapping("/characters")
    public ResponseEntity getCharacters() {
        List<Member> memberList = memberService.findAll();
        return new ResponseEntity(memberList, HttpStatus.OK);
    }

    @GetMapping("/markets")
    public ResponseEntity getMarkets() {
        List<Market> marketList = marketService.findAll();
        return new ResponseEntity(marketList, HttpStatus.OK);
    }
}
