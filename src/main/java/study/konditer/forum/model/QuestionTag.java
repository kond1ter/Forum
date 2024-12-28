package study.konditer.forum.model;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "question_tags")
public class QuestionTag extends BaseEntity {

    private Question question;
    private Tag tag;

    protected QuestionTag() {
    }

    public QuestionTag(Question question, Tag tag) {
        this.question = question;
        this.tag = tag;
    }

    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    public Question getQuestion() {
        return question;
    }

    @ManyToOne
    @JoinColumn(name = "tag_id", nullable = false)
    public Tag getTag() {
        return tag;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }
}
