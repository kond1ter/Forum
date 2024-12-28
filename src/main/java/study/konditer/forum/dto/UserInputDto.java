package study.konditer.forum.dto;

public record UserInputDto(
    String name,
    String password,
    String confirmPassword
) {}
