package lostark.todo.domain.recruitingBoard;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;
import lostark.todo.controller.dtoV2.recruitingBoard.CreateRecruitingBoardRequest;
import lostark.todo.controller.dtoV2.recruitingBoard.UpdateRecruitingBoardRequest;
import lostark.todo.domain.BaseTimeEntity;
import lostark.todo.domain.member.Member;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@ToString
public class RecruitingBoard extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recruiting_board_id")
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @JsonBackReference //순환참조 방지
    private Member member;

    private String title;

    @Column(length = 5000)
    private String body;

    private boolean showMainCharacter;

    @Enumerated(EnumType.STRING)
    private ExpeditionSettingEnum ExpeditionSetting;

    private String weekdaysPlay;

    private String weekendsPlay;

    @Enumerated(EnumType.STRING)
    private RecruitingCategoryEnum recruitingCategory;

    private String url1;

    private String url2;

    private String url3;

    private int showCount;

    public static RecruitingBoard toEntity(Member member, CreateRecruitingBoardRequest request) {
        return RecruitingBoard.builder()
                .member(member)
                .title(request.getTitle())
                .body(request.getBody())
                .showMainCharacter(request.getShowMainCharacter())
                .ExpeditionSetting(request.getExpeditionSetting())
                .weekdaysPlay(request.getWeekdaysPlay().toString())
                .weekendsPlay(request.getWeekendsPlay().toString())
                .recruitingCategory(request.getRecruitingCategory())
                .url1(request.getUrl1())
                .url2(request.getUrl2())
                .url3(request.getUrl3())
                .showCount(0)
                .build();
    }

    public void upShowCount() {
        this.showCount++;
    }

    public void update(UpdateRecruitingBoardRequest request) {
        this.title = request.getTitle();
        this.body = request.getBody();
        this.showMainCharacter = request.getShowMainCharacter();
        this.ExpeditionSetting = request.getExpeditionSetting();
        this.weekdaysPlay = request.getWeekdaysPlay().toString();
        this.weekendsPlay = request.getWeekendsPlay().toString();
        this.url1 = request.getUrl1();
        this.url2 = request.getUrl2();
        this.url3 = request.getUrl3();
    }
}
