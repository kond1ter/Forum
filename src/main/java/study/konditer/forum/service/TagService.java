package study.konditer.forum.service;

import java.util.List;

import study.konditer.forum.dto.TagInputDto;
import study.konditer.forum.dto.TagOutputDto;

public interface TagService {
    
    void add(TagInputDto tag);

    void remove(long id);

    TagOutputDto get(long id);

    List<TagOutputDto> getAll();

    List<TagOutputDto> getAllByQuestion(long questionId);
}
