package lostark.todo.domain.admin.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdminAdsSearchResponse {

    private long adsId;

    private LocalDateTime createdDate;

    private String name;

    private String proposerEmail;

    private long memberId;

    private boolean checked;
}
