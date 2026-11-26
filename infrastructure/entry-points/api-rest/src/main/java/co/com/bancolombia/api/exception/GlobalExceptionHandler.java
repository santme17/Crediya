package co.com.bancolombia.api.exception;

import co.com.bancolombia.model.exception.BusinessException;
import co.com.bancolombia.model.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Log4j2
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(
            BusinessException ex,
            HttpServletRequest request) {
        HttpStatus status = ex.getErrorCode() == ErrorCode.EMAIL_ALREADY_REGISTERED
                ? HttpStatus.CONFLICT
                : HttpStatus.BAD_REQUEST;

        log.warn("Error de negocio en {} [{}]: {}", request.getRequestURI(), ex.getErrorCode().getCode(), ex.getMessage());

        return buildResponse(status, ex.getErrorCode().getCode(), ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadableMessage(
            HttpMessageNotReadableException ex,
            HttpServletRequest request) {
        log.warn("Formato de petición inválido en {}", request.getRequestURI());

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "REQ-001",
                "El formato de los datos enviados es inválido. Verifique los tipos de campo (ej. salario_base numérico o fecha_nacimiento YYYY-MM-DD).",
                request.getRequestURI()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedException(
            Exception ex,
            HttpServletRequest request) {
        log.error("Error inesperado procesando petición en {}", request.getRequestURI(), ex);

        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "SRV-500",
                "Ocurrió un error interno al procesar la solicitud. Por favor intente nuevamente más tarde.",
                request.getRequestURI()
        );
    }

    private ResponseEntity<ErrorResponse> buildResponse(
            HttpStatus status,
            String code,
            String message,
            String path) {
        ErrorResponse body = ErrorResponse.builder()
                .code(code)
                .message(message)
                .timestamp(LocalDateTime.now())
                .path(path)
                .build();
        return ResponseEntity.status(status).body(body);
    }
}