package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestDataJpaTest {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;

    private User user;
    private ItemRequest itemRequest;

    @BeforeEach
    void setUp() {
        user = User.builder().name("Walter").email("w.white@gmail.com").build();
        userRepository.save(user);
        itemRequest = ItemRequest.builder().description("blue ice").requestor(user).created(LocalDateTime.now()).build();
        itemRequestRepository.save(itemRequest);
    }

    @Test
    public void shouldGetRequestOrderByCreated() {
        List<ItemRequest> result = itemRequestRepository.findItemRequestByRequestorIdOrderByCreatedDesc(user.getId());
        assertEquals(1, result.size());
        assertThat(result.contains(itemRequest));
    }
}
