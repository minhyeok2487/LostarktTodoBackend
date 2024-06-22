package lostark.todo.controller.adminDto;

import com.querydsl.core.annotations.QueryProjection;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class DashboardMemberResponse {

    @ApiModelProperty(example = "날짜")
    private String date;

    @ApiModelProperty(example = "가입자 수")
    private int count;

    @QueryProjection
    public DashboardMemberResponse(String date, int count) {
        this.date = date;
        this.count = count;
    }
}