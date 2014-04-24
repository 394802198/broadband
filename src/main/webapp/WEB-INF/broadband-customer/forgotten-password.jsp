<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"></c:set>

<jsp:include page="header.jsp" />
<jsp:include page="alert.jsp" />

<div class="container" style="padding-top: 20px; padding-bottom: 150px;">
	<div class="row">
		<div class="col-md-4 col-md-offset-4">
			<div class="panel panel-success">
				<div class="panel-heading">CyberPark Customer Forgotten Password</div>
				<div class="panel-body">
					
					<form class="form-horizontal">
						<div class="form-group">
							<div class="col-sm-12">
								<ul class="list-inline topup-list" style="margin: 5px 0 0 0;">
									<li>
										<input type="radio" name="type" value="email" checked="checked" /> &nbsp; 
										<strong>Email Address</strong>
									</li>
									<li>
										<input type="radio" name="type" value="cellphone" /> &nbsp; 
										<strong>Mobile Number</strong>
									</li>
								</ul>
							</div>
						</div>
					</form>
					<form>
						<div class="form-group">
							<input type="text" id="login_name" class="form-control" placeholder="" data-error-field/>
						</div>
						
						<button type="button" data-loading-text="loading..." class="btn btn-success btn-block btn-lg" id="submit-btn">Confirm</button>
					</form>
				</div>
			</div>
		</div>
	</div>
</div>


<jsp:include page="footer.jsp" />
<jsp:include page="script.jsp" />
<script type="text/javascript" src="${ctx}/public/bootstrap3/js/icheck.min.js"></script>
<script type="text/javascript">
(function($){
	
	$(':radio').iCheck({
		checkboxClass : 'icheckbox_square-green',
		radioClass : 'iradio_square-green'
	});
	
	$(document).keypress(function(e){
		if ($('#loginForm input:focus').length > 0 && event.keyCode == 13) {
			$('#submit-btn').trigger('click');
		}
	});
	
	$('#submit-btn').on("click", function(){
		
		var $btn = $(this);
		$btn.button('loading');
		var data = {
			login_name: $('#login_name').val()
			, type: $('input[name="type"]:checked').val()
		};
		$.post('${ctx}/forgotten-password', data, function(json){
			if (json.hasErrors) {
				$.jsonValidation(json, 'right');
			} else {
				window.location.href='${ctx}' + json.url;
			}
		}, 'json').always(function () {
			$btn.button('reset');
	    });
	});
})(jQuery);
</script>
<jsp:include page="footer-end.jsp" />