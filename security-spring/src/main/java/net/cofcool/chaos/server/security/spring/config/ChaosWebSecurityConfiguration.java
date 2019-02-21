package net.cofcool.chaos.server.security.spring.config;

import javax.servlet.Filter;
import net.cofcool.chaos.server.common.security.PasswordProcessor;
import net.cofcool.chaos.server.common.security.authorization.AuthService;
import net.cofcool.chaos.server.common.security.authorization.UserAuthorizationService;
import net.cofcool.chaos.server.core.config.WebApplicationContext;
import net.cofcool.chaos.server.security.spring.authorization.JsonAuthenticationFilter;
import net.cofcool.chaos.server.security.spring.authorization.SpringAuthServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * Spring Security 配置
 *
 * @author CofCool
 */
@Configuration
@EnableWebSecurity
public class ChaosWebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authenticationProvider(getApplicationContext().getBean("authenticationProvider", AuthenticationProvider.class))
                .addFilterAt(getApplicationContext().getBean("jsonAuthenticationFilter", UsernamePasswordAuthenticationFilter.class), UsernamePasswordAuthenticationFilter.class)
                .rememberMe()
                .and()
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/**").authenticated()
                .and()
                .formLogin().loginProcessingUrl(getLoginUrl())
                .and()
                .logout()
                .logoutUrl(WebApplicationContext.getConfiguration().getAuth().getLogoutUrl())
                .permitAll()
                .and()
                .sessionManagement()
                .maximumSessions(10)
                .expiredUrl(WebApplicationContext.getConfiguration().getAuth().getExpiredUrl());
    }

    private String getLoginUrl() {
        return WebApplicationContext.getConfiguration().getAuth().getLoginUrl();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService, PasswordProcessor passwordProcessor) {
        DaoAuthenticationProvider p = new DaoAuthenticationProvider();
        p.setPasswordEncoder(new PasswordEncoderDelegate(passwordProcessor));
        p.setUserDetailsService(userDetailsService);
        return p;
    }

    @Bean
    public Filter jsonAuthenticationFilter(HttpMessageConverter mappingJackson2HttpMessageConverter)
        throws Exception {
        JsonAuthenticationFilter filter = new JsonAuthenticationFilter();
        filter.setPostOnly(true);
        filter.setMessageConverter(mappingJackson2HttpMessageConverter);
        filter.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher(getLoginUrl(), "POST"));
        filter.setAuthenticationManager(authenticationManager());

        return filter;
    }

    @Bean
    public AuthService authService(UserAuthorizationService userAuthorizationService) {
        SpringAuthServiceImpl authService = new SpringAuthServiceImpl();
        authService.setUserAuthorizationService(userAuthorizationService);

        return authService;
    }

    class PasswordEncoderDelegate implements PasswordEncoder {

        private PasswordProcessor passwordProcessor;

        PasswordEncoderDelegate(
            PasswordProcessor passwordProcessor) {
            this.passwordProcessor = passwordProcessor;
        }

        @Override
        public String encode(CharSequence rawPassword) {
            return passwordProcessor.process(rawPassword.toString());
        }

        @Override
        public boolean matches(CharSequence rawPassword, String encodedPassword) {
            return passwordProcessor.doMatch(rawPassword.toString(), encodedPassword);
        }
    }

}
