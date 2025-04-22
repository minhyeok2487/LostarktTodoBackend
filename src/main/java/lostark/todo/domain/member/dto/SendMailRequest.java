package lostark.todo.domain.member.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SendMailRequest {

    @ApiModelProperty(notes = "메일")
    @Email(message = "올바른 이메일 형식을 입력해주세요.")
    String mail;
}
