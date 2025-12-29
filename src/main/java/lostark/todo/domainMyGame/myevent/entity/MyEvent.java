package lostark.todo.domainMyGame.myevent.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import lostark.todo.domainMyGame.myevent.enums.MyEventType;
import lostark.todo.domainMyGame.mygame.entity.MyGame;
import lostark.todo.global.entity.BaseTimeEntity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "my_event")
public class MyEvent extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "my_event_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "my_game_id")
    @JsonBackReference
    private MyGame game;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 2000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private MyEventType type;

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = false)
    private LocalDateTime endDate;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    @Builder.Default
    private List<MyEventImage> images = new ArrayList<>();

    public void addImage(MyEventImage image) {
        images.add(image);
        image.setEvent(this);
    }

    public void clearImages() {
        images.clear();
    }

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    @Builder.Default
    private List<MyEventVideo> videos = new ArrayList<>();

    public void addVideo(MyEventVideo video) {
        videos.add(video);
        video.setEvent(this);
    }

    public void clearVideos() {
        videos.clear();
    }
}
