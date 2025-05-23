package lostark.todo.domain.member.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MailCheckRequest {

    @ApiModelProperty(notes = "유저 이메일")
    private String mail;

    @ApiModelProperty(notes = "인증키")
    private int number;
}
