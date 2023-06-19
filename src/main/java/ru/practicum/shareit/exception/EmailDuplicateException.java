package ru.practicum.shareit.exception;

public class EmailDuplicateException extends RuntimeException {
    public EmailDuplicateException(final String massage) {
        super(massage);
    }
}
