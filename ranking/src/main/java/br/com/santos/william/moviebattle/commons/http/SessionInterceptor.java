package br.com.santos.william.moviebattle.commons.http;

import br.com.santos.william.moviebattle.commons.token.JwtTokenProvider;
import br.com.santos.william.moviebattle.player.Player;
import br.com.santos.william.moviebattle.player.Session;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class SessionInterceptor implements HandlerInterceptor {

    private final Session session;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;

    public SessionInterceptor(
            Session session,
            JwtTokenProvider jwtTokenProvider,
            UserDetailsService userDetailsService
    ) {
        this.session = session;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler
    ) throws Exception {
        var token = jwtTokenProvider.resolveToken(request);
        if (token != null) {
            var username = jwtTokenProvider.getUsername(token);
            var useDetails = userDetailsService.loadUserByUsername(username);
            session.setPlayer((Player) useDetails);
        }
        return true;
    }

}
