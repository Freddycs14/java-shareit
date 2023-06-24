package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserStorage {
    User create(User user);

    User update(User user);

    void remove(long userId);

    User getUserById(long userId);

    List<User> getAllUsers();
}
