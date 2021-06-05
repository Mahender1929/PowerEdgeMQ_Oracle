<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Login Page</title>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<link href="css/bootstrap.min.css" rel="stylesheet" type="text/css" >
<link href="css/pages1.css" 	   rel="stylesheet" type="text/css" />
<link href="css/pages-new.css"     rel="stylesheet" type="text/css" />
<link href="css/jquery-ui.css"     rel="stylesheet" type="text/css" />
<link href="css/style.css"         rel="stylesheet" type="text/css" />
<link href="css/select2.css"       rel="stylesheet" type="text/css" />
<link href="css/fonts/font-awesome/css/font-awesome.css" rel="stylesheet" type="text/css" />
<style type="text/css">
.errmsg {
	color: red;}
#message {
	color: red;}
.texterror {
	font-family: 'Source Sans Pro', sans-serif;
	font-size: 16px;
	color: red;}
.form {
	background: rgba(255, 255, 255, 0.5);
	border: 5px solid teal;}
	.select2-container {
    background: white !important;
    }
.authorized_msg{ margin: 20px 0 0;color: black; padding: 0 10px; line-height: 1.4;font-size: 12px;}
.select2-container .select2-choice{background: teal !important;border: 0; color: #fff;padding: 0;text-align: left;}
.select2-container .select2-choice .select2-arrow{background: none;border: 0;width: auto;}
.select2-results .select2-highlighted .select2-result-label{color: #fff!important;}

.wrapper {
    background: linear-gradient(to bottom, #fff, #fff, #fff);
}
</style>
</head>
<body>
	<div class="wrapper">
		<div style="background: rgba(0, 0, 0, 0.1); position: absolute; width: 100%; height: 100%;background: url(images/login_bc.jpg)  no-repeat center center fixed; background-size:100%"></div>
		<!-- <div style="width: 150px; margin: 10% auto 0;">
			<img src="images/c-Edge_logo.png" class="img-responsive">
		</div> -->
		
		<!-- <div id="particles-js"></div> -->
		<div class="container" style="margin-left: 100px;margin-top: 135px;" >
			<form:form id="loginform1"  modelAttribute="loginForm" class="form" action="${pageContext.request.contextPath}/landing" method="POST">
				<form:select data-init-plugin="select2"  style="display: none" id="namkName" path="bankName" class="form-input1" type="select" name="bankName" value="">
					<form:option value="Please Select Bank Name"></form:option>
					<span class="glyphicon glyphicon-triangle-bottom"></span>
					<c:forEach items="${banks}" var="bank">
						<form:option value="${bank.bankName}"></form:option>
					</c:forEach>
				</form:select>
				<form:input id="tellerid" style="margin-top:10px;" placeholder="Username" path="tellerid" class="form-input1" type="text" name="tellerid"></form:input>
				<span id="errmsg"></span>
				<form:input path="pwd" class="form-input1" placeholder="Password" type="password" name="pwd" id="pwd" ></form:input>
				<button id="loginform" type="button" class="login-button">
					<span class="glyphicon glyphicon-log-in"></span>&nbsp;&nbsp;&nbsp;Sign in
				</button>
				<div class="text-center w-full p-t-23">
					<p class="texterror">
						<c:out value='${INVALID_USER}' />
					</p>
					<p class="texterror">
						<c:out value='${SESSION_LOGOUT}' />
					</p>
					<p class="texterror">
						<c:out value='${LOGIN_ERROR}' />
					</p>
					<p class="texterror">
						<c:out value='${NO_PRVLG}' />
					</p>
					<p class="texterror">
						<c:out value='${error}' />
					</p>
						<p class="texterror">
						<c:out value='${errCode}' />
					</p>
					
				</div>
				<p class="authorized_msg">Only authorized users are allowed to access company assets.<br> All accesses are logged and monitored.</p>
			</form:form>
		</div>
		
		<div id="fade"></div>
		<div id="modal" style="height: auto; width: auto; padding: 0; border-radius: 0;">
			<img id="loader" style="width: 150px;" src="images/loading5.gif" />
		</div>
	</div>
	<script type="text/javascript" src="js/jquery-3.4.1.min.js"></script>
	<script type="text/javascript" src="js/aes.js"></script>
	<script type="text/javascript" src="js/bootstrap.min.js"></script>
	<script type="text/javascript" src="js/main.js"></script>
	<script type="text/javascript" src="js/report.js"></script>
	<script type="text/javascript" src="js/select2.min.js"></script>

	<script>
	$(document).ready(function () {
		$('#loginform').click(function(e) {
			processLoginForm();
		});
		  //called when key is pressed in textbox
		  $("#tellerid").keypress(function (e) {
		     //if the letter is not digit then display error and don't type anything
		     if (e.which != 8 && e.which != 0 && (e.which < 48 || e.which > 57)) {
		        //display error message
		        $("#errmsg").html("Digits Only").show().fadeOut("slow");
		               return false;
		    }
		   });
		  
		  $("#tellerid").keyup(function (e) {
			  if($('#tellerid').val()=='501'){
			  document.getElementById('namkName').style.display = 'inline-block';
			  $("#namkName").select2();
			  }
			  else{
				  document.getElementById('namkName').style.display = 'none';  
			  }
			   });
		});
	
	
	
	</script>

</body>
</html>
<!-- <script src="https://cdnjs.cloudflare.com/ajax/libs/crypto-js/3.1.2/rollups/aes.js"></script>
     <script src="http://crypto-js.googlecode.com/svn/tags/3.1.2/build/rollups/md5.js"></script> 
     <script src='http://cdnjs.cloudflare.com/ajax/libs/jquery/2.1.3/jquery.min.js'></script> -->
	