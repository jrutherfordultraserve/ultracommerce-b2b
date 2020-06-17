package com.mycompany.controller.account;

import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.core.checkout.service.exception.CheckoutException;
import org.broadleafcommerce.core.pricing.service.exception.PricingException;
import org.broadleafcommerce.profile.core.service.CountryService;
import org.broadleafcommerce.profile.core.service.StateService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.broadleafcommerce.account.web.controller.BroadleafOrderApprovalController;
import com.broadleafcommerce.account.web.controller.PendingOrdersInfoForm;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Controller
@RequestMapping("/account/orderApproval")
public class OrderApprovalController extends BroadleafOrderApprovalController {

    @Resource(name = "blStateService")
    StateService stateService;

    @Resource(name = "blCountryService")
    CountryService countryService;

    @RequestMapping(method = RequestMethod.GET)
    public String viewOrderApprovals(HttpServletRequest request, HttpServletResponse response, Model model,
                                     RedirectAttributes redirectAttributes) throws ServiceException {
        return super.viewOrderApprovals(request, response, model, redirectAttributes);
    }

    @RequestMapping(value = "/{orderNumber}", method = RequestMethod.GET)
    public String viewOrderApprovalDetails(HttpServletRequest request, Model model,
                                           @PathVariable("orderNumber") String orderNumber) {
        return super.viewOrderApprovalDetails(request, model, orderNumber);
    }

    @RequestMapping(method = RequestMethod.POST)
    public String approveOrders(HttpServletRequest request, HttpServletResponse response, Model model,
                                RedirectAttributes redirectAttributes, PendingOrdersInfoForm form, BindingResult result)
            throws CheckoutException {
        return super.approveOrders(request, response, model, redirectAttributes, form, result);
    }

    @RequestMapping(value = "/reject/{orderId}", method = RequestMethod.GET)
    public String rejectOrder(HttpServletRequest request, HttpServletResponse response, Model model,
                              RedirectAttributes redirectAttributes, @PathVariable("orderId") Long orderId) throws CheckoutException {
        return super.rejectOrder(request, response, model, redirectAttributes, orderId);
    }


    @Override
    public String modifyOrder(HttpServletRequest request, HttpServletResponse response, Model model,
                              RedirectAttributes redirectAttributes, Long orderId) throws PricingException {
        return super.modifyOrder(request, response, model, redirectAttributes, orderId);
    }

    @Override
    public String unlockOrder(HttpServletRequest request, HttpServletResponse response, Model model,
                              RedirectAttributes redirectAttributes, Long orderId) throws PricingException {
        return super.unlockOrder(request, response, model, redirectAttributes, orderId);
    }



}