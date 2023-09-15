package lostark.todo.domain.content;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
public class WeekContent extends Content{

    private String weekCategory; //카테고리 : 발탄, 비아키스

    private int gate; //관문

    private double honorShard; //명파

    private double leapStone; //돌파석

    private double destructionStone; //파괴석

    private double guardianStone; //수호석

    private int gold; //골드
}
