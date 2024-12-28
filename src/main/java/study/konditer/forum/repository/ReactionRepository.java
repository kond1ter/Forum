package study.konditer.forum.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import study.konditer.forum.model.Reaction;

@Repository
public interface ReactionRepository extends BaseRepository<Reaction, Long> {
    
    @Query("select r from Reaction r join r.answer a where a.id = ?1")
    List<Reaction> findAllByAnswerId(long answerId);

    @Query("select r from Reaction r " +
        "join r.answer a join r.author u " + 
        "where a.id = ?1 and u.id = ?2")
    Optional<Reaction> findByUserAndAnswer(long answerId, long userId);
}
