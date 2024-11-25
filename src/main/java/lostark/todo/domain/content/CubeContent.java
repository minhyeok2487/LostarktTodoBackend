package lostark.todo.domain.content;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@ToString
@SuperBuilder
public class CubeContent extends Content{

    private double jewelry; //1레벨 보석

    private double leapStone; //돌파석

    private double shilling; //실링

    private double solarGrace; //태양의 은총

    private double solarBlessing; //태양의 축복

    private double solarProtection; //태양의 가호

    private double cardExp;
}
