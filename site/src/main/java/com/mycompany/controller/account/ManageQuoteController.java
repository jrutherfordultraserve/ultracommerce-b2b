package com.mycompany.controller.account;

import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.core.order.service.call.NonDiscreteOrderItemRequestDTO;
import org.broadleafcommerce.core.order.service.call.OrderItemRequestDTO;
import org.broadleafcommerce.core.order.service.exception.AddToCartException;
import org.broadleafcommerce.core.order.service.exception.UpdateCartException;
import org.broadleafcommerce.core.pricing.service.exception.PricingException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.broadleafcommerce.quote.domain.Quote;
import com.broadleafcommerce.quote.domain.QuoteNote;
import com.broadleafcommerce.quote.domain.type.QuoteStatusType;
import com.broadleafcommerce.quote.service.QuoteException;
import com.broadleafcommerce.quote.web.controller.BroadleafManageQuoteController;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/account/quote")
public class ManageQuoteController extends BroadleafManageQuoteController {

    protected static final String QUOTE_VIEW_URL = "getQuoteView";
    protected static final String QUOTE_VIEW = "quote/partials/quoteView";
    protected static final String QUOTE_TABLE_VIEW = "account/partials/quoteTable";

    @Override
    public String viewQuotes(HttpServletRequest request, HttpServletResponse response, Model model) {
        return viewQuotes(request, response, model, null);
    }

    @RequestMapping(method = RequestMethod.GET)
    public String viewQuotes(HttpServletRequest request, HttpServletResponse response, Model model, @RequestParam(value = "quoteId", required = false) Long quoteId) {
        String returnView = super.viewQuotes(request, response, model);
        List<Quote> quotes = (List<Quote>) model.asMap().get("quotes");

        List<Quote> inprocessQuotes = new ArrayList<>();
        List<Quote> submittedQuotes = new ArrayList<>();
        List<Quote> acceptedQuotes = new ArrayList<>();
        List<Quote> rejectedQuotes = new ArrayList<>();
        List<Quote> archivedQuotes = new ArrayList<>();
        List<Quote> inrevisionQuotes = new ArrayList<>();

        //sort quotes by status, only getting most recent revisions
        for (Quote quote : quotes) {
            if (quote.getMostRecentRevision().getId().equals(quote.getId())) {
                if (QuoteStatusType.IN_PROCESS.equals(quote.getStatus())) {
                    inprocessQuotes.add(quote);
                }
                if (QuoteStatusType.SUBMITTED.equals(quote.getStatus())) {
                    submittedQuotes.add(quote);
                }
                if (QuoteStatusType.ACCEPTED.equals(quote.getStatus())) {
                    acceptedQuotes.add(quote);
                }
                if (QuoteStatusType.REJECTED.equals(quote.getStatus())) {
                    rejectedQuotes.add(quote);
                }
                if (QuoteStatusType.ARCHIVED.equals(quote.getStatus())) {
                    archivedQuotes.add(quote);
                }
                if (QuoteStatusType.IN_REVISION.equals(quote.getStatus())) {
                    inrevisionQuotes.add(quote);
                }
            }
        }

        model.addAttribute("inprocessQuotes", inprocessQuotes);
        model.addAttribute("submittedQuotes", submittedQuotes);
        model.addAttribute("acceptedQuotes", acceptedQuotes);
        model.addAttribute("rejectedQuotes", rejectedQuotes);
        model.addAttribute("archivedQuotes", archivedQuotes);
        model.addAttribute("inrevisionQuotes", inrevisionQuotes);

        if (quoteId != null)
            model.addAttribute("quoteToLoad", quoteId);

        return returnView;
    }

    @RequestMapping(value="/{id}", method = RequestMethod.GET)
    public String manageQuote(HttpServletRequest request, HttpServletResponse response, Model model,
                              @PathVariable("id") Long quoteId) throws ServiceException, QuoteException {
        Quote quote = retrieveAndValidateQuote(quoteId);

        // sort the quote notes by date so they display nicely in the thymeleaf template
        Collections.sort(quote.getQuoteChain().getQuoteNotes(), new Comparator<QuoteNote>() {
            @Override
            public int compare(QuoteNote q1, QuoteNote q2) {
                return q1.getAuditable().getDateCreated().compareTo(q2.getAuditable().getDateCreated());
            }
        });

        model.addAttribute("quote", quote);
        return getManageQuoteView();
    }

    @Override
    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    public String submitQuote(HttpServletRequest request, HttpServletResponse response, Model model,
                              @PathVariable("id") Long quoteId,
                              @RequestParam(value = "name", required = false) String name,
                              @RequestParam(value = "note", required = true) String note) throws ServiceException, QuoteException {
        super.submitQuote(request, response, model, quoteId, name, note);

        if (model.containsAttribute("errorMessage")) {
            return getManageQuoteView();
        }

        return getQuoteBaseRedirect() + '/' + quoteId;
    }

    @Override
    @RequestMapping(value = "/moveQuoteToCart", method = RequestMethod.POST)
    public String moveQuoteToCart(HttpServletRequest request, HttpServletResponse response, Model model,
                                  @RequestParam(value = "quoteId", required = true) Long quoteId) throws UpdateCartException, ServiceException, QuoteException {
        return super.moveQuoteToCart(request, response, model, quoteId);
    }

    @Override
    @RequestMapping(value = "/addQuoteItemToCart", method = RequestMethod.POST)
    public String addQuoteItemToCart (HttpServletRequest request, HttpServletResponse response, Model model,
                                      @RequestParam(value = "quoteItemId", required = true) Long quoteItemId) throws AddToCartException {
        return super.addQuoteItemToCart(request, response, model, quoteItemId);
    }

