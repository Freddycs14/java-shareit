package ru.practicum.shareit.request.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "requests")
@Table(name = "requests")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;
    @Column(name = "description")
    String description;
    @ManyToOne
    @JoinColumn(name = "users_id")
    User requestor;
    @Column(name = "created")
    LocalDateTime created;
}
