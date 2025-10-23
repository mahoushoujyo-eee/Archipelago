package org.eee.account.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * 自定义 OAuth2 用户服务
 * 负责从 OAuth2 提供商获取用户信息并进行处理
 */
@Slf4j
@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        try {
            log.info("开始加载 OAuth2 用户信息...");
            log.info("OAuth2 提供商: {}", userRequest.getClientRegistration().getRegistrationId());

            // 使用默认服务获取用户信息
            OAuth2User oauth2User = delegate.loadUser(userRequest);

            if (oauth2User == null) {
                log.error("从 OAuth2 提供商获取的用户信息为 null");
                throw new OAuth2AuthenticationException("用户信息获取失败");
            }

            log.info("成功获取 OAuth2 用户信息: {}", oauth2User.getName());
            log.info("用户属性: {}", oauth2User.getAttributes());

            // 根据不同的 OAuth2 提供商处理用户信息
            String registrationId = userRequest.getClientRegistration().getRegistrationId();
            return processOAuth2User(oauth2User, registrationId);

        } catch (Exception e) {
            log.error("OAuth2 用户信息加载失败", e);
            throw new OAuth2AuthenticationException("用户信息加载失败: " + e.getMessage());
        }
    }

    /**
     * 根据不同的 OAuth2 提供商处理用户信息
     */
    private OAuth2User processOAuth2User(OAuth2User oauth2User, String registrationId) {
        String nameAttributeKey;

        // 根据不同提供商设置用户名属性
        switch (registrationId.toLowerCase()) {
            case "github":
                nameAttributeKey = "login";
                break;
            case "google":
                nameAttributeKey = "email";
                break;
            case "facebook":
                nameAttributeKey = "name";
                break;
            default:
                nameAttributeKey = "name";
                log.warn("未知的 OAuth2 提供商: {}，使用默认的用户名属性: name", registrationId);
        }

        log.info("使用用户名属性: {} = {}", nameAttributeKey, oauth2User.getAttributes().get(nameAttributeKey));

        // 为用户添加默认权限
        return new DefaultOAuth2User(
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")),
            oauth2User.getAttributes(),
            nameAttributeKey
        );
    }
}
