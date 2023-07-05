package lostark.todo.controller.dtos;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lostark.todo.domain.member.Member;

import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Data
public class CharacterSaveDto {

    private long id;

    private String characterName;

    private String characterClassName;

    private double itemLevel; //아이템레벨

    private int chaos; //일일숙제 카오스던전 돌았는지 체크(0, 1, 2)

    private int chaosGauge; //카오스던전 휴식게이지
}
