<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix='fmt' uri="http://java.sun.com/jsp/jstl/fmt" %>  
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"></c:set>

<jsp:include page="../header.jsp" />

<style>
.personal-info li{
	padding:5px 40px;
}
</style>

<div class="container" style="margin-top:20px;">
	
	<div class="panel panel-success">
		<div class="panel-heading">
			<h4 class="panel-title">
				Please Review Application Information
			</h4>
		</div>
		
		<div class="panel-body">
		
			<div class="row">
				<div class="col-md-6 col-xs-12 col-sm-12">
					<h4 class="text-success">
						${customerReg.title } ${customerReg.first_name } ${customerReg.last_name }
					</h4>
				</div>
			</div>
			<div class="row">
				<div class="col-sm-6">
					<h4 class="text-success">
						${customerReg.address }
					</h4>
				</div>
				<div class="col-sm-6">
					<h4 class="text-success pull-right hidden-xs hidden-sm" >
						Order Date: 
						<strong class="text-info">
							<fmt:formatDate  value="${customerReg.customerOrder.order_create_date}" type="both" pattern="dd/MM/yyyy" />
						</strong>
					</h4>
					<h4 class="text-success hidden-md hidden-lg" >
						Order Date: 
						<strong class="text-info">
							<fmt:formatDate  value="${customerReg.customerOrder.order_create_date}" type="both" pattern="dd/MM/yyyy" />
						</strong>
					</h4>
				</div>
			</div>
			
			
			
			<hr />
			<h2>Personal Information</h2>
			<hr style="margin-top:0;"/>
			
			<div class="row" style="margin-top:5px;">
				<div class="col-sm-4"><strong>Mobile</strong></div>
				<div class="col-sm-6"><strong class="text-info">${customerReg.cellphone }</strong></div>
			</div>
			<div class="row" style="margin-top:5px;">
				<div class="col-sm-4"><strong>Email</strong></div>
				<div class="col-sm-6"><strong class="text-info">${customerReg.email }</strong></div>
			</div>
			<div class="row" style="margin-top:5px;">
				<div class="col-sm-4"><strong>Identity Type</strong></div>
				<div class="col-sm-6"><strong class="text-info">${customerReg.identity_type }</strong></div>
			</div>
			<div class="row" style="margin-top:5px;">
				<div class="col-sm-4"><strong>Identity Number</strong></div>
				<div class="col-sm-6"><strong class="text-info">${customerReg.identity_number }</strong></div>
			</div>
			<div class="row" style="margin-top:5px;">
				<div class="col-sm-4"><strong>Connection Date</strong></div>
				<div class="col-sm-6"><strong class="text-info">${customerReg.customerOrder.connection_date }</strong></div>
			</div>
			
			<c:if test="${customerReg.customerOrder.order_broadband_type == 'transition' }">
				<hr />
				<h2>Transition</h2>
				<hr style="margin-top:0;"/>
				<div class="row" >
					<div class="col-sm-4"><strong>Homeline Number</strong></div>
					<div class="col-sm-6"><strong class="text-info">${customerReg.customerOrder.transition_porting_number }</strong></div>
				</div>
				<div class="row" style="margin-top:5px;">
					<div class="col-sm-4"><strong>Provider Name</strong></div>
					<div class="col-sm-6"><strong class="text-info">${customerReg.customerOrder.transition_provider_name }</strong></div>
				</div>
				<div class="row" style="margin-top:5px;">
					<div class="col-sm-4"><strong>Account Name</strong></div>
					<div class="col-sm-6"><strong class="text-info">${customerReg.customerOrder.transition_account_holder_name }</strong></div>
				</div>
				<div class="row" style="margin-top:5px;">
					<div class="col-sm-4"><strong>Account Number</strong></div>
					<div class="col-sm-6"><strong class="text-info">${customerReg.customerOrder.transition_account_number }</strong></div>
				</div>
			</c:if>
							
			<hr/>
			
			<div class="table-responsive">
			<table class="table">
				<thead>
					<tr>
						<th>Service / Product</th>
						<th>Data</th>
						<!-- <th>Term</th> -->
						<th>Monthly Charge</th>
						<th>Qty</th>
						<th>Subtotal</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="detail" items="${customerReg.customerOrder.customerOrderDetails }">
						<c:choose>
							<c:when test="${fn:contains(detail.detail_type, 'plan-') }">
								<tr>
									<td>
										${detail.detail_name }
									</td>
									<td>
										<c:choose>
											<c:when test="${detail.detail_data_flow < 0 }">Unlimited</c:when>
											<c:otherwise>${detail.detail_data_flow } GB</c:otherwise>
										</c:choose>
									</td>
									<%-- <td>${detail.detail_term_period }</td> --%>
									<td><fmt:formatNumber value="${detail.detail_price }" type="number" pattern="#,##0.00" /></td>
									<td>${detail.detail_unit }</td>
									<td>
										<fmt:formatNumber value="${detail.detail_price * detail.detail_unit - customerReg.customerOrder.discount_price}" type="number" pattern="#,##0.00" />
										
									</td>
								</tr>
								<tr>
									<th>&nbsp;</th>
									<th>&nbsp;</th>
									<!-- <th>&nbsp;</th> -->
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
									<!-- <td>&nbsp;</td> -->
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
			</div>
			
			
			<div class="row">
				<div class="col-md-4 col-md-offset-8">
				
					<table class="table">
						<tbody>
							<tr>
								<td>Total before GST</td>
								<td>
									NZ$ 
									<fmt:formatNumber value="${customerReg.customerOrder.order_total_price * (1 - 0.15)}" type="number" pattern="#,##0.00" />
								</td>
							</tr>
							<tr>
								<td>GST at 15% </td>
								<td>
									NZ$ 
									<fmt:formatNumber value="${customerReg.customerOrder.order_total_price * 0.15}" type="number" pattern="#,##0.00" />
								</td>
							</tr>
							<tr>
								<td><strong>Order Total</strong></td>
								<td>
									<strong class="text-success">
										NZ$ 
										<fmt:formatNumber value="${customerReg.customerOrder.order_total_price }" type="number" pattern="#,##0.00" />
									</strong>
								</td>
							</tr>
							
							<tr id="voucherTR" style="display:none;">
								<td>Voucher Price</td>
								<td>
									<strong class="text-danger" id="vPrice"></strong>
								</td>
							</tr>
							<tr id="TAAV" style="display:none;">
								<td>Total After Appiled Voucher</td>
								<td>
									<strong class="text-success" id="TAAVPrice"></strong>
								</td>
							</tr>
						</tbody>
					</table>
				</div>
			</div>
			
			
			<hr>
			<h4 class="text-success">
				Would you like to add one Cyberpark Voucher ? Please click here
				<a href="javascript:void(0);" id="addVoucher">
					<span class="glyphicon glyphicon-plus"></span>
				</a>
			</h4>
			
			<div id="alertContainer"></div>
					
			<div id="tempAlertSuccessContainer" style="display:none;">
				<div id="alert-success" class="alert alert-success alert-dismissable fade in" style="display:none;">
					<button type="button" class="close" data-dismiss="alert" aria-hidden="true" >&times;</button>
					<span id="text-success"></span>
				</div>
			</div>
			<div id="tempAlertErrorContainer" style="display:none;">
				<div id="alert-error" class="alert alert-danger alert-dismissable fade in" style="display:none;">
					<button type="button" class="close" data-dismiss="alert" aria-hidden="true" >&times;</button>
					<span id="text-error"></span>
				</div>
			</div>
					
			<div id="voucherFormContainer">
				<c:set var="total_vprice" value="0"></c:set>
				<c:if test="${fn:length(customerReg.vouchers) > 0 }">
					<c:forEach var="v" items="${customerReg.vouchers }">
						<c:set var="total_vprice" value="${total_vprice + v.face_value }"></c:set>
						<div data-index>
							<hr>
							<form class="form-inline">
								<div class="form-group">
									<label for="serial_number">Voucher Card Number: ${v.serial_number } has been applied.</label>
								</div>
								<a href="javascript:void(0);" class="btn btn-success btn-sm" data-voucher-cancel 
									data-voucher-serial-number="${v.serial_number }"
									data-voucher-card-number="${v.card_number }"> 
									<span class="glyphicon glyphicon-remove"></span> Cancel
								</a>
							</form>
						</div>
					</c:forEach>
				</c:if>
			</div>
			
			<hr>
			<div class="row">
				<div class="col-md-12 col-xs-12 col-sm-12">
					<label class="well checkbox-inline pull-right"><!-- -->
						<input type="checkbox" id="termckb" value="1" />
						<a href="${ctx }/term-and-conditions" class="btn btn-link btn-lg hidden-xs hidden-sm" target="_blank"> &lt;&lt; CyberPark Terms & Conditions &gt;&gt;</a>
						<a href="${ctx }/term-and-conditions" class="hidden-md hidden-lg" target="_blank"> &lt;&lt; CyberPark Terms & Conditions &gt;&gt;</a>
					</label>
				</div>
			</div>
			<hr/>
			<div class="row">
				<div class="col-md-2 hidden-xs hidden-sm">
					<a href="${ctx }/plans/order" class="btn btn-success btn-lg btn-block">Back</a>
				</div>
				<div class="col-md-2 col-md-offset-8">
					<form class="form-horizontal" action="${ctx }/plans/order/dps" method="post" id="checkoutForm">
						
						<div class="btn-group dropup btn-block">
							<button type="button" class="btn btn-success btn-lg btn-block dropdown-toggle" data-toggle="dropdown">
								Checkout <span class="caret"></span>
							</button>
							<ul class="dropdown-menu" >
								<li><a href="javascript:void(0);" id="online_payment">Online Payment</a></li>
								<li><a href="javascript:void(0);" id="bank_depoist">Bank Deposit</a></li>
							</ul>
						</div>
					</form>
					
				</div>
				
			</div>		
			
		</div>
		
	</div>
	
</div>

<script type="text/html" id="voucher_form_tmpl">
<jsp:include page="../voucher-form.html" />
</script>

<script type="text/html" id="voucher_form_result_tmpl">
<jsp:include page="../voucher-form-result.html" />
</script>

<jsp:include page="../footer.jsp" />
<jsp:include page="../script.jsp" />
<script type="text/javascript" src="${ctx}/public/bootstrap3/js/jTmpl.js"></script>
<script type="text/javascript" src="${ctx}/public/bootstrap3/js/icheck.min.js"></script>
<script type="text/javascript" src="${ctx}/public/bootstrap3/js/spin.min.js"></script>
<script type="text/javascript" src="${ctx}/public/bootstrap3/js/ladda.min.js"></script>
<script type="text/javascript">
var ctx = '${ctx}';
var total_vprice = new Number(${total_vprice});
var order_price = new Number(${customerReg.customerOrder.order_total_price});
</script>
<script type="text/javascript" src="${ctx}/public/broadband-customer/plans/order-summary.js"></script>

<jsp:include page="../footer-end.jsp" />