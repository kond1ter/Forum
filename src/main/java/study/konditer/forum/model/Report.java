package study.konditer.forum.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import study.konditer.forum.model.emun.ReportStatus;

@Entity
@Table(name = "reports")
public class Report extends BaseEntity {

    private LocalDateTime createdAt;
    private User reportFrom;
    private Question reportedQuestion;
    private Answer reportedAnswer;
    private String reportText;
    private ReportStatus status;

    protected Report() {
    }

    public Report(LocalDateTime createdAt, User reportFrom, Question reportedQuestion, 
            Answer reportedAnswer, String reportText, ReportStatus status) {
        this.createdAt = createdAt;
        this.reportFrom = reportFrom;
        this.reportedQuestion = reportedQuestion;
        this.reportedAnswer = reportedAnswer;
        this.reportText = reportText;
        this.status = status;
    }

    @Column(name = "created_at", nullable = false)
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Column(name = "status", nullable = false)
    public ReportStatus getStatus() {
        return status;
    }

    @Column(name = "text", nullable = false)
    public String getReportText() {
        return reportText;
    }

    @ManyToOne
    @JoinColumn(name = "report_from", nullable = false)
    public User getReportFrom() {
        return reportFrom;
    }

    @ManyToOne
    @JoinColumn(name = "reported_question", nullable = true)
    public Question getReportedQuestion() {
        return reportedQuestion;
    }

    @ManyToOne
    @JoinColumn(name = "reported_answer", nullable = true)
    public Answer getReportedAnswer() {
        return reportedAnswer;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setReportFrom(User reportFrom) {
        this.reportFrom = reportFrom;
    }

    public void setReportedQuestion(Question reportedQuestion) {
        this.reportedQuestion = reportedQuestion;
    }

    public void setReportedAnswer(Answer reportedAnswer) {
        this.reportedAnswer = reportedAnswer;
    }

    public void setStatus(ReportStatus status) {
        this.status = status;
    }

    public void setReportText(String reportText) {
        this.reportText = reportText;
    }
}
