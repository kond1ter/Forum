package study.konditer.forum.dto;

import java.time.LocalDateTime;

public record AnswerOutputDto(
    Long id,
    LocalDateTime createdAt,
    UserOutputDto author,
    Long questionId,
    String text,
    Boolean banned
) {}