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

import org.lan.iti.cloud.iha.server.IhaServer;
import org.lan.iti.cloud.iha.server.model.ClientDetails;
import org.lan.iti.cloud.iha.server.model.IhaServerRequestParam;
import org.lan.iti.cloud.iha.server.model.IhaServerResponse;
import org.lan.iti.cloud.iha.server.model.Scope;
import org.lan.iti.cloud.iha.server.provider.RequestParamProvider;
import org.lan.iti.cloud.iha.server.provider.ScopeProvider;
import org.lan.iti.cloud.iha.server.util.EndpointUtil;
import org.lan.iti.cloud.iha.server.util.OAuthUtil;
import org.lan.iti.cloud.iha.server.util.StringUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * Confirm authorization endpoint
 *
 * @author NorthLan
 * @date 2021-07-06
 * @url https://noahlan.com
 */
public class ApprovalEndpoint extends AbstractEndpoint {
    /**
     * The default authorization confirmation page pops up
     *
     * @param request  current HTTP request
     * @param response current HTTP response
     * @throws IOException IOException
     */
    public void showConfirmPage(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String approvalContent = createConfirmPageHtml(request);
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().append(approvalContent);
    }

    /**
     * Obtain authorization information when you jump to the authorization confirmation page after successful login
     *
     * @param request HttpServletRequest
     * @return IhaServerResponse
     */
    public IhaServerResponse<String, Map<String, Object>> getAuthClientInfo(HttpServletRequest request) {
        IhaServerRequestParam param = RequestParamProvider.parseRequest(request);
        ClientDetails clientDetail = IhaServer.getContext().getClientDetailsService().getByClientId(param.getClientId());
        OAuthUtil.validClientDetail(clientDetail);

        List<Map<String, Object>> scopeInfo = getScopeInfo(param);

        Map<String, Object> result = new HashMap<>(5);
        result.put("appInfo", clientDetail);
        result.put("scopes", scopeInfo);
        result.put("params", param);

        return new IhaServerResponse<String, Map<String, Object>>().data(result);
    }

    /**
     * Generate the html of the authorization confirmation page
     *
     * @param request current HTTP request
     * @return Confirm the html of the authorization page
     */
    private String createConfirmPageHtml(HttpServletRequest request) {
        IhaServerRequestParam param = RequestParamProvider.parseRequest(request);
        String clientId = param.getClientId();
        ClientDetails clientDetail = IhaServer.getContext().getClientDetailsService().getByClientId(clientId);
        OAuthUtil.validClientDetail(clientDetail);

        StringBuilder builder = new StringBuilder();
        String html = "<!DOCTYPE html>\n"
                + "<html lang=\"en\">\n"
                + "  <head>\n"
                + "    <meta charset=\"utf-8\">\n"
                + "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1, shrink-to-fit=no\">\n"
                + "    <meta name=\"description\" content=\"\">\n"
                + "    <meta name=\"author\" content=\"\">\n"
                + "    <title>OAuth Approval</title>\n"
                + "  </head>\n"
                + "  \n";
        builder.append(html).append("<body><h1>OAuth Approval</h1>");
        builder.append("<p>Do you authorize \"<strong>").append(clientDetail.getClientName()).append("</strong>");
        builder.append(" (").append(clientId).append(")");
        builder.append("\" to access your protected resources?</p>");
        builder.append("<form id=\"confirmationForm\" name=\"confirmationForm\" action=\"");

        String requestPath = StringUtil.appendIfNotEndWith(EndpointUtil.getAuthorizeUrl(request), "?") + request.getQueryString();
        builder.append(requestPath).append("\" method=\"post\">");
        builder.append("<input name=\"user_oauth_approval\" value=\"true\" type=\"hidden\"/>");

        String authorizeInputTemplate = "<label><input name=\"authorize\" value=\"Authorize\" type=\"submit\"/></label></form>";

        if (param.getScope() != null) {
            builder.append(createScopes(param, request));
            builder.append(authorizeInputTemplate);
        } else {
            builder.append(authorizeInputTemplate);
            builder.append("<form id=\"denialForm\" name=\"denialForm\" action=\"");
            builder.append(requestPath).append("/oauth/authorize\" method=\"post\">");
            builder.append("<input name=\"user_oauth_approval\" value=\"false\" type=\"hidden\"/>");
            builder.append("<label><input name=\"deny\" value=\"Deny\" type=\"submit\"/></label></form>");
        }

        builder.append("</body></html>");

        return builder.toString();
    }

    /**
     * Generate the scope list of the authorization confirmation page
     *
     * @param param   Parameters of the current HTTP request
     * @param request current HTTP request
     * @return the scope list of the authorization confirmation page
     */
    private String createScopes(IhaServerRequestParam param, HttpServletRequest request) {
        StringBuilder builder = new StringBuilder("<ul style=\"list-style: none;padding-inline-start: 20px;\">");
        List<Map<String, Object>> scopeInfo = getScopeInfo(param);

        for (Map<String, Object> scope : scopeInfo) {
            String approved = (Boolean) scope.get("selected") ? " checked" : "";
            String denied = (Boolean) scope.get("selected") ? "" : " checked";

            builder.append("<li><div class=\"form-group\">");
            builder.append("<input type=\"checkbox\" name=\"scopes\"").append(" value=\"").append(scope.get("code")).append("\"").append(approved).append(" style=\"margin-right: 5px;\">")
                    .append(scope.get("code")).append(" - ").append(scope.get("description"))
                    .append("</input> ");
            builder.append(denied).append("</div></li>");
        }
        builder.append("</ul>");
        return builder.toString();
    }

    /**
     * Reorganize scope information
     *
     * @param param Parameters of the current HTTP request
     * @return List
     */
    private List<Map<String, Object>> getScopeInfo(IhaServerRequestParam param) {
        ClientDetails clientDetail = IhaServer.getContext().getClientDetailsService().getByClientId(param.getClientId());

        Set<String> userAuthorizedScopes = OAuthUtil.validateScope(param.getScope(), clientDetail.getScopes());

        Set<String> supportedScopes = StringUtil.convertStrToList(clientDetail.getScopes());

        List<Scope> scopeList = ScopeProvider.getScopeByCodes(supportedScopes);

        List<Map<String, Object>> scopeInfo = new LinkedList<>();
        Map<String, Object> scopeItem = null;
        for (Scope idsScope : scopeList) {
            scopeItem = new HashMap<>(5);
            scopeItem.put("code", idsScope.getCode());
            scopeItem.put("description", idsScope.getDescription());
            scopeItem.put("selected", userAuthorizedScopes.contains(idsScope.getCode()));
            scopeInfo.add(scopeItem);
        }
        return scopeInfo;
    }
}
