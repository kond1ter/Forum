package study.konditer.forum.dto;

public record ReportInputDto(
    Long authorId,
    Long questionId,
    Long answerId,
    String text
) {}
