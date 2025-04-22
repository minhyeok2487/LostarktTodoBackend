package lostark.todo.admin.dto;

import lombok.Data;

@Data
public class UpdateAdsDateRequest {

    private String proposerEmail;

    private long price;
}