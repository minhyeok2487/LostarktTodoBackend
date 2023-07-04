package lostark.todo.domain.content;

import lombok.Data;

import javax.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Data
public abstract class Content {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "content_id")
    private long id;

    @Enumerated(EnumType.STRING)
    private Category category; // 일일 숙제, 주간 숙제 분류

    private String name;

    private int level; //컨텐츠 레벨

}
