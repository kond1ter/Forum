package study.konditer.forum.service;

import java.util.List;

import study.konditer.forum.dto.UserInputDto;
import study.konditer.forum.dto.UserOutputDto;

public interface UserService {
    
    void add(UserInputDto user);

    UserOutputDto get(long id);

    UserOutputDto getByName(String name);

    List<UserOutputDto> getAll();
}
