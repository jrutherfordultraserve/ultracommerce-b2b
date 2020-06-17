/*
 * #%L
 * BroadleafCommerce Advanced CMS
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt).
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * 
 * NOTICE:  All information contained herein is, and remains
 * the property of Broadleaf Commerce, LLC
 * The intellectual and technical concepts contained
 * herein are proprietary to Broadleaf Commerce, LLC
 * and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Broadleaf Commerce, LLC.
 * #L%
 */
package com.broadleafcommerce.frontend.config;

import org.broadleafcommerce.presentation.condition.ConditionalOnTemplating;
import org.broadleafcommerce.presentation.resolver.BroadleafTemplateMode;
import org.broadleafcommerce.presentation.resolver.BroadleafThemeAwareTemplateResolver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.broadleafcommerce.theme.core.config.ThemeParentTemplateResolver;

@Configuration
@ConditionalOnTemplating
public class FrontendTemplatingConfig {

    @Value("${cache.page.templates}")
    protected Boolean cacheTemplate;
    
    @Value("${cache.page.templates.ttl}")
    protected Long cacheTTL;

    @Value("${theme.templates.folder}")
    protected String templateFolder;
    
    @Bean
    public BroadleafThemeAwareTemplateResolver blFrontendThemeAwareTemplateResolver() {
        BroadleafThemeAwareTemplateResolver templateResolver = new BroadleafThemeAwareTemplateResolver();
        templateResolver.setPrefix("classpath:themes/");
        templateResolver.setTemplateFolder(templateFolder);
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(BroadleafTemplateMode.HTML5);
        templateResolver.setCacheable(cacheTemplate);
        templateResolver.setCacheTTLMs(cacheTTL);
        templateResolver.setOrder(250);
        return templateResolver;
    }

    @Bean
    public ThemeParentTemplateResolver blFrontendThemeParentTemplateResolver() {
        ThemeParentTemplateResolver templateResolver = new ThemeParentTemplateResolver();
        templateResolver.setPrefix("classpath:themes/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateFolder(templateFolder);
        templateResolver.setTemplateMode(BroadleafTemplateMode.HTML5);
        templateResolver.setCacheable(cacheTemplate);
        templateResolver.setCacheTTLMs(cacheTTL);
        templateResolver.setOrder(275);
        return templateResolver;
    }
}
