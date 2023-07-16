package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.*;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.mapper.CommentMapper;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static org.springframework.data.domain.Sort.Direction.DESC;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public ItemDto create(Long userId, ItemDto itemDto) {
        if (userId == null) {
            throw new ValidationException("Id владельца не указан");
        }
        Item newItem = ItemMapper.toItem(itemDto, userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundException("Владелец не найден")));
        itemRepository.save(newItem);
        return ItemMapper.toItemDto(newItem);
    }

    @Override
    public ItemDto update(Long id, Long userId, ItemDto itemDto) {
        if (userId == null) {
            throw new ValidationException("Id владельца не указан");
        }
        Item updateItem = itemRepository.findById(itemDto.getId()).orElseThrow(() ->
                new ItemNotFoundException("Вещь не найдена"));
        if (!updateItem.getOwner().getId().equals(userId)) {
            throw new UserNotFoundException("Пользователь с id " + updateItem.getOwner().getId() +
                    " не является владельцем вещи с id " + updateItem.getId());
        }
        if (itemDto.getDescription() != null) {
            updateItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getName() != null) {
            updateItem.setName(itemDto.getName());
        }
        if (itemDto.getAvailable() != null) {
            updateItem.setAvailable(itemDto.getAvailable());
        }
        return ItemMapper.toItemDto(itemRepository.save(updateItem));
    }

    @Override
    @Transactional(readOnly = true)
    public ItemDto getItemById(Long userId, Long itemId) {
        if (itemId == null) {
            throw new ValidationException("Id вещи не указан");
        }
        if (userId == null) {
            throw new ValidationException("Id владельца не указан");
        }
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new ItemNotFoundException("Вещь не найдена"));
        List<Item> items = List.of(item);
        List<ItemDto> itemsDto = addBookingAndComments(items, LocalDateTime.now(), userId);
        return itemsDto.get(0);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> getUserItems(Long userId, int from, int size) {
        if (userId == null) {
            throw new ValidationException("Id владельца не указан");
        }
        Pageable page = PageRequest.of(from / size, size);
        List<Item> items = itemRepository.findItemByOwnerId(userId, page);
        List<ItemDto> itemsDto = addBookingAndComments(items, LocalDateTime.now(), userId);
        return itemsDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> searchItems(String text, int from, int size) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        Pageable page = PageRequest.of(from / size, size);
        List<Item> items = itemRepository.search(text, page).stream()
                .filter(Item::getAvailable)
                .collect(toList());
        return addBookingAndComments(items, LocalDateTime.now(), null);
    }

    @Override
    public CommentDto create(CommentDto commentDto, Long itemId, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("Пользователь с id " + userId + " не найден"));
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException("Вещь с id " + itemId + " не найдена"));
        Sort sort = Sort.by("end").descending();
        Booking booking = bookingRepository.findFirstByBookerIdAndItemIdAndEndIsBefore(userId, itemId, LocalDateTime.now(), sort);
        if (booking != null) {
            Comment comment = CommentMapper.toComment(commentDto, item, user);
            commentRepository.save(comment);
            return CommentMapper.toCommentDto(comment);
        } else {
            throw new ValidationException("Пользователь не заказывал эту вещь");
        }
    }

    @Override
    public List<ItemDto> getItemsByRequestId (Long itemRequestId) {
        List<Item> items = itemRepository.findAll()
                .stream()
                .filter(s->s.getRequestId() != null && s.getRequestId().equals(itemRequestId))
                .collect(toList());
        return addBookingAndComments(items, LocalDateTime.now(), null);
    }

    private List<ItemDto> addBookingAndComments(List<Item> items, LocalDateTime now, Long userId) {
        Map<Item, List<CommentDto>> commentsMap = commentRepository.findAllByItemIn(items, Sort.by(DESC, "created"))
                .stream()
                .collect(Collectors.groupingBy(Comment::getItem, Collectors.mapping(CommentMapper::toCommentDto, Collectors.toList())));

        List<Booking> bookings = bookingRepository.findAll();

        Map<Item, Booking> lastBookingsMap = new HashMap<>();
        for (Item item : items) {
            Booking booking = bookings.stream()
                    .filter(s -> s.getItem().getId().equals(item.getId()) && s.getStart().isBefore(now))
                    .max(Comparator.comparing(Booking::getEnd))
                    .orElse(null);
            lastBookingsMap.put(item, booking);
        }

        Map<Item, Booking> nextBookingsMap = new HashMap<>();
        for (Item item : items) {
            Booking booking = bookings.stream()
                    .filter(s -> s.getItem().getId().equals(item.getId()) && s.getStart().isAfter(now))
                    .min(Comparator.comparing(Booking::getStart))
                    .orElse(null);
            nextBookingsMap.put(item, booking);
        }

        List<ItemDto> itemsDto = new ArrayList<>();
        for (Item item : items) {
            ItemDto itemWithBooking = ItemMapper.toItemDto(item);
            List<CommentDto> commentsDto = commentsMap.get(item);
            if (commentsDto == null) {
                commentsDto = new ArrayList<>();
            }
            itemWithBooking.setComments(commentsDto);
            Booking lastBooking = lastBookingsMap.get(item);
            if (lastBooking != null && item.getOwner().getId().equals(userId)) {
                itemWithBooking.setLastBooking(BookingMapper.toBookingForItemDto(lastBooking));
            } else if (lastBooking != null && userId == null) {
                itemWithBooking.setLastBooking(BookingMapper.toBookingForItemDto(lastBooking));
            } else {
                itemWithBooking.setLastBooking(null);
            }
            Booking nextBooking = nextBookingsMap.get(item);
            if (nextBooking != null && item.getOwner().getId().equals(userId) && nextBooking.getStatus().equals(BookingStatus.APPROVED)) {
                itemWithBooking.setNextBooking(BookingMapper.toBookingForItemDto(nextBooking));
            } else if (nextBooking != null && userId == null) {
                itemWithBooking.setNextBooking(BookingMapper.toBookingForItemDto(nextBooking));
            } else {
                itemWithBooking.setNextBooking(null);
            }
            itemsDto.add(itemWithBooking);
        }
        return itemsDto;
    }
}
