package lostark.todo.domain.recruitingBoard;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;
import lostark.todo.domain.BaseTimeEntity;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class RecruitingBoardImages extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recruiting_board_images_id")
    private long id;

    private String fileName;

    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruiting_board_id")
    @JsonBackReference //순환참조 방지
    private RecruitingBoard recruitingBoard;
}
