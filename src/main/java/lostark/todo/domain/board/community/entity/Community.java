package lostark.todo.domain.board.community.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;
import lostark.todo.global.entity.BaseTimeEntity;
import lostark.todo.domain.member.entity.Member;
import lostark.todo.domain.board.community.dto.CommunitySaveRequest;
import lostark.todo.domain.character.entity.Character;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @JsonBackReference //순환참조 방지
    private Member memberId;

    private String characterImage;

    private String characterClassName;

    @Column(length = 100)
    private String name;

    @Column()
    private boolean showName;

    @Column(length = 1000)
    private String body;

    @Enumerated(EnumType.STRING)
    private CommunityCategory category;

    @Column()
    private long rootParentId;

    @Column()
    private long commentParentId;

    @Column()
    private boolean deleted = false;

    public static Community toEntity(Member member, CommunitySaveRequest request) {
        Character mainCharacter = member.getCharacters().stream()
                .filter(character -> character.getCharacterName().equals(member.getMainCharacterName()))
                .findFirst()
                .orElse(null);

        String name = request.isShowName()
                ? member.getMainCharacterName()
                : createName(member, mainCharacter != null ? mainCharacter.getCharacterClassName() : null);

        return Community.builder()
                .memberId(member)
                .characterImage(mainCharacter != null ? mainCharacter.getCharacterImage() : null)
                .characterClassName(mainCharacter != null ? mainCharacter.getCharacterClassName() : null)
                .name(name)
                .showName(request.isShowName())
                .body(request.getBody())
                .category(request.getCategory())
                .rootParentId(request.getRootParentId())
                .commentParentId(request.getCommentParentId())
                .build();
    }

    public static String createName(Member member, String characterClassName) {
        return "익명의 " + (characterClassName != null ? characterClassName : member.getCharacters().get(0).getCharacterClassName()) + " " + member.getId();
    }

    public void update(String body) {
        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime createdTime = this.getCreatedDate();

        // 현재 시간과 작성 시간의 차이를 분 단위로 계산
        long minutesDifference = ChronoUnit.MINUTES.between(createdTime, currentTime);

        if (minutesDifference > 15) {
            throw new IllegalStateException("게시글 작성 후 15분이 지나 수정할 수 없습니다.");
        }

        // 15분이 지나지 않았다면 내용 업데이트
        this.body = body;
    }

    public void delete() {
        deleted = true;
    }
}
