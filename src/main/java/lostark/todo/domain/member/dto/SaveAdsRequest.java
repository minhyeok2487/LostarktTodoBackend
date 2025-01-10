package lostark.todo.domain.member.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaveAdsRequest {

    @NotEmpty
    @ApiModelProperty(example = "신청 이메일")
    private String mail;

    @NotNull
    @ApiModelProperty(example = "입금자 이름")
    private String name;
}
