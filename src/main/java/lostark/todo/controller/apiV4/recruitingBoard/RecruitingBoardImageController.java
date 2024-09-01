package lostark.todo.controller.apiV4.recruitingBoard;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.boardsDto.ImageUrlDto;
import lostark.todo.controller.dtoV2.image.ImageResponse;
import lostark.todo.domain.member.Member;
import lostark.todo.service.ImagesService;
import lostark.todo.service.MemberService;
import lostark.todo.service.RecruitingBoardImagesService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/v4/recruiting-board/image")
@Api(tags = {"모집 게시판 이미지 API"})
public class RecruitingBoardImageController {

    private final MemberService memberService;
    private final ImagesService imagesService;
    private final RecruitingBoardImagesService recruitingBoardImagesService;

    @ApiOperation(value = "이미지 업로드", response = ImageUrlDto.class)
    @PostMapping("/image")
    public ResponseEntity<?> uploadImage(@AuthenticationPrincipal String username, @RequestPart("image") MultipartFile image) {
        Member member = memberService.get(username);
        String folderName = "recruiting/";
        ImageResponse imageResponse = imagesService.upload(image, folderName);
        recruitingBoardImagesService.uploadImage(imageResponse);
        return new ResponseEntity<>(new ImageUrlDto(imageResponse), HttpStatus.OK);
    }
}
