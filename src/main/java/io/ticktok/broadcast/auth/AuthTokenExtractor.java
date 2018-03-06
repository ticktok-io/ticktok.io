package io.ticktok.broadcast.auth;

import javax.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AuthTokenExtractor {

    public static final String AUTH_PARAM = "access_token";
    public static final String AUTH_HEADER = "Authorization";

    private static final Pattern AUTH_HEADER_PATTERN = Pattern.compile("token ([a-z0-9]+)");
    private final HttpServletRequest request;

    public AuthTokenExtractor(HttpServletRequest request) {
        this.request = request;
    }

    public Object extract() {
        String authToken = getAuthTokenFromHeader();
        if(authToken == null) {
            authToken = request.getParameter(AUTH_PARAM);
        }
        return authToken;
    }

    private String getAuthTokenFromHeader() {
        String auth = request.getHeader(AUTH_HEADER);
        if(auth != null) {
            Matcher matcher = AUTH_HEADER_PATTERN.matcher(auth);
            if (matcher.find()) {
                return matcher.group(1);
            }
        }
        return null;
    }
}
