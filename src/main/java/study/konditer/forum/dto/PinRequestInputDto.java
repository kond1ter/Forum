package study.konditer.forum.dto;

public record PinRequestInputDto(
    Long authorId,
    Long questionId,
    Integer days
) {}
