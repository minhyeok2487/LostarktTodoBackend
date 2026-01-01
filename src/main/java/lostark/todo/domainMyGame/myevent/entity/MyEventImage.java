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
@Table(name = "my_event_image")
public class MyEventImage extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "my_event_image_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "my_event_id")
    @JsonBackReference
    private MyEvent event;

    @Column(nullable = false, length = 500)
    private String url;

    @Column(length = 500)
    private String fileName;

    @Column(nullable = false)
    private Integer ordering;

    public void updateEvent(MyEvent event, int ordering) {
        this.event = event;
        this.ordering = ordering;
    }
}
