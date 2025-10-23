package org.eee.account.config;

import lombok.extern.slf4j.Slf4j;
import org.eee.account.filter.TokenFilter;
import org.eee.account.filter.UsernamePasswordLoginFilter;
import org.eee.account.handler.*;
import org.eee.account.service.UserPrincipalService;
import org.eee.account.service.CustomOAuth2UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
public class SecurityConfig {

    @Autowired
    private UserPrincipalService userDetailsService;

    @Autowired
    private LoginSuccessHandler loginSuccessHandler;

    @Autowired
    private LoginFailureHandler loginFailureHandler;

    @Autowired
    private OauthSuccessHandler oauthSuccessHandler;

    @Autowired
    private OauthFailureHandler oauthFailureHandler;

    @Autowired
    private LogoutSuccessHandler logoutSuccessHandler;

    @Autowired
    private AuthenticationConfiguration authenticationConfiguration;

    @Autowired
    private CustomOAuth2UserService customOAuth2UserService;

    public UsernamePasswordLoginFilter getUsernamePasswordLoginFilter() throws Exception
    {
        UsernamePasswordLoginFilter usernamePasswordLoginFilter = new UsernamePasswordLoginFilter();
        usernamePasswordLoginFilter.setAuthenticationManager(authenticationConfiguration.getAuthenticationManager());
        usernamePasswordLoginFilter.setAuthenticationSuccessHandler(loginSuccessHandler);
        usernamePasswordLoginFilter.setAuthenticationFailureHandler(loginFailureHandler);
        usernamePasswordLoginFilter.setFilterProcessesUrl("/auth/login");
        return usernamePasswordLoginFilter;
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth ->
                        {
                            auth.requestMatchers("/auth/**", "/public/**").permitAll();
                            auth.requestMatchers("/admin/**").hasRole("ADMIN");
                            auth.requestMatchers("/user/**").hasRole("USER");
                            auth.anyRequest().permitAll();
                        }
                ).oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)
                        )
                        .successHandler(oauthSuccessHandler)
                        .failureHandler(oauthFailureHandler)
                 )
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)

                .authenticationProvider(authenticationProvider())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .logout(logout -> logout.logoutSuccessHandler(logoutSuccessHandler));

        http.addFilterAt(getUsernamePasswordLoginFilter(), UsernamePasswordLoginFilter.class);
        http.addFilterBefore(new TokenFilter(), UsernamePasswordLoginFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder()
    {
        String idForEncode = "bcrypt"; // 默认加密方式
        Map<String, PasswordEncoder> encoders = new HashMap<>();
        encoders.put("bcrypt", new BCryptPasswordEncoder());
        encoders.put("noop", org.springframework.security.crypto.password.NoOpPasswordEncoder.getInstance());

        return new DelegatingPasswordEncoder(idForEncode, encoders);
    }


    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
}