package study.konditer.forum.controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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
import study.konditer.forum.dto.PinRequestOutputDto;
import study.konditer.forum.dto.QuestionInputDto;
import study.konditer.forum.dto.QuestionOutputDto;
import study.konditer.forum.dto.ReactionOutputDto;
import study.konditer.forum.dto.TagOutputDto;
import study.konditer.forum.dto.UserOutputDto;
import study.konditer.forum.exception.ServiceException;
import study.konditer.forum.model.emun.PinRequestStatus;
import study.konditer.forum.service.AnswerService;
import study.konditer.forum.service.PinRequestService;
import study.konditer.forum.service.QuestionService;
import study.konditer.forum.service.ReactionService;
import study.konditer.forum.service.TagService;
import study.konditer.forum.service.UserService;
import study.konditer.forumcontract.controller.QuestionController;
import study.konditer.forumcontract.input.AnswerCreateInputModel;
import study.konditer.forumcontract.input.QuestionCreateInputModel;
import study.konditer.forumcontract.viewmodel.AnswerViewModel;
import study.konditer.forumcontract.viewmodel.BaseViewModel;
import study.konditer.forumcontract.viewmodel.CreateQuestionPageViewModel;
import study.konditer.forumcontract.viewmodel.HomePageViewModel;
import study.konditer.forumcontract.viewmodel.QuestionPageViewModel;
import study.konditer.forumcontract.viewmodel.QuestionViewModel;
import study.konditer.forumcontract.viewmodel.TagViewModel;
import study.konditer.forumcontract.viewmodel.UserViewModel;

@Controller
public class QuestionControllerImpl implements QuestionController {

    private static final Logger LOG = LogManager.getLogger(Controller.class);

    private final QuestionService questionService;
    private final TagService tagService;
    private final UserService userService;
    private final PinRequestService pinRequestService;
    private final ReactionService reactionService;
    private final AnswerService answerService;

