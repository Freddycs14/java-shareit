package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.user.model.User;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserDataJpaTest {

    private final UserRepository repository;

    @Test
    public void createUserTest() {
    User user = User.builder().name("Walter").email("w.white@gmail.com").build();

    User result = repository.save(user);

    assertThat(result).hasFieldOrPropertyWithValue("id", 1L);
    assertThat(result).hasFieldOrPropertyWithValue("name", "Walter");
    assertThat(result).hasFieldOrPropertyWithValue("email", "w.white@gmail.com");
    }
}
