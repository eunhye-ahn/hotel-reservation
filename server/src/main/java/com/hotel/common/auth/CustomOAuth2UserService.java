package com.hotel.common.auth;

import com.hotel.user.domain.AuthProvider;
import com.hotel.user.domain.Role;
import com.hotel.user.domain.User;
import com.hotel.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        //사용자정보 받아오기(구글 API 호출)
        OAuth2User oAuth2User = super.loadUser(userRequest);

        Map<String, Object> attributes = oAuth2User.getAttributes();
        String providerId = (String) attributes.get("sub");
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");

        //DB에 중복검사 (provider, providerId 기준)
        User user = userRepository.findByProviderAndProviderId(AuthProvider.GOOGLE, providerId)
                .orElseGet(()->userRepository.save(
                        User.builder()
                                .email(email)
                                .name(name)
                                .provider(AuthProvider.GOOGLE)
                                .providerId(providerId)
                                .role(Role.ROLE_GUEST)
                                .build()
                ));

        //DB의 user정보를 attribute에 추가
        Map<String,Object> customAttributes = new HashMap<>(attributes);
        customAttributes.put("userId", user.getId());
        customAttributes.put("role", user.getRole().name());

        return new DefaultOAuth2User(
                List.of(new SimpleGrantedAuthority(user.getRole().name())),
                customAttributes,
                //attribute 중 "sub"을 표준으로 선언
                "sub");
    }
}
