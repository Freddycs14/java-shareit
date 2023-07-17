package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ItemRequestServiceImpl implements ItemRequestService {
    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;
    private final ItemService itemService;

    @Override
    public ItemRequestDto create(ItemRequestDto itemRequestDto, Long userId) {
        if (itemRequestDto.getDescription() == null) {
            throw new ValidationException("Описание не может быть пустым");
        }
        User requestor = checkUser(userId);
        itemRequestDto.setRequestor(UserMapper.toUserDto(requestor));
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto);
        itemRequest.setRequestor(requestor);
        itemRequest.setCreated(LocalDateTime.now());
        itemRequestRepository.save(itemRequest);
        List<ItemDto> items = itemService.getItemsByRequestId(itemRequest.getId());
        return ItemRequestMapper.toItemRequestDto(itemRequest, items);
    }

    @Override
    public List<ItemRequestDto> getUserRequest(Long userId) {
        checkUser(userId);
        List<ItemRequest> itemRequests = itemRequestRepository.findItemRequestByRequestorIdOrderByCreatedDesc(userId);

        List<Long> itemRequestIds = itemRequests.stream()
                .map(ItemRequest::getId)
                .collect(Collectors.toList());

        List<ItemDto> items = itemRepository.findAllByRequestIdIn(itemRequestIds).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());

        return itemRequests.stream()
                .map(s -> ItemRequestMapper.toItemRequestDto(s, items))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getOtherUserRequests(Long userId, int from, int size) {
        if (size <= 0 || from < 0) {
            throw new ValidationException("Неверно заданы параметры size и from");
        }
        checkUser(userId);
        List<ItemRequest> userRequests = itemRequestRepository.findAll(PageRequest.of(from / size, size))
                .stream()
                .filter(s -> !s.getRequestor().getId().equals(userId))
                .limit(size)
                .collect(Collectors.toList());

        List<Long> userRequestIds = userRequests.stream()
                .map(ItemRequest::getId)
                .collect(Collectors.toList());

        Map<Long, List<ItemDto>> itemsByRequestIds = itemRepository.findAllByRequestIdIn(userRequestIds).stream()
                .collect(Collectors.groupingBy(Item::getRequestId, Collectors.mapping(ItemMapper::toItemDto, Collectors.toList())));

        return userRequests.stream()
                .map(s -> ItemRequestMapper.toItemRequestDto(s, itemsByRequestIds.get(s.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto getRequest(Long userId, Long requestId) {
        checkUser(userId);
        ItemRequest itemRequest = itemRequestRepository.findById(requestId).orElseThrow(() -> new ItemRequestNotFoundException("Запрос не найден"));
        List<ItemDto> itemsDto = itemService.getItemsByRequestId(itemRequest.getId());
        return ItemRequestMapper.toItemRequestDto(itemRequest, itemsDto);
    }

    private User checkUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
    }
}
