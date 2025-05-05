package lostark.todo.domain.admin.dto;

import lombok.Data;

@Data
public class UpdateAdsDateRequest {

    private String proposerEmail;

    private long price;
}