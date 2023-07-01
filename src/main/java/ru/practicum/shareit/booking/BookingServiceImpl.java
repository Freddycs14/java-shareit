package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    private Long bookingId = 1L;

    @Override
    public BookingDto create(BookingDtoCreate bookingDtoCreate, Long userId) {
        if (bookingDtoCreate.getItemId() == null || bookingDtoCreate.getStart() == null ||
                bookingDtoCreate.getEnd() == null || bookingDtoCreate.getStart().equals(bookingDtoCreate.getEnd())) {
            throw new ValidationException("Неправильно задано бронирование");
        }
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
        Item item = itemRepository.findById(bookingDtoCreate.getItemId()).orElseThrow(() -> new ItemNotFoundException("Вещь не найдена"));
        if (item.getOwner().getId().equals(userId)) {
            throw new UserNotFoundException("Нельзя забронировать собственную вещь");
        }
        if (!item.getAvailable()) {
            throw new ValidationException("Вещь недоступна для бронирования");
        }
        if (bookingDtoCreate.getEnd().isBefore(LocalDateTime.now()) ||
                bookingDtoCreate.getEnd().isBefore(bookingDtoCreate.getStart()) ||
                bookingDtoCreate.getStart().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Неправильно указан период бронирования");
        }
        Booking booking = BookingMapper.toBookingDtoCreate(bookingDtoCreate, bookingId, item, user);
        booking.setId(bookingId);
        ++bookingId;
        bookingRepository.save(booking);
        return BookingMapper.tBookingDto(booking);
    }

    @Override
    public BookingDto update(Long bookingId, Long userId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new BookingNotFoundException("Бронирование не найдено"));
        if (booking.getBooker().getId().equals(userId)) {
            throw new UserNotFoundException("Нельзя забронировать собственную вещь");
        }
        if (booking.getStatus().equals(BookingStatus.APPROVED)) {
            throw new ValidationException("Заявка уже ободбрена");
        }
        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        bookingRepository.save(booking);
        return BookingMapper.tBookingDto(booking);
    }

    @Override
    public BookingDto getBooking(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new BookingNotFoundException("Бронирование не найдено"));
        Item item = itemRepository.findById(booking.getItem().getId()).orElseThrow(() -> new ItemNotFoundException("Вещь не найдена"));
        if (userId != item.getOwner().getId() && userId != booking.getBooker().getId()) {
            throw new UserNotFoundException("У данного пользователя нет прав для просмотра");
        }
        return BookingMapper.tBookingDto(booking);
    }

    @Override
    public List<BookingDto> getUserBookings(Long userId, State state) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
        List<BookingDto> usersBooking = new ArrayList<>();
        switch (state) {
            case ALL:
                usersBooking = bookingRepository.findAllByBookerIdOrderByStartDesc(userId)
                        .stream()
                        .map(BookingMapper::tBookingDto)
                        .collect(Collectors.toList());
                break;
            case WAITING:
                usersBooking = bookingRepository.findAllByBookerAndStatusEqualsOrderByStartDesc(user, BookingStatus.WAITING)
                        .stream()
                        .map(BookingMapper::tBookingDto)
                        .collect(Collectors.toList());
                break;
            case APPROVED:
                usersBooking = bookingRepository.findAllByBookerAndStatusEqualsOrderByStartDesc(user, BookingStatus.APPROVED)
                        .stream()
                        .map(BookingMapper::tBookingDto)
                        .collect(Collectors.toList());
                break;
            case REJECTED:
                usersBooking = bookingRepository.findAllByBookerAndStatusEqualsOrderByStartDesc(user, BookingStatus.REJECTED)
                        .stream()
                        .map(BookingMapper::tBookingDto)
                        .collect(Collectors.toList());
                break;
            case CURRENT:
                usersBooking = bookingRepository.findCurrentBookerBookings(userId, LocalDateTime.now())
                        .stream()
                        .map(BookingMapper::tBookingDto)
                        .collect(Collectors.toList());
                break;
            case FUTURE:
                usersBooking = bookingRepository.findAllByBookerIdAndStartIsAfter(userId, LocalDateTime.now())
                        .stream()
                        .sorted((a, b) -> a.getStart().isBefore(b.getStart()) ? 1 : -1)
                        .map(BookingMapper::tBookingDto)
                        .collect(Collectors.toList());
                break;
            case PAST:
                usersBooking = bookingRepository.findAllByBookerIdAndEndIsBefore(userId, LocalDateTime.now())
                        .stream()
                        .sorted((a, b) -> a.getStart().isBefore(b.getStart()) ? 1 : -1)
                        .map(BookingMapper::tBookingDto)
                        .collect(Collectors.toList());
                break;
            case UNSUPPORTED_STATUS:
                throw new ValidationStateException("Unknown state: UNSUPPORTED_STATUS");
        }
        return usersBooking;
    }

    @Override
    public List<BookingDto> getUserItemsBookings(Long userId, State state) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
        List<BookingDto> bookings = new ArrayList<>();
        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllByItemOwnerIdOrderByStartDesc(userId)
                        .stream()
                        .map(BookingMapper::tBookingDto)
                        .collect(Collectors.toList());
                break;
            case WAITING:
                bookings = bookingRepository.findAllByItem_OwnerAndStatusEqualsOrderByStartDesc(user, BookingStatus.WAITING)
                        .stream()
                        .map(BookingMapper::tBookingDto)
                        .collect(Collectors.toList());
                break;
            case APPROVED:
                bookings = bookingRepository.findAllByItem_OwnerAndStatusEqualsOrderByStartDesc(user, BookingStatus.APPROVED)
                        .stream()
                        .map(BookingMapper::tBookingDto)
                        .collect(Collectors.toList());
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByItem_OwnerAndStatusEqualsOrderByStartDesc(user, BookingStatus.REJECTED)
                        .stream()
                        .map(BookingMapper::tBookingDto)
                        .collect(Collectors.toList());
                break;
            case CURRENT:
                bookings = bookingRepository.findBookingsByItemOwnerCurrent(userId, LocalDateTime.now())
                        .stream()
                        .map(BookingMapper::tBookingDto)
                        .collect(Collectors.toList());
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByItemOwnerIdAndStartIsAfter(userId, LocalDateTime.now())
                        .stream()
                        .sorted((a, b) -> a.getStart().isBefore(b.getStart()) ? 1 : -1)
                        .map(BookingMapper::tBookingDto)
                        .collect(Collectors.toList());
                break;
            case PAST:
                bookings = bookingRepository.findAllByItemOwnerIdAndEndIsBefore(userId, LocalDateTime.now())
                        .stream()
                        .sorted((a, b) -> a.getStart().isBefore(b.getStart()) ? 1 : -1)
                        .map(BookingMapper::tBookingDto)
                        .collect(Collectors.toList());
                break;
            case UNSUPPORTED_STATUS:
                throw new ValidationStateException("Unknown state: UNSUPPORTED_STATUS");
        }
        return bookings;
    }
}
