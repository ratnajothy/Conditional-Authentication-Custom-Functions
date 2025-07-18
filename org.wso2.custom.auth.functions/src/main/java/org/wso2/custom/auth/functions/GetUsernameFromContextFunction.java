package org.wso2.custom.auth.functions;

import org.graalvm.polyglot.HostAccess;
import org.wso2.carbon.identity.application.authentication.framework.config.model.graph.js.JsAuthenticationContext;

@FunctionalInterface
public interface GetUsernameFromContextFunction {

    @HostAccess.Export
    String getUsernameFromContext(JsAuthenticationContext context, int step);
}
