<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix='fmt' uri="http://java.sun.com/jsp/jstl/fmt" %> 
<c:set var="ctx" value="${pageContext.request.contextPath}"></c:set>

<jsp:include page="header.jsp" />
<jsp:include page="../alert.jsp" />

<style>
.topup-list li {
	padding: 10px 20px;
}
</style>

<div class="container">
	<div class="page-header">
		<h1>
			2. Customer Information 
		</h1>
	</div>
	
	<div class="panel panel-success">
		<div class="panel-heading">
			<h2 class="panel-title">
				<a data-toggle="collapse" data-toggle="collapse" data-parent="#accordion" href="#application">
					Your Application Form <span class="text-danger">(All Fields Required)</span>
				</a>
			</h2>
		</div>
		<div id="application" class="panel-collapse collapse in">
			<div class="panel-body">
				<form id="customerInfoFrom" class="form-horizontal">
				
					<!-- customer address -->
					<h4 class="text-success">Your Address</h4>
					<hr/>
					
					<div class="form-group">
						<label for="address" class="control-label col-sm-4">Your Address</label>
						<div class="col-sm-8">
							<input type="text" id="address" name="address" value="${orderCustomer.address }" class="form-control" data-error-field data-placement="top"/>
						</div>
					</div>
					
					<!-- customer account -->
					<hr/>
					<h4 class="text-success">Create Your Account</h4>
					<hr/>
					
					<div class="form-group">
						<label for="cellphone" class="control-label col-sm-4">Your Mobile</label>
						<div class="col-sm-4">
							<input type="text" id="cellphone" name="cellphone" value="${orderCustomer.cellphone }"class="form-control" placeholder="e.g.: 0210800123" data-error-field />
						</div>
					</div>
					<div class="form-group">
						<label for="email" class="control-label col-sm-4">Your Email</label>
						<div class="col-sm-4">
							<input type="text" id="email" name="email" value="${orderCustomer.email }" class="form-control" placeholder="e.g.: welcome@cyberpark.co.nz" data-error-field/>
						</div>
					</div>
					
					<!-- Broadband Options -->
					<hr/>
					<h4 class="text-success">Broadband Options</h4>
					<hr/>
					
					<div class="form-group">
						<label class="control-label col-sm-4">Broadband Type</label>
						<div class="col-sm-4">
							<ul class="list-unstyled topup-list">
								<li>
									<input type="radio" name="order_broadband_type" 
										${orderCustomer.customerOrder.order_broadband_type=='new-connection'?'checked="checked"':'' } value="new-connection"/>
									&nbsp; <strong>New Connection Only</strong>
								</li>
								<li>
									<input type="radio" name="order_broadband_type" value="transition"
										<c:if test="${orderCustomer.customerOrder.order_broadband_type=='transition' || orderCustomer.customerOrder.order_broadband_type==null}">
											checked="checked"
										</c:if> />
									&nbsp; <strong>Transition</strong>
								</li>
							</ul>
						</div>
						<div class="col-sm-4">
							<c:choose>
								<c:when test="${orderPlan.plan_group == 'plan-no-term' }">
									<div class="well">
										<p>If you choose a new connection</p>
										<p> we will charge you </p>
										<p>$ 99 broadband opening costs</p>
									</div>
								</c:when>
								<c:when test="${orderPlan.plan_group == 'plan-term' }"></c:when>
							</c:choose>
						</div>
					</div>
					
					
					<!-- Transition Information -->
					<div id="transitionContainer" >
					
						<hr/>
						<h4 class="text-success">Transition Information</h4>
						<hr/>
						
						<div class="form-group">
							<label for="" class="control-label col-sm-4">Your Current Provider Name</label>
							<div class="col-sm-4">
								<input type="text" id="customerOrder.transition_provider_name" name="customerOrder.transition_provider_name" value="${orderCustomer.customerOrder.transition_provider_name }" class="form-control"/>
							</div>
						</div>
						<div class="form-group">
							<label for="" class="control-label col-sm-4">Account Holder Name</label>
							<div class="col-sm-4">
								<input type="text" id="customerOrder.transition_account_holder_name" name="customerOrder.transition_account_holder_name" value="${orderCustomer.customerOrder.transition_account_holder_name }" class="form-control" />
							</div>
						</div>
						<div class="form-group">
							<label for="" class="control-label col-sm-4">Your Current Account Number</label>
							<div class="col-sm-4">
								<input type="text" id="customerOrder.transition_account_number" name="customerOrder.transition_account_number" value="${orderCustomer.customerOrder.transition_account_number }" class="form-control" />
							</div>
						</div>
						<div class="form-group">
							<label for="" class="control-label col-sm-4">Your Telephone Number</label>
							<div class="col-sm-4">
								<input type="text" id="customerOrder.transition_porting_number" name="customerOrder.transition_porting_number" value="${orderCustomer.customerOrder.transition_porting_number }" class="form-control" />
							</div>
						</div>
					
					</div>
					
					
					<!-- Personal Information -->
					
					<hr/>
					<h4 class="text-success">Personal Information</h4>
					<hr/>
					
					<div class="form-group">
						<label for="title" class="control-label col-sm-4">Title</label>
						<div class="col-sm-2">
							<select name="title" id="title" class="selectpicker show-tick form-control">
								<option value="Mr">Mr</option>
								<option value="Mrs">Mrs</option>
								<option value="Ms">Ms</option>
							</select>
						</div>
					</div>
					<div class="form-group">
						<label for="first_name" class="control-label col-sm-4">First name</label>
						<div class="col-sm-4">
							<input type="text" id="first_name" name="first_name" value="${orderCustomer.first_name }" class="form-control" data-error-field />
						</div>
					</div>
					<div class="form-group">
						<label for="last_name" class="control-label col-sm-4">Last name</label>
						<div class="col-sm-4">
							<input type="text" id="last_name" name="last_name" value="${orderCustomer.last_name }" class="form-control" data-error-field />
						</div>
					</div>
						
					<hr>
					<div class="form-group">
						<div class="col-sm-2">
							<a href="${ctx }/broadband-user/sale/online/ordering/plans/${orderPlan.plan_classz}" class="btn btn-success btn-lg btn-block" >Back</a>
						</div>
						<div class="col-sm-2 col-sm-offset-8">
							<button type="button" class="btn btn-success btn-lg btn-block" id="confirm">Confirm</button>
						</div>
					</div>
				</form>
			</div>
		</div>
	</div>
		
	
