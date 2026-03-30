package org.wso2.custom.auth.functions.internal;

import org.wso2.carbon.identity.application.authentication.framework.JsFunctionRegistry;
import org.wso2.carbon.user.core.service.RealmService;

public class UserStoreFunctionsServiceXHolder {

    private static UserStoreFunctionsServiceXHolder instance = new UserStoreFunctionsServiceXHolder();

    private RealmService realmService;
    private JsFunctionRegistry jsFunctionRegistry;

    private UserStoreFunctionsServiceXHolder() {

    }

    public static UserStoreFunctionsServiceXHolder getInstance() {

        return instance;
    }

    public RealmService getRealmService() {

        return realmService;
    }

    public void setRealmService(RealmService realmService) {

        this.realmService = realmService;
    }

    public JsFunctionRegistry getJsFunctionRegistry() {

        return jsFunctionRegistry;
    }

    public void setJsFunctionRegistry(JsFunctionRegistry jsFunctionRegistry) {

        this.jsFunctionRegistry = jsFunctionRegistry;
    }
}
