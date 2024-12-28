package study.konditer.forum.model;

import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "tags")
public class Tag extends BaseEntity {

    private String name;
    private Set<QuestionTag> questions;

    protected Tag() {
    }

    public Tag(String name, Set<QuestionTag> questions) {
        this.name = name;
        this.questions = questions;
    }

    @Column(name = "name", nullable = false, unique = true)
    public String getName() {
        return name;
    }

    @OneToMany(mappedBy = "tag", fetch = FetchType.LAZY)
    public Set<QuestionTag> getQuestions() {
        return questions;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setQuestions(Set<QuestionTag> questions) {
        this.questions = questions;
    }
}
