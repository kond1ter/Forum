package study.konditer.forum.controller;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import study.konditer.forum.dto.UserInputDto;
import study.konditer.forum.exception.ServiceException;
import study.konditer.forum.service.UserService;
import study.konditer.forumcontract.controller.UserController;
import study.konditer.forumcontract.input.UserRegisterInputModel;

@Controller
public class UserControllerImpl implements UserController {

    private static final Logger LOG = LogManager.getLogger(Controller.class);

    private final UserService userService;

    public UserControllerImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public String registerPage(Model model) {
        LOG.log(Level.INFO, String.format(
            "Request to /register; Method: GET; Timestamp: %s",
            System.currentTimeMillis()
        ));

        if (!model.containsAttribute("userRegisterModel")) {
            model.addAttribute("userRegisterModel", 
                    new UserRegisterInputModel("", "", ""));
        }
        return "register";
    }

    @Override
    public String register(@Valid UserRegisterInputModel userRegisterModel, BindingResult bindingResult, 
            RedirectAttributes redirectAttributes) {

        LOG.log(Level.INFO, String.format(
            "Request to /register; Method: POST; Timestamp: %s",
            System.currentTimeMillis()
        ));

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("userRegisterModel", userRegisterModel);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.userRegisterModel",
                bindingResult);
            return "redirect:/users/register";
        }

        try {
            userService.add(new UserInputDto(
                userRegisterModel.username(), 
                userRegisterModel.password(),
                userRegisterModel.confirmPassword()
            ));
        }
        catch (ServiceException e) {
            redirectAttributes.addFlashAttribute("userRegisterModel", userRegisterModel);
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/users/register";
        }

        return "redirect:/users/login";
    }

    @Override
    public String loginPage() {
        LOG.log(Level.INFO, String.format(
            "Request to /login; Method: GET; Timestamp: %s",
            System.currentTimeMillis()
        ));

        return "login";
    }

    @Override
    public String loginError(String username, RedirectAttributes redirectAttributes) {
        LOG.log(Level.INFO, String.format(
            "Request to /login-error; Method: GET; Timestamp: %s",
            System.currentTimeMillis()
        ));

        redirectAttributes.addFlashAttribute("username", username);
        redirectAttributes.addFlashAttribute("errorMessage", "Invalid username or password");
        return "redirect:/users/login";
    }
}
