package study.konditer.forum.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "reactions")
public class Reaction extends BaseEntity {

    private LocalDateTime createdAt;
    private User author;
    private Answer answer;
    private boolean positive;

    protected Reaction() {
    }

    public Reaction(LocalDateTime createdAt, User author, Answer answer, boolean positive) {
        this.createdAt = createdAt;
        this.author = author;
        this.answer = answer;
        this.positive = positive;
    }

    @Column(name = "created_at", nullable = false)
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Column(name = "is_positive", nullable = false)
    public boolean isPositive() {
        return positive;
    }

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    public User getAuthor() {
        return author;
    }

    @ManyToOne
    @JoinColumn(name = "answer_id", nullable = false)
    public Answer getAnswer() {
        return answer;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public void setAnswer(Answer answer) {
        this.answer = answer;
    }

    public void setPositive(boolean positive) {
        this.positive = positive;
    }

}
