package lostark.todo.controller.dtoV2.admin;

import lombok.Data;

@Data
public class UpdateAdsDateRequest {

    private String username;

    private long date;
}