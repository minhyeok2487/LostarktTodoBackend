package lostark.todo.controller.dtoV2.recruitingBoard;

import lombok.Data;

@Data
public class CreateRecruitingBoardResponse {

    private long recruitingBoardId;

    public CreateRecruitingBoardResponse(long id) {
        this.recruitingBoardId = id;
    }
}
