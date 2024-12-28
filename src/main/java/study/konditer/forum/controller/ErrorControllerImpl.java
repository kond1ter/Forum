package study.konditer.forum.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class ErrorControllerImpl implements ErrorController {

    @RequestMapping("/error")
    public String handleError(Model model, HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (model.containsAttribute("errorMessage")) {
            return "error";
        }

        String errMsg = "Something went wrong";
        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());

            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                errMsg = "Resource not found";
            } 
            else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                errMsg = "Server error";
            }
        }

        model.addAttribute("errorMessage", errMsg);
        return "error";
    }
}
