<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"></c:set>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>CyberPark Online BroadBand Application Form</title>
    <link rel="shortcut icon" type="image/ico"  href="${ctx}/public/bootstrap3/images/icon.png"/>
    <link href="${ctx}/public/bootstrap3/css/bootstrap.min.css" rel="stylesheet" type="text/css" media="screen" />
    <link href="${ctx}/public/bootstrap3/css/user.css" rel="stylesheet" type="text/css"  />
    <link href="${ctx}/public/bootstrap3/css/datepicker3.css" rel="stylesheet" type="text/css" />
    <link href="${ctx}/public/bootstrap3/css/bootstrap-select.min.css" rel="stylesheet" type="text/css" />
    <link href="${ctx}/public/bootstrap3/css/skins/all.css" rel="stylesheet" type="text/css" />
    <!--[if lt IE 9]>
	<script src="${ctx}/public/bootstrap3/js/html5shiv.js"></script>
	<script src="${ctx}/public/bootstrap3/js/respond.min.js"></script>
    <![endif]-->
   	<style type="text/css">
body {
	padding: 0;
}
.logo {
	display: inline-block;
	height: 46px;
	width: 111px;
	background: url("${ctx}/public/bootstrap3/images/logo.png") no-repeat;
	margin-top:-12px;
	background-size: contain;
}
.navbar-darkgreen {
	background-color: #08373a;
	border-color: #386d71;
}
.app-form {
	color: white;
	font-size: 30px;
}
   	</style>
</head>
<body>
<jsp:include page="nav.jsp" />