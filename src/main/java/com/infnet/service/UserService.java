package com.infnet.service;

import com.infnet.dto.UserRequest;
import com.infnet.entity.UserEntity;
import com.infnet.repository.UserRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class UserService {

    private final UserRepository userRepository;
    private final Counter requestsCounter;

    @Inject
    public UserService(UserRepository userRepository, MeterRegistry meterRegistry) {
        this.userRepository = userRepository;
        this.requestsCounter = meterRegistry.counter("app_requests_total", "resource", "usuario");
    }

    @Transactional
    public UserEntity criarUsuario(UserRequest request) {
        requestsCounter.increment();

        userRepository.findByEmail(request.email()).ifPresent(user -> {
            throw new IllegalArgumentException("Email já cadastrado");
        });

        UserEntity user = new UserEntity(request.nome(), request.email(), request.senha());
        userRepository.persist(user);
        return user;
    }

    public List<UserEntity> listarUsuarios() {
        requestsCounter.increment();
        return userRepository.listAll();
    }

    @Transactional
    public int stressTest(int quantidade) {
        for (int i = 0; i < quantidade; i++) {
            UserRequest request = new UserRequest(
                    "Usuario " + UUID.randomUUID(),
                    "usuario" + UUID.randomUUID() + "@teste.com",
                    "123456"
            );
            criarUsuario(request);
        }
        return quantidade;
    }

    public int stressConsulta(int quantidade) {
        requestsCounter.increment();

        if (userRepository.listAll().isEmpty()) {
            return 0;
        }

        String emailBase = userRepository.listAll().get(0).email;
        int consultas = 0;

        for (int i = 0; i < quantidade; i++) {
            userRepository.findByEmail(emailBase);
            consultas++;
        }

        return consultas;
    }
}