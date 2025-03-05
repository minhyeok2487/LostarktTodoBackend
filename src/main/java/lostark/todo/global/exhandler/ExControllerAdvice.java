package lostark.todo.global.exhandler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.global.exhandler.exceptions.ConditionNotMetException;
import lostark.todo.global.service.webHook.WebHookService;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class ExControllerAdvice {

    private final WebHookService webHookService;

    // 요구 조건이 충족되지 않아서 발생하는 예외처리 (web hook 미전송)
    @ExceptionHandler(ConditionNotMetException.class)
    public ResponseEntity<ErrorResponse> handlerConditionNotMetException(ConditionNotMetException ex, HttpServletRequest request) {
        return handleExceptionInternal(ex, request, false);
    }

    // 유효성 검사 예외 처리 (web hook 미전송)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorListResponse> handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String requestInfo = String.format("%s %s", request.getMethod(), request.getRequestURI());

        log.warn("{} - {}", requestInfo, ex.getMessage());

        List<String> messages = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> String.format("%s 입력된 값: [%s]", fieldError.getDefaultMessage(), fieldError.getRejectedValue()))
                .toList();

        ErrorListResponse errorListResponse = ErrorListResponse.of(
                HttpStatus.BAD_REQUEST.value(), ex.getClass().getSimpleName(), messages
        );

        return new ResponseEntity<>(errorListResponse, HttpStatus.BAD_REQUEST);
    }

    // 그 외 오류 (web hook 전송)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handlerException(Exception ex, HttpServletRequest request) {
        return handleExceptionInternal(ex, request, true);
    }

    // 공통 예외 처리 메서드
    private ResponseEntity<ErrorResponse> handleExceptionInternal(Exception ex, HttpServletRequest request, boolean sendWebHook) {
        String requestInfo = String.format("%s %s", request.getMethod(), request.getRequestURI());

        if (sendWebHook) {
            // 에러 로그 먼저 호출
            log.error("{} - {}", requestInfo, ex.getMessage());

            // requestInfo에 헤더를 추가하기 위한 StringBuilder
            StringBuilder headerDetails = new StringBuilder();

            // 헤더 수집 및 로그 출력
            Collections.list(request.getHeaderNames())
                    .forEach(headerName -> {
                        String headerValue = request.getHeader(headerName);
                        headerDetails.append(String.format("Header [%s] = %s%n", headerName, headerValue));
                    });

            // 기존 requestInfo에 헤더 정보 추가
            String updatedRequestInfo = requestInfo + "\n" + headerDetails;

            // callEvent 호출
            webHookService.callEvent(ex, updatedRequestInfo);
        } else {
            log.warn("{} - {}", requestInfo, ex.getMessage());
        }

        ErrorResponse errorResponse = ErrorResponse.of(
                HttpStatus.BAD_REQUEST.value(), ex.getClass().getSimpleName(), ex.getLocalizedMessage()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

}

