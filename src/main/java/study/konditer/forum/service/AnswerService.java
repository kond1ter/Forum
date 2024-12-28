package study.konditer.forum.service;

import java.util.List;

import study.konditer.forum.dto.AnswerInputDto;
import study.konditer.forum.dto.AnswerOutputDto;

public interface AnswerService {
    
    void add(AnswerInputDto answer);

    AnswerOutputDto get(long id);

    List<AnswerOutputDto> getAll();

    List<AnswerOutputDto> getAllByQuestion(long questionId);
}
