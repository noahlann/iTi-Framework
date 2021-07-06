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

package org.lan.iti.cloud.iha.server.endpoint;

import cn.hutool.core.util.ObjectUtil;
import org.lan.iti.cloud.iha.server.IhaServer;
import org.lan.iti.cloud.iha.server.exception.IhaServerException;
import org.lan.iti.cloud.iha.server.model.ClientDetails;
import org.lan.iti.cloud.iha.server.model.IhaServerRequestParam;
import org.lan.iti.cloud.iha.server.model.IhaServerResponse;
import org.lan.iti.cloud.iha.server.model.User;
import org.lan.iti.cloud.iha.server.model.enums.ErrorResponse;
import org.lan.iti.cloud.iha.server.pipeline.Pipeline;
import org.lan.iti.cloud.iha.server.provider.RequestParamProvider;
import org.lan.iti.cloud.iha.server.util.EndpointUtil;
import org.lan.iti.cloud.iha.server.util.OAuthUtil;
import org.lan.iti.cloud.iha.server.util.StringUtil;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
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
                .append("          <input type=\"text\" id=\"username\" name=\"").append(IhaServer.getIhaServerConfig().getUsernameField())
                .append("\" class=\"form-control\" placeholder=\"Username\" required autofocus>\n").append("        </p>\n")
                .append("        <p>\n").append("          <label for=\"password\" class=\"sr-only\">Password</label>\n")
                .append("          <input type=\"password\" id=\"password\" name=\"")
                .append(IhaServer.getIhaServerConfig().getPasswordField()).append("\" class=\"form-control\" placeholder=\"Password\" required>\n")
                .append("        </p>\n").append("        <button class=\"btn btn-lg btn-primary btn-block\" type=\"submit\">Sign in</button>\n")
                .append("      </form>\n");

        sb.append("</div>\n");
        sb.append("</body></html>");

        return sb.toString();
    }

    /**
     * Login with account password
     *
     * @param servletRequest  current HTTP request
     * @param servletResponse current HTTP response
     * @return Confirm authorization page
     */
    public IhaServerResponse<String, String> login(ServletRequest servletRequest, ServletResponse servletResponse) {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        Pipeline<User> signInPipeline = IhaServer.getContext().getSignInPipeline();
        signInPipeline = this.getUserInfoIdsPipeline(signInPipeline);
        if (!signInPipeline.preHandle(request, servletResponse)) {
            throw new IhaServerException("SignInPipeline<User>.preHandle returns false, the process is blocked.");
        }
        IhaServerRequestParam param = RequestParamProvider.parseRequest(request);
        User user = signInPipeline.postHandle(request, servletResponse);
        if (null == user) {
            String username = param.getUsername();
            String password = param.getPassword();
            if (ObjectUtil.hasEmpty(username, password)) {
                throw new IhaServerException(ErrorResponse.INVALID_USER_CERTIFICATE);
            }
            user = IhaServer.getContext().getUserDetailService().loginByUsernameAndPassword(username, password, param.getClientId());
            if (null == user) {
                throw new IhaServerException(ErrorResponse.INVALID_USER_CERTIFICATE);
            }
        }

        IhaServer.saveUser(user, request);

        ClientDetails clientDetails = IhaServer.getContext().getClientDetailsService().getByClientId(param.getClientId());
        OAuthUtil.validClientDetail(clientDetails);

        String redirectUri;
        // When the client supports automatic authorization, it will judge whether the {@code autoapprove} function is enabled
        if (null != clientDetails.getAutoApprove() && clientDetails.getAutoApprove() &&
                StringUtil.isNotEmpty(param.getAutoApprove()) && "TRUE".equalsIgnoreCase(param.getAutoApprove())) {
            redirectUri = EndpointUtil.getAuthorizeAutoApproveUrl(request);
        } else {
            redirectUri = EndpointUtil.getConfirmPageUrl(request);
        }
        String fullUrl = OAuthUtil.createAuthorizeUrl(redirectUri, param);
        return new IhaServerResponse<String, String>().data(fullUrl);
    }
}
