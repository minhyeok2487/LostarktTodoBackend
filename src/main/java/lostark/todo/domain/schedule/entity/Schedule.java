package lostark.todo.domain.schedule.entity;

import lombok.*;
import lostark.todo.domain.schedule.dto.CreateScheduleRequest;
import lostark.todo.domain.schedule.dto.EditScheduleRequest;
import lostark.todo.domain.schedule.enums.ScheduleCategory;
import lostark.todo.domain.schedule.enums.ScheduleRaidCategory;
import lostark.todo.global.entity.BaseTimeEntity;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

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

    private DayOfWeek dayOfWeek;

    private LocalTime time;

    @Column(length = 300)
    private String memo;

    private boolean repeatWeek;

    private boolean leader;

    private long leaderScheduleId;

    private boolean checked;

    private LocalDate date; // 주간 반복이 아닐때 날짜

    @ColumnDefault("true")
    private boolean autoCheck;

    public static Schedule toEntity(CreateScheduleRequest request, Long characterId, Long leaderScheduleId, boolean isLeader) {
        return Schedule.builder()
                .characterId(characterId)
                .scheduleRaidCategory(request.getScheduleRaidCategory())
                .scheduleCategory(request.getScheduleCategory())
                .raidName(request.getRaidName())
                .raidLevel(request.getRaidLevel())
                .dayOfWeek(request.getDayOfWeek())
                .time(request.getTime())
                .memo(request.getMemo())
                .repeatWeek(request.isRepeatWeek())
                .leader(isLeader)
                .leaderScheduleId(leaderScheduleId)
                .date(request.getDate())
                .autoCheck(request.isAutoCheck())
                .build();
    }

    public static Schedule toEntityOfMain(Schedule main, Long characterId) {
        return Schedule.builder()
                .characterId(characterId)
                .scheduleRaidCategory(main.getScheduleRaidCategory())
                .scheduleCategory(main.getScheduleCategory())
                .raidName(main.getRaidName())
                .raidLevel(main.getRaidLevel())
                .dayOfWeek(main.getDayOfWeek())
                .time(main.getTime())
                .memo(main.getMemo())
                .repeatWeek(main.isRepeatWeek())
                .leader(false)
                .leaderScheduleId(main.id)
                .date(main.date)
                .autoCheck(main.isAutoCheck())
                .build();
    }

    public void edit(EditScheduleRequest request) {
        if (request.getDayOfWeek() != null) {
            this.dayOfWeek = request.getDayOfWeek();
        }
        if (request.getTime() != null) {
            this.time = request.getTime();
        }
        if (request.getMemo() != null) {
            this.memo = request.getMemo();
        }
        if (request.getDate() != null) {
            this.date = request.getDate();
        }
        this.autoCheck = request.isAutoCheck();
    }
}
