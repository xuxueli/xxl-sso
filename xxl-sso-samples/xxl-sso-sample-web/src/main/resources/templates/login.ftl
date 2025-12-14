<!DOCTYPE html>
<html>
<head>
    <#-- import macro -->
    <#import "./common/common.macro.ftl" as netCommon>

    <!-- 1-style start -->
    <@netCommon.commonStyle />
    <!-- iCheck -->
    <link rel="stylesheet" href="${request.contextPath}/static/adminlte/plugins/iCheck/square/blue.css">
    <!-- 1-style end -->

</head>
<body class="hold-transition login-page">

    <!-- 2-biz start -->

    <div class="login-box">
        <div class="login-logo">
            <a><b>XXL-SSO</b></a>
        </div>
        <form id="loginForm" method="post" >
            <div class="login-box-body">
                <p class="login-box-msg">XXL-SSO接入示例</p>
                <div class="form-group has-feedback">
                    <input type="text" name="username" class="form-control" placeholder="Please input username." value="user" maxlength="18" >
                    <span class="glyphicon glyphicon-envelope form-control-feedback"></span>
                </div>
                <div class="form-group has-feedback">
                    <input type="password" name="password" class="form-control" placeholder="Please input password." value="123456" maxlength="18" >
                    <span class="glyphicon glyphicon-lock form-control-feedback"></span>
                </div>
                <div class="row">
                    <div class="col-xs-8">
                        <div class="checkbox icheck">
                            <label>
                                <input type="checkbox" name="ifRemember" > 记住密码
                            </label>
                        </div>
                    </div><!-- /.col -->
                    <div class="col-xs-4">
                        <input type="hidden" name="redirectUrl" value="${redirect_url!""}">
                        <button type="submit" class="btn btn-primary btn-block btn-flat">Login</button>
                    </div>
                </div>
            </div>
        </form>
    </div>

    <!-- 2-biz end -->

<!-- 3-script start -->
<@netCommon.commonScript />
<script src="${request.contextPath}/static/adminlte/plugins/iCheck/icheck.min.js"></script>
<script>
    $(function(){

        // do login
        var loginFormValid = $("#loginForm").validate({
            errorElement : 'span',
            errorClass : 'help-block',
            focusInvalid : true,
            rules : {
                username : {
                    required : true ,
                    minlength: 4,
                    maxlength: 18
                },
                password : {
                    required : true ,
                    minlength: 4,
                    maxlength: 18
                }
            },
            messages : {
                username : {
                    required  : "请输入用户名",
                    minlength : "输入用户名长度非法"
                },
                password : {
                    required  : "请输入密码",
                    minlength : "输入密码长度非法"
                }
            },
            highlight : function(element) {
                $(element).closest('.form-group').addClass('has-error');
            },
            success : function(label) {
                label.closest('.form-group').removeClass('has-error');
                label.remove();
            },
            errorPlacement : function(error, element) {
                element.parent('div').append(error);
            },
            submitHandler : function(form) {

                // dologin
                $.post(base_url + "/weblogin/doLogin", $("#loginForm").serialize(), function(data, status) {
                    if (data.code == "200") {

                        layer.msg( "登录成功，跳转中..." );
                        setTimeout(function(){
                            window.location.href = base_url + "/";
                        }, 1000);
                        return;

                    } else {
                        layer.open({
                            title: "提示",
                            btn: "确认",
                            content: (data.msg || "登录失败" ),
                            icon: '2'
                        });
                    }
                });

            }
        });


        // input iCheck
        $('input').iCheck({
            checkboxClass: 'icheckbox_square-blue',
            radioClass: 'iradio_square-blue',
            increaseArea: '20%' // optional
        });

    });
</script>
<!-- 3-script end -->

</body>
</html>