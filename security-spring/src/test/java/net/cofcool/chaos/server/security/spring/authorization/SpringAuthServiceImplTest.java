/*
 * Copyright 2019 cofcool
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.cofcool.chaos.server.security.spring.authorization;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import net.cofcool.chaos.server.common.core.ConfigurationCustomizer;
import net.cofcool.chaos.server.common.core.ConfigurationSupport;
import net.cofcool.chaos.server.common.core.ConfigurationSupport.DefaultConfigurationCustomizer;
import net.cofcool.chaos.server.common.core.ExceptionCodeDescriptor;
import net.cofcool.chaos.server.common.core.ExceptionCodeManager;
import net.cofcool.chaos.server.common.core.Message;
import net.cofcool.chaos.server.common.security.AbstractLogin;
import net.cofcool.chaos.server.common.security.AbstractLogin.DefaultLogin;
import net.cofcool.chaos.server.common.security.AuthConfig;
import net.cofcool.chaos.server.common.security.AuthService;
import net.cofcool.chaos.server.common.security.PasswordProcessor;
import net.cofcool.chaos.server.common.security.User;
import net.cofcool.chaos.server.common.security.UserAuthorizationService;
import net.cofcool.chaos.server.common.security.UserStatus;
import net.cofcool.chaos.server.core.support.SimpleExceptionCodeDescriptor;
import net.cofcool.chaos.server.security.spring.authorization.SpringAuthServiceImplTest.Config;
import net.cofcool.chaos.server.security.spring.authorization.SpringAuthServiceImplTest.MvcConfig;
import net.cofcool.chaos.server.security.spring.config.SpringSecurityConfiguration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;

@SuppressWarnings("rawtypes")
@SpringBootTest(classes = {Config.class, MvcConfig.class})
@WebAppConfiguration
@SpringBootApplication
@EnableWebSecurity
class SpringAuthServiceImplTest {

    private static final DefaultLogin DEFAULT_LOGIN = new DefaultLogin("test", "1234");

    private static AuthConfig authConfig;

    @Resource
    SpringAuthServiceImpl springAuthService;

    @Resource
    ExceptionCodeManager exceptionCodeManager;

    @Resource
    WebApplicationContext webApplicationContext;

    @Resource
    ObjectMapper objectMapper;

    MockMvc mockMvc;

    @BeforeAll
    public static void setup() {
        authConfig = new AuthConfig();
        authConfig.setCsrfEnabled(false);
        authConfig.setUrls("/test");
    }

    @BeforeEach
    public void mockSetup() {
        mockMvc = MockMvcBuilders
            .webAppContextSetup(webApplicationContext)
            .apply(springSecurity())
            .build();
    }

    @Test
    void login() throws Exception {
        mockMvc
            .perform(
                MockMvcRequestBuilders.post(authConfig.getLoginUrl())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(DEFAULT_LOGIN))
            )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("code").value(exceptionCodeManager.getCode(ExceptionCodeDescriptor.SERVER_OK)));
    }

    @Test
    void unAuth() throws Exception {
        mockMvc
            .perform(
                MockMvcRequestBuilders.get("/xxx")
            )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
            .andExpect(MockMvcResultMatchers.redirectedUrlPattern(authConfig.getUnauthUrl() + "*"));
    }

    @Test
    void anno() throws Exception {
        mockMvc
            .perform(
                MockMvcRequestBuilders.get("/test")
            )
            .andDo(MockMvcResultHandlers.print());
    }

    @Configuration
    static class Config {

        @Bean
        public PasswordProcessor passwordProcessor() {
            return new PasswordProcessor() {
                @Override
                public String process(String rawPassword) {
                    return rawPassword;
                }

                @Override
                public boolean doMatch(String rawPassword, String encodedPassword) {
                    return rawPassword.equals(encodedPassword);
                }
            };
        }

        @Bean
        public ExceptionCodeManager exceptionCodeManager(@Autowired(required = false) ExceptionCodeDescriptor exceptionCodeDescriptor) {
            return new ExceptionCodeManager(
                exceptionCodeDescriptor == null
                    ? SimpleExceptionCodeDescriptor.DEFAULT_DESCRIPTOR : exceptionCodeDescriptor
            );
        }

        @Bean
        public ConfigurationSupport configurationSupport(
            ExceptionCodeManager exceptionCodeManager,
            @Autowired(required = false) ConfigurationCustomizer configurationCustomizer) {
            return ConfigurationSupport
                .builder()
                .exceptionCodeManager(exceptionCodeManager)
                .isDebug(true)
                .customizer(configurationCustomizer == null ? new DefaultConfigurationCustomizer() : configurationCustomizer)
                .build();
        }

        @Bean
        public UserAuthorizationService userAuthorizationService() {
            return new UserAuthorizationService() {
                @Override
                public User queryUser(AbstractLogin loginUser) {
                    User user = new User();
                    user.setUsername(DEFAULT_LOGIN.getUsername());
                    user.setPassword(DEFAULT_LOGIN.getPassword());
                    user.addUserStatus(UserStatus.NORMAL);
                    return user;
                }

                @Override
                public Message<Boolean> checkUser(User currentUser) {
                    return Message.of("", " ", true);
                }

                @Override
                public void setupUserData(User currentUser) {

                }

                @Override
                public void checkPermission(ServletRequest servletRequest,
                    ServletResponse servletResponse, Object authenticationInfo,
                    String requestPath) {

                }
            };
        }

        @Bean
        public SecurityFilterChain jsonSpringSecurityFilterChain(HttpSecurity httpSecurity, HttpMessageConverters messageConverter, PasswordProcessor passwordProcessor, UserAuthorizationService userAuthorizationService, ConfigurationSupport configurationSupport) throws Exception {
            SpringSecurityConfiguration securityConfiguration = new SpringSecurityConfiguration(messageConverter, userAuthorizationService, configurationSupport, passwordProcessor, authConfig);

            return securityConfiguration.buildFilterChain(httpSecurity);
        }

        @Bean
        public AuthService authService() {
            return new SpringAuthServiceImpl();
        }
    }

    @RestController
    static class MvcConfig {
        @Resource
        AuthService authService;

        @GetMapping("/test")
        public Message<User> test() {
            return ConfigurationSupport.getConfiguration().getMessage("OK", authService.readCurrentUser());
        }

        @RequestMapping("/auth/unauth")
        public Message unauth(String ex) {
            return ConfigurationSupport.getConfiguration().getMessage(ExceptionCodeDescriptor.AUTH_ERROR, ex);
        }
    }
}