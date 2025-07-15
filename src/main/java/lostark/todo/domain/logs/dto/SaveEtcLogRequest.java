package lostark.todo.domain.logs.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class SaveEtcLogRequest {

    @NotNull
    private Long characterId;

    @NotNull
    private LocalDate localDate;

    @NotBlank
    private String message;

    @NotNull
    private double profit;
}