package co.com.bancolombia.jpa;

import co.com.bancolombia.jpa.entity.UserEntity;
import co.com.bancolombia.jpa.helper.AdapterOperations;
import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.gateways.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Repository
public class JPARepositoryAdapter extends AdapterOperations<User, UserEntity, Long, JPARepository>
        implements UserRepository {

    public JPARepositoryAdapter(JPARepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, User.class));
    }

    @Override
    @Transactional
    public User save(User user) {
        log.info("Iniciando persistencia transaccional de usuario en base de datos");
        User savedUser = super.save(user);
        log.info("Usuario persistido exitosamente con id: {}", savedUser.getId());
        return savedUser;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByCorreoElectronico(String correoElectronico) {
        log.debug("Consultando existencia de correo electrónico en base de datos");
        return repository.existsByCorreoElectronico(correoElectronico);
    }
}