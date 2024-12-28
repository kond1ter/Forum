package study.konditer.forum.service;

import java.util.List;

import org.springframework.data.domain.Page;

import study.konditer.forum.dto.QuestionInputDto;
import study.konditer.forum.dto.QuestionOutputDto;

public interface QuestionService {
    
    void add(QuestionInputDto question);

    void close(Long id, Long userId);

    QuestionOutputDto get(Long id);

    QuestionOutputDto get(Long id, Long userId);

    List<QuestionOutputDto> getAll();

    Page<QuestionOutputDto> getPage(Long tagId, int page, int size);

    Page<QuestionOutputDto> getPageByUser(Long userId, Long tagId, int page, int size);
}
