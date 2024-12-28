package study.konditer.forum.controller;

import java.security.Principal;
import java.util.List;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import study.konditer.forum.dto.AnswerOutputDto;
import study.konditer.forum.dto.QuestionOutputDto;
import study.konditer.forum.dto.ReportInputDto;
import study.konditer.forum.dto.ReportOutputDto;
import study.konditer.forum.dto.UserOutputDto;
import study.konditer.forum.exception.ServiceException;
import study.konditer.forum.service.AnswerService;
import study.konditer.forum.service.QuestionService;
import study.konditer.forum.service.ReportService;
import study.konditer.forum.service.UserService;
import study.konditer.forumcontract.controller.ReportController;
import study.konditer.forumcontract.input.ReportCreateInputModel;
import study.konditer.forumcontract.viewmodel.AnswerViewModel;
import study.konditer.forumcontract.viewmodel.BaseViewModel;
import study.konditer.forumcontract.viewmodel.CreateReportPageViewModel;
import study.konditer.forumcontract.viewmodel.QuestionViewModel;
import study.konditer.forumcontract.viewmodel.ReportPageViewModel;
import study.konditer.forumcontract.viewmodel.ReportViewModel;
import study.konditer.forumcontract.viewmodel.ReportsPageViewModel;
import study.konditer.forumcontract.viewmodel.TagViewModel;
import study.konditer.forumcontract.viewmodel.UserViewModel;

@Controller
public class ReportControllerImpl implements ReportController {

    private static final Logger LOG = LogManager.getLogger(Controller.class);

    private final QuestionService questionService;
    private final AnswerService answerService;
    private final UserService userService;
    private final ReportService reportService;

    public ReportControllerImpl(
            QuestionService questionService,
            AnswerService answerService,
            ReportService reportService,
            UserService userService) {
        this.questionService = questionService;
        this.answerService = answerService;
        this.userService = userService;
        this.reportService = reportService;
    }

    private ReportViewModel mapReportViewModel(ReportOutputDto reportDto) {
        return new ReportViewModel(
            reportDto.id(),
            reportDto.questionId(),
            reportDto.author().name(),
            reportDto.text(),
            reportDto.createdAt(),
            reportDto.status()
        );
    }

    private QuestionViewModel mapQuestionViewModel(QuestionOutputDto questionDto) {
        return new QuestionViewModel(
            questionDto.id(),
            questionDto.title(),
            questionDto.text(),
            questionDto.author().id(),
            questionDto.author().name(),
            questionDto.createdAt(),
            questionDto.pinnedTo(),
            questionDto.tags().stream().map(t -> new TagViewModel(t.id(), t.name())).toList(),
            questionDto.closed()
        );
    }

    private AnswerViewModel mapAnswerViewModel(AnswerOutputDto answerDto) {
        return new AnswerViewModel(
            answerDto.id(),
            answerDto.text(),
            answerDto.author().id(),
            answerDto.author().name(),
            answerDto.createdAt(),
            null, null, null
        );
    }

    @Override
    public String reportsList(int page, int size, Principal principal, 
            Model model, RedirectAttributes redirectAttributes) {

        UserOutputDto user = userService.getByName(principal.getName());
        LOG.log(Level.INFO, String.format(
                "Request to /reports; Method: GET; Username: %s; User ID: %s; Timestamp: %s", 
                user.name(), user.id(), System.currentTimeMillis()
        ));

        Page<ReportOutputDto> reportDtos;

        try {
            reportDtos = reportService.getPage(page, size);
        }
        catch (ServiceException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/error";
        }

        List<ReportViewModel> reportViewModels = reportDtos.stream()
            .map(r -> mapReportViewModel(r)).toList();
        UserViewModel userModel = new UserViewModel(
            user.id(),
            user.name()
        );
        ReportsPageViewModel viewModel = new ReportsPageViewModel(
            new BaseViewModel("Reports", userModel), 
            reportViewModels,
            reportDtos.getTotalPages()
        );
        
        model.addAttribute("model", viewModel);
        model.addAttribute("page", page);
        model.addAttribute("size", size);

        return "reports-list";
    }

