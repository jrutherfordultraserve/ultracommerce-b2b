package com.mycompany.controller.catalog;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.broadleafcommerce.search.domain.typeahead.TypeAheadResponse;
import com.broadleafcommerce.search.exception.TypeAheadException;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Nick Crum (ncrum)
 */
@RestController
@RequestMapping("/type-ahead")
public class TypeAheadController extends com.broadleafcommerce.search.web.controller.TypeAheadController {

    @Override
    @RequestMapping(value = "/search", produces = "application/json")
    public TypeAheadResponse typeAhead(HttpServletRequest request, HttpServletResponse response,
                                       @RequestParam(value = "q") String query,
                                       @RequestParam(value = "name") String name,
                                       @RequestParam(value = "contributor", required = false) Set<String> includedContributorKeys) throws TypeAheadException {
        return super.typeAhead(request, response, query, name, includedContributorKeys);
    }
}
