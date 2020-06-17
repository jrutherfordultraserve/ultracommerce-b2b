package com.mycompany.controller.account;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.broadleafcommerce.account.web.controller.BroadleafAccountOrderHistoryController;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/account/orders-account")
public class AccountOrderHistoryController extends BroadleafAccountOrderHistoryController {

    protected static String orderHistoryView = "account/orderHistory";
    protected static String orderDetailsView = "account/partials/orderDetails";
    protected static String orderDetailsRedirectView = "account/partials/orderDetails";

    @RequestMapping(method = RequestMethod.GET)
    public String viewOrderHistory(HttpServletRequest request, Model model) {
        return super.viewOrderHistory(request, model);
    }

    @RequestMapping(value = "/{orderNumber}", method = RequestMethod.GET)
    public String viewOrderDetails(HttpServletRequest request, Model model, @PathVariable("orderNumber") String orderNumber) {
        return super.viewOrderDetails(request, model, orderNumber);
    }

    public String getOrderHistoryView() {
        return orderHistoryView;
    }

    public String getOrderDetailsView() {
        return orderDetailsView;
    }

    public String getOrderDetailsRedirectView() {
        return orderDetailsRedirectView;
    }
}