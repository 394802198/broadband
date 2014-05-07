<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix='fmt' uri="http://java.sun.com/jsp/jstl/fmt" %>  
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"></c:set>

<jsp:include page="header.jsp" />

<style>
.personal-info li{
	padding:5px 40px;
}
.nav-pills>li.active>a, .nav-pills>li.active>a:hover, .nav-pills>li.active>a:focus {
color: #fff;
background-color: #7BC3EC;
}
</style>
<div class="container" >

	<ul class="panel panel-success nav nav-pills nav-justified"><!-- nav-justified -->
		<li >
			<a  class="btn-lg">
				1. Choose Plans And Pricing
				<span class="glyphicon glyphicon-hand-right pull-right"></span>
			</a>
		</li>
		<li class="">
			<a class="btn-lg">
				2. Fill Application Form
				<span class="glyphicon glyphicon-hand-right pull-right" ></span>
			</a>
		</li>
		<li class="active">
			<a  class="btn-lg">
				3. Review and Checkout 
			</a>
		</li>
	</ul>
	<div class="panel panel-success">
		<div class="panel-heading">
			<h4 class="panel-title">
				<a data-toggle="collapse" data-toggle="collapse" data-parent="#customerOrderAccordion" href="#collapseOrderInfo">
					Please Review Application Information
				</a>
			</h4>
		</div>
		<div id="collapseOrderInfo" class="panel-collapse collapse in">
			<div class="panel-body">
			
				<div class="row">
					<div class="col-md-6">
						<h4 class="text-success">
							<c:if test="${customer.customer_type == 'personal' }">
								${customer.title } ${customer.first_name } ${customer.last_name }
							</c:if>
							<c:if test="${customer.customer_type == 'business' }">
								${customer.organization.org_name }
							</c:if>
						</h4>
					</div>
					<div class="col-md-6"></div>
				</div>
				<div class="row">
					<div class="col-sm-6">
						<h4 class="text-success">
							${customer.address }
						</h4>
					</div>
					<div class="col-sm-6">
						<h4 class="text-success pull-right" >
							Order Date: 
							<strong class="text-info">
								<fmt:formatDate  value="${customer.customerOrder.order_create_date}" type="both" pattern="yyyy-MM-dd" />
							</strong>
						</h4>
					</div>
				</div>
				
				<c:if test="${customer.customer_type == 'business' }">
					<hr style="margin-top:0;"/>
					<h2>Business Information</h2>
					<hr style="margin-top:0;"/>
					<div class="row">
						<div class="col-sm-4"><strong>Organization Type</strong></div>
						<div class="col-sm-6"><strong class="text-info">${customer.organization.org_type }</strong></div>
					</div>
					<div class="row" style="margin-top:5px;">
						<div class="col-sm-4"><strong>Trading Name</strong></div>
						<div class="col-sm-6"><strong class="text-info">${customer.organization.org_trading_name }</strong></div>
					</div>
					<div class="row" style="margin-top:5px;">
						<div class="col-sm-4"><strong>Registration No.</strong></div>
						<div class="col-sm-6"><strong class="text-info">${customer.organization.org_register_no }</strong></div>
					</div>
					<div class="row" style="margin-top:5px;">
						<div class="col-sm-4"><strong>Date Incoporated</strong></div>
						<div class="col-sm-6">
							<strong class="text-info">
								<fmt:formatDate  value="${customer.organization.org_incoporate_date }" type="both" pattern="yyyy-MM-dd" />
							</strong>
						</div>
					</div>
					<hr /><!-- style="margin-top:0;" -->
					<h2>Account Holder Information</h2>
					<hr style="margin-top:0;"/>
					<div class="row" >
						<div class="col-sm-4"><strong>Full name</strong></div>
						<div class="col-sm-6"><strong class="text-info">${customer.organization.holder_name }</strong></div>
					</div>
					<div class="row" style="margin-top:5px;">
						<div class="col-sm-4"><strong>Job Title</strong></div>
						<div class="col-sm-6"><strong class="text-info">${customer.organization.holder_job_title }</strong></div>
					</div>
					<div class="row" style="margin-top:5px;">
						<div class="col-sm-4"><strong>Phone</strong></div>
						<div class="col-sm-6"><strong class="text-info">${customer.organization.holder_phone }</strong></div>
					</div>
					<div class="row" style="margin-top:5px;">
						<div class="col-sm-4"><strong>Email</strong></div>
						<div class="col-sm-6"><strong class="text-info">${customer.organization.holder_email }</strong></div>
					</div>
				</c:if>
				
				<c:if test="${customer.customer_type == 'personal' }">
					<hr /><!-- style="margin-top:0;" -->
					<h2>Personal Information</h2>
					<hr style="margin-top:0;"/>
					<%-- <div class="row" >
						<div class="col-sm-4"><strong>Phone</strong></div>
						<div class="col-sm-6"><strong class="text-info">${customer.cellphone }</strong></div>
					</div> --%>
					<div class="row" style="margin-top:5px;">
						<div class="col-sm-4"><strong>Mobile</strong></div>
						<div class="col-sm-6"><strong class="text-info">${customer.cellphone }</strong></div>
					</div>
					<div class="row" style="margin-top:5px;">
						<div class="col-sm-4"><strong>Email</strong></div>
						<div class="col-sm-6"><strong class="text-info">${customer.email }</strong></div>
					</div>
				</c:if>
				
				<c:if test="${customer.customerOrder.order_broadband_type == 'transition' }">
					<hr /><!-- style="margin-top:0;" -->
					<h2>Transition</h2>
					<hr style="margin-top:0;"/>
					<div class="row" >
						<div class="col-sm-4"><strong>Current Provider Name</strong></div>
						<div class="col-sm-6"><strong class="text-info">${customer.customerOrder.transition_provider_name }</strong></div>
					</div>
					<div class="row" style="margin-top:5px;">
						<div class="col-sm-4"><strong>Account Holder Name</strong></div>
						<div class="col-sm-6"><strong class="text-info">${customer.customerOrder.transition_account_holder_name }</strong></div>
					</div>
					<div class="row" style="margin-top:5px;">
						<div class="col-sm-4"><strong>Current Account Number</strong></div>
						<div class="col-sm-6"><strong class="text-info">${customer.customerOrder.transition_account_number }</strong></div>
					</div>
					<div class="row" style="margin-top:5px;">
						<div class="col-sm-4"><strong>Telephone Number</strong></div>
						<div class="col-sm-6"><strong class="text-info">${customer.customerOrder.transition_porting_number }</strong></div>
					</div>
				</c:if>
							
							
				<hr/>
				<table class="table">
					<thead>
						<tr>
							<th>Service / Product</th>
							<th>Data</th>
							<th>Term(mth)</th>
							<th>Monthly Charge</th>
							<th>Qty</th>
							<th>Subtotal</th>
						</tr>
					</thead>
					<tbody>
						<c:forEach var="detail" items="${customer.customerOrder.customerOrderDetails }">
							<c:choose>
								<c:when test="${fn:contains(detail.detail_type, 'plan-') }">
									<tr>
										<td>
											${detail.detail_name }
										</td>
										<td>
											${detail.detail_data_flow } GB
											
										</td>
										<td>${detail.detail_term_period }</td>
										<td><fmt:formatNumber value="${detail.detail_price }" type="number" pattern="#,##0.00" /></td>
										<td>${detail.detail_unit }</td>
										<td>
											<fmt:formatNumber value="${detail.detail_price * detail.detail_unit}" type="number" pattern="#,##0.00" />
											
										</td>
									</tr>
									<tr>
										<th>&nbsp;</th>
										<th>&nbsp;</th>
										<th>&nbsp;</th>
										<th>Unit Price</th>
										<th>Qty</th>
										<th>Subtotal</th>
									</tr>
								</c:when>
								<c:otherwise>
									<tr>
										<td>
											${detail.detail_name }&nbsp;
											<c:if test="${detail.detail_type == 'pstn' || detail.detail_type == 'voip'}">
												<c:if test="${detail.pstn_number != null && detail.pstn_number != '' }">
													<strong class="text-danger">(${detail.pstn_number })</strong>
												</c:if>
											</c:if>
										</td>
										<td>&nbsp;</td>
										<td>&nbsp;</td>
										<td><fmt:formatNumber value="${detail.detail_price }" type="number" pattern="#,##0.00" /></td>
										<td>${detail.detail_unit }</td>
										<td>
											<fmt:formatNumber value="${detail.detail_price * detail.detail_unit}" type="number" pattern="#,##0.00" />
											
										</td>
									</tr>
								</c:otherwise>
							</c:choose>
							
						</c:forEach>
					</tbody>
				</table>
				<div class="row">
					<div class="col-md-4 col-md-offset-8">
					
						<table class="table">
							<tbody>
								<tr>
									<td>Total before GST</td>
									<td>
										NZ$ 
										<fmt:formatNumber value="${customer.customerOrder.order_total_price * (1 - 0.15)}" type="number" pattern="#,##0.00" />
									</td>
								</tr>
								<tr>
									<td>GST at 15% </td>
									<td>
										NZ$ 
										<fmt:formatNumber value="${customer.customerOrder.order_total_price * 0.15}" type="number" pattern="#,##0.00" />
									</td>
								</tr>
								<tr>
									<td><strong>Order Total</strong></td>
									<td>
										<strong class="text-success">
											NZ$ 
											<fmt:formatNumber value="${customer.customerOrder.order_total_price }" type="number" pattern="#,##0.00" />
										</strong>
									</td>
								</tr>
							</tbody>
						</table>
					</div>
				</div>
				<hr>
				<div class="row">
					<div class="col-md-12">
						<label class="well checkbox-inline pull-right">
							<input type="checkbox" id="termckb" value="1" checked="checked"/>
							<a class="btn btn-link btn-lg"  data-toggle="modal" data-target="#cyberParkTerm"> &lt;&lt; CyberPark Terms & Conditions &gt;&gt;</a>
						</label>
					</div>
				</div>
				<hr/>
				<div class="row">
					<div class="col-md-12">
						<c:choose>
							<c:when test="${orderPlan.plan_group == 'plan-no-term' || orderPlan.plan_group == 'plan-term' }">
								<a href="${ctx }/order/${orderPlan.id}" style="width:120px;" class="btn btn-success btn-lg pull-left">Back</a>
							</c:when>
							<c:when test="${orderPlan.plan_group == 'plan-topup' }">
								<a href="${ctx }/order/${orderPlan.id}/topup/<fmt:formatNumber value="${orderPlan.topup.topup_fee}" type="number" pattern="##" />" style="width:120px;" class="btn btn-success btn-lg pull-left">Back</a>
							</c:when>
						</c:choose>
						<form class="form-horizontal" action="${ctx }/order/checkout" method="post" id="checkoutForm">
							<button type="submit" style="width:120px;" class="btn btn-success btn-lg pull-right">Checkout</button>
						</form>
					</div>
					
				</div>		
				
			</div>
		</div>
		
	</div>
	
