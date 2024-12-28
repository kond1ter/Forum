package study.konditer.forum.controller;

import java.security.Principal;
import java.util.List;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import study.konditer.forum.dto.PinRequestInputDto;
import study.konditer.forum.dto.PinRequestOutputDto;
import study.konditer.forum.dto.QuestionOutputDto;
import study.konditer.forum.dto.UserOutputDto;
import study.konditer.forum.exception.ServiceException;
import study.konditer.forum.service.PinRequestService;
import study.konditer.forum.service.QuestionService;
import study.konditer.forum.service.UserService;
import study.konditer.forumcontract.controller.PinRequestController;
import study.konditer.forumcontract.input.PinRequestCreateInputModel;
import study.konditer.forumcontract.viewmodel.BaseViewModel;
import study.konditer.forumcontract.viewmodel.CreatePinRequestPageViewModel;
import study.konditer.forumcontract.viewmodel.PinRequestViewModel;
import study.konditer.forumcontract.viewmodel.PinRequestsPageViewModel;
import study.konditer.forumcontract.viewmodel.QuestionViewModel;
import study.konditer.forumcontract.viewmodel.TagViewModel;
import study.konditer.forumcontract.viewmodel.UserViewModel;

@Controller
public class PinRequestControllerImpl implements PinRequestController {

    private static final Logger LOG = LogManager.getLogger(Controller.class);

    private final PinRequestService pinRequestService;
    private final QuestionService questionService;
    private final UserService userService;

    public PinRequestControllerImpl(
            PinRequestService pinRequestService,
            QuestionService questionService,
            UserService userService) {
        this.pinRequestService = pinRequestService;
        this.questionService = questionService;
        this.userService = userService;
    }

    private PinRequestViewModel mapPinRequestViewModel(PinRequestOutputDto pinRequestDto) {
        QuestionOutputDto question = questionService.get(pinRequestDto.questionId());
        QuestionViewModel questionViewModel = mapQuestionViewModel(question);

        return new PinRequestViewModel(
            pinRequestDto.id(),
            pinRequestDto.createdAt(),
            questionViewModel,
            pinRequestDto.status(),
            pinRequestDto.days()
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

    @Override
    public String pinRequestsList(int page, int size, Principal principal, 
            Model model, RedirectAttributes redirectAttributes) {

        UserOutputDto user = userService.getByName(principal.getName());
        LOG.log(Level.INFO, String.format(
                "Request to /pins(page=%s, size=%s); Method: GET; Username: %s; User ID: %s; Timestamp: %s", 
                page, size, user.name(), user.id(), System.currentTimeMillis()
        ));

        Page<PinRequestOutputDto> pinRequestsPage;
        List<PinRequestViewModel> pinRequestViewModels;

        try {
            pinRequestsPage = pinRequestService.getPage(page, size);
            pinRequestViewModels = pinRequestsPage.stream().map(r -> mapPinRequestViewModel(r)).toList();
        }
        catch (ServiceException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/error";
        }

        UserViewModel userModel = new UserViewModel(
            user.id(),
            user.name()
        );
        PinRequestsPageViewModel viewModel = new PinRequestsPageViewModel(
            new BaseViewModel("Pin Requests", userModel), 
            pinRequestViewModels,
            pinRequestsPage.getTotalPages()
        );

        model.addAttribute("model", viewModel);
        model.addAttribute("page", page);
        model.addAttribute("size", size);

        return "pin-requests-list";
    }

    @Override
    public String newPinRequest(Long questionId, Principal principal, 
            Model model, RedirectAttributes redirectAttributes) {

        UserOutputDto user = userService.getByName(principal.getName());
        LOG.log(Level.INFO, String.format(
                "Request to /pins/%s; Method: GET; Username: %s; User ID: %s; Timestamp: %s", 
                questionId, user.name(), user.id(), System.currentTimeMillis()
        ));

        QuestionOutputDto questionDto;

        try {
            questionDto = questionService.get(questionId, user.id());
        }
        catch (ServiceException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/error";
        }

        QuestionViewModel questionViewModel = mapQuestionViewModel(questionDto);
        PinRequestCreateInputModel form = new PinRequestCreateInputModel(questionId, 1);
        UserViewModel userModel = new UserViewModel(
            user.id(),
            user.name()
        );
        CreatePinRequestPageViewModel viewModel = new CreatePinRequestPageViewModel(
            new BaseViewModel("Create Pin Request", userModel), 
            questionViewModel
        );

        model.addAttribute("model", viewModel);
        model.addAttribute("form", form);

        return "new-pin-request";
    }

    @Override
    public String registerPinRequest(PinRequestCreateInputModel pinRequestModel, 
            Principal principal, RedirectAttributes redirectAttributes) {

        UserOutputDto user = userService.getByName(principal.getName());
        LOG.log(Level.INFO, String.format(
                "Request to /pins; Method: POST; Username: %s; User ID: %s; Timestamp: %s", 
                user.name(), user.id(), System.currentTimeMillis()
        ));

        PinRequestInputDto pinRequestDto = new PinRequestInputDto(
            user.id(),
            pinRequestModel.questionId(),
            pinRequestModel.days()
        );

        try {
            pinRequestService.add(pinRequestDto);
        }
        catch (ServiceException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/error";
        }

        return "redirect:/questions/" + pinRequestModel.questionId();
    }

    @Override
    public String rejectPinRequest(Long id, Principal principal, RedirectAttributes redirectAttributes) {

        UserOutputDto user = userService.getByName(principal.getName());
        LOG.log(Level.INFO, String.format(
                "Request to /pins/reject/%s; Method: GET; Username: %s; User ID: %s; Timestamp: %s", 
                id, user.name(), user.id(), System.currentTimeMillis()
        ));

        try {
            pinRequestService.reject(id);
        }
        catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/error";
        }

        return "redirect:/pins";
    }

    @Override
    public String approvePinRequest(Long id, Principal principal, RedirectAttributes redirectAttributes) {

        UserOutputDto user = userService.getByName(principal.getName());
        LOG.log(Level.INFO, String.format(
                "Request to /pins/approve/%s; Method: GET; Username: %s; User ID: %s; Timestamp: %s", 
                id, user.name(), user.id(), System.currentTimeMillis()
        ));

        try {
            pinRequestService.approve(id);
        }
        catch (ServiceException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/error";
        }

        return "redirect:/pins";
    }
}
