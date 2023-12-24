package lostark.todo.security;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.config.TokenProvider;
import lostark.todo.domain.keyvalue.KeyValueRepository;
import lostark.todo.domain.member.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

import static lostark.todo.security.RedirectUrlCookieFilter.REDIRECT_URI_PARAM;

@Slf4j
@Component
@AllArgsConstructor
@Transactional
public class OAuthSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

  private static final String LOCAL_REDIRECT_URL = "http://localhost:3000";

  @Autowired
  KeyValueRepository keyValueRepository;

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
    log.info("auth succeeded");
    ApplicationOAuth2User userPrincipal = (ApplicationOAuth2User) authentication.getPrincipal();
    String username = userPrincipal.getName();

    String key = keyValueRepository.findByKeyName("JWT-KEY");
    TokenProvider tokenProvider = new TokenProvider();
    String token = tokenProvider.createToken(authentication,key);

    Optional<Cookie> oCookie = Arrays.stream(request.getCookies()).filter(cookie -> cookie.getName().equals(REDIRECT_URI_PARAM)).findFirst();
    Optional<String> redirectUri = oCookie.map(Cookie::getValue);
    response.sendRedirect(redirectUri.orElseGet(() -> LOCAL_REDIRECT_URL)+"/sociallogin?token="+token+"&username="+username);
  }

}