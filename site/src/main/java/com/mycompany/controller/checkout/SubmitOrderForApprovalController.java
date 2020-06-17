package com.mycompany.controller.checkout;

import org.broadleafcommerce.common.payment.PaymentGatewayType;
import org.broadleafcommerce.common.payment.PaymentTransactionType;
import org.broadleafcommerce.common.payment.PaymentType;
import org.broadleafcommerce.core.checkout.service.exception.CheckoutException;
import org.broadleafcommerce.core.checkout.service.gateway.PassthroughPaymentConstants;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.payment.domain.OrderPayment;
import org.broadleafcommerce.core.payment.domain.PaymentTransaction;
import org.broadleafcommerce.core.pricing.service.exception.PricingException;
import org.broadleafcommerce.core.web.order.CartState;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.broadleafcommerce.account.web.controller.BroadleafSubmitOrderForApprovalController;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class SubmitOrderForApprovalController extends BroadleafSubmitOrderForApprovalController {

    @RequestMapping(value = "/order-approval/submit", method = RequestMethod.POST)
    public String submitForApproval(HttpServletRequest request, HttpServletResponse response, Model model, RedirectAttributes redirectAttributes) throws PricingException, CheckoutException {
        Order cart = CartState.getCart();

        //Invalidate any payments already on the order that do not have transactions on them that are UNCONFIRMED
        //In addition, try to find any Temporary Credit Card payments that may have a Billing Address associated with it.
        List<OrderPayment> paymentsToInvalidate = new ArrayList<OrderPayment>();
        OrderPayment tempCCBilling = null;

        for (OrderPayment payment : cart.getPayments()) {
            if (payment.isActive()) {
                if (payment.getTransactions() == null || payment.getTransactions().isEmpty()) {
                    paymentsToInvalidate.add(payment);
                } else {
                    for (PaymentTransaction transaction : payment.getTransactions()) {
                        if (!PaymentTransactionType.UNCONFIRMED.equals(transaction.getType())) {
                            paymentsToInvalidate.add(payment);
                        }
                    }
                }

                if (PaymentType.CREDIT_CARD.equals(payment.getType()) &&
                        PaymentGatewayType.TEMPORARY.equals(payment.getGatewayType()) &&
                        payment.getBillingAddress() != null) {
                    tempCCBilling = payment;
                }

            }
        }

        for (OrderPayment payment : paymentsToInvalidate) {
            cart.getPayments().remove(payment);
            if (paymentGatewayCheckoutService != null) {
                paymentGatewayCheckoutService.markPaymentAsInvalid(payment.getId());
            }
        }

        //Create a new pass through purchase order
        OrderPayment passthroughPayment = orderPaymentService.create();
        passthroughPayment.setType(PaymentType.PURCHASE_ORDER);
        passthroughPayment.setPaymentGatewayType(PaymentGatewayType.PASSTHROUGH);
        passthroughPayment.setAmount(cart.getTotalAfterAppliedPayments());
        passthroughPayment.setOrder(cart);

        if (tempCCBilling != null) {
            passthroughPayment.setBillingAddress(tempCCBilling.getBillingAddress());
        }

        // Create the transaction for the payment
        PaymentTransaction transaction = orderPaymentService.createTransaction();
        transaction.setAmount(cart.getTotalAfterAppliedPayments());
        transaction.setRawResponse("Passthrough Purchase Order Payment");
        transaction.setSuccess(true);
        transaction.setType(PaymentTransactionType.AUTHORIZE_AND_CAPTURE);
        transaction.getAdditionalFields().put(PassthroughPaymentConstants.PASSTHROUGH_PAYMENT_TYPE, PaymentType.PURCHASE_ORDER.getType());

        transaction.setOrderPayment(passthroughPayment);
        passthroughPayment.addTransaction(transaction);
        orderService.addPaymentToOrder(cart, passthroughPayment, null);

        orderService.save(cart, true);
        return super.submitForApproval(request, response, model, redirectAttributes);
    }
}