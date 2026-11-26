package co.com.bancolombia.usecase.user;

import co.com.bancolombia.model.exception.BusinessException;
import co.com.bancolombia.model.exception.ErrorCode;
import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public class UserUseCase {

    private static final BigDecimal MIN_SALARY = BigDecimal.ZERO;
    private static final BigDecimal MAX_SALARY = new BigDecimal("15000000");
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*\\.[A-Za-z]{2,}$");

    private final UserRepository userRepository;

    public User registerUser(User user) {
        validateMandatoryFields(user);
        String normalizedEmail = user.getCorreoElectronico().trim().toLowerCase();
        validateEmailFormat(normalizedEmail);
        validateSalaryRange(user.getSalarioBase());
        validateUniqueEmail(normalizedEmail);

        User userToSave = user.toBuilder()
                .nombres(user.getNombres().trim())
                .apellidos(user.getApellidos().trim())
                .correoElectronico(normalizedEmail)
                .build();

        return userRepository.save(userToSave);
    }

    private void validateMandatoryFields(User user) {
        if (user == null) {
            throw new BusinessException(ErrorCode.REQUIRED_FIELD, "Los datos del solicitante son obligatorios");
        }
        if (isBlank(user.getNombres())) {
            throw new BusinessException(ErrorCode.REQUIRED_FIELD, "El campo nombres es obligatorio");
        }
        if (isBlank(user.getApellidos())) {
            throw new BusinessException(ErrorCode.REQUIRED_FIELD, "El campo apellidos es obligatorio");
        }
        if (isBlank(user.getCorreoElectronico())) {
            throw new BusinessException(ErrorCode.REQUIRED_FIELD, "El campo correo_electronico es obligatorio");
        }
        if (user.getSalarioBase() == null) {
            throw new BusinessException(ErrorCode.REQUIRED_FIELD, "El campo salario_base es obligatorio");
        }
    }

    private void validateEmailFormat(String email) {
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new BusinessException(ErrorCode.INVALID_EMAIL_FORMAT, "El correo electrónico no tiene un formato válido");
        }
    }

    private void validateSalaryRange(BigDecimal salary) {
        if (salary.compareTo(MIN_SALARY) < 0 || salary.compareTo(MAX_SALARY) > 0) {
            throw new BusinessException(
                    ErrorCode.INVALID_SALARY_RANGE,
                    "El salario_base debe estar entre 0 y 15000000"
            );
        }
    }

    private void validateUniqueEmail(String email) {
        if (userRepository.existsByCorreoElectronico(email)) {
            throw new BusinessException(
                    ErrorCode.EMAIL_ALREADY_REGISTERED,
                    "Ya existe un solicitante registrado con el correo electrónico proporcionado"
            );
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}