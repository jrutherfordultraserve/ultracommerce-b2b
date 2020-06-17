package com.blcdemo.controller.catalog;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.broadleafcommerce.inventory.advanced.notification.domain.InventoryNotification;
import com.broadleafcommerce.inventory.advanced.web.controller.BroadleafInventoryNotificationController;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Controller
public class InventoryNotificationController extends BroadleafInventoryNotificationController {

    @Override
    @RequestMapping(value = "/inventory/notification/{skuId}", method = RequestMethod.GET)
    public String getNotificationForm(HttpServletRequest request, HttpServletResponse response, Model model,
            @PathVariable(value = "skuId") Long skuId) {
        return super.getNotificationForm(request, response, model, skuId);
    }
    
    /**
     * Creates a new {@link InventoryNotification}
     */
    @Override
    @RequestMapping(value = "/inventory/notification", method = RequestMethod.POST, produces = "application/json")
    public @ResponseBody Map<String, Object> addNotification(HttpServletRequest request, HttpServletResponse response, Model model,
            @RequestParam(value = "skuId") Long skuId,
            @RequestParam(value = "email") String email) {
        return super.addNotification(request, response, model, skuId, email);
    }
}
