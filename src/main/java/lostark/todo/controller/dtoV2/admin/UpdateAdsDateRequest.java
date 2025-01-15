package lostark.todo.controller.dtoV2.admin;

import lombok.Data;

@Data
public class UpdateAdsDateRequest {

    private String proposerEmail;

    private long price;
}