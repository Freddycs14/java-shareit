package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    public User create(User user) {
        if (user.getEmail() == null || user.getName() == null) {
            throw new ValidationException("Неверные данные пользователя");
        }
        checkUser(user);
        repository.save(user);
        return user;
    }

    @Override
    public User update(Long userId, User user) {
        checkUser(user);
        User updateUser = repository.findById(userId).orElseThrow(() -> new UserNotFoundException("Пользватель не найден"));
        if (user.getName() != null) {
            updateUser.setName(user.getName());
        }
        if (user.getEmail() != null) {
            updateUser.setEmail(user.getEmail());
        }
        repository.save(updateUser);
        return updateUser;
    }

    @Override
    public void remove(Long userId) {
        repository.deleteById(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserById(Long userId) {
        return repository.findById(userId).orElseThrow(() -> new UserNotFoundException("Пользватель не найден"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getAllUser() {
        return repository.findAll();
    }

    private boolean checkUser(User user) {
        if (user.getName() != null && (user.getName().isEmpty() || user.getName().isBlank())) {
            throw new ValidationException("Неверное имя пользователя");
        }
        if (user.getEmail() != null && (user.getEmail().isEmpty() || user.getEmail().isBlank() || !user.getEmail().contains("@"))) {
            throw new ValidationException("Неверный email пользователя");
        }
        return true;
    }
}
