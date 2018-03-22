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

SSO Server

<#if userInfoList?exists>
    <br>
    <#list userInfoList as user>
        ${user.username}<br>
    </#list>
</#if>


</body>
<@netCommon.commonScript />
</html>