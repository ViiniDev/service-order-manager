package com.viinidev.serviceorder.config;

import com.viinidev.serviceorder.domain.*;
import com.viinidev.serviceorder.repository.ServiceOrderRepository;
import com.viinidev.serviceorder.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedData(UserRepository userRepository, ServiceOrderRepository orderRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.count() > 0) {
                return;
            }
            User admin = userRepository.save(new User("Admin Demo", "admin@demo.com", passwordEncoder.encode("123456"), Role.ADMIN));
            User technician = userRepository.save(new User("Tecnico Demo", "tecnico@demo.com", passwordEncoder.encode("123456"), Role.TECHNICIAN));
            User client = userRepository.save(new User("Cliente Demo", "cliente@demo.com", passwordEncoder.encode("123456"), Role.CLIENT));

            ServiceOrder first = new ServiceOrder(
                    "Computador nao inicializa",
                    "Ao ligar o equipamento, o sistema nao passa da tela inicial.",
                    OrderPriority.HIGH,
                    client
            );
            first.assignTo(technician);
            first.updateStatus(OrderStatus.IN_PROGRESS);
            first.addComment(new OrderComment("Chamado recebido e diagnostico inicial iniciado.", admin, first));
            first.addComment(new OrderComment("Verificando disco e memoria do equipamento.", technician, first));
            orderRepository.save(first);

            orderRepository.save(new ServiceOrder(
                    "Solicitacao de instalacao de impressora",
                    "Cliente precisa configurar impressora de rede no setor financeiro.",
                    OrderPriority.MEDIUM,
                    client
            ));
        };
    }
}
