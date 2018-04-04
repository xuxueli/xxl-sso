<!DOCTYPE html>
<html>
<head lang="en">
    <meta charset="UTF-8" />
    <title>SSO Server</title>

    <#import "common/common.macro.ftl" as netCommon>
    <@netCommon.commonStyle />

</head>
<body class="hold-transition skin-blue layout-top-nav">


    <div class="wrapper">

        <header class="main-header">
            <nav class="navbar navbar-static-top">
                <div class="container">
                    <div class="navbar-header">
                        <a href="${request.contextPath}/" class="navbar-brand"><b>XXL SSO</b></a>
                        <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar-collapse">
                            <i class="fa fa-bars"></i>
                        </button>
                    </div>

                    <!-- Navbar Right Menu -->
                    <div class="navbar-custom-menu">
                        <ul class="nav navbar-nav">

                            <!-- user -->
                            <li class="dropdown active">
                                <a href="#" class="dropdown-toggle" data-toggle="dropdown">
                                    <span class="hidden-xs">${xxlUser.username}</span>
                                </a>
                                <ul class="dropdown-menu">
                                    <li><a href="${request.contextPath}/logout">注销</a></li>
                                </ul>
                            </li>

                        </ul>
                    </div>
                    <!-- /.navbar-custom-menu -->
                </div>
                <!-- /.container-fluid -->
            </nav>
        </header>


        <!-- Full Width Column -->
        <div class="content-wrapper">
            <div class="container">

                <!-- Main content -->
                <section class="content">

                    <div class="box box-default">
                        <div class="box-header with-border">
                            <h3 class="box-title">SSO Server</h3>
                        </div>
                        <div class="box-body">
                            Hi, ${xxlUser.username}
                        </div>
                        <!-- /.box-body -->
                    </div>
                    <!-- /.box -->
                </section>
                <!-- /.content -->
            </div>
            <!-- /.container -->
        </div>

        <!-- /.content-wrapper -->
        <footer class="main-footer">
            <div class="container">
                <div class="pull-right hidden-xs">
                    <b>Version</b> 2.4.0
                </div>
                <strong>Copyright &copy; 2014-2016 <a href="https://adminlte.io">Almsaeed Studio</a>.</strong> All rights
                reserved.
            </div>
            <!-- /.container -->
        </footer>

    </div>
    <!-- ./wrapper -->



    <div style="text-align: center;margin-top: 100px;">
        <h1> Hi, ${xxlUser.username} </h1>

        <a href="${request.contextPath}/logout"><input type="button" value="Logout" /></a>


    </div>

</body>
<@netCommon.commonScript />
</html>