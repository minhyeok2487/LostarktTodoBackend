package lostark.todo.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.auth.OAuthAttributes;
import lostark.todo.domain.Role;
import lostark.todo.domain.member.Member;
import lostark.todo.domain.member.MemberRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collections;

@RequiredArgsConstructor
@Service
@Slf4j
@Transactional
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId(); //현재 로그인 진행 중인 서비스를 구분하는 코드
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails()
                .getUserInfoEndpoint().getUserNameAttributeName();                      //OAuth2 로그인 진행 시 키가 되는 필드값
        OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());

        String authProvider = userRequest.getClientRegistration().getClientName();
        log.info("userName : {}", attributes.getEmail());
        Member member = null;
        // 유저가 존재하지 않으면 새로 생성한다.
        if (!memberRepository.existsByUsername(attributes.getEmail())) {
            member = Member.builder()
                    .username(attributes.getEmail())
                    .accessKey(userRequest.getAccessToken().getTokenValue())
                    .authProvider(authProvider)
                    .characters(new ArrayList<>())
                    .friends(new ArrayList<>())
                    .role(Role.USER)
                    .build();
            member = memberRepository.save(member);
            log.info("{} Signup Success", attributes.getEmail());
        } else {
            member = memberRepository.findByUsername(attributes.getEmail()).orElseThrow();
            member.setAccessKey(userRequest.getAccessToken().getTokenValue());
            log.info("{} Login Success", attributes.getEmail());
        }

        return new ApplicationOAuth2User(member.getUsername(), oAuth2User.getAttributes());
    }
}