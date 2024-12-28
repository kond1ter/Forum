package study.konditer.forum.dto;

import java.util.List;

public record QuestionInputDto(
    Long authorId,
    String title,
    String text,
    List<Long> tagIds
) {}
