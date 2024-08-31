package lostark.todo.service;

import lostark.todo.controller.dtoV2.recruitingBoard.CreateRecruitingBoardRequest;
import lostark.todo.domain.member.Member;
import lostark.todo.domain.member.MemberRepository;
import lostark.todo.domain.recruitingBoard.ExpeditionSettingEnum;
import lostark.todo.domain.recruitingBoard.RecruitingBoard;
import lostark.todo.domain.recruitingBoard.RecruitingCategoryEnum;
import lostark.todo.domain.recruitingBoard.TimeCategoryEnum;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class RecruitingBoardServiceTest {

    @Autowired
    private RecruitingBoardService recruitingBoardService;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("모집 게시판 글 작성 성공")
    void createRecruitingBoard_Success() {
        // given
        CreateRecruitingBoardRequest request = new CreateRecruitingBoardRequest();
        request.setShowMainCharacter(true);
        request.setExpeditionSetting(ExpeditionSettingEnum.MAIN_CHARACTER);
        request.setWeekdaysPlay(List.of(TimeCategoryEnum.DAY, TimeCategoryEnum.NIGHT));
        request.setWeekendsPlay(List.of(TimeCategoryEnum.DAY, TimeCategoryEnum.NIGHT, TimeCategoryEnum.DAWN));
        request.setRecruitingCategory(RecruitingCategoryEnum.FRIENDS);
        request.setBody("게시글 작성 테스트");
        request.setUrl1("asdasd");

        Member member = memberRepository.get("repeat2487@gmail.com").orElseThrow();

        // when
        RecruitingBoard recruitingBoard = recruitingBoardService.create(member, request);

        // then
        Assertions.assertThat(recruitingBoard.getId()).isNotNull();
        Assertions.assertThat(recruitingBoard.getMember()).isEqualTo(member);
        Assertions.assertThat(recruitingBoard.getWeekdaysPlay()).isEqualTo(request.getWeekdaysPlay().toString());
    }
}