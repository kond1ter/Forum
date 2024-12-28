package study.konditer.forum.model;

import java.time.LocalDateTime;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "questions")
public class Question extends BaseEntity {

    private String title;
    private String text;
    private boolean closed;
    private LocalDateTime pinnedTo;
    private LocalDateTime createdAt;
    private User author;
    private Set<Answer> answers;
    private Set<QuestionTag> tags;
    private boolean banned;

    protected Question() {
    }

    public Question(String title, String text, boolean closed,
            LocalDateTime pinnedTo, LocalDateTime createdAt, User author,
            Set<Answer> answers, Set<QuestionTag> tags, boolean banned) {
        this.title = title;
        this.text = text;
        this.closed = closed;
        this.pinnedTo = pinnedTo;
        this.createdAt = createdAt;
        this.author = author;
        this.answers = answers;
        this.tags = tags;
        this.banned = banned;
    }

    @Column(name = "title", nullable = false)
    public String getTitle() {
        return title;
    }

    @Column(name = "text", columnDefinition = "TEXT", nullable = false)
    public String getText() {
        return text;
    }

    @Column(name = "is_closed", nullable = false)
    public boolean isClosed() {
        return closed;
    }

    @Column(name = "pinned_to", nullable = true)
    public LocalDateTime getPinnedTo() {
        return pinnedTo;
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
    @JoinColumn(name = "author_id", nullable = false)
    public User getAuthor() {
        return author;
    }

    @OneToMany(mappedBy = "question", fetch = FetchType.EAGER)
    public Set<QuestionTag> getTags() {
        return tags;
    }

    @OneToMany(mappedBy = "question", fetch = FetchType.EAGER)
    public Set<Answer> getAnswers() {
        return answers;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    public void setPinnedTo(LocalDateTime pinnedTo) {
        this.pinnedTo = pinnedTo;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setTags(Set<QuestionTag> tags) {
        this.tags = tags;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public void setAnswers(Set<Answer> answers) {
        this.answers = answers;
    }

    public void setBanned(boolean banned) {
        this.banned = banned;
    }

}
