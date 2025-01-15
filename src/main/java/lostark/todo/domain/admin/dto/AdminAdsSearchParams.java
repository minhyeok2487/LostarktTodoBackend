package lostark.todo.domain.admin.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AdminAdsSearchParams {

    @ApiModelProperty(name = "후원 ID, 첫 글이면 X")
    private Long adsId;
}
