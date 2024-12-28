package study.konditer.forum.controller;

import java.security.Principal;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import study.konditer.forum.dto.AnswerInputDto;
import study.konditer.forum.dto.UserOutputDto;
import study.konditer.forum.exception.ServiceException;
import study.konditer.forum.service.AnswerService;
import study.konditer.forum.service.UserService;
import study.konditer.forumcontract.controller.AnswerController;
import study.konditer.forumcontract.input.AnswerCreateInputModel;

@Controller
public class AnswerControllerImpl implements AnswerController {

    private static final Logger LOG = LogManager.getLogger(Controller.class);

    private final AnswerService answerService;
    private final UserService userService;

    public AnswerControllerImpl(AnswerService answerService,
            UserService userService) {
        this.answerService = answerService;
        this.userService = userService;
    }

    @Override
    public String registerAnswer(@Valid AnswerCreateInputModel answerModel, BindingResult bindingResult,
            Principal principal, RedirectAttributes redirectAttributes) {

        UserOutputDto user = userService.getByName(principal.getName());
        LOG.log(Level.INFO, String.format(
                "Request to /answers/create; Method: POST; Username: %s; User ID: %s; Timestamp: %s", 
                user.name(), user.id(), System.currentTimeMillis()
        ));

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("answerModel", answerModel);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.answerModel",
                bindingResult);
            return "redirect:/questions/" + answerModel.questionId();
        }

        AnswerInputDto answerDto = new AnswerInputDto(
            user.id(),
            answerModel.questionId(),
            answerModel.text()
        );

        try {
            answerService.add(answerDto);
        }
        catch (ServiceException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/error";
        }

        return "redirect:/questions/" + answerModel.questionId(); 
    }
}
