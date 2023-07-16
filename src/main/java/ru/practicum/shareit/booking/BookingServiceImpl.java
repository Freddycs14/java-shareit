package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
@Transactional
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;


    @Override
    public BookingDto create(BookingDtoCreate bookingDtoCreate, Long userId) {
        if (bookingDtoCreate.getItemId() == null || bookingDtoCreate.getStart() == null ||
                bookingDtoCreate.getEnd() == null || bookingDtoCreate.getStart().equals(bookingDtoCreate.getEnd())) {
            throw new ValidationException("Неправильно задано бронирование");
        }
        User user = checkUser(userId);
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
        Booking booking = BookingMapper.toBookingDtoCreate(bookingDtoCreate, item, user);
        bookingRepository.save(booking);
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public BookingDto update(Long bookingId, Long userId, Boolean approved) {
        Booking booking = checkBooking(bookingId);
        if (booking.getBooker().getId().equals(userId)) {
            throw new UserNotFoundException("Нельзя забронировать собственную вещь");
        }
        if (booking.getStatus().equals(BookingStatus.APPROVED)) {
            throw new ValidationException("Заявка уже ободбрена");
        }
        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        bookingRepository.save(booking);
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingDto getBooking(Long bookingId, Long userId) {
        Booking booking = checkBooking(bookingId);
        Item item = itemRepository.findById(booking.getItem().getId()).orElseThrow(() -> new ItemNotFoundException("Вещь не найдена"));
        if (!userId.equals(item.getOwner().getId()) && !userId.equals(booking.getBooker().getId())) {
            throw new UserNotFoundException("У данного пользователя нет прав для просмотра");
        }
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getUserBookings(Long userId, State state, int from, int size) {
        checkParameters(from, size);
        User user = checkUser(userId);
        List<BookingDto> usersBooking = new ArrayList<>();
        Sort sort = Sort.by("start").descending();
        LocalDateTime now = LocalDateTime.now();
        switch (state) {
            case ALL:
                usersBooking = bookingRepository.findAllByBookerIdOrderByStartDesc(userId, PageRequest.of(from / size, size))
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
                break;
            case WAITING:
                usersBooking = bookingRepository.findAllByBookerAndStatusEquals(user, BookingStatus.WAITING, PageRequest.of(from / size, size, sort))
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
                break;
            case APPROVED:
                usersBooking = bookingRepository.findAllByBookerAndStatusEquals(user, BookingStatus.APPROVED, PageRequest.of(from / size, size, sort))
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
                break;
            case REJECTED:
                usersBooking = bookingRepository.findAllByBookerAndStatusEquals(user, BookingStatus.REJECTED, PageRequest.of(from / size, size, sort))
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
                break;
            case CURRENT:
                usersBooking = bookingRepository.findCurrentBookerBookings(userId, now, PageRequest.of(from / size, size))
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
                break;
            case FUTURE:
                usersBooking = bookingRepository.findAllByBookerIdAndStartIsAfter(userId, now, PageRequest.of(from / size, size, sort))
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
                break;
            case PAST:
                usersBooking = bookingRepository.findAllByBookerIdAndEndIsBefore(userId, now, PageRequest.of(from / size, size, sort))
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
                break;
            case UNSUPPORTED_STATUS:
                throw new ValidationStateException("Unknown state: UNSUPPORTED_STATUS");
        }
        return usersBooking;
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getUserItemsBookings(Long userId, State state, int from, int size) {
        checkParameters(from, size);
        User user = checkUser(userId);
        List<BookingDto> bookings = new ArrayList<>();
        Sort sort = Sort.by("start").descending();
        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllByItemOwnerIdOrderByStartDesc(userId, PageRequest.of(from / size, size))
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
                break;
            case WAITING:
                bookings = bookingRepository.findAllByItem_OwnerAndStatusEquals(user, BookingStatus.WAITING, PageRequest.of(from / size, size, sort))
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
                break;
            case APPROVED:
                bookings = bookingRepository.findAllByItem_OwnerAndStatusEquals(user, BookingStatus.APPROVED, PageRequest.of(from / size, size, sort))
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByItem_OwnerAndStatusEquals(user, BookingStatus.REJECTED, PageRequest.of(from / size, size, sort))
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
                break;
            case CURRENT:
                bookings = bookingRepository.findBookingsByItemOwnerCurrent(userId, LocalDateTime.now(), PageRequest.of(from / size, size))
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByItemOwnerIdAndStartIsAfter(userId, LocalDateTime.now(), PageRequest.of(from / size, size, sort))
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
                break;
            case PAST:
                bookings = bookingRepository.findAllByItemOwnerIdAndEndIsBefore(userId, LocalDateTime.now(), PageRequest.of(from / size, size, sort))
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
                break;
            case UNSUPPORTED_STATUS:
                throw new ValidationStateException("Unknown state: UNSUPPORTED_STATUS");
        }
        return bookings;
    }

    private User checkUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
    }

    private void checkParameters(int from, int size) {
        if (size <= 0 || from < 0) {
            throw new ValidationException("Неверно заданы параметры size и from");
        }
    }

    private Booking checkBooking(Long bookingId) {
        return bookingRepository.findById(bookingId).orElseThrow(() -> new BookingNotFoundException("Бронирование не найдено"));
    }
}
