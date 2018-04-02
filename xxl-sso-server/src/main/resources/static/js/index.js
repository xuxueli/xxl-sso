$(function () {

    $('#Login').click(function () {

        var username = $('#username').val();
        var password = $('#password').val();
        if (!username) {
            alert('请输入账号名');
            return;
        }
        if (!password) {
            alert('请输入密码');
            return;
        }

        $.ajax({
            type : 'POST',
            async: false,
            url : base_url + '/login',
            data : {
                "username":username,
                "password":password
            },
            dataType : "json",
            success : function(data){
                if (data.code == 200) {
                    var redirectUrlFinal = redirect_url + "?sso_sessionid=" + data.data;;
                    window.location.href = redirectUrlFinal;
                } else {
                    alert(data.msg || '登陆失败' );
                }
            }
        });
    });


});