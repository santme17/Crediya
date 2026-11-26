package co.com.bancolombia.jpa.helper;

import co.com.bancolombia.jpa.JPARepository;
import co.com.bancolombia.jpa.JPARepositoryAdapter;
import co.com.bancolombia.jpa.entity.UserEntity;
import co.com.bancolombia.model.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.data.domain.Example;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class AdapterOperationsTest {

    @Mock
    private JPARepository repository;

    @Mock
    private ObjectMapper objectMapper;

    private JPARepositoryAdapter adapter;
    private User domainUser;
    private UserEntity entityUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        domainUser = User.builder()
                .id(1L)
                .nombres("Santiago")
                .apellidos("Muñoz Estrada")
                .correoElectronico("santiago@correo.com")
                .salarioBase(new BigDecimal("3500000"))
                .build();

        entityUser = UserEntity.builder()
                .id(1L)
                .nombres("Santiago")
                .apellidos("Muñoz Estrada")
                .correoElectronico("santiago@correo.com")
                .salarioBase(new BigDecimal("3500000"))
                .build();

        when(objectMapper.map(domainUser, UserEntity.class)).thenReturn(entityUser);
        when(objectMapper.map(entityUser, User.class)).thenReturn(domainUser);

        adapter = new JPARepositoryAdapter(repository, objectMapper);
    }

    @Test
    void testSave() {
        when(repository.save(entityUser)).thenReturn(entityUser);

        User result = adapter.save(domainUser);

        assertEquals(domainUser, result);
    }

    @Test
    void testSaveAllEntities() {
        List<User> domainList = List.of(domainUser);
        List<UserEntity> entityList = List.of(entityUser);

        when(repository.saveAll(entityList)).thenReturn(entityList);

        List<User> result = adapter.saveAllEntities(domainList);

        assertEquals(domainList, result);
    }

    @Test
    void testFindById() {
        when(repository.findById(1L)).thenReturn(Optional.of(entityUser));

        User result = adapter.findById(1L);

        assertEquals(domainUser, result);
    }

    @Test
    void testFindAll() {
        List<User> domainList = List.of(domainUser);
        List<UserEntity> entityList = List.of(entityUser);

        when(repository.findAll()).thenReturn(entityList);

        List<User> result = adapter.findAll();

        assertEquals(domainList, result);
    }

    @Test
    @SuppressWarnings("unchecked")
    void testFindByExample() {
        List<User> domainList = List.of(domainUser);
        List<UserEntity> entityList = List.of(entityUser);

        when(repository.findAll(any(Example.class))).thenReturn(entityList);

        List<User> result = adapter.findByExample(domainUser);

        assertEquals(domainList, result);
    }

    @Test
    void testExistsByCorreoElectronico() {
        when(repository.existsByCorreoElectronico("santiago@correo.com")).thenReturn(true);

        assertTrue(adapter.existsByCorreoElectronico("santiago@correo.com"));
    }
}