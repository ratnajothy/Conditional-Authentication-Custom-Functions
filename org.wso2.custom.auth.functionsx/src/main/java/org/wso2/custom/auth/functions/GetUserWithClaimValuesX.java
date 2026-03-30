package org.wso2.custom.auth.functions;

import org.wso2.carbon.identity.application.authentication.framework.config.model.graph.js.JsAuthenticatedUser;
import org.wso2.carbon.identity.application.authentication.framework.exception.FrameworkException;

import java.util.Map;

@FunctionalInterface
public interface GetUserWithClaimValuesX {

    JsAuthenticatedUser getX(Map<String, String> claimMap, Object... parameters) throws
            FrameworkException;
}
