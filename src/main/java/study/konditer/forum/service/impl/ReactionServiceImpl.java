package study.konditer.forum.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import study.konditer.forum.dto.ReactionInputDto;
import study.konditer.forum.dto.ReactionOutputDto;
import study.konditer.forum.dto.UserOutputDto;
import study.konditer.forum.exception.NotFoundServiceException;
import study.konditer.forum.exception.ResourceAccessServiceException;
import study.konditer.forum.exception.UserBannedServiceException;
import study.konditer.forum.model.Answer;
import study.konditer.forum.model.Reaction;
import study.konditer.forum.model.User;
import study.konditer.forum.repository.AnswerRepository;
import study.konditer.forum.repository.ReactionRepository;
import study.konditer.forum.repository.UserRepository;
import study.konditer.forum.service.ReactionService;

@Service
public class ReactionServiceImpl implements ReactionService {

    private final ReactionRepository reactionRepository;
    private final UserRepository userRepository;
    private final AnswerRepository answerRepository;

    public ReactionServiceImpl(ReactionRepository reactionRepository,
            UserRepository userRepository,
            AnswerRepository answerRepository) {
        this.reactionRepository = reactionRepository;
        this.userRepository = userRepository;
        this.answerRepository = answerRepository;
    }

    @Override
    public void add(ReactionInputDto reactionDto) {
        User author = userRepository.findById(reactionDto.authorId())
                .orElseThrow(() -> new NotFoundServiceException("User not found"));

        if (author.isBanned()) {
            throw new UserBannedServiceException("User is banned");
        }

        Optional<Reaction> currReaction = reactionRepository
                .findByUserAndAnswer(reactionDto.answerId(), reactionDto.authorId());
        
        if (currReaction.isPresent()) {
            Reaction reaction = currReaction.get();
            reaction.setPositive(reactionDto.positive());
            reactionRepository.save(reaction);
            return;
        }

        reactionRepository.save(mapToEntity(reactionDto));
    }

    @Override
    public void remove(long id, Long userId) {
        Reaction reaction = reactionRepository.findById(id)
                .orElseThrow(() -> new NotFoundServiceException("Reaction not found"));

        if (reaction.getAuthor().getId() != userId) {
            throw new ResourceAccessServiceException("Reaction unavailable for current user");
        }

        reactionRepository.delete(reaction);
    }

    @Override
    public ReactionOutputDto get(long id) {
        Reaction reaction = reactionRepository.findById(id)
                .orElseThrow(() -> new NotFoundServiceException("Reaction not found"));
        return mapToDto(reaction);
    }

    @Override
    public List<ReactionOutputDto> getAll() {
        List<Reaction> reactions = reactionRepository.findAll();
        return reactions.stream()
                .map(reaction -> mapToDto(reaction)).toList();
    }

    @Override
    public List<ReactionOutputDto> getAllByAnswer(long answerId) {
        List<Reaction> reactions = reactionRepository.findAllByAnswerId(answerId);
        return reactions.stream()
                .map(reaction -> mapToDto(reaction)).toList();
    }

    private Reaction mapToEntity(ReactionInputDto reactionDto) {

        User author = userRepository.findById(reactionDto.authorId())
                .orElseThrow(() -> new NotFoundServiceException("User not found"));
        
        Answer answer = answerRepository.findById(reactionDto.answerId())
                .orElseThrow(() -> new NotFoundServiceException("Answer not found"));

        return new Reaction(
            LocalDateTime.now(),
            author,
            answer,
            reactionDto.positive()
        );
    }

    private ReactionOutputDto mapToDto(Reaction reaction) {
        User author = reaction.getAuthor();

        return new ReactionOutputDto(
            reaction.getId(),
            reaction.getCreatedAt(),
            new UserOutputDto(
                author.getId(),
                author.getCreatedAt(),
                author.getName(),
                author.getApprovedReportsAmount()
            ),
            reaction.getAnswer().getId(),
            reaction.isPositive()
        );
    }
}
