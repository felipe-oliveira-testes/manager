package com.manager.service;

import java.util.List;
import java.util.Optional;

import com.manager.entity.User;
import com.manager.exception.EntityNotFound;
import com.manager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) throws EntityNotFound {
        return getEntityByIdIfExists(id);
    }

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public User updateUser(Long id, User user) throws EntityNotFound {
        User userDatabase = getEntityByIdIfExists(id);
        if (user.getEmail() != null && !user.getEmail().isEmpty()) {
            userDatabase.setEmail(user.getEmail());
        }

        if (user.getName() != null && !user.getName().isEmpty()) {
            userDatabase.setName(user.getName());
        }

        return userRepository.save(userDatabase);
    }

    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }

    private User getEntityByIdIfExists(Long id) throws EntityNotFound {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            return optionalUser.get();
        } else {
            throw new EntityNotFound(String.format("User with id %s not found ", id));
        }
    }
}
