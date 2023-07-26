package lostark.todo.controller.dto.contentDto;

import lombok.Data;

@Data
public class SortedDayContentProfitDto {

    private String characterName;

    private String category;

    private String contentName;

    private int checked;

    private double profit;
}
