package com.mycompany.controller.quote;

import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.core.catalog.service.CatalogService;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.service.call.OrderItemRequestDTO;
import org.broadleafcommerce.core.order.service.exception.AddToCartException;
import org.broadleafcommerce.core.order.service.type.OrderStatus;
import org.broadleafcommerce.core.pricing.service.exception.PricingException;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.web.core.CustomerState;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.broadleafcommerce.quote.domain.Quote;
import com.broadleafcommerce.quote.domain.QuoteNote;
import com.broadleafcommerce.quote.domain.type.QuoteStatusType;
import com.broadleafcommerce.quote.service.QuoteException;
import com.broadleafcommerce.quote.web.controller.BroadleafManageQuoteController;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by caderea on 9/12/16.
 */
@Controller
@RequestMapping("/quote")
public class QuoteController extends BroadleafManageQuoteController {

    @Resource(name = "blCatalogService")
    protected CatalogService catalogService;

    @RequestMapping(value = "/createFromCart", method = RequestMethod.POST)
    public String createQuoteFromCart(HttpServletRequest request, HttpServletResponse response, Model model,
                                      @RequestParam(value = "name", required = true) String name,
                                      @RequestParam(value = "message", required = false) String message) throws PricingException, AddToCartException {
        return super.createQuoteFromCart(request, response, model, name, message);
    }

    /*
     * Used to returns current quotes in the mini quotes partial
     */
    @RequestMapping("/mini")
    public String miniQuotes(HttpServletRequest request, HttpServletResponse response, Model model) throws PricingException {
        super.viewQuotes(request, response, model);
        List<Quote> quotes = (List<Quote>) model.asMap().get("quotes");

        List<Quote> inprocessQuotes = new ArrayList<>();
        List<Quote> acceptedQuotes = new ArrayList<>();

        //sort quotes by status, only getting most recent revisions
        for (Quote quote : quotes) {
            if (quote.getMostRecentRevision().getId().equals(quote.getId())) {
                if (QuoteStatusType.IN_PROCESS.equals(quote.getStatus())) {
                    inprocessQuotes.add(quote);
                }
                if (QuoteStatusType.ACCEPTED.equals(quote.getStatus())) {
                    Order order = quote.getOrder();
                    if (OrderStatus.QUOTE.equals(order.getStatus())) {
                        //filter out any quotes that may have already been checked out
                        acceptedQuotes.add(quote);
                    }
                }
            }
        }

        model.addAttribute("quoteCount", inprocessQuotes.size()+acceptedQuotes.size());
        model.addAttribute("inprocessQuotes", inprocessQuotes);
        model.addAttribute("acceptedQuotes", acceptedQuotes);

        return "quote/miniQuotes";
    }

    @RequestMapping(value = "/addItemToQuote", produces = {"application/json"} )
    public @ResponseBody Map<String, Object> addItemToQuoteJson(HttpServletRequest request, HttpServletResponse response, Model model, RedirectAttributes redirectAttributes,
                                                                @ModelAttribute("addToQuoteItem") OrderItemRequestDTO addToQuoteItem,
                                                                @RequestParam(value = "quoteId", required = false) Long quoteId) throws IOException, PricingException, AddToCartException, ServiceException, QuoteException {
        super.addItemToQuote(request, response, model, quoteId, addToQuoteItem);
        Map<String, Object> responseMap = new HashMap<>();

        responseMap.put("productName", catalogService.findProductById(addToQuoteItem.getProductId()).getName());
        responseMap.put("quantityAdded", addToQuoteItem.getQuantity());

        if (model.containsAttribute("newQuote")) {
            Quote quote = (Quote) model.asMap().get("newQuote");
            responseMap.put("newQuoteId", quote.getId());
            responseMap.put("newQuoteName", quote.getName());
        }

        return responseMap;
    }

    @RequestMapping(value="/{id}", method = RequestMethod.GET)
    public String manageQuote(HttpServletRequest request, HttpServletResponse response, Model model,
                              @PathVariable("id") Long quoteId) throws ServiceException, QuoteException {
        Quote quote = retrieveAndValidateQuote(quoteId);

        if (quote == null) {
            return "redirect:/";
        }

        // sort the quote notes by date so they display nicely in the thymeleaf template
        Collections.sort(quote.getQuoteChain().getQuoteNotes(), new Comparator<QuoteNote>() {
            @Override
            public int compare(QuoteNote q1, QuoteNote q2) {
                return q1.getAuditable().getDateCreated().compareTo(q2.getAuditable().getDateCreated());
            }
        });

        model.addAttribute("quote", quote);
        model.addAttribute("anonymousQuote","anonymous");
        return getManageQuoteView();
    }


    @RequestMapping(value="/transfer")
    public String showAnonymousQuote(HttpServletRequest request, HttpServletResponse response, Model model) throws ServiceException, QuoteException {
        Customer customer = CustomerState.getCustomer();
        List<Quote> quotesForCustomer = quoteService.findQuotesForCustomer(customer);

        if (quotesForCustomer.isEmpty()) {
            return "redirect:/";
        }
        else {
            return manageQuote(request, response, model, quotesForCustomer.get(0).getId());
        }
    }
}
