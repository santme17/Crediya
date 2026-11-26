package co.com.bancolombia.api;

import co.com.bancolombia.api.dto.CreateUserRequest;
import co.com.bancolombia.api.dto.UserResponse;
import co.com.bancolombia.api.mapper.UserRestMapper;
import co.com.bancolombia.model.user.User;
import co.com.bancolombia.usecase.user.UserUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApiRestTest {

    @Mock
    private UserUseCase userUseCase;

    private ApiRest apiRest;

    @BeforeEach
    void setUp() {
        apiRest = new ApiRest(userUseCase, new UserRestMapper());
    }

    @Test
    void shouldReturnCreatedWhenRegisteringValidUser() {
        CreateUserRequest request = CreateUserRequest.builder()
                .nombres("Santiago")
                .apellidos("Muñoz Estrada")
                .fechaNacimiento(LocalDate.of(2000, 5, 15))
                .direccion("Calle 10 # 20-30")
                .telefono("3001234567")
                .correoElectronico("santiago@correo.com")
                .salarioBase(new BigDecimal("3500000"))
                .build();

        User savedUser = User.builder()
                .id(1L)
                .nombres("Santiago")
                .apellidos("Muñoz Estrada")
                .fechaNacimiento(LocalDate.of(2000, 5, 15))
                .direccion("Calle 10 # 20-30")
                .telefono("3001234567")
                .correoElectronico("santiago@correo.com")
                .salarioBase(new BigDecimal("3500000"))
                .build();

        when(userUseCase.registerUser(any(User.class))).thenReturn(savedUser);

        ResponseEntity<UserResponse> response = apiRest.registerUser(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        assertEquals("santiago@correo.com", response.getBody().getCorreoElectronico());
    }
}