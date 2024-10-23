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
public class CommunityLike extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "community_like_id")
    private long id;

    private long memberId;

    private long communityId;
}
