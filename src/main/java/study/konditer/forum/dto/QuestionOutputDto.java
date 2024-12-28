package study.konditer.forum.dto;

import java.time.LocalDateTime;
import java.util.List;

public record QuestionOutputDto(
    Long id,
    LocalDateTime createdAt,
    UserOutputDto author,
    String title,
    String text,
    LocalDateTime pinnedTo,
    Boolean closed,
    Boolean banned,
    List<TagOutputDto> tags
) {}
