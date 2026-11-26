package co.com.bancolombia.model.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    REQUIRED_FIELD("USR-001", "Campo obligatorio faltante o vacío"),
    INVALID_EMAIL_FORMAT("USR-002", "El formato del correo electrónico no es válido"),
    INVALID_SALARY_RANGE("USR-003", "El salario base debe estar entre 0 y 15000000"),
    EMAIL_ALREADY_REGISTERED("USR-004", "El correo electrónico ya se encuentra registrado");

    private final String code;
    private final String defaultMessage;
}