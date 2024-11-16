package lostark.todo.global.customAnnotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NotTestMember {
    String message() default "테스트 계정은 접근이 불가능합니다.";
}
