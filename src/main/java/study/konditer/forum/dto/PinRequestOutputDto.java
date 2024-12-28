package study.konditer.forum.dto;

import java.time.LocalDateTime;

public record PinRequestOutputDto(
    Long id,
    LocalDateTime createdAt,
    UserOutputDto author,
    Long questionId,
    Integer days,
    String status
) {}
