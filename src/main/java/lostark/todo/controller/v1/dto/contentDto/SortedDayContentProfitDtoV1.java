package lostark.todo.controller.v1.dto.contentDto;

import lombok.Data;

@Data
public class SortedDayContentProfitDtoV1 {

    private String characterName;

    private String category;

    private String contentName;

    private int checked;

    private double profit;
}
