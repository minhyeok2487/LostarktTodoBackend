package lostark.todo.domain.content.entity;

import lombok.*;
import lombok.experimental.SuperBuilder;
import lostark.todo.domain.content.enums.Category;

import javax.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class Content {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "content_id")
    private long id;

    @Enumerated(EnumType.STRING)
    private Category category;

    private String name;

    private double level; //컨텐츠 레벨

}
