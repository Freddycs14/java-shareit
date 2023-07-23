package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional
public class ItemRequestIntegrationTest {
    @Autowired
    private ItemRequestService service;
    @Autowired
    private ItemRequestRepository repository;
    @Autowired
    private UserRepository userRepository;

    @Test
    public void shouldGetRequestTest() {
        User booker = User.builder().name("Walter").email("w.white@gmail.com").build();
        userRepository.save(booker);
        ItemRequest itemRequest = ItemRequest.builder().id(1L).description("desc").requestor(booker).created(LocalDateTime.now()).build();
        repository.save(itemRequest);
        ItemRequestDto result = service.getRequest(booker.getId(), itemRequest.getId());
        assertEquals(1, result.getId());
        assertEquals(itemRequest.getDescription(), result.getDescription());
    }
}