    public QuestionControllerImpl(
            QuestionService questionService,
            TagService tagService,
            PinRequestService pinRequestService,
            ReactionService reactionService,
            AnswerService answerService,
            UserService userService) {
        this.questionService = questionService;
        this.tagService = tagService;
        this.pinRequestService = pinRequestService;
        this.reactionService = reactionService;
        this.answerService = answerService;
        this.userService = userService;
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

    private TagViewModel mapTagViewModel(TagOutputDto tagDto) {
        return new TagViewModel(
            tagDto.id(),
            tagDto.name()
        );
    }

    private AnswerViewModel mapAnswerViewModel(AnswerOutputDto answerDto, Long currUserId) {
        List<ReactionOutputDto> reactions = reactionService.getAllByAnswer(answerDto.id());
        int reactionsCounter = reactions.stream()
                .map(r -> r.positive() ? 1 : -1)
                .reduce(0, (count, r) -> count + r);
        List<ReactionOutputDto> reactionsByUser = reactions.stream()
                .filter(r -> Objects.equals(r.author().id(), currUserId)).toList();
        ReactionOutputDto userReaction = reactionsByUser.isEmpty()
                ? null
                : reactionsByUser.get(0);
        Boolean currUserReaction = userReaction == null ? null : userReaction.positive();
        Long currUserReactionId = userReaction == null ? null : userReaction.id();

        return new AnswerViewModel(
            answerDto.id(),
            answerDto.text(),
            answerDto.author().id(),
            answerDto.author().name(),
            answerDto.createdAt(),
            reactionsCounter,
            currUserReaction,
            currUserReactionId
        );
    }

    @Override
    public String questionsList(Long tag, Boolean personal, int page, int size, Principal principal, 
            Model model, RedirectAttributes redirectAttributes) {

        UserOutputDto user = userService.getByName(principal.getName());
        LOG.log(Level.INFO, String.format(
                "Request to /questions(page=%s, size=%s, personal=%s, tag=%s); Method: GET; Username: %s; User ID: %s; Timestamp: %s", 
                page, size, personal, tag, user.name(), user.id(), System.currentTimeMillis()
        ));

        Page<QuestionOutputDto> questionsPage;
        List<TagOutputDto> tagsDtos;

        try {
            if (personal) {
                questionsPage = questionService.getPageByUser(user.id(), tag, page, size);
            } else {
                questionsPage = questionService.getPage(tag, page, size);
            }
            tagsDtos = tagService.getAll();
        }
        catch (ServiceException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/error";
        }

        List<QuestionViewModel> questionViewModels = questionsPage.stream()
                .map(q -> mapQuestionViewModel(q)).toList();
        List<TagViewModel> availableTags = tagsDtos.stream()
                .map(t -> mapTagViewModel(t)).toList();
        UserViewModel userModel = new UserViewModel(
            user.id(),
            user.name()
        );
        HomePageViewModel viewModel = new HomePageViewModel(
            new BaseViewModel(personal ? "My questions" : "Home", userModel), 
            questionViewModels, 
            availableTags,
            questionsPage.getTotalPages()
        );

        model.addAttribute("tag", tag);
        model.addAttribute("personal", personal);
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        model.addAttribute("model", viewModel);

        return "questions-list";
    }

    @Override
    public String question(Long id, Principal principal, 
            Model model, RedirectAttributes redirectAttributes) {

        System.out.println(redirectAttributes.getFlashAttributes().size());

        UserOutputDto user = userService.getByName(principal.getName());
        LOG.log(Level.INFO, String.format(
                "Request to /questions/%s; Method: GET; Username: %s; User ID: %s; Timestamp: %s", 
                id, user.name(), user.id(), System.currentTimeMillis()
        ));

        QuestionOutputDto questionDto;
        List<AnswerOutputDto> answerDtos;

        try {
            questionDto = questionService.get(id);
            answerDtos = answerService.getAllByQuestion(id);
        }
        catch (ServiceException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/error";
        }

        String pinRequestStatus = "NONE";
        List<PinRequestOutputDto> pinRequests = pinRequestService.getAllByQuestion(id);
        if (!pinRequests.isEmpty()) {
            pinRequestStatus = pinRequests.get(0).status();
        }
        if (pinRequestStatus.equals(PinRequestStatus.APPROVED.name())) {
            pinRequestStatus = LocalDateTime.now().isAfter(questionDto.pinnedTo())
                ? "NONE"
                : pinRequestStatus;
        }

        QuestionViewModel questionViewModel = mapQuestionViewModel(questionDto);
        List<AnswerViewModel> answerViewModels = answerDtos.stream()
                .map(a -> mapAnswerViewModel(a, user.id())).toList();
        UserViewModel userModel = new UserViewModel(
            user.id(),
            user.name()
        );
        QuestionPageViewModel viewModel = new QuestionPageViewModel(
            new BaseViewModel("Disscussion", userModel), 
            questionViewModel, 
            answerViewModels,
            pinRequestStatus
        );

        if (!model.containsAttribute("answerModel")) {
            model.addAttribute("answerModel", new AnswerCreateInputModel(id, ""));
        }
        model.addAttribute("model", viewModel);

        return "question";
    }

    @Override
    public String newQuestion(Principal principal, 
            Model model, RedirectAttributes redirectAttributes) {

        UserOutputDto user = userService.getByName(principal.getName());
        LOG.log(Level.INFO, String.format(
                "Request to /questions/create; Method: GET; Username: %s; User ID: %s; Timestamp: %s", 
                user.name(), user.id(), System.currentTimeMillis()
        ));

        List<TagOutputDto> tagsDtos;

        try {
            tagsDtos = tagService.getAll();
        }
        catch (ServiceException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/error";
        }

        List<TagViewModel> availableTags = tagsDtos.stream()
                .map(t -> mapTagViewModel(t)).toList();
        UserViewModel userModel = new UserViewModel(
            user.id(),
            user.name()
        );
        CreateQuestionPageViewModel viewModel = new CreateQuestionPageViewModel(
            new BaseViewModel("New Question", userModel),
            availableTags
        );

        if (!model.containsAttribute("questionModel")) {
            model.addAttribute("questionModel", new QuestionCreateInputModel("", "", ""));
        }
        model.addAttribute("model", viewModel);

        return "new-question";
    }

    @Override
    public String registerQuestion(@Valid QuestionCreateInputModel questionModel, BindingResult bindingResult, 
            Principal principal, RedirectAttributes redirectAttributes) {

        UserOutputDto user = userService.getByName(principal.getName());
        LOG.log(Level.INFO, String.format(
                "Request to /questions/create; Method: POST; Username: %s; User ID: %s; Timestamp: %s", 
                user.name(), user.id(), System.currentTimeMillis()
        ));

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("questionModel", questionModel);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.questionModel",
                bindingResult);
            return "redirect:/questions/create";
        }

        List<Long> tagsIds;
        if (questionModel.tagsIds() == null) {
            tagsIds = new ArrayList<>();
        } else {
            tagsIds = Arrays.asList(questionModel.tagsIds().split(","))
                    .stream().map(tid -> Long.valueOf(tid)).toList();
        }

        QuestionInputDto questionDto = new QuestionInputDto(
            user.id(),
            questionModel.title(),
            questionModel.text(),
            tagsIds
        );

        try {
            questionService.add(questionDto);
        }
        catch (ServiceException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/error";
        }

        return "redirect:/questions?personal=true";
    }

    @Override
    public String closeQuestion(Long id, Principal principal, RedirectAttributes redirectAttributes) {

        UserOutputDto user = userService.getByName(principal.getName());
        LOG.log(Level.INFO, String.format(
                "Request to /questions/close/%s; Method: GET; Username: %s; User ID: %s; Timestamp: %s", 
                id, user.name(), user.id(), System.currentTimeMillis()
        ));

        try {
            questionService.close(id, user.id());
        }
        catch (ServiceException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/error";
        }

        return "redirect:/questions/" + id;
    }
}
