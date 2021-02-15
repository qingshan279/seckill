<%--
  Created by IntelliJ IDEA.
  User: haijun
  Date: 2021/2/12
  Time: 10:27
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@include file="common/tag.jsp" %>

<html>
<head>
    <title>秒杀列表</title>
    <%@include file="common/head.jsp" %>
</head>

<body>
<div class="panel panel-default">
    <div class="panel-heading text-center">
        <h2>秒杀列表</h2>
    </div>

    <div class="panel-body">
        <table class="table table-hover">
            <thead>
            <tr>
                <td>名称</td>
                <td>库存</td>
                <td>开始时间</td>
                <td>结束时间</td>
                <td>创建时间</td>
                <td>详情页</td>
            </tr>
            </thead>
            <tbody>
            <c:forEach items="${list}" var="sk">
                <tr>
                    <td>${sk.name}</td>
                    <td>${sk.number}</td>
                    <td><fmt:formatDate value="${sk.startTime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
                    <td><fmt:formatDate value="${sk.endTime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
                    <td><fmt:formatDate value="${sk.createTime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
                    <td><a class="btn btn-info" href="/seckill/${sk.seckillId}/detail" target="_blank">详情</a></td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>
</div>
</body>
<script src="https://cdn.staticfile.org/jquery/2.1.1/jquery.min.js"></script>
<script src="https://cdn.staticfile.org/twitter-bootstrap/3.3.7/js/bootstrap.min.js"></script>
</html>
