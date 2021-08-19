/*
 *
 *  * Copyright (c) [2019-2021] [NorthLan](lan6995@gmail.com)
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.lan.iti.iha.server.endpoint;

import lombok.AllArgsConstructor;
import org.lan.iti.common.core.util.StringUtil;
import org.lan.iti.iha.oauth2.util.ClientCertificateUtil;
import org.lan.iti.iha.security.IhaSecurity;
import org.lan.iti.iha.security.authentication.Authentication;
import org.lan.iti.iha.security.clientdetails.ClientDetails;
import org.lan.iti.iha.security.exception.authentication.AuthenticationException;
import org.lan.iti.iha.security.userdetails.UserDetails;
import org.lan.iti.iha.server.IhaServer;
import org.lan.iti.iha.server.security.IhaServerRequestParam;
import org.lan.iti.iha.server.util.EndpointUtil;
import org.lan.iti.iha.server.util.OAuth2Util;
import org.lan.iti.iha.simple.IhaSimple;
import org.lan.iti.iha.simple.security.SimpleRequestParameter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Login Endpoint
 *
 * @author NorthLan
 * @date 2021-07-06
 * @url https://noahlan.com
 */
@AllArgsConstructor
public class LoginEndpoint extends AbstractEndpoint {
    /**
     * 显示默认的登录页面
     *
     * @param request  current HTTP request
     * @param response current HTTP response
     * @throws IOException IOException
     */
    public void showLoginPage(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String loginPageHtml = generateLoginPageHtml(request);
        response.setContentType("text/html;charset=UTF-8");
        response.setContentLength(loginPageHtml.getBytes(StandardCharsets.UTF_8).length);
        response.getWriter().write(loginPageHtml);
    }

    private String generateLoginPageHtml(HttpServletRequest request) {
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html>\n"
                + "<html lang=\"en\">\n"
                + "  <head>\n"
                + "    <meta charset=\"utf-8\">\n"
                + "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1, shrink-to-fit=no\">\n"
                + "    <meta name=\"description\" content=\"\">\n"
                + "    <meta name=\"author\" content=\"\">\n"
                + "    <title>Please sign in</title>\n"
                + "    <link href=\"https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0-beta/css/bootstrap.min.css\" rel=\"stylesheet\" integrity=\"sha384-/Y6pD6FV/Vv2HJnA6t+vslU6fwYXjCFtcEpHbNJ0lyAFsXTsjBbfaDjzALeQsN6M\" crossorigin=\"anonymous\">\n"
                + "    <link href=\"https://getbootstrap.com/docs/4.0/examples/signin/signin.css\" rel=\"stylesheet\" crossorigin=\"anonymous\"/>\n"
                + "  </head>\n"
                + "  <body>\n"
                + "     <div class=\"container\">\n");

        String authenticationUrl = StringUtil.appendIfNotEndWith(EndpointUtil.getLoginUrl(request), "?") + request.getQueryString();
        sb.append("      <form class=\"form-signin\" method=\"post\" action=\"").append(authenticationUrl).append("\">\n")
                .append("        <h2 class=\"form-signin-heading\">Please sign in</h2>\n")
                .append("        <p>\n")
                .append("          <label for=\"username\" class=\"sr-only\">Username</label>\n")
                .append("          <input type=\"text\" id=\"username\" name=\"").append("username")
                .append("\" class=\"form-control\" placeholder=\"Username\" required autofocus>\n").append("        </p>\n")
                .append("        <p>\n").append("          <label for=\"password\" class=\"sr-only\">Password</label>\n")
                .append("          <input type=\"password\" id=\"password\" name=\"")
                .append("password").append("\" class=\"form-control\" placeholder=\"Password\" required>\n")
                .append("        </p>\n").append("        <button class=\"btn btn-lg btn-primary btn-block\" type=\"submit\">Sign in</button>\n")
                .append("      </form>\n");

        sb.append("</div>\n");
        sb.append("</body></html>");

        return sb.toString();
    }

    /**
     * Login with account password
     *
     * @param request  current HTTP request
     * @param response current HTTP response
     * @return Confirm authorization page
     */
    public String login(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        SimpleRequestParameter parameter = new SimpleRequestParameter(request, response);
        // TODO simple config
        parameter.setConfig(IhaSimple.getConfig());

        Authentication authentication = IhaSecurity.getSecurityManager().authenticate(parameter);
        if (!authentication.isAuthenticated()) {
            throw new AuthenticationException("failed");
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        if (userDetails == null) {
            throw new AuthenticationException("failed");
        }
        IhaServer.getContext().getUserStoreService().save(userDetails, request, response);

        IhaServerRequestParam serverRequestParam = new IhaServerRequestParam(parameter);

        // TODO 验证 client auth method
        serverRequestParam.setClient(ClientCertificateUtil.getClientCertificate(request));
        ClientDetails clientDetails = IhaSecurity.getContext().getClientDetailsService().getByClientId(serverRequestParam.getClientId());
        OAuth2Util.validClientDetails(clientDetails);

        String redirectUri;
        // When the client supports automatic authorization, it will judge whether the {@code autoapprove} function is enabled
        if (clientDetails.isAutoApprove() &&
                StringUtil.isNotEmpty(serverRequestParam.getAutoApprove()) && "TRUE".equalsIgnoreCase(serverRequestParam.getAutoApprove())) {
            redirectUri = EndpointUtil.getAuthorizeAutoApproveUrl(request);
        } else {
            redirectUri = EndpointUtil.getConfirmPageUrl(request);
        }
        return OAuth2Util.createAuthorizeUrl(redirectUri, serverRequestParam);
    }
}
