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
@Table(name = "users")
public class User extends BaseEntity {

    public static final int BAN_REPORTS_AMOUNT = 3;

    private String name;
    private String password;
    private Role role;
    private LocalDateTime createdAt;
    private Set<Question> questions;
    private Set<Answer> answers;
    private Set<Reaction> reactions;
    private int approvedReportsAmount;
    private boolean banned;

    protected User() {
    }

    public User(String name, String password, Role role, 
            LocalDateTime createdAt, Set<Question> questions, Set<Answer> answers,
            Set<Reaction> reactions, int approvedReportsAmount, boolean banned) {
        this.name = name;
        this.password = password;
        this.role = role;
        this.createdAt = createdAt;
        this.questions = questions;
        this.answers = answers;
        this.reactions = reactions;
        this.approvedReportsAmount = approvedReportsAmount;
        this.banned = banned;
    }

    @Column(name = "name", nullable = false, unique = true)
    public String getName() {
        return name;
    }

    @Column(name = "password", nullable = false)
    public String getPassword() {
        return password;
    }

    @ManyToOne
    @JoinColumn(name = "role", nullable = false)
    public Role getRole() {
        return role;
    }

    @Column(name = "created_at", nullable = false)
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Column(name = "approved_reports_amount", nullable = false)
    public int getApprovedReportsAmount() {
        return approvedReportsAmount;
    }

    @Column(name = "is_banned", nullable = false)
    public boolean isBanned() {
        return banned;
    }

    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY)
    public Set<Question> getQuestions() {
        return questions;
    }

    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY)
    public Set<Answer> getAnswers() {
        return answers;
    }

    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY)
    public Set<Reaction> getReactions() {
        return reactions;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setQuestions(Set<Question> questions) {
        this.questions = questions;
    }

    public void setAnswers(Set<Answer> answers) {
        this.answers = answers;
    }

    public void setReactions(Set<Reaction> reactions) {
        this.reactions = reactions;
    }

    public void setApprovedReportsAmount(int approvedReportsAmount) {
        this.approvedReportsAmount = approvedReportsAmount;
    }

    public void setBanned(boolean banned) {
        this.banned = banned;
    }
}
