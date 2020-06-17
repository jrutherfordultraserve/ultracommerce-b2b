package com.mycompany.controller.account;

import org.broadleafcommerce.common.exception.ServiceException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.broadleafcommerce.account.web.controller.BroadleafAccountMaintenanceController;
import com.broadleafcommerce.account.web.controller.EditAccountMemberForm;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/account/{accountId}/accountMembers")
public class AccountMaintenanceController extends BroadleafAccountMaintenanceController {

    @RequestMapping(method = RequestMethod.GET)
    public String viewAccountMembers(HttpServletRequest request, @PathVariable("accountId") String accountId, Model model) {
        return super.viewAccountMembersForAccount(request, accountId, model);
    }

    @RequestMapping(value = "/{memberId}", method = RequestMethod.GET)
    public String viewAccountMember(HttpServletRequest request, HttpServletResponse response,
                                    @PathVariable("accountId") String accountId, @PathVariable("memberId") String memberId,
                                    @ModelAttribute("blEditAccountMemberForm") EditAccountMemberForm accountMemberForm,
                                    Model model,RedirectAttributes redirectAttributes)throws ServiceException{
        return super.viewAccountMember(request, response, accountId, memberId, accountMemberForm, model, redirectAttributes);
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public String saveAccountMember(HttpServletRequest request, HttpServletResponse response,
                                    @PathVariable("accountId") String accountId,
                                    @ModelAttribute("blEditAccountMemberForm") EditAccountMemberForm accountMemberForm, BindingResult result, Model model,
                                    RedirectAttributes redirectAttributes) throws ServiceException {
        return super.saveAccountMember(request, response, accountId, accountMemberForm, result, model, redirectAttributes);
    }
}