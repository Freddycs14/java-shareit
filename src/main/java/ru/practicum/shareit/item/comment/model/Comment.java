package ru.practicum.shareit.item.comment.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "comments")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String text;
    @ManyToOne
    @JoinColumn(name = "items_id")
    Item item;
    @ManyToOne
    @JoinColumn(name = "users_id")
    User user;
    LocalDateTime created;
}
