package lostark.todo.controller.dtoV2.notification;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
public class GetNotificationResponse {

    @NotEmpty
    @ApiModelProperty(example = "클릭 링크")
    private String url;

}
