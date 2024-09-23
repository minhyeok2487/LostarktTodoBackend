package lostark.todo.domainV2.board.recrutingBoard.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import lostark.todo.domain.character.Character;
import lostark.todo.domainV2.board.recrutingBoard.dto.CreateRecruitingBoardRequest;
import lostark.todo.domainV2.board.recrutingBoard.dto.UpdateRecruitingBoardRequest;
import lostark.todo.domain.BaseTimeEntity;
import lostark.todo.domain.member.Member;
import lostark.todo.domainV2.board.recrutingBoard.enums.ExpeditionSettingEnum;
import lostark.todo.domainV2.board.recrutingBoard.enums.RecruitingCategoryEnum;
import lostark.todo.domainV2.board.recrutingBoard.enums.TimeCategoryEnum;

import javax.persistence.*;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    private ExpeditionSettingEnum expeditionSetting;

    private String weekdaysPlay;

    private String weekendsPlay;

    @Enumerated(EnumType.STRING)
    private RecruitingCategoryEnum recruitingCategory;

    private String url1;

    private String url2;

    private String url3;

    private int showCount;

    @OneToMany(mappedBy = "recruitingBoard", cascade = {CascadeType.ALL}, orphanRemoval=true)
    @JsonManagedReference
    private List<RecruitingBoardImages> boardImages;

    public static RecruitingBoard toEntity(Member member, CreateRecruitingBoardRequest request) {
        return RecruitingBoard.builder()
                .member(member)
                .title(request.getTitle())
                .body(request.getBody())
                .showMainCharacter(request.getShowMainCharacter())
                .expeditionSetting(request.getExpeditionSetting())
                .weekdaysPlay(request.getWeekdaysPlay().stream()
                        .map(TimeCategoryEnum::toString) // 또는 .name(), 필요에 따라 선택
                        .collect(Collectors.joining(",")))
                .weekendsPlay(request.getWeekendsPlay().stream()
                        .map(TimeCategoryEnum::toString)
                        .collect(Collectors.joining(",")))
                .recruitingCategory(request.getRecruitingCategory())
                .url1(!request.getUrl().isEmpty() ? request.getUrl().get(0) : null)
                .url2(request.getUrl().size() > 1 ? request.getUrl().get(1) : null)
                .url3(request.getUrl().size() > 2 ? request.getUrl().get(2) : null)
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
        this.expeditionSetting = request.getExpeditionSetting();
        this.weekdaysPlay = request.getWeekdaysPlay().toString();
        this.weekendsPlay = request.getWeekendsPlay().toString();
        this.url1 = request.getUrl1();
        this.url2 = request.getUrl2();
        this.url3 = request.getUrl3();
    }

    public RecruitingBoard addImages(RecruitingBoardImages image) {
        boardImages.add(image);
        image.setRecruitingBoard(this);
        return this;
    }

    // 세팅에 따라 표시할 아이템레벨 계산
    public double calculateDisplayItemLevel(String mainCharacter) {
        List<Character> characters = this.getMember().getCharacters();

        return switch (this.getExpeditionSetting()) {
            case MAIN_CHARACTER -> getMainCharacterItemLevel(characters, mainCharacter);
            case AVG_GOLD_CHARACTER -> calculateAverageGoldCharacterItemLevel(characters);
            case AVG_ALL_CHARACTER -> calculateAverageItemLevel(characters);
            default -> 0.0;
        };
    }

    private double getMainCharacterItemLevel(List<Character> characters, String mainCharacter) {
        return characters.stream()
                .filter(character -> character.getCharacterName().equals(mainCharacter))
                .findFirst()
                .map(Character::getItemLevel)
                .orElse(0.0);
    }

    private double calculateAverageGoldCharacterItemLevel(List<Character> characters) {
        return calculateAverageItemLevel(
                characters.stream().filter(Character::isGoldCharacter).toList()
        );
    }

    private double calculateAverageItemLevel(List<Character> characters) {
        return characters.stream()
                .mapToDouble(Character::getItemLevel)
                .average()
                .orElse(0.0);
    }

    // 작성자 캐릭터 닉네임 - 비공개 설정시 null로 변경
    public String getDisplayCharacterName() {
        if (!this.isShowMainCharacter()) {
            return null;
        }
        return determineMainCharacter();
    }

    public String determineMainCharacter() {
        String mainCharacter = this.getMember().getMainCharacterName();
        if (mainCharacter != null) {
            return mainCharacter;
        }
        return this.getMember().getCharacters().get(0).getCharacterName();
    }

    public List<String> createUrlList() {
        return Stream.of(this.getUrl1(), this.getUrl2(), this.getUrl3())
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public List<TimeCategoryEnum> createWeekdaysPlay() {
        return Arrays.stream(this.getWeekdaysPlay().split(","))
                .map(TimeCategoryEnum::valueOf)
                .collect(Collectors.toList());
    }


    public List<TimeCategoryEnum> createWeekendsPlay() {
        return Arrays.stream(this.getWeekendsPlay().split(","))
                .map(TimeCategoryEnum::valueOf)
                .collect(Collectors.toList());
    }

    public boolean checkAuthDelete(Member member) {
        if (member == null) {
            return false;
        }
        return member.getId() == this.getMember().getId();
    }
}