    @RequestMapping(value = {"/getQuoteView", "/{id}/getQuoteView"})
    public String getQuoteView(Model model, @RequestParam("quoteId") Long quoteId) {
        model.addAttribute("quote", quoteService.findQuoteById(quoteId, true));
        return QUOTE_VIEW;
    }

    @RequestMapping(value = "/getQuoteTableView")
    public String getQuoteTableView(Model model, @RequestParam("quoteId") Long quoteId) {
        model.addAttribute("quote", quoteService.findQuoteById(quoteId, true));
        return QUOTE_TABLE_VIEW;
    }

    @RequestMapping(value = "/addCustomQuoteItem")
    public String addCustomQuoteItem(HttpServletRequest request, HttpServletResponse response, Model model, RedirectAttributes redirectAttributes,
                                                                @ModelAttribute("addToQuoteItem") NonDiscreteOrderItemRequestDTO addToQuoteItem, @ModelAttribute("quoteId") Long quoteId) throws ServiceException, QuoteException, AddToCartException, PricingException {
        super.addItemToQuote(request, response, model, quoteId, addToQuoteItem);
        model.addAttribute("quote", quoteService.findQuoteById(quoteId, true));
        return QUOTE_VIEW;
    }

    @RequestMapping(value="/{quoteId}/{quoteItemId}/addQuoteItemNote", produces = {"application/json"})
    public String addNoteToQuoteItem(HttpServletRequest request, HttpServletResponse response, Model model,
                                     @PathVariable("quoteId") Long quoteId, @PathVariable("quoteItemId") Long quoteItemId, @RequestParam("note") String note)
            throws ServiceException, QuoteException {
        super.addQuoteItemNote(request, response, model, quoteId, quoteItemId, note);

        model.addAttribute("quote", quoteService.findQuoteById(quoteId, true));
        return QUOTE_VIEW;
    }

    @RequestMapping(value = "/quoteItemNoteModal", produces = {"text/html"})
    public String getQuoteItemNoteModal(Model model, @RequestParam("quoteId") Long quoteId, @RequestParam("quoteItemId") Long quoteItemId) {
        String returnPath = super.getQuoteItemNoteModal();

        model.addAttribute("quoteId", quoteId);
        model.addAttribute("quoteItemId", quoteItemId);
        model.addAttribute("modalTitle", "Add Quote Item Note");

        return returnPath;
    }

    @RequestMapping(value = "/getEditQuoteNameModal", produces = {"text/html"})
    public String getEditQuoteNameModal(Model model, @RequestParam("quoteId") Long quoteId) {
        String returnPath = super.getEditQuoteNameModal();

        model.addAttribute("modalTitle", "Edit Quote Name");
        model.addAttribute("quoteId", quoteId);

        return returnPath;
    }

    @RequestMapping(value = "{quoteId}/editQuoteName", produces = {"application/json"})
    public String editQuoteName(HttpServletRequest request, HttpServletResponse response, Model model,
                                @PathVariable("quoteId") Long quoteId, @RequestParam("note") String note) {
        super.editQuoteName(quoteId, note);

        model.addAttribute("quote", quoteService.findQuoteById(quoteId, true));
        return QUOTE_VIEW;
    }

    @RequestMapping(value = "/removeQuoteItem", produces = {"application/json"})
    public String removeItemFromQuoteJson(HttpServletRequest request, HttpServletResponse response, Model model,
                                          @RequestParam("quoteId") Long quoteId, @RequestParam("quoteItemId") Long quoteItemId) throws ServiceException, QuoteException {
        super.removeItemFromQuote(request, response, model, quoteId, quoteItemId);

        model.addAttribute("quote", quoteService.findQuoteById(quoteId, true));
        return QUOTE_VIEW;
    }

    @RequestMapping(value = "/updateItemQuantity", produces = {"application/json"}, method = RequestMethod.POST)
    public String updateItemQuantity(HttpServletRequest request, HttpServletResponse response, Model model,
                                     @RequestParam("quoteId") Long quoteId, OrderItemRequestDTO itemRequestDTO) throws ServiceException, QuoteException {
        super.updateItemQuantityOnQuote(request, response, model, quoteId, itemRequestDTO);

        model.addAttribute("quote", quoteService.findQuoteById(quoteId, true));
        return QUOTE_VIEW;
    }

    @RequestMapping(value = "{quoteId}/archiveQuote", produces = {"text/html"})
    public String archiveQuote(HttpServletRequest request, HttpServletResponse response, Model model,
                               @PathVariable("quoteId") Long quoteId) throws QuoteException {
        String returnView = super.archiveQuote(quoteId);
        model.addAttribute("quoteId",quoteId); //the manage all quotes page will load with the archived quote opened
        return returnView;
    }

    @RequestMapping(value = "{quoteId}/modifyQuote", produces = {"text/html"})
    public String modifyQuote(HttpServletRequest request, HttpServletResponse response, Model model,
                              @PathVariable("quoteId") Long quoteId) throws QuoteException {
        return super.modifyQuote(quoteId, model);
    }

    @RequestMapping(value = "{quoteId}/cancelModifyQuote", produces = {"text/html"})
    public String cancelModifyQuote(HttpServletRequest request, HttpServletResponse response, Model model,
                                    @PathVariable("quoteId") Long quoteId) throws QuoteException {
        return super.cancelModifyQuote(quoteId, model);
    }
}