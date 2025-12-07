package lostark.todo.domainMyGame.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaginationResponse {

    private long total;
    private int page;
    private int limit;
    private int totalPages;

    public static PaginationResponse from(Page<?> page) {
        return PaginationResponse.builder()
                .total(page.getTotalElements())
                .page(page.getNumber() + 1)  // 0-based to 1-based
                .limit(page.getSize())
                .totalPages(page.getTotalPages())
                .build();
    }
}
