package lostark.todo.global.customAnnotation;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import static lostark.todo.global.Constant.TEST_USERNAME;

@Aspect
@Component
public class NotTestMemberAspect {

    @Before("@annotation(notTestMember)")
    public void validateNotTestMember(JoinPoint joinPoint, NotTestMember notTestMember) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            if (TEST_USERNAME.equals(username)) {
                throw new IllegalStateException(notTestMember.message());
            }
        }
    }
}
