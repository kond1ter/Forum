package study.konditer.forum.dto;

public record ReactionInputDto(
    Long authorId,
    Long answerId,
    Boolean positive
) {}
