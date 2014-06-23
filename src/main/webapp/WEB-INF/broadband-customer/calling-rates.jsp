<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix='fmt' uri="http://java.sun.com/jsp/jstl/fmt" %> 
<c:set var="ctx" value="${pageContext.request.contextPath}"></c:set>

<jsp:include page="header.jsp" />

<style>
.affix {
	width: 263px;
	top: 20px;
}
.nav-pills>li>a {
	border-radius: 0px;
}
.nav-pills>li:first-child>a {
	border-radius: 4px 4px 0 0;
}
.nav-pills>li:last-child>a {
	border-radius: 0 0 4px 4px;
}
.panel-primary>.panel-footer {
	color: #fff;
	background-color: #428bca;
	border-color: #428bca;
}
.panel-info>.panel-footer {
	color: #31708f;
	background-color: #d9edf7;
	border-color: #bce8f1;
}

.pagination>li:first-child>a, 
.pagination>li:first-child>span {
	margin-left: -1px;
	border-bottom-left-radius: 0;
	border-top-left-radius: 0;
}
.pagination>li:last-child>a, 
.pagination>li:last-child>span {
	border-bottom-right-radius: 0;
	border-top-right-radius: 0;
}
</style>

<div class="container">
	<div class="row" style="margin-bottom:20px;">
		
		<div class="col-md-9 col-xs-12 col-sm-12">
			<div class="panel panel-success">
				<div class="panel-heading">
					<h4 class="panel-title">International Calling Rates</h4>
				</div>
				<c:if test="${fn:length(letterCirs) > 0}">
					<table class="table table-bordered">
						<thead>
							<tr>
								<th>Country name</th>
								<th>Cents per min rate</th>
							</tr>
						</thead>
						<tbody>
							<c:forEach var="lCir" items="${letterCirs }">
								<tr class="active" id="${lCir.letter}">
									<td colspan="2">${lCir.letter}</td>
								</tr>
								<c:forEach var="cir" items="${lCir.cirs }">
									<tr>
										<td>${cir.area_name}</td>
										<td>$ ${cir.rate_cost}</td>
									</tr>
								</c:forEach>
							</c:forEach>
						</tbody>
					</table>
				</c:if>
			</div>
		</div>
		<div class="col-md-3 hidden-xs hidden-sm">
			 <div data-spy="affix" data-offset-top="150"><!-- navbar-example panel panel-default -->
				<ul class="pagination" style="margin-top:0;"><!-- nav nav-pills nav-stacked -->
					<c:forEach var="lCir" items="${letterCirs }" varStatus="item">
						<li class="${item.first ? 'active' : '' }" >
							<a href="#${lCir.letter }" style="${item.last ? 'width:80px;height:35px;' : 'width:40px;height:35px;'}">${lCir.letter }</a>
						</li>
					</c:forEach>
				</ul>
			</div>
		</div>
	</div>
</div>

<jsp:include page="footer.jsp" />
<jsp:include page="script.jsp" />
<script type="text/javascript">
(function($){
	$('body').scrollspy({ target: '.navbar-example' });
})(jQuery);
</script>
<jsp:include page="footer-end.jsp" />