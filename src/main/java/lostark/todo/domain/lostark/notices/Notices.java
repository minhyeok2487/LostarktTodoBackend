package lostark.todo.domain.lostark.notices;

import lombok.*;
import lostark.todo.domain.lostark.enums.NoticesType;

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
