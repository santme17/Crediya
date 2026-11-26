package co.com.bancolombia.jpa;

import co.com.bancolombia.jpa.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;

public interface JPARepository extends CrudRepository<UserEntity, Long>, QueryByExampleExecutor<UserEntity> {
    boolean existsByCorreoElectronico(String correoElectronico);
}