package lostark.todo.domain.member.infra;

import com.google.common.annotations.VisibleForTesting;
import lostark.todo.global.exhandler.exceptions.ConditionNotMetException;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

import static lostark.todo.global.exhandler.ErrorMessageConstants.EMAIL_REGISTRATION_IN_PROGRESS;

@Component
public class MemberLockManager {
    private final ConcurrentHashMap<String, Boolean> locks = new ConcurrentHashMap<>();

    public MemberLock acquireLock(String username) {
        return new MemberLock(locks, username);
    }

    @VisibleForTesting // 테스트 코드용
    public void setLock(String username) {
        locks.put(username, true);
    }

    @VisibleForTesting
    public boolean isLocked(String username) {
        return locks.containsKey(username);
    }

    public static class MemberLock implements AutoCloseable {
        private final ConcurrentHashMap<String, Boolean> locks;
        private final String username;

        public MemberLock(ConcurrentHashMap<String, Boolean> locks, String username) {
            this.locks = locks;
            this.username = username;
            if (locks.putIfAbsent(username, true) != null) {
                throw new ConditionNotMetException(EMAIL_REGISTRATION_IN_PROGRESS);
            }
        }

        @Override
        public void close() {
            locks.remove(username);
        }
    }
}
