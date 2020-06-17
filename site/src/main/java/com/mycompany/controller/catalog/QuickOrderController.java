package com.mycompany.controller.catalog;

import org.broadleafcommerce.core.inventory.service.InventoryUnavailableException;
import org.broadleafcommerce.core.order.domain.DiscreteOrderItem;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.service.exception.AddToCartException;
import org.broadleafcommerce.core.order.service.exception.ProductOptionValidationException;
import org.broadleafcommerce.core.order.service.exception.RequiredAttributeNotProvidedException;
import org.broadleafcommerce.core.pricing.service.exception.PricingException;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.web.core.CustomerState;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import com.broadleafcommerce.account.service.exception.QuickOrderException;
import com.broadleafcommerce.account.web.controller.BroadleafQuickOrderController;
import com.broadleafcommerce.account.web.controller.QuickOrderForm;
import com.broadleafcommerce.account.web.controller.QuickOrderItemRequest;
import com.broadleafcommerce.account.web.controller.QuickOrderPasteDTO;
import com.broadleafcommerce.quote.domain.Quote;
import com.broadleafcommerce.quote.dto.QuoteRequest;
import com.broadleafcommerce.quote.service.QuoteService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class QuickOrderController extends BroadleafQuickOrderController {

    @Resource(name = "blQuoteService")
    protected QuoteService quoteService;

    @Override
    @RequestMapping(value = "/quick-order", method = RequestMethod.GET)
    public String quickOrder(HttpServletRequest request, HttpServletResponse response,  Model model, @ModelAttribute("quickOrderForm") QuickOrderForm form) throws Exception {
        return super.quickOrder(request, response, model, form);
    }

    @RequestMapping(value = "/quick-order", method = RequestMethod.POST)
    public @ResponseBody Map<String, Object> quickOrderAddJSON(HttpServletRequest request, HttpServletResponse response, @RequestBody List<QuickOrderItemRequest> quickOrderItems) throws Exception {
        Map<String, Object> responseMap = new HashMap<>();
        try {
            Order cart = super.quickOrderAdd(request, response, quickOrderItems);
            buildAddResponse(responseMap, quickOrderItems, cart);
        } catch (Exception e) {
            buildAddErrorResponse(responseMap, e);
        }

        return responseMap;
    }

    protected Quote quickQuoteAdd(Customer customer, List<QuickOrderItemRequest> quickOrderItems) throws AddToCartException, PricingException {
        Date date = new Date();
        QuoteRequest quoteRequest = new QuoteRequest()
                .withName(quoteService.createDefaultQuoteName(date))
                .withTargetCustomer(customer)
                .withStartDate(date);

        for (QuickOrderItemRequest quickOrderItem : quickOrderItems) {
            if (quickOrderItem.getQuantity() != null &&
                    quickOrderItem.getSkuId() != null &&
                    quickOrderItem.getProductId() != null) {

                quoteRequest.addOrderItemRequest(quickOrderItem);
            }
        }

        return quoteService.createQuote(quoteRequest);
    }

    @RequestMapping(value = "/quick-quote", method = RequestMethod.POST)
    public @ResponseBody Map<String, Object> quickQuoteAddJSON(HttpServletRequest request, HttpServletResponse response, @RequestBody List<QuickOrderItemRequest> quickOrderItems) throws Exception {
        Map<String, Object> responseMap = new HashMap<>();
        try {
            Quote quote = quickQuoteAdd(CustomerState.getCustomer(request), quickOrderItems);
            buildAddResponse(responseMap, quickOrderItems, quote.getOrder());
        } catch (Exception e) {
            buildAddErrorResponse(responseMap, e);
        }

        return responseMap;
    }

    @RequestMapping(value = "/quick-order-paste", method = RequestMethod.POST)
    public @ResponseBody Map<String, Object> quickOrderPasteJSON(HttpServletRequest request, HttpServletResponse response, @RequestBody QuickOrderPasteDTO quickOrderPasteDto) throws Exception {
        Map<String, Object> responseMap = new HashMap<>();
        try {
            List<QuickOrderItemRequest> quickOrderItems = super.parseQuickOrderItems(quickOrderPasteDto.getQuickOrderPasteText());
            Order cart = super.quickOrderAdd(request, response, quickOrderItems);
            buildAddResponse(responseMap, quickOrderItems, cart);
        } catch (Exception e) {
            buildAddErrorResponse(responseMap, e);
        }
        return responseMap;
    }

    @RequestMapping(value = "/quick-quote-paste", method = RequestMethod.POST)
    public @ResponseBody Map<String, Object> quickQuotePasteJSON(HttpServletRequest request, HttpServletResponse response, @RequestBody QuickOrderPasteDTO quickOrderPasteDto) throws Exception {
        Map<String, Object> responseMap = new HashMap<>();
        try {
            List<QuickOrderItemRequest> quickOrderItems = super.parseQuickOrderItems(quickOrderPasteDto.getQuickOrderPasteText());
            Quote quote = quickQuoteAdd(CustomerState.getCustomer(request), quickOrderItems);
            buildAddResponse(responseMap, quickOrderItems, quote.getOrder());
        } catch (Exception e) {
            buildAddErrorResponse(responseMap, e);
        }
        return responseMap;
    }

    protected void buildAddErrorResponse(Map<String, Object> responseMap, Exception e) throws Exception {
        if (e.getCause() instanceof RequiredAttributeNotProvidedException) {
            RequiredAttributeNotProvidedException exception = (RequiredAttributeNotProvidedException) e.getCause();
            responseMap.put("error", "allOptionsRequired");
            responseMap.put("productId", exception.getProductId());
        } else if (e.getCause() instanceof ProductOptionValidationException) {
            ProductOptionValidationException exception = (ProductOptionValidationException) e.getCause();
            responseMap.put("error", "productOptionValidationError");
            responseMap.put("errorCode", exception.getErrorCode());
            responseMap.put("errorMessage", exception.getMessage());
            //blMessages.getMessage(exception.get, lfocale))
        } else if (e.getCause() instanceof InventoryUnavailableException) {
            responseMap.put("error", "inventoryUnavailable");
        } else if (e instanceof IllegalArgumentException) {
            IllegalArgumentException exception = (IllegalArgumentException) e;
            responseMap.put("error", "addOnValidationError");
            responseMap.put("errorMessage", exception.getMessage());
        } else if (e instanceof QuickOrderException) {
            responseMap.put("error", "quickOrderException");
            responseMap.put("errorMessage", e.getMessage());
        } else {
            throw e;
        }
    }

    protected void buildAddResponse(Map<String, Object> responseMap, List<QuickOrderItemRequest> quickOrderItems, Order cart) {
        List<String> productNames = new ArrayList<>();
        Integer quantity = 0;

        for (QuickOrderItemRequest quickOrderItem : quickOrderItems) {
            for (DiscreteOrderItem doi : cart.getDiscreteOrderItems()) {
                if (doi.getProduct().getId().equals(quickOrderItem.getProductId())) {
                    productNames.add(doi.getProduct().getName());
                    quantity += quickOrderItem.getQuantity();
                }
            }
        }

        responseMap.put("productNames", productNames);
        responseMap.put("quantityAdded", quantity);
        responseMap.put("cartItemCount", String.valueOf(cart.getItemCount()));
    }

    @RequestMapping(value = "/quick-order-select/{productId}", method = RequestMethod.GET)
    public String quickOrderItemSelect(HttpServletRequest request, HttpServletResponse response, Model model, @PathVariable Long productId) throws IOException {
        return super.quickOrderItemSelect(request, response, model, productId);
    }

    @RequestMapping(value = "/quick-order-select-multi/{productId}", method = RequestMethod.GET)
    public String quickOrderItemSelectMulti(HttpServletRequest request, HttpServletResponse response, Model model, @PathVariable Long productId) throws IOException {
        return super.quickOrderItemSelectMulti(request, response, model, productId);
    }

}