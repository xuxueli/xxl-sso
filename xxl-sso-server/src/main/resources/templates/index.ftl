<!DOCTYPE html>
<html>
<head>
    <#-- import macro -->
    <#import "./common/common.macro.ftl" as netCommon>

    <!-- 1-style start -->
    <@netCommon.commonStyle />
    <!-- 1-style end -->

</head>
<body class="hold-transition skin-blue sidebar-mini">
<div class="wrapper">

    <!-- 2-header start -->
    <header class="main-header">
        <!-- header-logo -->
        <a href="${request.contextPath}/" class="logo">
            <span class="logo-mini"><b>XXL</b></span>
            <span class="logo-lg"><b>XXL SSO</b></span>
        </a>
        <nav class="navbar navbar-static-top" role="navigation">
            <!--header left -->
            <a href="#" class="sidebar-toggle" data-toggle="push-menu" role="button">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </a>
            <!--header right -->
            <div class="navbar-custom-menu">
                <ul class="nav navbar-nav">
                    <li class="dropdown user user-menu">
                        <a href="${request.contextPath}/logout"   >
                            <span class="hidden-xs">注销【${loginInfo.userName}】</span>
                        </a>
                    </li>
                </ul>
            </div>
        </nav>
    </header>
    <!-- 2-header end -->

    <!-- 3-left start -->
    <aside class="main-sidebar">
        <section class="sidebar">
            <ul class="sidebar-menu">
                <li class="header">导航</li>
                <li class="nav-click active" ><a href="${request.contextPath}/"><i class="fa fa-circle-o text-gray"></i><span>使用教程</span></a></li>
            </ul>
        </section>
    </aside>
    <!-- 3-left end -->

    <!-- 4-right start -->
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
    <!-- 4-right end -->

    <!-- 5-footer start -->
    <footer class="main-footer">
        Powered by <b>XXL-SSO</b> 2.2.0
        <div class="pull-right hidden-xs">
            <strong>Copyright &copy; 2018-${.now?string('yyyy')} &nbsp;
                <a href="https://www.xuxueli.com/" target="_blank" >xuxueli</a>
                &nbsp;
                <a href="https://github.com/xuxueli/xxl-sso" target="_blank" >github</a>
            </strong><!-- All rights reserved. -->
        </div>
    </footer>
    <!-- 5-footer end -->

</div>
</body>

<!-- 6-script start -->
<@netCommon.commonScript />
<script src="${request.contextPath}/static/biz/admin.common.js"></script>
<!-- 6-script end -->

</html>
