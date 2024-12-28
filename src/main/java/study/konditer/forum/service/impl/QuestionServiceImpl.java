package study.konditer.forum.service.impl;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import study.konditer.forum.dto.QuestionInputDto;
import study.konditer.forum.dto.QuestionOutputDto;
import study.konditer.forum.dto.TagOutputDto;
import study.konditer.forum.dto.UserOutputDto;
import study.konditer.forum.exception.NotFoundServiceException;
import study.konditer.forum.exception.QuestionClosedServiceException;
import study.konditer.forum.exception.ResourceAccessServiceException;
import study.konditer.forum.exception.UserBannedServiceException;
import study.konditer.forum.model.Question;
import study.konditer.forum.model.QuestionTag;
import study.konditer.forum.model.Tag;
import study.konditer.forum.model.User;
import study.konditer.forum.repository.QuestionRepository;
import study.konditer.forum.repository.QuestionTagRepository;
import study.konditer.forum.repository.TagRepository;
import study.konditer.forum.repository.UserRepository;
import study.konditer.forum.service.QuestionService;

@Service
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final QuestionTagRepository questionTagRepository;
    private final TagRepository tagRepository;

    public QuestionServiceImpl(QuestionRepository questionRepository,
            UserRepository userRepository, QuestionTagRepository questionTagRepository,
            TagRepository tagRepository) {
        this.questionRepository = questionRepository;
        this.userRepository = userRepository;
        this.questionTagRepository = questionTagRepository;
        this.tagRepository = tagRepository;
    }

    @Override
    public void add(QuestionInputDto questionDto) {
        User author = userRepository.findById(questionDto.authorId())
                .orElseThrow(() -> new NotFoundServiceException("User not found"));

        if (author.isBanned()) {
            throw new UserBannedServiceException("User is banned");
        }

        Question question = mapToEntity(questionDto);
        questionRepository.save(question);

        for (Long tagId : questionDto.tagIds()) {
            Tag tag = tagRepository.findById(tagId)
                    .orElseThrow(() -> new NotFoundServiceException("Tag not found"));
            questionTagRepository.save(new QuestionTag(question, tag));
        }
    }

    @Override
    public void close(Long id, Long userId) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new NotFoundServiceException("Question not found"));

        if (question.getAuthor().getId() != userId) {
            throw new ResourceAccessServiceException("Question unavailable for current user");
        }

        if (question.isClosed()) {
            throw new QuestionClosedServiceException("Question already closed");
        }

        question.setClosed(true);
        questionRepository.save(question);
    }

    @Override
    public QuestionOutputDto get(Long id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new NotFoundServiceException("Question not found"));

        return mapToDto(question);
    }

    @Override
    public QuestionOutputDto get(Long id, Long userId) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new NotFoundServiceException("Question not found"));

        if (question.getAuthor().getId() != userId) {
            throw new ResourceAccessServiceException("Question unavailable for current user");
        }

        return mapToDto(question);
    }

    @Override
    public List<QuestionOutputDto> getAll() {
        List<Question> questions = questionRepository.findAll();
        return questions.stream()
                .map(question -> mapToDto(question)).toList();
    }

    @Override
    public Page<QuestionOutputDto> getPage(Long tagId, int page, int size) {

        Pageable pageable = PageRequest.of(page, size);
        List<Question> questions = tagId == null
            ? questionRepository.findAllNotBannedAndNotClosed()
            : questionRepository.findAllByTagIdNotBannedAndNotClosed(tagId);

        Collections.sort(questions, new QuestionComparator());
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), questions.size());
        List<Question> questionsPage = questions.subList(start, end);

        List<QuestionOutputDto> questionsPageDtos = questionsPage.stream()
                .map(question -> mapToDto(question)).toList();

        return new PageImpl<>(questionsPageDtos, pageable, questions.size());
    }

    @Override
    public Page<QuestionOutputDto> getPageByUser(Long userId, Long tagId, int page, int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Question> questions = tagId == null
            ? questionRepository.findPageByAuthorId(userId, pageable)
            : questionRepository.findPageByTagIdAndAuthorId(tagId, userId, pageable);

        return questions.map(q -> mapToDto(q));
    }

    private Question mapToEntity(QuestionInputDto questionDto) {

        User author = userRepository.findById(questionDto.authorId())
                .orElseThrow(() -> new NotFoundServiceException("User not found"));
        
        return new Question(
            questionDto.title(),
            questionDto.text(),
            false,
            null,
            LocalDateTime.now(),
            author,
            new HashSet<>(),
            new HashSet<>(),
            false
        );
    }

    private QuestionOutputDto mapToDto(Question question) {
        User author = question.getAuthor();

        return new QuestionOutputDto(
            question.getId(),
            question.getCreatedAt(),
            new UserOutputDto(
                author.getId(),
                author.getCreatedAt(),
                author.getName(),
                author.getApprovedReportsAmount()
            ),
            question.getTitle(),
            question.getText(),
            question.getPinnedTo(),
            question.isClosed(),
            question.isBanned(),
            question.getTags().stream().map(t -> new TagOutputDto(
                t.getTag().getId(),
                t.getTag().getName()
            )).sorted(
                (TagOutputDto t0, TagOutputDto t1) -> t0.name().compareToIgnoreCase(t1.name())
            ).toList()
        );
    }

    class QuestionComparator implements Comparator<Question> {

        @Override
        public int compare(Question q0, Question q1) {
            LocalDateTime time = LocalDateTime.now();
            int isPinned0;
            int isPinned1;

            if (q0.getPinnedTo() != null) {
                isPinned0 = q0.getPinnedTo().isAfter(time) ? 1 : 0;
            } else {
                isPinned0 = 0;
            }

            if (q1.getPinnedTo() != null) {
                isPinned1 = q1.getPinnedTo().isAfter(time) ? 1 : 0;
            } else {
                isPinned1 = 0;
            }

            if (isPinned0 != isPinned1) {
                return isPinned1 - isPinned0;
            }

            return q1.getCreatedAt().isAfter(q0.getCreatedAt()) ? 1 : -1;
        }
    }
}
