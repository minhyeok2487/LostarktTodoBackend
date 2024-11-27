package lostark.todo.global.keyvalue;

import lombok.*;
import lostark.todo.global.entity.BaseTimeEntity;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class KeyValue extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "key_id")
    private long id;

    @Column(length = 1000)
    private String keyName;

    @Column(length = 1000)
    private String keyValue;
}
