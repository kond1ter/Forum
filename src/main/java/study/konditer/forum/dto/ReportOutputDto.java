package study.konditer.forum.dto;

import java.time.LocalDateTime;

public record ReportOutputDto(
    Long id,
    LocalDateTime createdAt,
    UserOutputDto author,
    Long questionId,
    Long answerId,
    String text,
    String status
) {}
