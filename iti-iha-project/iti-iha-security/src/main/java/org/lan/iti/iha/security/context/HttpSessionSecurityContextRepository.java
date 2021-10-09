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

package org.lan.iti.iha.security.context;

import cn.hutool.core.lang.Assert;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.lan.iti.iha.security.authentication.Authentication;
import org.lan.iti.iha.security.authentication.support.AnonymousAuthenticationToken;

import javax.servlet.AsyncContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.ServletResponseWrapper;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * A {@code SecurityContextRepository} implementation which stores the security context in
 * the {@code HttpSession} between requests.
 *
 * @author NorthLan
 * @date 2021/8/19
 * @url https://blog.noahlan.com
 */
@Setter
@Accessors(chain = true)
@Slf4j
public class HttpSessionSecurityContextRepository implements SecurityContextRepository {
    /**
     * The default key under which the security context will be stored in the session.
     */
    public static final String IHA_SECURITY_CONTEXT_KEY = "IHA_SECURITY_CONTEXT";

    /**
     * SecurityContext instance used to check for equality with default (unauthenticated)
     * content
     */
    private final Object contextObject = SecurityContextHolder.createEmptyContext();
    private boolean allowSessionCreation = true;
    private boolean disableUrlRewriting = false;
    private String securityContextKey = IHA_SECURITY_CONTEXT_KEY;

    @Override
    public SecurityContext loadContext(HttpRequestResponseHolder holder) {
        HttpServletRequest request = holder.getRequest();
        HttpServletResponse response = holder.getResponse();
        HttpSession httpSession = request.getSession(false);
        SecurityContext context = readSecurityContextFromSession(httpSession);
        if (context == null) {
            context = generateNewContext();
            if (log.isTraceEnabled()) {
                log.trace(String.format("Created %s", context));
            }
        }
        SaveToSessionResponseWrapper wrappedResponse = new SaveToSessionResponseWrapper(response, request,
                httpSession != null, context);
        SaveToSessionRequestWrapper wrappedRequest = new SaveToSessionRequestWrapper(request, wrappedResponse);
        holder.setRequest(wrappedRequest);
        holder.setResponse(wrappedResponse);
        return context;
    }


    @SuppressWarnings("ConstantConditions")
    @Override
    public void saveContext(SecurityContext context, HttpServletRequest request, HttpServletResponse response) {
        SaveContextOnUpdateOrErrorResponseWrapper responseWrapper = getNativeResponse(response, SaveContextOnUpdateOrErrorResponseWrapper.class);
        Assert.state(responseWrapper != null, () -> "Cannot invoke saveContext on response " + response
                + ". You must use the HttpRequestResponseHolder.response after invoking loadContext");
        responseWrapper.saveContext(context);
    }

