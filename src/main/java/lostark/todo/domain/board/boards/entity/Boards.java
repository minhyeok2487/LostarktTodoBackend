package lostark.todo.domain.board.boards.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import lostark.todo.global.entity.BaseTimeEntity;
import lostark.todo.domain.member.entity.Member;
import javax.persistence.*;
import java.util.List;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Boards extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "boards_id")
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @JsonBackReference //순환참조 방지
    private Member member;

    private String title;

    private String content;

    private boolean isNotice;

    private int views;

    @OneToMany(mappedBy = "boards", cascade = {CascadeType.ALL}, orphanRemoval=true)
    @JsonManagedReference
    private List<BoardImages> boardImages;


    @Override
    public String toString() {
        return "Boards{" +
                "id=" + id +
                ", member=" + member.getUsername() +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", isNotice=" + isNotice +
                ", views=" + views +
                '}';
    }

    // board 엔티티에 boardImage 리스트 저장
    public Boards addImages(BoardImages image) {
        boardImages.add(image);
        image.setBoards(this);
        return this;
    }
}
