package co.com.bancolombia.api;

import co.com.bancolombia.api.dto.CreateUserRequest;
import co.com.bancolombia.api.dto.UserResponse;
import co.com.bancolombia.api.mapper.UserRestMapper;
import co.com.bancolombia.model.user.User;
import co.com.bancolombia.usecase.user.UserUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequestMapping(value = "/api/v1/usuarios", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ApiRest {

    private final UserUseCase userUseCase;
    private final UserRestMapper userRestMapper;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserResponse> registerUser(@RequestBody CreateUserRequest request) {
        log.info("Recibida petición POST /api/v1/usuarios para registrar solicitante");
        User domainUser = userRestMapper.toDomain(request);
        User registeredUser = userUseCase.registerUser(domainUser);
        UserResponse response = userRestMapper.toResponse(registeredUser);
        log.info("Registro completado exitosamente para el usuario con id: {}", response.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}