    @Override
    public boolean containsContext(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }
        return session.getAttribute(this.securityContextKey) != null;
    }

    @Nullable
    private <T> T getNativeResponse(ServletResponse response, @Nullable Class<T> requiredType) {
        if (requiredType != null) {
            if (requiredType.isInstance(response)) {
                return (T) response;
            } else if (response instanceof ServletResponseWrapper) {
                return getNativeResponse(((ServletResponseWrapper) response).getResponse(), requiredType);
            }
        }
        return null;
    }

    /**
     * @param httpSession the session obtained from the request.
     */
    private SecurityContext readSecurityContextFromSession(HttpSession httpSession) {
        if (httpSession == null) {
            log.trace("No HttpSession currently exists");
            return null;
        }
        // Session exists, so try to obtain a context from it.
        Object contextFromSession = httpSession.getAttribute(this.securityContextKey);
        if (contextFromSession == null) {
            log.trace(String.format("Did not find SecurityContext in HttpSession %s "
                    + "using the IHA_SECURITY_CONTEXT session attribute", httpSession.getId()));
            return null;
        }

        // We now have the security context object from the session.
        if (!(contextFromSession instanceof SecurityContext)) {
            log.warn(String.format(
                    "%s did not contain a SecurityContext but contained: '%s'; are you improperly "
                            + "modifying the HttpSession directly (you should always use SecurityContextHolder) "
                            + "or using the HttpSession attribute reserved for this class?",
                    this.securityContextKey, contextFromSession));
            return null;
        }

        if (log.isTraceEnabled()) {
            log.trace(String.format("Retrieved %s from %s", contextFromSession, this.securityContextKey));
        } else if (log.isDebugEnabled()) {
            log.debug(String.format("Retrieved %s", contextFromSession));
        }
        // Everything OK. The only non-null return from this method.
        return (SecurityContext) contextFromSession;
    }

    /**
     * By default, calls {@link SecurityContextHolder#createEmptyContext()} to obtain a
     * new context (there should be no context present in the holder when this method is
     * called). Using this approach the context creation strategy is decided by the
     * {@link SecurityContextHolderStrategy} in use. The default implementations will
     * return a new <tt>SecurityContextImpl</tt>.
     *
     * @return a new SecurityContext instance. Never null.
     */
    protected SecurityContext generateNewContext() {
        return SecurityContextHolder.createEmptyContext();
    }

    private static class SaveToSessionRequestWrapper extends HttpServletRequestWrapper {

        private final SaveContextOnUpdateOrErrorResponseWrapper response;

        SaveToSessionRequestWrapper(HttpServletRequest request, SaveContextOnUpdateOrErrorResponseWrapper response) {
            super(request);
            this.response = response;
        }

        @Override
        public AsyncContext startAsync() {
            this.response.disableSaveOnResponseCommitted();
            return super.startAsync();
        }

        @Override
        public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse)
                throws IllegalStateException {
            this.response.disableSaveOnResponseCommitted();
            return super.startAsync(servletRequest, servletResponse);
        }

    }

    /**
     * Wrapper that is applied to every request/response to update the
     * <code>HttpSession</code> with the <code>SecurityContext</code> when a
     * <code>sendError()</code> or <code>sendRedirect</code> happens. See SEC-398.
     * <p>
     * Stores the necessary state from the start of the request in order to make a
     * decision about whether the security context has changed before saving it.
     */
    final class SaveToSessionResponseWrapper extends SaveContextOnUpdateOrErrorResponseWrapper {

        private final HttpServletRequest request;

        private final boolean httpSessionExistedAtStartOfRequest;

        private final SecurityContext contextBeforeExecution;

        private final Authentication authBeforeExecution;

        private boolean isSaveContextInvoked;

        /**
         * Takes the parameters required to call <code>saveContext()</code> successfully
         * in addition to the request and the response object we are wrapping.
         *
         * @param request                            the request object (used to obtain the session, if one exists).
         * @param httpSessionExistedAtStartOfRequest indicates whether there was a session
         *                                           in place before the filter chain executed. If this is true, and the session is
         *                                           found to be null, this indicates that it was invalidated during the request and
         *                                           a new session will now be created.
         * @param context                            the context before the filter chain executed. The context will
         *                                           only be stored if it or its contents changed during the request.
         */
        SaveToSessionResponseWrapper(HttpServletResponse response, HttpServletRequest request,
                                     boolean httpSessionExistedAtStartOfRequest, SecurityContext context) {
            super(response, HttpSessionSecurityContextRepository.this.disableUrlRewriting);
            this.request = request;
            this.httpSessionExistedAtStartOfRequest = httpSessionExistedAtStartOfRequest;
            this.contextBeforeExecution = context;
            this.authBeforeExecution = context.getAuthentication();
        }

        /**
         * Stores the supplied security context in the session (if available) and if it
         * has changed since it was set at the start of the request. If the
         * AuthenticationTrustResolver identifies the current user as anonymous, then the
         * context will not be stored.
         *
         * @param context the context object obtained from the SecurityContextHolder after
         *                the request has been processed by the filter chain.
         *                SecurityContextHolder.getContext() cannot be used to obtain the context as it
         *                has already been cleared by the time this method is called.
         */
        @Override
        protected void saveContext(SecurityContext context) {
            final Authentication authentication = context.getAuthentication();
            HttpSession httpSession = this.request.getSession(false);
            String securityContextKey = HttpSessionSecurityContextRepository.this.securityContextKey;
            // See SEC-776
            if (authentication == null
                    || authentication instanceof AnonymousAuthenticationToken) {
                if (httpSession != null && this.authBeforeExecution != null) {
                    // SEC-1587 A non-anonymous context may still be in the session
                    // SEC-1735 remove if the contextBeforeExecution was not anonymous
                    httpSession.removeAttribute(securityContextKey);
                    this.isSaveContextInvoked = true;
                }
                if (authentication == null) {
                    log.debug("Did not store empty SecurityContext");
                } else {
                    log.debug("Did not store anonymous SecurityContext");
                }
                return;
            }
            httpSession = (httpSession != null) ? httpSession : createNewSessionIfAllowed(context, authentication);
            // If HttpSession exists, store current SecurityContext but only if it has
            // actually changed in this thread (see SEC-37, SEC-1307, SEC-1528)
            if (httpSession != null) {
                // We may have a new session, so check also whether the context attribute
                // is set SEC-1561
                if (contextChanged(context) || httpSession.getAttribute(securityContextKey) == null) {
                    httpSession.setAttribute(securityContextKey, context);
                    this.isSaveContextInvoked = true;
                    log.debug(String.format("Stored %s to HttpSession [%s]", context, httpSession));
                }
            }
        }

        private boolean contextChanged(SecurityContext context) {
            return this.isSaveContextInvoked || context != this.contextBeforeExecution
                    || context.getAuthentication() != this.authBeforeExecution;
        }

        private HttpSession createNewSessionIfAllowed(SecurityContext context, Authentication authentication) {
            if (this.httpSessionExistedAtStartOfRequest) {
                log.debug("HttpSession is now null, but was not null at start of request; "
                        + "session was invalidated, so do not create a new session");
                return null;
            }
            if (!HttpSessionSecurityContextRepository.this.allowSessionCreation) {
                log.debug("The HttpSession is currently null, and the "
                        + HttpSessionSecurityContextRepository.class.getSimpleName()
                        + " is prohibited from creating an HttpSession "
                        + "(because the allowSessionCreation property is false) - SecurityContext thus not "
                        + "stored for next request");
                return null;
            }
            // Generate a HttpSession only if we need to
            if (HttpSessionSecurityContextRepository.this.contextObject.equals(context)) {
                log.debug(String.format(
                        "HttpSession is null, but SecurityContext has not changed from "
                                + "default empty context %s so not creating HttpSession or storing SecurityContext",
                        context));
                return null;
            }
            try {
                HttpSession session = this.request.getSession(true);
                log.debug("Created HttpSession as SecurityContext is non-default");
                return session;
            } catch (IllegalStateException ex) {
                // Response must already be committed, therefore can't create a new
                // session
                log.warn("Failed to create a session, as response has been committed. "
                        + "Unable to store SecurityContext.");
            }
            return null;
        }

    }
}
