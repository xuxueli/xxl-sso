<!DOCTYPE html>
<html>
<head>
    <#-- import macro -->
    <#import "common/common.macro.ftl" as netCommon>
    <#-- commonStyle -->
    <@netCommon.commonStyle />
</head>
<body class="hold-transition skin-blue sidebar-mini">
<div class="wrapper">
        <!-- header -->
    <@netCommon.commonHeader />
        <!-- left -->
    <@netCommon.commonLeft "help" />

    <!-- Content Wrapper. Contains page content -->
    <div class="content-wrapper">
        <!-- Content Header (Page header) -->
        <section class="content-header">
            <h1>使用教程</h1>
        </section>

        <!-- Main content -->
        <section class="content">

            <div class="box box-default">
                <div class="box-header with-border">
                    <h3 class="box-title">Welcome：${loginInfo.userName} </h3>
                </div>
                <div class="box-body">
                    <a href="http://xxlssoclient1.com:8081/xxl-sso-sample-cas/" target="_blank" class="label-danger" >打开“Client应用01”</a>
                    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                    <a href="http://xxlssoclient2.com:8081/xxl-sso-sample-cas/" target="_blank" class="label-danger" >打开“Client应用02”</a>
                </div>
                <!-- /.box-body -->
            </div>

            <div class="callout callout-info">
                <h4>单点登录框架</h4>
                <br>
                <p>
                    <a target="_blank" href="https://github.com/xuxueli/xxl-sso">Github</a>&nbsp;&nbsp;&nbsp;&nbsp;
                    <iframe src="https://ghbtns.com/github-btn.html?user=xuxueli&repo=xxl-sso&type=star&count=true" frameborder="0" scrolling="0" width="170px" height="20px" style="margin-bottom:-5px;"></iframe>
                    <br><br>
                    <a target="_blank" href="https://www.xuxueli.com/xxl-sso/">文档地址</a>
                    <br><br>

                </p>
                <p></p>
            </div>
        </section>
        <!-- /.content -->
    </div>
    <!-- /.content-wrapper -->

    <!-- footer -->
<@netCommon.commonFooter />
</div>
</body>
<@netCommon.commonScript />
</html>
