package com.mycompany.controller.account;

import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.core.pricing.service.exception.PricingException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.broadleafcommerce.account.web.controller.BroadleafSelfRegisterAccountController;
import com.broadleafcommerce.account.web.controller.RegisterAccountMemberForm;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/account-register")
public class SelfRegisterAccountController extends BroadleafSelfRegisterAccountController {

    @RequestMapping(method= RequestMethod.GET)
    public String selfRegister(HttpServletRequest request, HttpServletResponse response, Model model,
                               @ModelAttribute("registrationForm") RegisterAccountMemberForm registerAccountMemberForm) {
        return super.selfRegister(registerAccountMemberForm, request, response, model);
    }

    @RequestMapping(method=RequestMethod.POST)
    public String processSelfRegister(HttpServletRequest request, HttpServletResponse response, Model model,
                                      @ModelAttribute("registrationForm") RegisterAccountMemberForm registerAccountMemberForm, BindingResult errors) throws ServiceException, PricingException {
        return super.processSelfRegister(registerAccountMemberForm, errors, request, response, model);
    }

    @ModelAttribute("registrationForm")
    public RegisterAccountMemberForm initAccountMemberRegistrationForm() {
        return super.initAccountMemberRegistrationForm();
    }
}