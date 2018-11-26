<%--
  Created by IntelliJ IDEA.
  User: zyf
  Date: 2018/10/30
  Time: 17:35
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head>
    <title>登录</title>
  </head>

  <body>
  <div class="container-fluid">
    <div class="row">
      <div class="col-md-offset-3">
        <h3>请登录</h3>
      </div>
    </div>
    <form method="post" action="javascript:0" id="login" role="form">
      <div class="row">
        <div class="col-md-offset-3 col-md-4">
          <div class="form-group">
            <label for="ulicense">证件号：</label>
            <input type="text" name="ulicense" class="form-control" id="ulicense" placeholder="请输入证件号"></label>
          </div>
        </div>
      </div>
      <div class="row">
        <div class="col-md-offset-3 col-md-4">
          <div class="form-group">
            <label for="password">密码：</label>
            <input type="password" name="password" class="form-control" id="password" placeholder="请输入密码"></label>
          </div>
        </div>
      </div>
      <div class="row">
        <div class="col-md-offset-5">
          <input type="submit" value="登录" class="btn btn-default">
        </div>
      </div>
      <div>
        <span id="status"></span>
      </div>
    </form>
  </div>
  </body>

  <script src="https://apps.bdimg.com/libs/jquery/2.1.4/jquery.min.js">
  </script>
  <link href="https://cdn.staticfile.org/twitter-bootstrap/3.3.7/css/bootstrap.min.css" rel="stylesheet">
  <script>
      $(document).ready(function() {
          $("#login").submit(function (e) {
              $.post("login",
                  {
                      ulicense: $("#ulicense").val(),
                      password: $("#password").val()
                  },
                  function (data, status) {
                      if(data=="NOTEXIST"){
                          $("#status").html("用户不存在");
                      }
                      else if(data=="WRONG"){
                          $("#status").html("密码错误");
                      }
                      else{
                          $("#status").html("登录成功");
                          if(data=="ADMIN")
                            window.location.href="admin.html"
                          else if(data=="OK")
                              window.location.href="main.html"
                      }

                      //alert("数据: \n" + data + "\n状态: " + status)
                  });
          });
      });
  </script>
</html>
