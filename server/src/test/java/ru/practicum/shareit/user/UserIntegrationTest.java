package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional
public class UserIntegrationTest {
    @Autowired
    private UserController userController;

    private final UserDto userDtoOne = UserDto.builder().name("Walter").email("w.white@gmail.com").build();
    private final UserDto userDtoTwo = UserDto.builder().name("Jesse").email("j.pinkman@gmail.com").build();

    @Test
    public void shouldCreateUser() {
        UserDto userDtoFirst = userController.create(userDtoOne);
        assertEquals(userDtoFirst, userController.getUserById(1L));
        assertEquals("Walter", userDtoFirst.getName());
        assertEquals("w.white@gmail.com", userDtoFirst.getEmail());
    }

    @Test
    public void shouldUpdateUser() {
        UserDto userDtoFirst = userController.create(userDtoOne);
        UserDto updateEmailUser = UserDto.builder().id(0L).name("Walter").email("w.white@mail.ru").build();
        UserDto updateNameUser = UserDto.builder().id(0L).name("Heizenberg").email("w.white@mail.ru").build();

        userController.update(1L, updateEmailUser);
        UserDto updateEmailTest = userController.getUserById(1L);
        assertNotEquals(updateEmailTest, userDtoFirst);
        assertEquals("w.white@mail.ru", updateEmailTest.getEmail());

        userController.update(1L, updateNameUser);
        UserDto updateNameTest = userController.getUserById(1L);
        assertNotEquals(updateNameTest, userDtoFirst);
        assertEquals("Heizenberg", updateNameTest.getName());
    }

    @Test
    public void shouldDeleteUser() {
        UserDto userDtoFirst = userController.create(userDtoOne);
        userController.remove(1L);
        List<UserDto> users = userController.getAllUsers();
        assertEquals(0, users.size());
    }

    @Test
    public void shouldGetUserById() {
        UserDto userDtoFirst = userController.create(userDtoOne);
        assertEquals(userDtoFirst, userController.getUserById(1L));
    }

    @Test
    public void shouldGetAllUser() {
        UserDto userDtoFirst = userController.create(userDtoOne);
        UserDto userDtoSecond = userController.create(userDtoTwo);
        List<UserDto> users = userController.getAllUsers();
        assertEquals(2, users.size());
        assertEquals(userDtoFirst, users.get(0));
        assertEquals(userDtoSecond, users.get(1));
    }
}
