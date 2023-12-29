package lostark.todo.controller.dto.mailDto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MailRequestDto {

    @ApiModelProperty(notes = "메일")
    String mail;
}
