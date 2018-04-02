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
        username：<input type="text" id="username" value="user" maxlength="50" /> <br>
        password：<input type="password" id="password" value="123456" maxlength="50" /> <br>
        <input type="button" id="Login" value="登陆" />
    </div>

</body>
<@netCommon.commonScript />
<script>
    var base_url = '${request.contextPath}';
    var redirect_url = '${redirect_url}';
</script>
<script src="${request.contextPath}/static/js/index.js"></script>
</html>