package com.cargomate.security.config;

import com.cargomate.security.model.Role;
import com.cargomate.security.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RoleDataLoader {

    @Bean
    public CommandLineRunner loadInitialRoles(RoleRepository roleRepository) {
        return args -> {
            if (roleRepository.count() == 0) { // Load roles only if the table is empty
                Role customerRole = new Role();
                customerRole.setDescription("Customer role");
                customerRole.setName("CUSTOMER");

                Role dispatcherRole = new Role();
                dispatcherRole.setDescription("Dispatcher role");
                dispatcherRole.setName("DISPATCHER");

                Role driverRole = new Role();
                driverRole.setDescription("Driver role");
                driverRole.setName("DRIVER");

                Role managerRole = new Role();
                managerRole.setDescription("Manager role");
                managerRole.setName("MANAGER");

                roleRepository.save(customerRole);
                roleRepository.save(dispatcherRole);
                roleRepository.save(driverRole);
                roleRepository.save(managerRole);
            }
        };
    }
}
