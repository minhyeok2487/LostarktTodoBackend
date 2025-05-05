package lostark.todo.domain.admin.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SearchAdminMemberResponse {

    private long memberId;

    private String username;

    private LocalDateTime createdDate;

    private String authProvider;

    private String mainCharacter;

    private String apiKey;

    @QueryProjection
    public SearchAdminMemberResponse(long memberId, String username, LocalDateTime createdDate,
                                     String authProvider, String mainCharacter, String apiKey) {
        this.memberId = memberId;
        this.username = username;
        this.createdDate = createdDate;
        this.authProvider = authProvider;
        this.mainCharacter = mainCharacter;
        this.apiKey = apiKey;
    }
}
