package lostark.todo.domainMyGame.myevent.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;
import lostark.todo.global.entity.BaseTimeEntity;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "my_event_video")
public class MyEventVideo extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "my_event_video_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "my_event_id")
    @JsonBackReference
    private MyEvent event;

    @Column(nullable = false, length = 500)
    private String url;

    @Column(nullable = false)
    private Integer ordering;
}
