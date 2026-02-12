-- ShedLock 테이블: 스케줄러 분산 락
-- 배포 시 인스턴스가 2개 이상 동시에 뜰 때 스케줄 작업이 중복 실행되는 것을 방지
CREATE TABLE IF NOT EXISTS shedlock (
    name       VARCHAR(64)  NOT NULL,
    lock_until TIMESTAMP(3) NOT NULL,
    locked_at  TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    locked_by  VARCHAR(255) NOT NULL,
    PRIMARY KEY (name)
);
