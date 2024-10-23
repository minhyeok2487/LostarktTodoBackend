package lostark.todo.domainV2.board.community.entity;

import lombok.*;
import lostark.todo.domain.BaseTimeEntity;
import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Community extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "community_id")
    private long id;

    private long memberId;

    @Column(length = 100)
    private String name;

    @Column()
    private boolean showName;

    @Column(length = 1000)
    private String body;

    @Enumerated(EnumType.STRING)
    private CommunityCategory category;

    @Column()
    private int likeCount;

    @Column()
    private long rootParentId;

    @Column()
    private long commentParentId;
}
