package lostark.todo.domain.keyvalue;

import lombok.*;
import lostark.todo.domain.BaseTimeEntity;

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
