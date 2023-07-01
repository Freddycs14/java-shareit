package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private Long itemId = 1L;
    private Long commentId = 1L;

    @Override
    public ItemDto create(Long userId, ItemDto itemDto) {
        if (userId == null) {
            throw new ValidationException("Id владельца не указан");
        }
        Item newItem = ItemMapper.toItem(itemDto, userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundException("Владелец не найден")));
        newItem.setId(itemId);
        ++itemId;
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
    public ItemDto getItemById(Long userId, Long itemId) {
        if (itemId == null) {
            throw new ValidationException("Id вещи не указан");
        }
        if (userId == null) {
            throw new ValidationException("Id владельца не указан");
        }
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new ItemNotFoundException("Вещь не найдена"));
        return addBookingAndComments(item, LocalDateTime.now(), userId);
    }

    @Override
    public List<ItemDto> getUserItems(Long userId) {
        if (userId == null) {
            throw new ValidationException("Id владельца не указан");
        }
        return itemRepository.findItemByOwnerId(userId).stream()
                .map(s -> addBookingAndComments(s, LocalDateTime.now(), userId))
                .sorted(Comparator.comparing(s -> s.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        return itemRepository.search(text).stream()
                .filter(Item::getAvailable)
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto create(CommentDto commentDto, Long itemId, Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("Пользователь с id " + userId + " не найден"));
        Booking booking = bookingRepository.findFirstByBookerIdAndItemIdAndEndIsBeforeOrderByEndDesc(userId, itemId, LocalDateTime.now());
        if (booking != null) {
            Comment comment = Comment.builder()
                    .created(LocalDateTime.now())
                    .item(booking.getItem())
                    .user(booking.getBooker())
                    .text(commentDto.getText())
                    .id(commentId)
                    .build();
            ++commentId;
            commentRepository.save(comment);
            return CommentMapper.toCommentDto(comment);
        } else {
            throw new ValidationException("Пользователь не заказывал эту вещь");
        }
    }

    private ItemDto addBookingAndComments(Item item, LocalDateTime now, Long userId) {
        ItemDto itemWithBooking = ItemMapper.toItemDto(item);
        Booking lastBooking = bookingRepository.findAll()
                .stream()
                .filter(s -> s.getItem().getId().equals(item.getId()) && s.getStart().isBefore(now))
                .max(Comparator.comparing(Booking::getEnd))
                .orElse(null);

        if (lastBooking != null && item.getOwner().getId().equals(userId)) {
            itemWithBooking.setLastBooking(BookingMapper.toBookingForItemDto(lastBooking));
        } else {
            itemWithBooking.setLastBooking(null);
        }

        Booking nextBooking = bookingRepository.findAll()
                .stream()
                .filter(s -> s.getItem().getId().equals(item.getId()) && s.getStart().isAfter(now))
                .min(Comparator.comparing(Booking::getStart))
                .orElse(null);

        if (nextBooking != null && item.getOwner().getId().equals(userId) && nextBooking.getStatus().equals(BookingStatus.APPROVED)) {
            itemWithBooking.setNextBooking(BookingMapper.toBookingForItemDto(nextBooking));
        } else {
            itemWithBooking.setNextBooking(null);
        }
        List<CommentDto> comments = commentRepository.findAllByItem(item).stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
        if (comments.isEmpty()) {
            comments = new ArrayList<>();
        }
        itemWithBooking.setComments(comments);
        return itemWithBooking;


    }
}
