package study.konditer.forum.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import study.konditer.forum.model.Answer;

@Repository
public interface AnswerRepository extends SafeBaseRepository<Answer, Long> {
    
    @Query("select a from Answer a " + 
        "join a.question q join a.author u " + 
        "where q.id = ?1 and a.banned = false and u.banned = false " +
        "order by a.createdAt desc")
    List<Answer> findAllByQuestionIdAndNotBanned(long questionId);
}
