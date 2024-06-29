package lostark.todo.domain.schedule;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleRepository extends JpaRepository<Schedule, Long>, ScheduleCustomRepository {

}
