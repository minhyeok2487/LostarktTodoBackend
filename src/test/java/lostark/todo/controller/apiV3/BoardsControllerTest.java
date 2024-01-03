//package lostark.todo.controller.apiV3;
//
//import lombok.extern.slf4j.Slf4j;
//import lostark.todo.controller.dto.boardsDto.BoardInsertDto;
//import lostark.todo.controller.dto.boardsDto.BoardResponseDto;
//import lostark.todo.controller.dto.boardsDto.BoardUpdateDto;
//import lostark.todo.controller.dto.boardsDto.BoardsDto;
//import lostark.todo.domain.Role;
//import lostark.todo.domain.boards.Boards;
//import lostark.todo.domain.member.Member;
//import lostark.todo.exhandler.exceptions.CustomIllegalArgumentException;
//import lostark.todo.service.BoardsService;
//import lostark.todo.service.MemberService;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.data.domain.Page;
//
//import javax.transaction.Transactional;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//import static org.assertj.core.api.Assertions.*;
//import static org.junit.jupiter.api.Assertions.*;
//
//@SpringBootTest
//@Transactional
//@Slf4j
//class BoardsControllerTest {
//
//    @Autowired
//    BoardsService boardsService;
//
//    @Autowired
//    MemberService memberService;
//
//    /*사이트 공지사항 가져오기 Method*/
//    public BoardsDto findAllNoticeMethod(int page, int size) {
//        if (page < 1) {
//            throw new CustomIllegalArgumentException("사이트 공지사항 가져오기 에러", "page 입력 값은 1보다 커야 합니다.", null);
//        }
//        if (size < 1) {
//            throw new CustomIllegalArgumentException("사이트 공지사항 가져오기 에러", "size 입력 값은 1보다 커야 합니다.", null);
//        }
//        Page<Boards> all = boardsService.findAll(page - 1, size);
//        List<BoardResponseDto> boardResponseDtoList = all
//                .stream().map(board -> new BoardResponseDto().toDto(board))
//                .collect(Collectors.toList());
//        int totalPages = all.getTotalPages();
//        BoardsDto boardsDto = new BoardsDto().toDto(boardResponseDtoList, totalPages, page);
//        log.info("사이트 공지사항 리스트를 성공적으로 검색했습니다. Page: {}, Size: {}", page, size);
//        return boardsDto;
//    }
//
//    @Test
//    @DisplayName("사이트 공지사항 최근 10개 가져오기")
//    void getAllNoticeTest() {
//        //given
//        int page = 1;
//        int size = 10;
//
//        //when
//        BoardsDto allNoticeMethod = findAllNoticeMethod(page, size);
//
//        //then
//        Page<Boards> boards = boardsService.findAll(page - 1, size);
//        assertThat(allNoticeMethod.getPage()).isEqualTo(page);
//        assertThat(allNoticeMethod.getBoardResponseDtoList().get(0).getId())
//                .isEqualTo(boards.getContent().get(0).getId());
//    }
//
//    @Test
//    @DisplayName("사이트 공지사항 가져오기 - page error")
//    void getAllNoticeTestPageError() {
//        //given
//        int size = 10;
//
//        //when
//        int page = 0;
//
//        //then
//        assertThrows(IllegalArgumentException.class, () -> {
//            findAllNoticeMethod(page, size);
//        });
//    }
//
//    /*사이트 공지사항 인덱스 번호로 가져오기 Method*/
//    public BoardResponseDto findById(long id) {
//        if (id < 0) {
//            throw new CustomIllegalArgumentException("사이트 공지사항 id로 가져오기 에러","id는 0보다 커야합니다.", null);
//        }
//        BoardResponseDto dto = new BoardResponseDto().toDto(boardsService.findById(id));
//        log.info("사이트 공지사항을 성공적으로 검색했습니다. Id: {}", id);
//        return dto;
//    }
//
//    @Test
//    @DisplayName("사이트 공지사항 인덱스 번호로 가져오기 테스트")
//    void findByIdTest() {
//        //given
//        long id = 3;
//
//        //when
//        BoardResponseDto boardResponseDto = findById(id);
//
//        //then
//        Boards boards = boardsService.findById(id);
//        assertThat(boardResponseDto.getId()).isEqualTo(boards.getId());
//        assertThat(boardResponseDto.getTitle()).isEqualTo(boards.getTitle());
//        assertThat(boardResponseDto.getWriter()).isEqualTo(boards.getMember().getUsername());
//        assertThat(boardResponseDto.getRegDate()).isEqualTo(boards.getCreatedDate());
//    }
//
//    @Test
//    @DisplayName("사이트 공지사항 가져오기 - 없는 id")
//    void findByIdTestPageError() {
//
//        //when
//        long id = 2;
//
//        //then
//        assertThrows(IllegalArgumentException.class, () -> {
//            findById(id);
//        });
//    }
//
//    /*사이트 공지사항 저장 Method*/
//    public Boards save(String username, BoardInsertDto boardInsertDto) {
//        Member member = memberService.findMember(username);
//
//        if (member.getRole().equals(Role.ADMIN)) {
//            Boards entity = Boards.builder()
//                    .member(member)
//                    .title(boardInsertDto.getTitle())
//                    .content(boardInsertDto.getContent())
//                    .views(0)
//                    .build();
//
//            Boards save = boardsService.save(entity);
//
//            log.info("사이트 공지사항을 성공적으로 저장하였습니다. Id: {}", save.getId());
//            return save;
//        } else {
//            throw new CustomIllegalArgumentException("사이트 공지사항 저장 에러", "권한이 없습니다.", member);
//        }
//    }
//
//    @Test
//    @DisplayName("사이트 공지사항 저장 테스트")
//    void saveTest() {
//        //given
//        String username = "repeat2487@gmail.com";
//        String message = "사이트 공지사항 저장 테스트";
//        BoardInsertDto boardInsertDto = BoardInsertDto.builder()
//                .title(message)
//                .content(message+" 입니다.")
//                .build();
//
//        //when
//        Boards save = save(username, boardInsertDto);
//
//        //then
//        log.info(save.toString());
//        assertThat(save.getId()).isNotNull();
//        assertThat(save.getMember().getUsername()).isEqualTo(username);
//        assertThat(save.getTitle()).isEqualTo(message);
//        assertThat(save.getContent()).isEqualTo(message+" 입니다.");
//    }
//
//    @Test
//    @DisplayName("사이트 공지사항 저장 테스트 - 권한 없음")
//    void saveTestNotRole() {
//        //given
//        String message = "사이트 공지사항 저장 테스트 - 권한 없음";
//        BoardInsertDto boardInsertDto = BoardInsertDto.builder()
//                .title(message)
//                .content(message+" 입니다.")
//                .build();
//
//        //when
//        String username = "qwe2487@ajou.ac.kr";
//
//        //then
//        assertThrows(CustomIllegalArgumentException.class, () -> {
//            save(username, boardInsertDto);
//        });
//    }
//
//    /*사이트 공지사항 수정 Method*/
//    public Boards update(String username, BoardUpdateDto boardUpdateDto) {
//        Member member = memberService.findMember(username);
//
//        if (member.getRole().equals(Role.ADMIN)) {
//            Boards update = boardsService.update(boardUpdateDto);
//            log.info("사이트 공지사항을 성공적으로 수정하였습니다. Id: {}, 수정자 : {}", update.getId(), username);
//            return update;
//        } else {
//            throw new CustomIllegalArgumentException("사이트 공지사항 수정 에러", "권한이 없습니다.", member);
//        }
//    }
//
//    @Test
//    @DisplayName("사이트 공지사항 수정 테스트")
//    void updateTest() {
//        //given
//        long id = 3L;
//        Boards boards = boardsService.findById(3L);
//        String beforeTitle = boards.getTitle();
//        String beforeContent = boards.getContent();
//        String username = "repeat2487@gmail.com";
//
//        //when
//        String message = "사이트 공지사항 수정 테스트";
//        BoardUpdateDto boardUpdateDto = BoardUpdateDto.builder()
//                .id(id)
//                .title(message)
//                .content(message+" 입니다.")
//                .build();
//        Boards update = update(username, boardUpdateDto);
//
//        //then
//        log.info(update.toString());
//        assertThat(update.getId()).isEqualTo(id);
//        assertThat(update.getMember()).isEqualTo(boards.getMember());
//        assertThat(update.getTitle()).isNotEqualTo(beforeTitle);
//        assertThat(update.getContent()).isNotEqualTo(beforeContent);
//    }
//
//    /*사이트 공지사항 삭제 Method*/
//    public void delete(String username,long id) {
//        Member member = memberService.findMember(username);
//
//        if (member.getRole().equals(Role.ADMIN)) {
//            boardsService.delete(id);
//            log.info("사이트 공지사항을 성공적으로 삭제하였습니다. Id: {}, 수정자 : {}", id, username);
//        } else {
//            throw new CustomIllegalArgumentException("사이트 공지사항 수정 에러", "권한이 없습니다.", member);
//        }
//    }
//
//    @Test
//    @DisplayName("사이트 공지사항 삭제 테스트")
//    void deleteTest() {
//        //given
//        long id = 3L;
//        String username = "repeat2487@gmail.com";
//
//        //when
//        delete(username, id);
//
//        //then
//        assertThrows(IllegalArgumentException.class, () -> {
//            boardsService.findById(3L);
//        });
//    }
//}