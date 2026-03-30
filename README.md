# Conditional-Authentication-Custom-Functions
Test Project

*Steps to deploy*
- Build the sample using maven `mvn clean install`
- Copy the `org.wso2.custom.auth.functionsx-0.1` binary file from `target` directory into 
  `<IS_HOME>/repository/components/dropins` directory
- Restart WSO2 IS

**getX()**

```js
var onLoginRequest = function(context) {
    executeStep(1, {
        onSuccess: function (context) {
            var claimMap = {};
            claimMap["http://wso2.org/claims/username"] = "ratnajothy@xxx.com";
            var mappedUsername = getX(claimMap, context);
            Log.info(mappedUsername);
        } 
    });
};
```
