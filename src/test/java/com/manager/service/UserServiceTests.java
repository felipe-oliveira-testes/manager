package com.manager.service;

import com.manager.entity.User;
import com.manager.exception.EntityNotFound;
import com.manager.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserServiceTests {

    @InjectMocks
    private UserService userService;
    @Mock
    private UserRepository userRepository;

    User user1 = new User(1L, "name 1", "email1");
    User user2 = new User(2L, "name 2", "email2");

    User userToSave = new User("name 3", "email3");
    User user3 = new User(3L, "name 3", "email3");

    @BeforeEach
    void initMocks() {
        MockitoAnnotations.openMocks(this);

        List<User> users = new ArrayList<>();
        users.add(user1);
        users.add(user2);

        Mockito.when(userRepository.findById(1l)).thenReturn(Optional.of(user1));
        Mockito.when(userRepository.findById(2l)).thenReturn(Optional.of(user2));
        Mockito.when(userRepository.findAll()).thenReturn(users);

        Mockito.when(userRepository.save(userToSave)).thenReturn(user3);
        User userToUpdate = user1;
        userToUpdate.setName(userToSave.getName());
        userToUpdate.setEmail(userToSave.getEmail());
        Mockito.when(userRepository.save(userToUpdate)).thenReturn(userToUpdate);
    }

    @Test
    public void getUsersTest() {
        List<User> result = userService.getUsers();
        Assertions.assertThat(result.size()).isEqualTo(2);
    }

    @Test
    public void getUserByIdTest() {
        try {
            User result = userService.getUserById(user1.getId());
            Assertions.assertThat(result).isEqualTo(user1);
        } catch (EntityNotFound e) {
            Assertions.fail("EntityNotFound should not be thrown");
        }
    }

    @Test
    public void getUserByIdTestFail() {
        Assertions.assertThatThrownBy(() -> userService.getUserById(3L)).isInstanceOf(EntityNotFound.class);
    }

    @Test
    public void createUserTest() {
        User result = userService.createUser(userToSave);
        Assertions.assertThat(result).isEqualTo(user3);
    }

    @Test
    public void updateUserTest() {
        try {
            User result = userService.updateUser(1l, userToSave);
            Assertions.assertThat(result.getName()).isEqualTo(userToSave.getName());
            Assertions.assertThat(result.getEmail()).isEqualTo(userToSave.getEmail());
        } catch (EntityNotFound e) {
            Assertions.fail("EntityNotFound should not be thrown");
        }

    }

    @Test
    public void updateUserEntityNotFoundTest() {
        Assertions.assertThatThrownBy(
                () -> userService.updateUser(3l, userToSave)
        ).isInstanceOf(EntityNotFound.class);
    }
}
