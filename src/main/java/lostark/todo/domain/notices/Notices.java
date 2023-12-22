package lostark.todo.domain.notices;

import lombok.*;
import lostark.todo.service.lostarkApi.NoticesType;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@ToString
public class Notices {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notices_id")
    private long id;

    @Enumerated(EnumType.STRING)
    private NoticesType type;

    private String title;

    private LocalDateTime date;

    private String link;
}