    @Override
    public String report(Long id, Principal principal, 
            Model model, RedirectAttributes redirectAttributes) {

        UserOutputDto user = userService.getByName(principal.getName());
        LOG.log(Level.INFO, String.format(
                "Request to /reports/%s; Method: GET; Username: %s; User ID: %s; Timestamp: %s", 
                id, user.name(), user.id(), System.currentTimeMillis()
        ));

        ReportOutputDto reportDto;
        ReportViewModel reportViewModel;
        QuestionViewModel questionViewModel;
        AnswerViewModel answerViewModel = null;

        try {
            reportDto = reportService.get(id);
            QuestionOutputDto questionDto = questionService.get(reportDto.questionId());
            questionViewModel = mapQuestionViewModel(questionDto);

            if (reportDto.answerId() != null) {
                AnswerOutputDto answerDto = answerService.get(reportDto.answerId());
                answerViewModel = mapAnswerViewModel(answerDto);
            }
        }
        catch (ServiceException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/error";
        }

        reportViewModel = mapReportViewModel(reportDto);
        UserViewModel userModel = new UserViewModel(
            user.id(),
            user.name()
        );
        ReportPageViewModel viewModel = new ReportPageViewModel(
            new BaseViewModel("Create new report", userModel),
            reportViewModel,
            questionViewModel,
            answerViewModel
        );

        model.addAttribute("model", viewModel);

        return "report";
    }

    @Override
    public String newReport(Long questionId, Long answerId, Principal principal, 
            Model model, RedirectAttributes redirectAttributes) {

        UserOutputDto user = userService.getByName(principal.getName());
        LOG.log(Level.INFO, String.format(
                "Request to /reports/create(questionId=%s, answerId=%s); Method: GET; Username: %s; User ID: %s; Timestamp: %s", 
                questionId, answerId, user.name(), user.id(), System.currentTimeMillis()
        ));

        QuestionViewModel questionViewModel;
        AnswerViewModel answerViewModel = null;

        try {
            QuestionOutputDto questionDto = questionService.get(questionId);
            questionViewModel = mapQuestionViewModel(questionDto);

            if (answerId != null) {
                AnswerOutputDto answerDto = answerService.get(answerId);
                answerViewModel = mapAnswerViewModel(answerDto);
            }
        }
        catch (ServiceException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/error";
        }

        UserViewModel userModel = new UserViewModel(
            user.id(),
            user.name()
        );
        CreateReportPageViewModel viewModel = new CreateReportPageViewModel(
            new BaseViewModel("Create new report", userModel),
            questionViewModel,
            answerViewModel
        );

        if (!model.containsAttribute("reportModel")) {
            model.addAttribute("reportModel", 
                    new ReportCreateInputModel(questionId, answerId, ""));
        }
        model.addAttribute("model", viewModel);

        return "new-report";
    }

    @Override
    public String registerReport(@Valid ReportCreateInputModel reportModel, BindingResult bindingResult,
            Principal principal, RedirectAttributes redirectAttributes) {

        UserOutputDto user = userService.getByName(principal.getName());
        LOG.log(Level.INFO, String.format(
                "Request to /reports/create; Method: POST; Username: %s; User ID: %s; Timestamp: %s", 
                user.name(), user.id(), System.currentTimeMillis()
        ));

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("reportModel", reportModel);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.reportModel",
                bindingResult);

            return "redirect:/reports/create?questionId=" + reportModel.reportedQuestionId() +
                    "&answerId=" + (reportModel.reportedAnswerId() == null ? "" : reportModel.reportedAnswerId());
        }

        ReportInputDto reportDto = new ReportInputDto(
            user.id(),
            reportModel.reportedQuestionId(),
            reportModel.reportedAnswerId(),
            reportModel.reportText()
        );

        try {
            reportService.add(reportDto);
        }
        catch (ServiceException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/error";
        }

        return "redirect:/questions/" + reportModel.reportedQuestionId();
    }

    @Override
    public String rejectReport(Long id, Principal principal, RedirectAttributes redirectAttributes) {

        UserOutputDto user = userService.getByName(principal.getName());
        LOG.log(Level.INFO, String.format(
                "Request to /reports/reject/%s; Method: GET; Username: %s; User ID: %s; Timestamp: %s", 
                id, user.name(), user.id(), System.currentTimeMillis()
        ));

        try {
            reportService.reject(id);
        }
        catch (ServiceException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/error";
        }

        return "redirect:/reports";
    }

    @Override
    public String approveReport(Long id, Boolean ban, Principal principal, RedirectAttributes redirectAttributes) {

        UserOutputDto user = userService.getByName(principal.getName());
        LOG.log(Level.INFO, String.format(
                "Request to /reports/approve/%s; Method: GET; Username: %s; User ID: %s; Timestamp: %s", 
                id, user.name(), user.id(), System.currentTimeMillis()
        ));

        try {
            reportService.approve(id, ban);
        }
        catch (ServiceException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/error";
        }

        return "redirect:/reports";
    }
}
