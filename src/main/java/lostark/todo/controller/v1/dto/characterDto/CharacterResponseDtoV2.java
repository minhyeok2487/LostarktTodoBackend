package lostark.todo.controller.v1.dto.characterDto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lostark.todo.domain.character.Character;
import lostark.todo.domain.content.DayContent;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CharacterResponseDtoV2 {

    private long id;

    @ApiModelProperty(notes = "캐릭터 클래스")
    private String characterClassName;

    @ApiModelProperty(notes = "캐릭터 이미지 url")
    private String characterImage;

    @ApiModelProperty(notes = "캐릭터 이름")
    private String characterName;

    @ApiModelProperty(notes = "캐릭터 아이템 레벨")
    private double itemLevel;

    @ApiModelProperty(notes = "캐릭터 선택, false 라면 숙제 안할 캐릭, true / false")
    private boolean selected;

    @ApiModelProperty(notes = "카오스던전 숙제 할 캐릭인지 선택, true / false")
    private boolean chaosSelected;

    @ApiModelProperty(notes = "카오스던전 컨텐츠 내용")
    private DayContent chaosName;

    @ApiModelProperty(notes = "카오스던전 돌았는지 체크, 최소 0, 최대 2")
    private int chaosCheck;

    @ApiModelProperty(notes = "카오스던전 휴식게이지, 최소 0, 최대 100, 10단위 증가")
    private int chaosGauge;

    @ApiModelProperty(notes = "카오스던전 숙제 완료 시 예상 수익")
    private double chaosProfit;

    @ApiModelProperty(notes = "가디언토벌 숙제 할 캐릭인지 선택, true / false")
    private boolean guardianSelected;

    @ApiModelProperty(notes = "가디언토벌 컨텐츠 내용")
    private DayContent guardianName;

    @ApiModelProperty(notes = "가디언토벌 돌았는지 체크, 최소 0, 최대 2, 1씩 증감")
    private int guardianCheck;

    @ApiModelProperty(notes = "가디언토벌 휴식게이지, 최소 0, 최대 100, 10씩 증감")
    private int guardianGauge;

    @ApiModelProperty(notes = "가디언토벌 숙제 완료 시 예상 수익")
    private double guardianProfit;

}
