package lostark.todo.domain.board.community.entity;

import lombok.*;
import lostark.todo.global.entity.BaseTimeEntity;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class CommunityImages extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "community_images_id")
    private long id;

    private long communityId;

    @Column(length = 1000)
    private String url;

    @Column(length = 1000)
    private String fileName;

    @Column()
    private long ordering;

    @Column()
    private boolean deleted = false;

    public void update(long communityId, int i) {
        this.communityId = communityId;
        this.ordering = i;
    }
}
