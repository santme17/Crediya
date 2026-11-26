package co.com.bancolombia.usecase.user;

import co.com.bancolombia.model.exception.BusinessException;
import co.com.bancolombia.model.exception.ErrorCode;
import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.gateways.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserUseCase userUseCase;

    private User validUser;

    @BeforeEach
    void setUp() {
        validUser = User.builder()
                .nombres("Santiago")
                .apellidos("Muñoz Estrada")
                .fechaNacimiento(LocalDate.of(2000, 5, 15))
                .direccion("Calle 10 # 20-30")
                .telefono("3001234567")
                .correoElectronico("Santiago@Correo.com")
                .salarioBase(new BigDecimal("3500000"))
                .build();
    }

    @Test
    void shouldRegisterUserSuccessfully() {
        when(userRepository.existsByCorreoElectronico("santiago@correo.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            return u.toBuilder().id(1L).build();
        });

        User result = userUseCase.registerUser(validUser);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("santiago@correo.com", result.getCorreoElectronico());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldAllowBoundarySalariesZeroAndFifteenMillion() {
        when(userRepository.existsByCorreoElectronico("santiago@correo.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User minSalaryUser = validUser.toBuilder().salarioBase(BigDecimal.ZERO).build();
        User maxSalaryUser = validUser.toBuilder().salarioBase(new BigDecimal("15000000")).build();

        assertEquals(BigDecimal.ZERO, userUseCase.registerUser(minSalaryUser).getSalarioBase());
        assertEquals(new BigDecimal("15000000"), userUseCase.registerUser(maxSalaryUser).getSalarioBase());
    }

    @Test
    void shouldThrowExceptionWhenUserIsNull() {
        BusinessException ex = assertThrows(BusinessException.class, () -> userUseCase.registerUser(null));
        assertEquals(ErrorCode.REQUIRED_FIELD, ex.getErrorCode());
    }

    @Test
    void shouldThrowExceptionWhenMandatoryFieldsAreMissing() {
        User withoutNames = validUser.toBuilder().nombres("   ").build();
        User withoutLastNames = validUser.toBuilder().apellidos(null).build();
        User withoutEmail = validUser.toBuilder().correoElectronico("").build();
        User withoutSalary = validUser.toBuilder().salarioBase(null).build();

        assertEquals(ErrorCode.REQUIRED_FIELD,
                assertThrows(BusinessException.class, () -> userUseCase.registerUser(withoutNames)).getErrorCode());
        assertEquals(ErrorCode.REQUIRED_FIELD,
                assertThrows(BusinessException.class, () -> userUseCase.registerUser(withoutLastNames)).getErrorCode());
        assertEquals(ErrorCode.REQUIRED_FIELD,
                assertThrows(BusinessException.class, () -> userUseCase.registerUser(withoutEmail)).getErrorCode());
        assertEquals(ErrorCode.REQUIRED_FIELD,
                assertThrows(BusinessException.class, () -> userUseCase.registerUser(withoutSalary)).getErrorCode());

        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenEmailFormatIsInvalid() {
        User invalidEmailUser = validUser.toBuilder().correoElectronico("correo-invalido").build();

        BusinessException ex = assertThrows(BusinessException.class, () -> userUseCase.registerUser(invalidEmailUser));

        assertEquals(ErrorCode.INVALID_EMAIL_FORMAT, ex.getErrorCode());
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenSalaryIsOutOfRange() {
        User negativeSalary = validUser.toBuilder().salarioBase(new BigDecimal("-1")).build();
        User excessiveSalary = validUser.toBuilder().salarioBase(new BigDecimal("15000001")).build();

        assertEquals(ErrorCode.INVALID_SALARY_RANGE,
                assertThrows(BusinessException.class, () -> userUseCase.registerUser(negativeSalary)).getErrorCode());
        assertEquals(ErrorCode.INVALID_SALARY_RANGE,
                assertThrows(BusinessException.class, () -> userUseCase.registerUser(excessiveSalary)).getErrorCode());

        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        when(userRepository.existsByCorreoElectronico("santiago@correo.com")).thenReturn(true);

        BusinessException ex = assertThrows(BusinessException.class, () -> userUseCase.registerUser(validUser));

        assertEquals(ErrorCode.EMAIL_ALREADY_REGISTERED, ex.getErrorCode());
        verify(userRepository, never()).save(any());
    }
}