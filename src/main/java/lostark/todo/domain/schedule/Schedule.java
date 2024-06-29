package lostark.todo.domain.schedule;

import lombok.*;
import lostark.todo.controller.dtoV2.schedule.CreateScheduleRequest;
import lostark.todo.domain.BaseTimeEntity;
import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Schedule extends BaseTimeEntity {

    // 스케줄 테이블
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id")
    private long id;

    @Column(nullable = false)
    private long characterId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ScheduleRaidCategory scheduleRaidCategory;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ScheduleCategory scheduleCategory;

    private String raidName;

    private String raidLevel;

    private LocalDateTime time;

    @Column(length = 300)
    private String memo;

    private int repeatDay;

    private boolean leader;


    public static Schedule toEntity(CreateScheduleRequest request, Long characterId, boolean isLeader) {
        return Schedule.builder()
                .characterId(characterId)
                .scheduleRaidCategory(request.getScheduleRaidCategory())
                .scheduleCategory(request.getScheduleCategory())
                .raidName(request.getRaidName())
                .raidLevel(request.getRaidLevel())
                .time(request.getTime())
                .memo(request.getMemo())
                .repeatDay(request.getRepeatDay())
                .leader(isLeader)
                .build();
    }
}
