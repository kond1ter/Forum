package study.konditer.forum.dto;

import java.time.LocalDateTime;

public record ReactionOutputDto(
    Long id,
    LocalDateTime createdAt,
    UserOutputDto author,
    Long answerId,
    Boolean positive
) {}
