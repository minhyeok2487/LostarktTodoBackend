package lostark.todo.global.dto;

import lombok.Data;

import java.util.List;

@Data
public class CursorResponse<T> {

    private List<T> content;

    private boolean hasNext;

    public CursorResponse(List<T> content, boolean hasNext) {
        this.content = content;
        this.hasNext = hasNext;
    }
}
