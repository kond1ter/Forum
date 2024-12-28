package study.konditer.forum.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import study.konditer.forum.dto.PinRequestInputDto;
import study.konditer.forum.dto.PinRequestOutputDto;
import study.konditer.forum.dto.UserOutputDto;
import study.konditer.forum.exception.NotFoundServiceException;
import study.konditer.forum.exception.QuestionClosedServiceException;
import study.konditer.forum.exception.ResourceAccessServiceException;
import study.konditer.forum.exception.UserBannedServiceException;
import study.konditer.forum.model.PinRequest;
import study.konditer.forum.model.Question;
import study.konditer.forum.model.User;
import study.konditer.forum.model.emun.PinRequestStatus;
import study.konditer.forum.repository.PinRequestRepository;
import study.konditer.forum.repository.QuestionRepository;
import study.konditer.forum.repository.UserRepository;
import study.konditer.forum.service.PinRequestService;

@Service
public class PinRequestServiceImpl implements PinRequestService {

    private final PinRequestRepository pinRequestRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;

    public PinRequestServiceImpl(
            UserRepository userRepository,
            PinRequestRepository pinRequestRepository,
            QuestionRepository questionRepository) {
        this.userRepository = userRepository;
        this.pinRequestRepository = pinRequestRepository;
        this.questionRepository = questionRepository;
    }
    
    @Override
    public void add(PinRequestInputDto pinRequestDto) {
        User author = userRepository.findById(pinRequestDto.authorId())
                .orElseThrow(() -> new NotFoundServiceException("User not found"));

        if (author.isBanned()) {
            throw new UserBannedServiceException("User is banned");
        }

        PinRequest pinRequest = mapToEntity(pinRequestDto);
        List<PinRequest> byQuestion =
                pinRequestRepository.findAllByQuestion(pinRequest.getQuestion().getId());

        if (pinRequest.getQuestion().isClosed()) {
            throw new QuestionClosedServiceException("Question closed");
        }

        if (pinRequest.getQuestion().getAuthor().getId() != pinRequestDto.authorId()) {
            throw new ResourceAccessServiceException("Question unavailable for current user");
        }

        if (!byQuestion.isEmpty() && byQuestion.get(0).getStatus().equals(PinRequestStatus.PENDING)) {
            throw new RuntimeException("Request is already created");
        }
        pinRequestRepository.save(pinRequest);
    }

    @Override
    public void approve(Long id) {
        PinRequest pinRequest = pinRequestRepository.findById(id)
                .orElseThrow(() -> new NotFoundServiceException("Pin request not found"));

        if (!pinRequest.getStatus().equals(PinRequestStatus.PENDING)) {
            throw new ResourceAccessServiceException("Pin request is already processed");
        }
        
        Question question = pinRequest.getQuestion();
        question.setPinnedTo(LocalDateTime.now().plusDays(pinRequest.getDays()));
        pinRequest.setStatus(PinRequestStatus.APPROVED);

        questionRepository.save(question);
        pinRequestRepository.save(pinRequest);
    }

    @Override
    public void reject(Long id) {
        PinRequest pinRequest = pinRequestRepository.findById(id)
                .orElseThrow(() -> new NotFoundServiceException("Pin request not found"));

        if (!pinRequest.getStatus().equals(PinRequestStatus.PENDING)) {
            throw new ResourceAccessServiceException("Pin request is already processed");
        }
        
        pinRequest.setStatus(PinRequestStatus.REJECTED);
        pinRequestRepository.save(pinRequest);
    }

    @Override
    public PinRequestOutputDto get(Long id) {
        PinRequest pinRequest = pinRequestRepository.findById(id)
                .orElseThrow(() -> new NotFoundServiceException("Pin request not found"));
        
        return mapToDto(pinRequest);
    }

    @Override
    public Page<PinRequestOutputDto> getPage(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PinRequest> pinRequests = pinRequestRepository.findPage(pageable);

        return pinRequests.map(r -> mapToDto(r));
    }

    @Override
    public List<PinRequestOutputDto> getAllByQuestion(Long id) {
        questionRepository.findById(id)
                .orElseThrow(() -> new NotFoundServiceException("Question not found"));
        
        List<PinRequest> pinRequests = pinRequestRepository.findAllByQuestion(id);
        return pinRequests.stream().map(r -> mapToDto(r)).toList();
    }

    @Override
    public List<PinRequestOutputDto> getAll() {
        List<PinRequest> pinRequestDtos = pinRequestRepository.findAll();
        return pinRequestDtos.stream().map(r -> mapToDto(r)).toList();
    }

    private PinRequestOutputDto mapToDto(PinRequest pinRequest) {
        User author = pinRequest.getQuestion().getAuthor();

        return new PinRequestOutputDto(
            pinRequest.getId(),
            pinRequest.getCreatedAt(),
            new UserOutputDto(
                author.getId(),
                author.getCreatedAt(),
                author.getName(),
                author.getApprovedReportsAmount()
            ),
            pinRequest.getQuestion().getId(),
            pinRequest.getDays(),
            pinRequest.getStatus().name()
        );
    }
    
    private PinRequest mapToEntity(PinRequestInputDto pinRequestDto) {

        Question question = questionRepository.findById(pinRequestDto.questionId())
                .orElseThrow(() -> new NotFoundServiceException("Question not found"));

        return new PinRequest(
            LocalDateTime.now(),
            question,
            PinRequestStatus.PENDING,
            pinRequestDto.days()
        );
    }
}
