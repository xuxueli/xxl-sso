<!DOCTYPE html>
<html>
<head lang="en">
    <meta charset="UTF-8" />
    <title>SSO Server</title>

    <#import "common/common.macro.ftl" as netCommon>
    <@netCommon.commonStyle />

    <link rel="stylesheet" href="${request.contextPath}/static/plugins/bootstrap/bootstrap.min.css">
</head>
<body>

    <div style="text-align: center;margin-top: 100px;">
        <form action="${request.contextPath}/doLogin">
            username：<input type="text" name="username" value="user" maxlength="50" /> <br>
            password：<input type="password" name="password" value="123456" maxlength="50" /> <br>
            <input type="hidden" name="redirect_url" value="${redirect_url!''}" />
            <#if errorMsg?exists>
            <p style="color: red;">${errorMsg}</p>
            </#if>
            <input type="submit" value="Login" />
        </form>
    </div>

</body>
<@netCommon.commonScript />
</html>