package study.konditer.forum.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import study.konditer.forum.model.PinRequest;

@Repository
public interface PinRequestRepository extends SafeBaseRepository<PinRequest, Long> {

    @Query("select r from PinRequest r " + 
        "join r.question q " + 
        "where q.id = ?1 " + 
        "order by r.createdAt desc")
    List<PinRequest> findAllByQuestion(Long id);

    @Query("select r from PinRequest r " +
        "order by r.createdAt desc")
    Page<PinRequest> findPage(Pageable pageable);
}
