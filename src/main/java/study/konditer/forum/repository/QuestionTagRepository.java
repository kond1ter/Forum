package study.konditer.forum.repository;

import org.springframework.stereotype.Repository;

import study.konditer.forum.model.QuestionTag;

@Repository
public interface QuestionTagRepository extends BaseRepository<QuestionTag, Long> {
    
}
