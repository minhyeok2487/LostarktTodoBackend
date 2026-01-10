package lostark.todo.domain.admin.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardSummaryResponse {

    private long totalMembers;
    private long totalCharacters;
    private long todayNewMembers;
    private long todayNewCharacters;
}
