package study.konditer.forum.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import study.konditer.forum.model.emun.PinRequestStatus;

@Entity
@Table(name = "pin_requests")
public class PinRequest extends BaseEntity {
    
    private LocalDateTime createdAt;
    private Question question;
    private PinRequestStatus status;
    private Integer days;

    protected PinRequest() {}

    public PinRequest(LocalDateTime createdAt, Question question, 
            PinRequestStatus status, Integer days) {
        this.createdAt = createdAt;
        this.question = question;
        this.status = status;
        this.days = days;
    }

    @Column(name = "created_at", nullable = false)
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Column(name = "status", nullable = false)
    public PinRequestStatus getStatus() {
        return status;
    }

    @Column(name = "days", nullable = false)
    public Integer getDays() {
        return days;
    }

    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    public Question getQuestion() {
        return question;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setStatus(PinRequestStatus status) {
        this.status = status;
    }

    public void setDays(Integer days) {
        this.days = days;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }
}
