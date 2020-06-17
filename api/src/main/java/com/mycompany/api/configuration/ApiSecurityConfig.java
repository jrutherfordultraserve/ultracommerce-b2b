package com.mycompany.api.configuration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.profile.web.core.security.RestApiCustomerStateFilter;
import org.broadleafcommerce.profile.web.site.security.SessionFixationProtectionFilter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.access.channel.ChannelDecisionManagerImpl;
import org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationFilter;

import java.util.UUID;

import javax.servlet.Filter;

/**
 * @author Elbert Bautista (elbertbautista)
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class ApiSecurityConfig extends WebSecurityConfigurerAdapter {
    
    private static final Log LOG = LogFactory.getLog(ApiSecurityConfig.class);

    @Value("${asset.server.url.prefix.internal}")
    protected String assetServerUrlPrefixInternal;

    @Bean(name="blAuthenticationManager")
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManager();
    }
    
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        String password = UUID.randomUUID().toString();
        String user = "broadleafapi";
        auth.inMemoryAuthentication()
            .withUser(user)
            .password(password)
            .roles("USER");
        LOG.info(String.format("%n%n%nBasic auth configured with user %s and password: %s%n%n%n", user, password));
    }
    
   @Override
   public void configure(WebSecurity web) throws Exception {
       web.ignoring()
           .antMatchers("/api/**/webjars/**",
               "/api/**/images/favicon-*",
               "/api/**/jhawtcode/**",
               "/api/**/swagger-ui.html",
               "/api/**/swagger-resources/**",
               "/api/**/v2/api-docs");
   }
    
   @Override
   protected void configure(HttpSecurity http) throws Exception {
       http
           .antMatcher("/api/**")
           .httpBasic()
           .and()
           .csrf().disable()
           .authorizeRequests()
               .antMatchers("/api/**")
               .authenticated()
               .and()
           .sessionManagement()
               .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
               .sessionFixation()
               .none()
               .enableSessionUrlRewriting(false)
               .and()
           .requiresChannel()
               .anyRequest()
               .requires(ChannelDecisionManagerImpl.ANY_CHANNEL)
           .and()
           .addFilterAfter(apiCustomerStateFilter(), RememberMeAuthenticationFilter.class);
   }

    @Bean
    public Filter apiCustomerStateFilter() {
        return new RestApiCustomerStateFilter();
    }

    /**
     * Don't allow the auto registration of the filter for the main API request flow
     *
     * @param filter the Filter instance to disable in the main flow
     * @return the registration bean that designates the filter as being disabled in the main flow
     */
    @Bean
    public FilterRegistrationBean blSessionFixationProtectionFilterFilterRegistrationBean(@Qualifier("blSessionFixationProtectionFilter") SessionFixationProtectionFilter filter) {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean(filter);
        registrationBean.setEnabled(false);
        return registrationBean;
    }
}
