package study.konditer.forum.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import study.konditer.forum.dto.ReportInputDto;
import study.konditer.forum.dto.ReportOutputDto;
import study.konditer.forum.dto.UserOutputDto;
import study.konditer.forum.exception.NotFoundServiceException;
import study.konditer.forum.exception.UserBannedServiceException;
import study.konditer.forum.model.Answer;
import study.konditer.forum.model.Question;
import study.konditer.forum.model.Report;
import study.konditer.forum.model.User;
import study.konditer.forum.model.emun.ReportStatus;
import study.konditer.forum.repository.AnswerRepository;
import study.konditer.forum.repository.QuestionRepository;
import study.konditer.forum.repository.ReportRepository;
import study.konditer.forum.repository.UserRepository;
import study.konditer.forum.service.ReportService;

@Service
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;

    public ReportServiceImpl(ReportRepository reportRepository,
            UserRepository userRepository,
            AnswerRepository answerRepository,
            QuestionRepository questionRepository) {
        this.reportRepository = reportRepository;
        this.userRepository = userRepository;
        this.answerRepository = answerRepository;
        this.questionRepository = questionRepository;
    }

    @Override
    public void add(ReportInputDto reportDto) {
        User author = userRepository.findById(reportDto.authorId())
                .orElseThrow(() -> new NotFoundServiceException("User not found"));

        if (author.isBanned()) {
            throw new UserBannedServiceException("User is banned");
        }

        reportRepository.save(mapToEntity(reportDto));
    }

    @Override
    public void approve(long id, boolean ban) {

        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new NotFoundServiceException("Report not found"));
        Question reportedQuestion = report.getReportedQuestion();
        Answer reportedAnswer = report.getReportedAnswer();
        User reportedUser = reportedAnswer == null
                ? reportedQuestion.getAuthor()
                : reportedAnswer.getAuthor();

        reportedUser.setApprovedReportsAmount(reportedUser.getApprovedReportsAmount() + 1);
        if (reportedUser.getApprovedReportsAmount() >= User.BAN_REPORTS_AMOUNT || ban) {
            reportedUser.setBanned(true);
        }
        userRepository.save(reportedUser);

        if (reportedAnswer == null) {
            List<Report> reports = reportRepository.findByReportedQuestion(reportedQuestion.getId());
            reports.forEach(r -> r.setStatus(ReportStatus.APPROVED));
            reportedQuestion.setBanned(true);

            reportRepository.saveAll(reports);
            questionRepository.save(reportedQuestion);
        }

        if (reportedAnswer != null) {
            List<Report> reports = reportRepository.findByReportedAnswer(reportedAnswer.getId());
            reports.forEach(r -> r.setStatus(ReportStatus.APPROVED));
            reportedAnswer.setBanned(true);

            reportRepository.saveAll(reports);
            answerRepository.save(reportedAnswer);
        }
    }

    @Override
    public void reject(long id) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new NotFoundServiceException("Report not found"));
        report.setStatus(ReportStatus.REJECTED);
        reportRepository.save(report);
    }

    @Override
    public ReportOutputDto get(long id) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new NotFoundServiceException("Report not found"));
        return mapToDto(report);
    }

    @Override
    public List<ReportOutputDto> getAll() {
        List<Report> reports = reportRepository.findAll();
        return reports.stream()
                .map(report -> mapToDto(report)).toList();
    }

    @Override
    public Page<ReportOutputDto> getPage(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Report> reports = reportRepository.findPage(pageable);

        return reports.map(r -> mapToDto(r));
    }

    private Report mapToEntity(ReportInputDto reportDto) {

        User author = userRepository.findById(reportDto.authorId())
                .orElseThrow(() -> new NotFoundServiceException("User not found"));

        Question question = questionRepository.findById(reportDto.questionId())
                .orElseThrow(() -> new NotFoundServiceException("Question not found"));

        Answer answer = null;
        if (reportDto.answerId() != null) {
            answer = answerRepository.findById(reportDto.answerId())
                    .orElseThrow(() -> new NotFoundServiceException("Answer not found"));
        }

        return new Report(
            LocalDateTime.now(),
            author,
            question,
            answer,
            reportDto.text(),
            ReportStatus.PENDING
        );
    }

    private ReportOutputDto mapToDto(Report report) {
        User author = report.getReportFrom();

        return new ReportOutputDto(
            report.getId(),
            report.getCreatedAt(),
            new UserOutputDto(
                author.getId(),
                author.getCreatedAt(),
                author.getName(),
                author.getApprovedReportsAmount()
            ),
            report.getReportedQuestion().getId(),
            report.getReportedAnswer() == null ? null : report.getReportedAnswer().getId(),
            report.getReportText(),
            report.getStatus().name()
        );
    }
}
