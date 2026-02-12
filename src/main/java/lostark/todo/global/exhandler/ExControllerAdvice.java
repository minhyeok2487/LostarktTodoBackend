package lostark.todo.global.exhandler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.global.exhandler.exceptions.ConditionNotMetException;
import lostark.todo.global.service.webHook.WebHookService;
import org.apache.catalina.connector.ClientAbortException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
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

    // InvalidDataAccessApiUsageException 중 ConditionNotMetException이 원인인 경우 (web hook 미전송)
    @ExceptionHandler(InvalidDataAccessApiUsageException.class)
    public ResponseEntity<ErrorResponse> handleInvalidDataAccessApiUsageException(InvalidDataAccessApiUsageException ex, HttpServletRequest request) {
        if (ex.getCause() instanceof ConditionNotMetException cause) {
            return handleExceptionInternal(cause, request, false);
        }
        return handleExceptionInternal(ex, request, true);
    }

    // 클라이언트 연결 종료 예외 처리 (web hook 미전송, 응답 불필요)
    @ExceptionHandler(ClientAbortException.class)
    public void handleClientAbortException(ClientAbortException ex, HttpServletRequest request) {
        // 클라이언트가 연결을 끊었으므로 응답을 보낼 수 없음 - 무시
        log.debug("{} {} - Client disconnected", request.getMethod(), request.getRequestURI());
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

            // callEvent 호출 (비동기, 실패해도 응답에 영향 없음)
            try {
                webHookService.callEvent(ex, updatedRequestInfo);
            } catch (Exception webhookEx) {
                log.warn("Webhook 호출 실패: {}", webhookEx.getMessage());
            }
        } else {
            log.warn("{} - {}", requestInfo, ex.getMessage());
        }

        ErrorResponse errorResponse = ErrorResponse.of(
                HttpStatus.BAD_REQUEST.value(), ex.getClass().getSimpleName(), ex.getLocalizedMessage()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

}

