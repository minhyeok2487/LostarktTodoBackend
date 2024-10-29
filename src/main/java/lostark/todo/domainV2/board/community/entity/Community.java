package lostark.todo.domainV2.board.community.entity;

import lombok.*;
import lostark.todo.domain.BaseTimeEntity;
import lostark.todo.domain.member.Member;
import lostark.todo.domainV2.board.community.dto.CommunitySaveRequest;

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

    public static Community toEntity(Member member, CommunitySaveRequest request) {
        return Community.builder()
                .memberId(member.getId())
                .name(request.isShowName() ? member.getMainCharacterName() : createName(member))
                .showName(request.isShowName())
                .body(request.getBody())
                .category(request.getCategory())
                .likeCount(0)
                .rootParentId(request.getRootParentId())
                .commentParentId(request.getCommentParentId())
                .build();
    }

    public static String createName(Member member) {
        return "익명의 " + member.getCharacters().get(0).getCharacterClassName() + member.getId();
    }
}
