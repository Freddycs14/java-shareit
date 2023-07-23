package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {
    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    public void setUp() {
        userService = Mockito.mock(UserService.class);
        userController = new UserController(userService);
    }

    @Test
    public void shouldCreateUser() {
        User user = User.builder().id(null).name("Walter").email("w.white@gmail.com").build();
        User createUser = User.builder().id(1L).name("Walter").email("w.white@gmail.com").build();
        Mockito.when(userService.create(user)).thenReturn(createUser);

        User result = UserMapper.toUser(userController.create(UserMapper.toUserDto(user)));

        assertEquals(createUser, result);
        Mockito.verify(userService).create(user);
    }

    @Test
    public void shouldUpdateUser() {
        User user = User.builder().id(1L).name("Walter").email("w.white@gmail.com").build();
        User updateUser = User.builder().id(1L).name("Jesse").email("j.pinkman@gmail.com").build();
        Mockito.when(userService.update(1L, user)).thenReturn(updateUser);

        User result = UserMapper.toUser(userController.update(1L, UserMapper.toUserDto(user)));

        assertEquals(updateUser, result);
        Mockito.verify(userService).update(1L, user);
    }

    @Test
    public void testRemove() {
        userController.remove(1L);

        Mockito.verify(userService).remove(1L);
    }

    @Test
    public void shouldGetUserById() {
        User user = User.builder().id(1L).name("Walter").email("w.white@gmail.com").build();
        Mockito.when(userService.getUserById(1L)).thenReturn(user);
        User result = UserMapper.toUser(userController.getUserById(1L));
        assertEquals(user, result);
        Mockito.verify(userService).getUserById(1L);
    }

    @Test
    public void shouldGetAllUsers() {
        User userOne = User.builder().id(1L).name("Walter").email("w.white@gmail.com").build();
        User userTwo = User.builder().id(2L).name("Jesse").email("j.pinkman@gmail.com").build();
        List<User> users = List.of(userOne, userTwo);
        Mockito.when(userService.getAllUser()).thenReturn(users);
        List<UserDto> usersDto = users.stream()
                .map(s -> UserMapper.toUserDto(s))
                .collect(Collectors.toList());
        List<UserDto> result = userController.getAllUsers();

        assertEquals(usersDto, result);
        Mockito.verify(userService).getAllUser();
    }
}
