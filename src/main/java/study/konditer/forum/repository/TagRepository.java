package study.konditer.forum.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import study.konditer.forum.model.Tag;

@Repository
public interface TagRepository extends BaseRepository<Tag, Long> {
    
    @Query("select t from Tag t " + 
        "join t.questions qt " + 
        "join qt.question q " + 
        "where q.id = ?1")
    List<Tag> findAllByQuestionId(long questionId);
}
