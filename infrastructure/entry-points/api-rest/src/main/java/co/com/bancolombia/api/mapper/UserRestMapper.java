package co.com.bancolombia.api.mapper;

import co.com.bancolombia.api.dto.CreateUserRequest;
import co.com.bancolombia.api.dto.UserResponse;
import co.com.bancolombia.model.user.User;
import org.springframework.stereotype.Component;

@Component
public class UserRestMapper {

    public User toDomain(CreateUserRequest request) {
        if (request == null) {
            return null;
        }
        return User.builder()
                .nombres(request.getNombres())
                .apellidos(request.getApellidos())
                .fechaNacimiento(request.getFechaNacimiento())
                .direccion(request.getDireccion())
                .telefono(request.getTelefono())
                .correoElectronico(request.getCorreoElectronico())
                .salarioBase(request.getSalarioBase())
                .build();
    }

    public UserResponse toResponse(User user) {
        if (user == null) {
            return null;
        }
        return UserResponse.builder()
                .id(user.getId())
                .nombres(user.getNombres())
                .apellidos(user.getApellidos())
                .fechaNacimiento(user.getFechaNacimiento())
                .direccion(user.getDireccion())
                .telefono(user.getTelefono())
                .correoElectronico(user.getCorreoElectronico())
                .salarioBase(user.getSalarioBase())
                .build();
    }
}