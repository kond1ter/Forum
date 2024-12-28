package study.konditer.forum.controller;

import java.security.Principal;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import study.konditer.forum.dto.ReactionInputDto;
import study.konditer.forum.dto.UserOutputDto;
import study.konditer.forum.exception.ServiceException;
import study.konditer.forum.service.ReactionService;
import study.konditer.forum.service.UserService;
import study.konditer.forumcontract.controller.ReactionController;

@Controller
public class ReactionControllerImpl implements ReactionController {

    private static final Logger LOG = LogManager.getLogger(Controller.class);

    private final UserService userService;
    private final ReactionService reactionService;

    public ReactionControllerImpl(
            ReactionService reactionService,
            UserService userService) {
        this.reactionService = reactionService;
        this.userService = userService;
    }

    @Override
    public String removeReaction(Long questionId, Long reactionId, 
            Principal principal, RedirectAttributes redirectAttributes) {

        UserOutputDto user = userService.getByName(principal.getName());
        LOG.log(Level.INFO, String.format(
                "Request to /reactions/remove(questionId=%s, reactionId=%s); Method: GET; Username: %s; User ID: %s; Timestamp: %s", 
                questionId, reactionId, user.name(), user.id(), System.currentTimeMillis()
        ));

        try {
            reactionService.remove(reactionId, user.id());
        }
        catch (ServiceException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/error";
        }

        return "redirect:/questions/" + questionId;
    }

    @Override
    public String registerReaction(Long questionId, Long answerId, Boolean isPositive, 
            Principal principal, RedirectAttributes redirectAttributes) {

        UserOutputDto user = userService.getByName(principal.getName());
        LOG.log(Level.INFO, String.format(
                "Request to /reactions/create(questionId=%s, answerId=%s); Method: GET; Username: %s; User ID: %s; Timestamp: %s", 
                questionId, answerId, user.name(), user.id(), System.currentTimeMillis()
        ));

        try {
            reactionService.add(new ReactionInputDto(
                user.id(),
                answerId,
                isPositive
            ));
        }
        catch (ServiceException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/error";
        }

        return "redirect:/questions/" + questionId;
    }
}
