package lostark.todo.domain.logs;

import lombok.*;
import lombok.experimental.SuperBuilder;
import lostark.todo.domain.BaseTimeEntity;

import javax.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class Logs extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "logs_id")
    private long id;

    private String message;
}
