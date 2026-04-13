package com.infnet.service;

import com.infnet.dto.UserRequest;
import com.infnet.entity.UserEntity;
import com.infnet.repository.UserRepository;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    @Test
    void deveCriarUsuarioComSucesso() {
        FakeUserRepository repository = new FakeUserRepository();
        UserService service = new UserService(repository, new SimpleMeterRegistry());

        UserRequest request = new UserRequest("João", "joao@teste.com", "123456");
        UserEntity user = service.criarUsuario(request);

        assertNotNull(user);
        assertEquals("João", user.nome);
        assertEquals("joao@teste.com", user.email);
        assertEquals(1, repository.users.size());
    }

    @Test
    void deveImpedirEmailDuplicado() {
        FakeUserRepository repository = new FakeUserRepository();
        UserService service = new UserService(repository, new SimpleMeterRegistry());

        service.criarUsuario(new UserRequest("João", "joao@teste.com", "123456"));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.criarUsuario(new UserRequest("Outro", "joao@teste.com", "abcdef"))
        );

        assertEquals("Email já cadastrado", exception.getMessage());
    }

    @Test
    void deveExecutarStressTest() {
        FakeUserRepository repository = new FakeUserRepository();
        UserService service = new UserService(repository, new SimpleMeterRegistry());

        int criados = service.stressTest(10);

        assertEquals(10, criados);
        assertEquals(10, repository.users.size());
    }

    static class FakeUserRepository extends UserRepository {
        final List<UserEntity> users = new ArrayList<>();

        @Override
        public Optional<UserEntity> findByEmail(String email) {
            return users.stream()
                    .filter(u -> u.email.equals(email))
                    .findFirst();
        }

        @Override
        public void persist(UserEntity entity) {
            entity.id = (long) (users.size() + 1);
            users.add(entity);
        }

        @Override
        public List<UserEntity> listAll() {
            return users;
        }
    }
}