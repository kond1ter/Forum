package study.konditer.forum.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import study.konditer.forum.model.Report;

@Repository
public interface ReportRepository extends SafeBaseRepository<Report, Long> {
    
    @Query("select r from Report r " +
        "join r.reportedQuestion q " + 
        "where q.id = ?1")
    List<Report> findByReportedQuestion(long questionId);

    @Query("select r from Report r " +
        "join r.reportedAnswer a " + 
        "where a.id = ?1")
    List<Report> findByReportedAnswer(long answerId);

    @Query("select r from Report r " +
        "order by r.createdAt desc")
    Page<Report> findPage(Pageable pageable);
}
