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