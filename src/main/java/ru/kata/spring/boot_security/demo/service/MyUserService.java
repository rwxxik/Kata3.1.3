package ru.kata.spring.boot_security.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.entity.Role;
import ru.kata.spring.boot_security.demo.entity.User;
import ru.kata.spring.boot_security.demo.repository.UserRepository;
import ru.kata.spring.boot_security.demo.security.MyUserDetails;

import java.util.List;
import java.util.Optional;

@Service
public class MyUserService implements UserDetailsService, UserCRUDService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    @Autowired
    public MyUserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsername(username);

        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }
        return new MyUserDetails(user.get());
    }

    @Override
    @Transactional
    public void addUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.isUserCheckBox()) {
            user.addRole(new Role(1, "ROLE_USER"));
        }
        if (user.isAdminCheckBox()) {
            user.addRole(new Role(2, "ROLE_ADMIN"));
        }
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void updateUser(int id, User updatedUser) {
        User userToBeUpdated = userRepository.getById(id);
        userToBeUpdated.setFirstName(updatedUser.getFirstName());
        userToBeUpdated.setLastName(updatedUser.getLastName());
        userToBeUpdated.setEmail(updatedUser.getEmail());
        userToBeUpdated.setUsername(updatedUser.getUsername());
        userToBeUpdated.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        userRepository.save(userToBeUpdated);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUser(int id) {
        Optional<User> userFromDb = userRepository.findById(id);
        return userFromDb.orElse(null);
    }

    @Override
    @Transactional
    public void removeUser(int id) {
        userRepository.delete(getUser(id));
    }
}
