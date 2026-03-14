package lostark.todo.global.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.global.auth.OAuthAttributes;
import lostark.todo.domain.member.enums.Role;
import lostark.todo.domain.member.entity.Member;
import lostark.todo.domain.member.repository.MemberRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@RequiredArgsConstructor
@Service
@Slf4j
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 외부 HTTP 호출 (Google/Kakao API) — 트랜잭션 밖에서 실행
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails()
                .getUserInfoEndpoint().getUserNameAttributeName();
        OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());

        String authProvider = userRequest.getClientRegistration().getClientName();
        log.info("userName : {}", attributes.getEmail());

        // DB 작업 — repository 메서드 각각이 자체 @Transactional을 가짐
        Member member;
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
            member = memberRepository.get(attributes.getEmail());
            member.setAccessKey(userRequest.getAccessToken().getTokenValue());
            member = memberRepository.save(member);
            log.info("{} Login Success", attributes.getEmail());
        }

        return new ApplicationOAuth2User(member.getUsername(), oAuth2User.getAttributes());
    }
}
