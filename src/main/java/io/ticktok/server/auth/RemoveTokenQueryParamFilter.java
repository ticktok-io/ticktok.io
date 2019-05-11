package io.ticktok.server.auth;

import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class RemoveTokenQueryParamFilter extends GenericFilterBean {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest originalRequest = (HttpServletRequest) request;
        HttpServletRequest newRequest = new NoTokenServletRequest(originalRequest);
        chain.doFilter(newRequest, response);
    }

}
