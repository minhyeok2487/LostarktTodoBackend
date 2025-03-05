package lostark.todo.domain.util.cube.entity;

import lombok.*;
import lostark.todo.domain.util.cube.dto.CubeUpdateRequest;
import lostark.todo.domain.util.cube.enums.CubeContentName;
import lostark.todo.global.exhandler.exceptions.ConditionNotMetException;

import javax.persistence.*;
import java.lang.reflect.Field;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Cubes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cubes_id")
    private long id;

    private long characterId;

    private int ban1;

    private int ban2;

    private int ban3;

    private int ban4;

    private int ban5;

    private int unlock1;

    private int unlock2;

    private int unlock3;

    public static Cubes toEntity(long characterId) {
        return Cubes.builder()
                .characterId(characterId)
                .ban1(0)
                .ban2(0)
                .ban3(0)
                .ban4(0)
                .ban5(0)
                .unlock1(0)
                .unlock2(0)
                .unlock3(0)
                .build();
    }

    public Cubes update(CubeUpdateRequest request) {
        this.ban1 = request.getBan1();
        this.ban2 = request.getBan2();
        this.ban3 = request.getBan3();
        this.ban4 = request.getBan4();
        this.ban5 = request.getBan5();
        this.unlock1 = request.getUnlock1();
        this.unlock2 = request.getUnlock2();
        this.unlock3 = request.getUnlock3();
        return this;
    }

    public void spend(CubeContentName cubeContentName) {
        String name = cubeContentName.getVariable();
        try {
            Field field = getClass().getDeclaredField(name);
            field.setAccessible(true); // 필드에 접근할 수 있도록 설정
            int value = field.getInt(this);
            if (value < 1) {
                throw new ConditionNotMetException("소모할 수 있는 티켓이 없습니다.");
            }
            field.set(this, value - 1);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new ConditionNotMetException("존재하지 않는 필드이거나 접근할 수 없는 필드입니다. " + name);
        }
    }
}
