<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix='fmt' uri="http://java.sun.com/jsp/jstl/fmt" %> 
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"></c:set>

<jsp:include page="../header.jsp" />
<jsp:include page="../alert.jsp" />

<div class="container">
	<div class="row">
		<div class="col-md-12">
			<div class="panel panel-default">
				<div class="panel-heading">
					<h4 class="panel-title">Plan View&nbsp;
					<c:if test="${true}">
						<div class="btn-group">
							<button type="button" class="btn btn-default btn-sm dropdown-toggle" data-toggle="dropdown">
								Operations <span class="caret"></span>
							</button>
							<ul class="dropdown-menu" data-role="menu">
								<li>
									<a href="javascript:void(0);" id="delete_selected_plan">
										DELETE:
										<span class="text-danger">Delete Selected Plan</span>
									</a>
								</li>
							</ul>
						</div>
						<div class="btn-group">
							<button type="button" class="btn btn-default btn-sm dropdown-toggle" data-toggle="dropdown">
								Change Group <span class="caret"></span>
							</button>
							<ul class="dropdown-menu" data-role="menu">
								<li>
									<a href="javascript:void(0);" id="delete_selected_plan">
										DELETE:
										<span class="text-danger">Delete Selected Plan</span>
									</a>
								</li>
								<li>
									<a href="javascript:void(0);" id="delete_selected_plan">
										DELETE:
										<span class="text-danger">Delete Selected Plan</span>
									</a>
								</li>
								<li>
									<a href="javascript:void(0);" id="delete_selected_plan">
										DELETE:
										<span class="text-danger">Delete Selected Plan</span>
									</a>
								</li>
							</ul>
						</div>
					</c:if>
					</h4>
				</div>
				<c:if test="${fn:length(page.results) > 0 }">
					<form id="planForm" action="${ctx }/broadband-user/plan/delete" method="post">
					<table class="table" style="font-size:12px;">
						<thead >
							<tr>
								<th><input type="checkbox" id="checkbox_plans_top" /></th>
								<th>Plan Name</th>
								<th>Group</th>
								<th>Type</th>
								<th>Sort</th>
								<th>Monthly fee (Inc GST)($)</th>
								<th>New Connection fee (Inc GST)($)</th>
								<th>Data Flow (GB)</th>
								<th>Status</th>
							</tr>
						</thead>
						<tbody>
							<c:forEach var="plan" items="${page.results }">
								<tr class="${plan.plan_status == 'selling' ? 'success' : '' }">
									<td>
										<input type="checkbox" name="checkbox_plans" value="${plan.id}"/>
									</td>
									<td>
										<a href="${ctx }/broadband-user/plan/edit/${plan.id}">
											${plan.plan_name }
										</a>
									</td>
									<td  class="<c:choose>
										<c:when test="${plan.plan_group=='plan-topup'}">text-danger</c:when>
										<c:when test="${plan.plan_group=='plan-no-term'}">text-info</c:when>
										<c:when test="${plan.plan_group=='plan-term'}">text-warning</c:when>
									</c:choose>">
									
										${plan.plan_group }
									</td>
									<td>
										${plan.plan_type }
									</td>
									<td>
										${plan.plan_sort }
									</td>
									<td>
										<fmt:formatNumber value="${plan.plan_price }" type="number" pattern="#,#00.00" />
										
									</td>
									<td>
										<fmt:formatNumber value="${plan.plan_new_connection_fee }" type="number" pattern="#,#00.00" />
										
									</td>
									<td>${plan.data_flow }</td>
									<td>${plan.plan_status}</td>
								</tr>
							</c:forEach>
						</tbody>
						<tfoot>
						<tr>
							<td colspan="11">
								<ul class="pagination">
									<c:forEach var="num" begin="1" end="${page.totalPage }" step="1">
										<li class="${page.pageNo == num ? 'active' : ''}">
											<a href="${ctx}/broadband-user/plan/view/${num}">${num}</a>
										</li>
									</c:forEach>
								</ul>
							</td>
						</tr>
					</tfoot>
					</table>
					</form>
				</c:if>
				<c:if test="${fn:length(page.results) <= 0 }">
					<div class="panel-body">
						<div class="alert alert-warning">No records have been found.</div>
					</div>
				</c:if>
			</div>
		</div>
	</div>
</div>
<jsp:include page="../footer.jsp" />
<jsp:include page="../script.jsp" />
<script type="text/javascript">
(function($){
	$('#checkbox_plans_top').click(function(){
		var b = $(this).prop("checked");
		if (b) {
			$('input[name="checkbox_plans"]').prop("checked", true);
		} else {
			$('input[name="checkbox_plans"]').prop("checked", false);
		}
	});
	
	$('#delete_selected_plan').click(function(){
		$('#planForm').submit();
	});
})(jQuery);
</script>
<jsp:include page="../footer-end.jsp" />