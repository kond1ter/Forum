package study.konditer.forum.service;

import java.util.List;

import study.konditer.forum.dto.ReactionInputDto;
import study.konditer.forum.dto.ReactionOutputDto;

public interface ReactionService {
    
    void add(ReactionInputDto reaction);

    void remove(long id, Long userId);

    ReactionOutputDto get(long id);

    List<ReactionOutputDto> getAll();

    List<ReactionOutputDto> getAllByAnswer(long answerId);
}
