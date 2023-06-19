package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage storage;

    @Override
    public User create(User user) {
        return storage.create(user);
    }

    @Override
    public User update(User user) {
        return storage.update(user);
    }

    @Override
    public void remove(Long userId) {
        storage.remove(userId);
    }

    @Override
    public User getUserById(Long userId) {
        return storage.getUserById(userId);
    }

    @Override
    public List<User> getAllUser() {
        return storage.getAllUsers();
    }
}
