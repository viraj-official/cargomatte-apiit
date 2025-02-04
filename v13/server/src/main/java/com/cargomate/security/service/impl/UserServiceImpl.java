package com.cargomate.security.service.impl;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import com.cargomate.security.model.Role;
import com.cargomate.security.model.User;
import com.cargomate.security.repository.UserRepository;
import com.cargomate.security.request.CreateCustomerRequest;
import com.cargomate.security.service.RoleService;
import com.cargomate.security.service.UserService;
import com.cargomate.system.enums.AccountStatus;
import com.cargomate.system.model.Customer;
import com.cargomate.system.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service(value = "userService")
public class UserServiceImpl implements UserDetailsService, UserService {

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private BCryptPasswordEncoder bcryptEncoder;

    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(email).get();
        if(user == null){
            throw new UsernameNotFoundException("Invalid username or password.");
        }
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), getAuthority(user));
    }

    private Set<SimpleGrantedAuthority> getAuthority(User user) {
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        user.getRoles().forEach(role -> {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));
        });
        return authorities;
    }

    @Override
    public Customer saveCustomer(CreateCustomerRequest request) {
        if (userRepository.existsByUsername(request.getEmail())) {
            throw new IllegalArgumentException("Email is already in use: " + request.getEmail());
        }

        User user = new User();
        user.setUsername(request.getEmail());
        user.setPassword(bcryptEncoder.encode(request.getPassword()));

        Role customerRole = roleService.findByName("CUSTOMER");
        Set<Role> roleSet = new HashSet<>();
        if (customerRole != null) {
            roleSet.add(customerRole);
        }
        user.setRoles(roleSet);

        user = userRepository.save(user);

        Customer customer = new Customer();
        customer.setFirstName(request.getFirstName());
        customer.setLastName(request.getLastName());
        customer.setDisplayName("");  // or set some default display name if required
        customer.setPhoneNumber(request.getPhoneNumber());
        customer.setEmail(request.getEmail());
        customer.setAddress(request.getAddress());
        customer.setLatitude(0.00);
        customer.setLongitude(0.00);
        customer.setPreferredDeliveryTime(getDefaultPreferredTime());
        customer.setUser(user);
        customer.setLoyaltyPoints(0);
        customer.setAccountStatus(AccountStatus.ACTIVE);

        return customerRepository.save(customer);
    }

    @Override
    public User curentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;

        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }
        return userRepository.findByUsername(username).get();
    }

    private LocalDateTime getDefaultPreferredTime() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime tomorrow = now.plusDays(1).toLocalDate().atStartOfDay(); // Start of tomorrow
        return tomorrow.plusHours(9); // Default time: 9:00 AM tomorrow
    }
}