</div>

<!-- Modal -->
<div class="modal fade" id="cyberParkTerm" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
	<div class="modal-dialog" style="width:800px;">
    	<div class="modal-content">
      		<div class="modal-header">
        		<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
        		<h4 class="modal-title" id="myModalLabel">CyberPark Terms & Conditions</h4>
      		</div>
      		<div class="modal-body">
      			<c:choose>
      				<c:when test="${orderPlan.plan_class=='business' }">
      					${company.tc_business_retails }
      				</c:when>
      				<%-- <c:when test="">
      					${company.tc_business_wifi }
      				</c:when> --%>
      				<c:when test="${orderPlan.plan_class=='personal' }">
      					${company.tc_personal }
      				</c:when>
      				<c:when test="${orderPlan.plan_type=='UFB' }">
      					${company.tc_ufb }
      				</c:when>
      				<c:otherwise>
      					${company.term_contracts }
      				</c:otherwise>
      			</c:choose>
        		
      		</div>
    	</div><!-- /.modal-content -->
  	</div><!-- /.modal-dialog -->
</div><!-- /.modal -->

<jsp:include page="footer.jsp" />
<jsp:include page="script.jsp" />
<script type="text/javascript" src="${ctx}/public/bootstrap3/js/holder.js"></script>
<script type="text/javascript" src="${ctx}/public/bootstrap3/js/icheck.min.js"></script>
<script type="text/javascript">
(function(){
	$(':checkbox').iCheck({
		checkboxClass : 'icheckbox_square-green',
		radioClass : 'iradio_square-green'
	});
	
	$('button[type="submit"]').click(function(e){
		e.preventDefault();
		var b = $('#termckb').prop('checked');
		if (b) {
			$('#checkoutForm').submit();
		} else {
			alert("You must agree to CyberPark terms, in order to continue to buy and register as a member.");
		}
	});
})(jQuery);
</script>
<jsp:include page="footer-end.jsp" />