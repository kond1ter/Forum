package study.konditer.forum.dto;

import java.time.LocalDateTime;

public record UserOutputDto(
    Long id,
    LocalDateTime createdAt,
    String name,
    Integer approvedReportsAmount
) {}
