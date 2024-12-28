package study.konditer.forum.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import study.konditer.forum.dto.AnswerInputDto;
import study.konditer.forum.dto.AnswerOutputDto;
import study.konditer.forum.dto.UserOutputDto;
import study.konditer.forum.exception.NotFoundServiceException;
import study.konditer.forum.model.Answer;
import study.konditer.forum.model.Question;
import study.konditer.forum.model.User;
import study.konditer.forum.repository.AnswerRepository;
import study.konditer.forum.repository.QuestionRepository;
import study.konditer.forum.repository.UserRepository;
import study.konditer.forum.service.AnswerService;

@Service
public class AnswerServiceImpl implements AnswerService {

    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;

    public AnswerServiceImpl(AnswerRepository answerRepository,
            UserRepository userRepository,
            QuestionRepository questionRepository) {
        this.answerRepository = answerRepository;
        this.userRepository = userRepository;
        this.questionRepository = questionRepository;
    }

    @Override
    public void add(AnswerInputDto answerDto) {
        User author = userRepository.findById(answerDto.authorId())
                .orElseThrow(() -> new NotFoundServiceException("User not found"));

        Question question = questionRepository.findById(answerDto.questionId())
                .orElseThrow(() -> new NotFoundServiceException("Question not found"));
        
        if (author.isBanned()) {
            throw new RuntimeException("User is banned");
        }

        if (question.isClosed()) {
            throw new RuntimeException("Question is closed");
        }

        answerRepository.save(mapToEntity(answerDto));
    }

    @Override
    public AnswerOutputDto get(long id) {
        Answer answer = answerRepository.findById(id)
                .orElseThrow(() -> new NotFoundServiceException("Answer not found"));
        return mapToDto(answer);
    }

    @Override
    public List<AnswerOutputDto> getAll() {
        List<Answer> answers = answerRepository.findAll();
        return answers.stream()
                .map(answer -> mapToDto(answer)).toList();
    }

    @Override
    public List<AnswerOutputDto> getAllByQuestion(long questionId) {
        questionRepository.findById(questionId)
                .orElseThrow(() -> new NotFoundServiceException("Question not found"));

        List<Answer> answers = answerRepository.findAllByQuestionIdAndNotBanned(questionId);
        return answers.stream().map(answer -> mapToDto(answer)).toList();
    }

    private Answer mapToEntity(AnswerInputDto answerDto) {

        User author = userRepository.findById(answerDto.authorId())
                .orElseThrow(() -> new NotFoundServiceException("User not found"));

        Question question = questionRepository.findById(answerDto.questionId())
                .orElseThrow(() -> new NotFoundServiceException("Question not found"));

        return new Answer(
            answerDto.text(),
            LocalDateTime.now(),
            question,
            author,
            false
        );
    }

    private AnswerOutputDto mapToDto(Answer answer) {
        User author = answer.getAuthor();

        return new AnswerOutputDto(
            answer.getId(),
            answer.getCreatedAt(),
            new UserOutputDto(
                author.getId(),
                author.getCreatedAt(),
                author.getName(),
                author.getApprovedReportsAmount()
            ),
            answer.getQuestion().getId(),
            answer.getText(),
            answer.isBanned()
        );
    }
}
