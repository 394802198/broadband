<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"></c:set>

<jsp:include page="header.jsp" />
<jsp:include page="alert.jsp" />

<style>
.panel-success {
	min-height: 251px;
}
hr {
	margin:0 0 5px 0;
}
</style>
<div class="container">
    <div class="row">
        <div class="col-md-3">
        	<div class="panel panel-success">
		  		<div class="panel-heading">
		  			<h3 class="panel-title"><strong class="text-success">Plan</strong></h3>
		  		</div>
			  	<div class="panel-body">
			    	<p>Create a plan products, plan on-line sales</p>
                    <ul class="list-unstyled">
                    	<li>
                    		<span class="glyphicon glyphicon-list" style="padding-right:10px;"></span>
                    		<a href="${ctx }/broadband-user/plan/view">View Plan</a>
                    	</li>
                    	<li>
                    		<span class="glyphicon glyphicon-plus" style="padding-right:10px;"></span>
                    		<a href="${ctx }/broadband-user/plan/create">Create Plan</a>
                    	</li>
                    </ul>
                    <hr/>
                    <ul class="list-unstyled">
                    	<li>
                    		<span class="glyphicon glyphicon-list" style="padding-right:10px;"></span>
                    		<a href="${ctx }/broadband-user/plan/hardware/view/1">View Hardware</a>
                    	</li>
                    	<li>
                    		<span class="glyphicon glyphicon-plus" style="padding-right:10px;"></span>
                    		<a href="${ctx }/broadband-user/plan/hardware/create">Create Hardware</a>
                    	</li>
                    </ul>
			 	</div>
			</div>
        </div>
        <div class="col-md-3">
        	<div class="panel panel-success">
		  		<div class="panel-heading">
		  			<h3 class="panel-title"><strong class="text-success">CRM</strong></h3>
		  		</div>
			  	<div class="panel-body">
			  		<p>Simple customer relationship management module</p>
                    <ul class="list-unstyled">
                    	<li>
                    		<span class="glyphicon glyphicon-list" style="padding-right:10px;"></span>
                    		<a href="${ctx }/broadband-user/crm/customer/view/1">View Customer</a>
                    	</li>
						<li>
                    		<span class="glyphicon glyphicon-plus" style="padding-right:10px;"></span>
                    		<a href="${ctx }/broadband-user/crm/customer/personal/create">Create Personal Customer</a>
						</li>
						<li>
                    		<span class="glyphicon glyphicon-plus" style="padding-right:10px;"></span>
                    		<a href="${ctx }/broadband-user/crm/customer/business/create">Create Business Customer</a>
						</li>
                    </ul>
			  	</div>
			</div>
        </div>
        <div class="col-md-3">
        	<div class="panel panel-success">
		  		<div class="panel-heading">
		  			<h3 class="panel-title"><strong class="text-success">Billing</strong></h3>
		  		</div>
			  	<div class="panel-body">
			  		<p>Accounting module, contains all billing related details.</p>
					<ul class="list-unstyled">
						<li>
                    		<span class="glyphicon glyphicon-list-alt" style="padding-right:10px;"></span>
                    		<a href="${ctx }/broadband-user/billing/early-termination-charge/view/1/unpaid">View Early Termination Charge</a>
                    	</li>
					</ul>
					<hr/>
					<ul class="list-unstyled">
						<li>
                    		<span class="glyphicon glyphicon-list-alt" style="padding-right:10px;"></span>
                    		<a href="${ctx }/broadband-user/billing/termination-refund/view/1/unpaid">View Termination Refund</a>
                    	</li>
					</ul>
			  	</div>
			</div>
        </div>
        <div class="col-md-3">
        	<div class="panel panel-success">
		  		<div class="panel-heading">
		  			<h3 class="panel-title"><strong class="text-success">Provision</strong></h3>
		  		</div>
			  	<div class="panel-body">
			  		<p>Review all customer's purchased and paid orders.</p>
                    <ul class="list-unstyled">
                    	<li>
                    		<span class="glyphicon glyphicon-tasks" style="padding-right:10px;"></span>
                    		<a href="${ctx }/broadband-user/provision/view/1">View Provision</a>
                    	</li>
                    </ul>
                    <hr/>
                    <ul class="list-unstyled">
                    	<li>
                    		<span class="glyphicon glyphicon-tasks" style="padding-right:10px;"></span>
                    		<a href="${ctx }/broadband-user/provision/customer/view/1/paid">Provision Customer Order</a>
                    	</li>
                    </ul>
                    <hr/>
                    <ul class="list-unstyled">
                    	<li>
							<div class="btn-group">
	                    		<span class="glyphicon glyphicon-tasks" style="padding-right:10px;"></span>
	                    		<a href="${ctx }/broadband-user/provision/contact-us/view/1/new">
	                    			View Contact Us&nbsp;&nbsp;<span class="badge">${newContactUsSum}</span>
	                    		</a>
	                    	</div>
                    	</li>
                    </ul>
                    <hr/>
                    <ul class="list-unstyled">
                    	<li>
                    		<span class="glyphicon glyphicon-plus" style="padding-right:10px;"></span>
                    		<a href="${ctx }/broadband-user/provision/sale/view/1">View Sales</a>
                    	</li>
                    </ul>
			  	</div>
			</div>
        </div>
    </div>
	<div class="row">
		<div class="col-md-3">
			<div class="panel panel-success">
		  		<div class="panel-heading">
		  			<h3 class="panel-title"><strong class="text-success">Data</strong></h3>
		  		</div>
			  	<div class="panel-body">
			  		<p>Configure, manage, query, customer data traffic.</p>
                    <ul class="list-unstyled">
                    	<li>
                    		<span class="glyphicon glyphicon-cloud" style="padding-right:10px;"></span>
                    		<a href="${ctx }/broadband-user/data/operatre">Data Operatre</a>
                    	</li>
                    	<li>
                    		<span class="glyphicon glyphicon-cloud" style="padding-right:10px;"></span>
                    		<a href="${ctx }/broadband-user/data/customer/view">Data Customer View</a>
                    	</li>
                    </ul>
			  	</div>
			</div>
		</div>
		<div class="col-md-3">
			<div class="panel panel-success">
		  		<div class="panel-heading">
		  			<h3 class="panel-title"><strong class="text-success">System</strong></h3>
		  		</div>
			  	<div class="panel-body">
			  		<p>User management, email, SMS templates.</p>
                    <ul class="list-unstyled">
                    	<li>
                    		<span class="glyphicon glyphicon-list" style="padding-right:10px;"></span>
                    		<a href="${ctx }/broadband-user/system/user/view/1">View User</a>
                    	</li>
                    	<li>
                    		<span class="glyphicon glyphicon-plus" style="padding-right:10px;"></span>
                    		<a href="${ctx }/broadband-user/system/user/create">Create User</a>
                    	</li>
                    </ul>
                    <hr/>
                    <ul class="list-unstyled">
                    	<li>
                    		<span class="glyphicon glyphicon-list" style="padding-right:10px;"></span>
                    		<a href="${ctx }/broadband-user/system/notification/view/1">View Notification</a>
                    	</li>
                    	<li>
                    		<span class="glyphicon glyphicon-plus" style="padding-right:10px;"></span>
                    		<a href="${ctx }/broadband-user/system/notification/create">Create Notification</a>
                    	</li>
                    </ul>
                    <hr/>
                    <ul class="list-unstyled">
                    	<li>
                    		<span class="glyphicon glyphicon-pencil" style="padding-right:10px;"></span>
                    		<a href="${ctx }/broadband-user/system/company-detail/edit">Edit Company Detail</a>
                    	</li>
                    </ul>
                    <hr/>
                    <ul class="list-unstyled">
                    	<li>
                    		<span class="glyphicon glyphicon-picture" style="padding-right:10px;"></span>
                    		<a href="${ctx }/broadband-user/system/chart/customer-register/0">Chart(Register Customer)</a>
                    	</li>
                    </ul>
			  	</div>
			</div>
		</div>
		<div class="col-md-3">
			<div class="panel panel-success">
		  		<div class="panel-heading">
		  			<h3 class="panel-title"><strong class="text-success">Sales</strong></h3>
		  		</div>
			  	<div class="panel-body">
			  		<p>Sale Online Ordering.</p>
                    <ul class="list-unstyled">
                    	<li>
                    		<span class="glyphicon glyphicon-list" style="padding-right:10px;"></span>
                    		<a href='${ctx }/broadband-user/sale/online/ordering/view/1/${userSession.user_role == "sales" ? userSession.id : 0 }'>View Online Orders (PAD | PC)</a>
                    	</li>
                    	<li>
                    		<span class="glyphicon glyphicon-plus" style="padding-right:10px;"></span>
                    		<a href="${ctx }/broadband-user/sale/online/ordering/plans/business">Ordering Online (PAD | PC)</a>
                    	</li>
                    </ul>
			  	</div>
			</div>
		</div>
		<div class="col-md-3">
			<div class="panel panel-success">
		  		<div class="panel-heading">
		  			<h3 class="panel-title"><strong class="text-success">Manual Manipulation</strong></h3>
		  		</div>
			  	<div class="panel-body">
			  		<p>These are substitutes for automatic execute program.</p>
                    <ul class="list-unstyled">
                    	<li>
                    		<span class="glyphicon glyphicon-list" style="padding-right:10px;"></span>
                    		<a href='${ctx }/broadband-user/manual-manipulation/manual-manipulation-record/view/1/generate-termed-invoice'>Manual Termed Invoice</a>
                    	</li>
                    </ul>
                    <hr/>
                    <ul class="list-unstyled">
						<li>
                    		<span class="glyphicon glyphicon-earphone" style="padding-right:10px;"></span>
                    		<a href="${ctx }/broadband-user/manual-manipulation/call-billing-record/view/1/inserted">Customer Calling Billing</a>
                    	</li>
						<li>
                    		<span class="glyphicon glyphicon-registration-mark" style="padding-right:10px;"></span>
                    		<a href="${ctx }/broadband-user/manual-manipulation/call-international-rate/view/1">Calling International Rate</a>
                    	</li>
                    </ul>
			  	</div>
			</div>
		</div>
	</div>
</div>
<jsp:include page="footer.jsp" />
<jsp:include page="script.jsp" />
<script type="text/javascript" src="${ctx}/public/bootstrap3/js/holder.js"></script>
<jsp:include page="footer-end.jsp" />
