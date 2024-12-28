package study.konditer.forum.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import study.konditer.forum.model.Question;

@Repository
public interface QuestionRepository extends SafeBaseRepository<Question, Long> {
    
    @Query("select q from Question q " + 
        "join q.author a " +
        "where q.banned = false and q.closed = false and a.banned = false")
    List<Question> findAllNotBannedAndNotClosed();

    @Query("select q from Question q " + 
        "join q.tags qt join qt.tag t join q.author a " +
        "where q.banned = false and q.closed = false and t.id = ?1 and a.banned = false")
    List<Question> findAllByTagIdNotBannedAndNotClosed(long tagId);

    @Query("select q from Question q " + 
        "join q.author a " + 
        "where a.id = ?1 " +
        "order by q.createdAt desc")
    Page<Question> findPageByAuthorId(long userId, Pageable pageable);

    @Query("select q from Question q " + 
        "join q.tags qt join qt.tag t join q.author a " +
        "where t.id = ?1 and a.id = ?2 " +
        "order by q.createdAt desc")
    Page<Question> findPageByTagIdAndAuthorId(long tagId, long userId, Pageable pageable);
}
