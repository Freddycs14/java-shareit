package ru.practicum.shareit.item.comment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.mapper.CommentMapper;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CommentMapperTest {
    private Item item;
    private User owner;
    private User booker;

    @BeforeEach
    public void setUp() {
        owner = User.builder().id(1L).name("Walter").email("w.white@gmail.com").build();
        booker = User.builder().id(2L).name("Jesse").email("j.pinkman@gmail.com").build();
        item = Item.builder().id(1L).name("name").description("description").owner(owner).available(true).build();
    }


    @Test
    public void toCommentDtoTest() {
        CommentDto commentDto = CommentDto.builder().id(1L).text("text").authorName(booker.getName())
                .created(LocalDateTime.now()).build();
        Comment result = CommentMapper.toComment(commentDto, item, booker);
        assertEquals(commentDto.getText(), result.getText());
        assertEquals(commentDto.getAuthorName(), result.getUser().getName());
    }

    @Test
    public void toCommentTest() {
        Comment comment = Comment.builder().id(1L).item(item).text("text").user(booker).created(LocalDateTime.now()).build();
        CommentDto result = CommentMapper.toCommentDto(comment);
        assertEquals(comment.getText(), result.getText());
        assertEquals(ItemMapper.toItemDto(item), result.getItemDto());
        assertEquals(booker.getName(), result.getAuthorName());
    }
}
