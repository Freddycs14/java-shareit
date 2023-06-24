package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.EmailDuplicateException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private long id = 1;

    private long getNextId() {
        return id++;
    }

    @Override
    public User create(User user) {
        emailExist(user.getId(), user.getEmail());
        user.setId(getNextId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        if (users.containsKey(user.getId())) {
            User oldUser = users.get(user.getId());
            if (user.getEmail() != null) {
                emailExist(user.getId(), user.getEmail());
                oldUser.setEmail(user.getEmail());
            }
            if (user.getName() != null) {
                oldUser.setName(user.getName());
            }
            return oldUser;
        }
        throw new UserNotFoundException("Пользователь не сущетсвует");
    }

    @Override
    public void remove(long userId) {
        checkId(userId);
        users.remove(userId);
    }

    @Override
    public User getUserById(long userId) {
        checkId(userId);
        return users.get(userId);
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    private void checkId(long userId) {
        if (!users.containsKey(userId)) {
            log.warn("Неправильный id");
            throw new UserNotFoundException("Пользователя с id " + userId + " не существует");
        }
    }

    private boolean emailExist(long userId, String email) {
        if (!users.isEmpty()) {
            if (users.values().stream().anyMatch(user -> user.getEmail().equals(email))) {
                if (users.get(userId) != null && users.get(userId).getEmail().equals(email)) {
                    return false;
                }
                throw new EmailDuplicateException("Пользователь с таким email уже существует");
            }
        }
        return false;
    }
}
