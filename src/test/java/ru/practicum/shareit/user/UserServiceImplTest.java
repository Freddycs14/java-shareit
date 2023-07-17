package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.EmailDuplicateException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    public void shouldCreateUserDuplicate() {
        User user = User.builder().name("Walter").email("w.white@gmail.com").build();
        User userDuplicate = User.builder().name("Walter").email("w.white@gmail.com").build();

        Mockito.when(userRepository.save(user)).thenReturn(user);
        Mockito.when(userRepository.save(userDuplicate)).thenThrow(EmailDuplicateException.class);
        assertThrows(EmailDuplicateException.class, () -> userService.create(userDuplicate));
    }

    @Test
    public void shouldUpdateUser() {
        User user = User.builder().id(1L).name("Walter").email("w.white@gmail.com").build();
        User updateUser = User.builder().id(1L).name("Jesse").email("j.pinkman@gmail.com").build();
        Long userId = 1L;
        Mockito.when(userRepository.save(user)).thenReturn(user);
        Mockito.when((userRepository.findById(userId))).thenReturn(Optional.of(user));
        User result = userService.update(userId, updateUser);
        assertEquals(updateUser, result);
    }

    @Test
    public void shouldUpdateUserThrow() {
        User user = User.builder().id(1L).name("Walter").email("w.white@gmail.com").build();
        Long userId = 0L;
        Mockito.when((userRepository.findById(userId))).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.update(userId, user));
        Mockito.verify(userRepository, Mockito.never()).save(user);
    }

    @Test
    public void shouldDeleteUser() {
        long userId = 1L;
        userService.remove(userId);
        Mockito.verify(userRepository, Mockito.times(1)).deleteById(userId);
    }

    @Test
    public void shouldGetAllUser() {
        User user = User.builder().id(1L).name("Walter").email("w.white@gmail.com").build();
        List<User> users = List.of(user);
        Mockito.when(userRepository.findAll()).thenReturn(users);
        List<User> result = userService.getAllUser();
        assertEquals(users, result);
    }

    @Test
    public void shouldGetUserById() {
        User user = User.builder().id(1L).name("Walter").email("w.white@gmail.com").build();
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        User result = userService.getUserById(user.getId());
        assertEquals(user, result);
    }

    @Test
    public void shouldGetUserByIdThrow() {
        Long userId = 0L;
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.getUserById(userId));
    }
}
