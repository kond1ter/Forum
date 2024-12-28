package study.konditer.forum.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "answers")
public class Answer extends BaseEntity {

    private String text;
    private LocalDateTime createdAt;
    private Question question;
    private User author;
    private boolean banned;

    protected Answer() {
    }

    public Answer(String text, LocalDateTime createdAt, Question question, User author, boolean banned) {
        this.text = text;
        this.createdAt = createdAt;
        this.question = question;
        this.author = author;
        this.banned = banned;
    }

    @Column(name = "text", columnDefinition = "TEXT", nullable = false)
    public String getText() {
        return text;
    }

    @Column(name = "created_at", nullable = false)
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Column(name = "is_banned", nullable = false)
    public boolean isBanned() {
        return banned;
    }

    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    public Question getQuestion() {
        return question;
    }

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    public User getAuthor() {
        return author;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public void setBanned(boolean banned) {
        this.banned = banned;
    }
}
