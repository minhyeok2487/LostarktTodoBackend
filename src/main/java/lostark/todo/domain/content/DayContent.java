package lostark.todo.domain.content;

import lombok.*;
import lombok.experimental.SuperBuilder;
import lostark.todo.domain.character.DayTodo;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@SuperBuilder
@Entity

public class DayContent extends Content{

    private double shilling; //실링

    private double honorShard; //명파

    private double leapStone; //돌파석

    private double destructionStone; //파괴석

    private double guardianStone; //수호석

    private double jewelry; //1레벨 보석
}
