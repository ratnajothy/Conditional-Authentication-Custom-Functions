package org.wso2.custom.auth.functions;

import java.util.HashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.CarbonConstants;
import org.wso2.custom.auth.functions.internal.UserStoreFunctionsServiceXHolder;
import org.wso2.carbon.identity.application.authentication.framework.config.model.graph.JsWrapperFactoryProvider;
import org.wso2.carbon.identity.application.authentication.framework.config.model.graph.js.JsAuthenticatedUser;
import org.wso2.carbon.identity.application.authentication.framework.config.model.graph.js.JsAuthenticationContext;
import org.wso2.carbon.identity.application.authentication.framework.exception.FrameworkException;
import org.wso2.carbon.identity.application.authentication.framework.model.AuthenticatedUser;
import org.wso2.carbon.identity.core.util.IdentityTenantUtil;
import org.wso2.carbon.identity.core.util.IdentityUtil;
import org.wso2.carbon.user.api.UserRealm;
import org.wso2.carbon.user.api.UserStoreException;
import org.wso2.carbon.user.core.UserStoreManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 *  This functional class will expose the user store related utility functions to the adaptive authentication script.
 */
public class UserStoreFunctionsX implements GetUserWithClaimValuesX {

    private static final Log LOG = LogFactory.getLog(UserStoreFunctionsX.class);

    public JsAuthenticatedUser getX(Map<String, String> claimMap, Object... parameters)
            throws FrameworkException {

        JsAuthenticationContext authenticationContext = null;
        String tenantDomain = null;
        String profile = "default";
        if (claimMap == null || parameters == null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Passed parameter to getUniqueUserWithClaimValues method has null values");
            }
            return null;
        }
        if (parameters.length == 2) {
            if ( parameters[0] instanceof JsAuthenticationContext) {
                authenticationContext = (JsAuthenticationContext) parameters[0];
                tenantDomain = authenticationContext.getContext().getTenantDomain();
            }
            if ( parameters[1] instanceof String) {
                profile = (String) parameters[1];
            }
        }
        if (parameters.length == 1 && parameters[0] instanceof JsAuthenticationContext) {
            authenticationContext = (JsAuthenticationContext) parameters[0];
            tenantDomain = authenticationContext.getContext().getTenantDomain();
        }

        if (tenantDomain != null) {
            int tenantId = IdentityTenantUtil.getTenantId(tenantDomain);
            try {
                List<String> selectedUsers = new ArrayList<>();
                UserRealm userRealm = UserStoreFunctionsServiceXHolder.getInstance().getRealmService()
                        .getTenantUserRealm(tenantId);
                if (userRealm != null) {
                    UserStoreManager userStoreManager = (UserStoreManager) userRealm.getUserStoreManager();
                    // Get the user list using the first Claim value
                    Map.Entry<String,String> claimEntry = claimMap.entrySet().iterator().next();
                    String firstClaim = claimEntry.getKey();
                    String firstClaimValue = claimEntry.getValue();
                    claimMap.remove(firstClaim);
                    String[] userList = userStoreManager.getUserList(firstClaim, firstClaimValue, profile);
                    if (userList == null) {
                        return null;
                    }
                    selectedUsers.addAll(Arrays.asList(userList));
                    for (String userName : userList) {
                        for (Map.Entry<String, String> entry : claimMap.entrySet()) {
                            String userClaimValue = userStoreManager.getUserClaimValue(userName, entry.getKey(),
                                    profile);
                            if (userClaimValue == null || !userClaimValue.equals(entry.getValue())) {
                                selectedUsers.remove(userName);
                                break;
                            }
                        }
                    }
                    if (selectedUsers.isEmpty()) {
                        return null;
                    }
                    if (selectedUsers.size() > 1) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("There are more than one user with the provided claim values.");
                        }
                        return null;
                    }
                    String username = selectedUsers.get(0);
                    AuthenticatedUser authenticatedUser = new AuthenticatedUser();
                    if (username.indexOf(CarbonConstants.DOMAIN_SEPARATOR) > 0) {
                        String[] subjectIdentifierSplits = username.split(CarbonConstants.DOMAIN_SEPARATOR, 2);
                        authenticatedUser.setUserStoreDomain(subjectIdentifierSplits[0]);
                        username = subjectIdentifierSplits[1];
                    } else {
                        authenticatedUser.setUserStoreDomain(IdentityUtil.getPrimaryDomainName());
                    }
                    authenticatedUser.setUserName(username);
                    authenticatedUser.setTenantDomain(tenantDomain);
                    if (authenticationContext != null) {
                        return (JsAuthenticatedUser) JsWrapperFactoryProvider.getInstance().getWrapperFactory().
                                createJsAuthenticatedUser(authenticationContext.getContext(), authenticatedUser);
                    }
                    return (JsAuthenticatedUser) JsWrapperFactoryProvider.getInstance().getWrapperFactory().
                            createJsAuthenticatedUser(authenticatedUser);
                } else {
                    LOG.error("Cannot find the user realm for the given tenant: " + tenantId);
                }
            } catch (UserStoreException e) {
                String msg = "getUserListWithClaimValue Function failed while getting user attributes ";
                if (LOG.isDebugEnabled()) {
                    LOG.debug(msg, e);
                }
                throw new FrameworkException(msg, e);
            }
        }
        return null;
    }
}
