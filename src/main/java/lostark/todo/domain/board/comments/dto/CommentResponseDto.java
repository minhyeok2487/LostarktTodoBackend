package lostark.todo.domain.board.comments.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lostark.todo.domain.member.enums.Role;
import lostark.todo.domain.board.comments.entity.Comments;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentResponseDto {

    private long id;

    private String body;

    private String username;

    private long parentId;

    private LocalDateTime regDate;

    private long memberId;

    private Role role;

    public CommentResponseDto createResponseDto(Comments comments) {
        return CommentResponseDto.builder()
                .id(comments.getId())
                .regDate(comments.getCreatedDate())
                .body(comments.getBody())
                .username(comments.getMember().getUsername())
                .parentId(comments.getParentId())
                .memberId(comments.getMember().getId())
                .role(comments.getMember().getRole())
                .build();
    }
}
