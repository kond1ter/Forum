package study.konditer.forum.dto;

public record AnswerInputDto(
    Long authorId,
    Long questionId,
    String text
) {}