</div>
<div id="map_canvas" style="width:720px;height:600px;display:none;"></div>


<jsp:include page="footer.jsp" />
<jsp:include page="script.jsp" />
<script type="text/javascript" src="${ctx}/public/bootstrap3/js/icheck.min.js"></script>
<script type="text/javascript" src="${ctx}/public/bootstrap3/js/bootstrap-select.min.js"></script>
<script type="text/javascript">
(function($){
	
	$(':radio').iCheck({
		checkboxClass : 'icheckbox_square-green',
		radioClass : 'iradio_square-green'
	});
	
	$('.selectpicker').selectpicker(); 
	
	$('input[name="order_broadband_type"]').on('ifChecked', function(){
		var val = this.value;
		if (val === "new-connection") {
			$('#transitionContainer').hide('fast');
		} else if (val === "transition") {
			$('#transitionContainer').show('fast');
		}
	});
	
	$('#confirm').click(function(){
		var $btn = $(this);
		$btn.button('loading');
		//console.log($('input[name="order_broadband_type"]:checked').val());
		
		var url = '${ctx}/broadband-user/sale/online/ordering/order/personal';
		var customer = {
			address: $('#address').val()
			, cellphone: $('#cellphone').val()
			, email: $('#email').val()
			, title: $('#title').val()
			, first_name: $('#first_name').val()
			, last_name: $('#last_name').val()
			, customerOrder: {
				order_broadband_type: $('input[name="order_broadband_type"]:checked').val()
			}
			, customer_type: 'personal'
		};
		
		if (customer.customerOrder.order_broadband_type == 'transition') {
			customer.customerOrder.transition_provider_name = $('#customerOrder\\.transition_provider_name').val();
			customer.customerOrder.transition_account_holder_name = $('#customerOrder\\.transition_account_holder_name').val();
			customer.customerOrder.transition_account_number = $('#customerOrder\\.transition_account_number').val();
			customer.customerOrder.transition_porting_number = $('#customerOrder\\.transition_porting_number').val();
		}
		
		//console.log("customer request:");
		//console.log(customer);
		
		$.ajax({
			type: 'post'
			, contentType:'application/json;charset=UTF-8'         
	   		, url: url
		   	, data: JSON.stringify(customer)
		   	, dataType: 'json'
		   	, success: function(json){
				if (json.hasErrors) {
					$.jsonValidation(json, 'right');
				} else {
					//console.log("customer response:");
					//console.log(json.model);
					window.location.href='${ctx}' + json.url;
				}
		   	}
		}).always(function () {
			$btn.button('reset');
	    });
	});
	
})(jQuery);
</script>
<script src="https://maps.google.com/maps/api/js?sensor=false&libraries=places&region=NZ" type="text/javascript"></script>
<script type="text/javascript" src="${ctx}/public/bootstrap3/js/autoCompleteAddress.js"></script>
<jsp:include page="../footer-end.jsp" />