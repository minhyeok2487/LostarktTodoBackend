package lostark.todo.domainMyGame.myevent.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;
import lostark.todo.domainMyGame.mygame.entity.MyGame;
import lostark.todo.global.entity.BaseTimeEntity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "my_event")
public class MyEvent extends BaseTimeEntity {

    @Id
    @Column(name = "my_event_id", length = 100)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "my_game_id")
    @JsonBackReference
    private MyGame game;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 2000)
    private String description;

    @Column(length = 50)
    private String type;

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = false)
    private LocalDateTime endDate;

    @Column(length = 500)
    private String image;

    @Column(length = 200)
    private String location;
}
