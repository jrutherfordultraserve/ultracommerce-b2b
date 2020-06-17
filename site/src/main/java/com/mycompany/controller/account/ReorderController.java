package com.blcdemo.controller.account;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.broadleafcommerce.core.order.service.exception.AddToCartException;
import org.broadleafcommerce.core.pricing.service.exception.PricingException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.broadleafcommerce.account.web.controller.BroadleafReorderController;

@Controller
@RequestMapping("/account/orders/reorder")
public class ReorderController extends BroadleafReorderController {

    @RequestMapping(value = "/{orderNumber}", method = RequestMethod.GET)
    public String viewOrderDetails(HttpServletRequest request, Model model, @PathVariable("orderNumber") String orderNumber) {
        return super.viewOrder(request, model, orderNumber);
    }

    @RequestMapping(value = "all/{orderNumber}", method = RequestMethod.POST)
    public String orderAll(HttpServletRequest request, HttpServletResponse response, Model model, @PathVariable("orderNumber") String orderNumber,
                           RedirectAttributes ra) throws AddToCartException, PricingException {
        super.viewOrder(request, model, orderNumber);
        return super.addAll(request, response, model, orderNumber, ra);
    }

}