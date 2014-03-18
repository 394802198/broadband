<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"></c:set>

<jsp:include page="header.jsp" />
<jsp:include page="alert.jsp" />

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
                    		<a href="${ctx }/broadband-user/plan/view/1">View Plan</a>
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
                    <%--  <hr/>
                    <ul class="list-unstyled">
                    	<li>
                    		<span class="glyphicon glyphicon-list" style="padding-right:10px;"></span>
                    		<a href="${ctx }/broadband-user/plan/topup/view/1">View Topup</a>
                    	</li>
                    	<li>
                    		<span class="glyphicon glyphicon-plus" style="padding-right:10px;"></span>
                    		<a href="${ctx }/broadband-user/plan/topup/create">Create Topup</a>
                    	</li>
                    </ul> --%>
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
                    		<a href="${ctx }/broadband-user/crm/customer/pre-create">Create Customer</a>
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
			  		<p>The generation, management, customer billing transmission, chorus bills.</p>
					<ul class="list-unstyled">
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
			  		<p>Review all customer purchase orders and payment orders.</p>
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
                    		<a href="${ctx }/broadband-user/system/company-detail/pre-edit">Edit Company Detail</a>
                    	</li>
                    </ul>
			  	</div>
			</div>
		</div>
		<div class="col-md-3"></div>
		<div class="col-md-3"></div>
	</div>
</div>
<jsp:include page="footer.jsp" />
<jsp:include page="script.jsp" />
<script type="text/javascript"
    src="${ctx}/public/bootstrap3/js/holder.js"></script>
<jsp:include page="footer-end.jsp" />