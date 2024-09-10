package lostark.todo.domainV2.board.recrutingBoard.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class GetRecruitingBoardRequest {

    @ApiModelProperty(example = "로그인 토큰")
    private String token;

}
