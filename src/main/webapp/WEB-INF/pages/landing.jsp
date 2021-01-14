<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ include file="header.jsp" %>  
<title>Dashboard</title>
<link href="css/material-dashboard.css" rel="stylesheet" type="text/css" />
<link href="css/nucleo.css"         rel="stylesheet" type="text/css">
<link href="css/argon.css?v=1.2.0"  rel="stylesheet" type="text/css">

<style type="text/css">
.col-3-width{width:16%;padding:0 5px;}
.header-color{line-height: 10px;}
.container {
	padding: 0;
}
.static-btn {
	margin: 22% 0 0;
}

.panel-red {
    border-color: #d82d0d;
    background-color: #ea0707;
}
.zoom {

    transition: transform .7s;  
}
/* .row{margin: 0;} */
.zoom:hover {
    -ms-transform: scale(1.1); /* IE 9 */
    -webkit-transform: scale(1.1); /* Safari 3-8 */
    transform: scale(1.1); 
}
.hidebar{
display:none;
}
.homebtn{
display:none;
}
.btn-success:hover{background-color: #339bb7 !important;border-color: #339bb7 !important;}
.panel .panel-heading{padding: 8px 10px; min-height: auto;}
</style>
<body onload="checkbankcode()">
	
		<div id="page-wrapper" style="min-height: 575px;">
		<div class="row" style="margin: 0; padding: 0;">
			<div class="col-lg-12">
				<h3 class="page-header" style="margin-top: 10px">
					Dashboard
					<%-- <p style="float: right ; margin-top: 8px;color:black;">${bnkname}</p> --%>
					<p style="float: right ; margin-top: 8px;color:black;">${sessionScope.bank}</p>
				</h3>
			</div>
		</div>
		<div  style="width:85%;margin: 0 auto;">
              <div class="row">
				<div class="col-lg-3 col-md-6 col-sm-6">
					<div class="card card-stats">
						<div class="card-header card-header-warning card-header-icon">
							<div class="card-icon">
								<!-- <i class="material-icons"></i> -->
								<i class="fa fa-clipboard fa-lg" style="line-height: 1.2;"></i>
							</div>
							<p class="card-category"></p>
							<h4 class="card-title">Create Report</h4>
						</div>
						<div class="card-footer">
							<div class="stats">
								<a href="${pageContext.request.contextPath}/report"> <span
									class="pull-left" style="font-weight: 500;">View Details</span>
									</a>
							</div>
						</div>
					</div>
				</div>
				<c:choose>
					<c:when test="${sessionScope.isCCdp=='true'}">
						<div class="col-lg-3 col-md-6 col-sm-6">
							<div class="card card-stats">
								<div class="card-header card-header-success card-header-icon">
									<div class="card-icon">
										<i class="fa fa-file-text-o fa-lg" style="line-height: 1.2;"></i>
									</div>
									<p class="card-category"></p>
									<h4 class="card-title">CPA</h4>
								</div>
								<div class="card-footer">
									<div class="stats">
										<a href="${pageContext.request.contextPath}/staticreport">
											<span class="pull-left" style="font-weight: 500;">View
												Details</span> 
										</a>
									</div>
								</div>
							</div>
						</div>
					</c:when>
				</c:choose>
			<c:choose>
					<c:when test="${sessionScope.isCCdp=='false'}">
						<div class="col-lg-3 col-md-6 col-sm-6">
							<div class="card card-stats">
								<div class="card-header card-header-success card-header-icon">
									<div class="card-icon">
										<i class="fa fa-file fa-lg" style="line-height: 1.2;"></i>
									</div>
									<p class="card-category"></p>
									<h4 class="card-title">CPA</h4>
								</div>
								<div class="card-footer">
									<div class="stats">
										<a href="#"> <span class="pull-left"
											style="font-weight: 500;">View Details</span> </a>
									</div>
								</div>
							</div>
						</div>
					</c:when>
				</c:choose> 
				<div class="col-lg-3 col-md-6 col-sm-6">
					<div class="card card-stats">
						<div class="card-header card-header-danger card-header-icon">
							<div class="card-icon">
								<i class="fa fa-pie-chart fa-lg" style="line-height: 1.2;"></i>
							</div>
							<p class="card-category"></p>
							<h4 class="card-title">Show Chart</h4>
						</div>
						<div class="card-footer">
							<div class="stats">
								<a href="${pageContext.request.contextPath}/showchartDetails">
									<span class="pull-left" style="font-weight: 500;">View
										Details</span> 
								</a>
							</div>
						</div>
					</div>
				</div>
				<!-- Deposit statement start here -->
				<div class="col-md-3 col-3-width" id="dipoststmtdivid">
					<div class="zoom">
						<div class="panel panel-primary">
							<div class="panel-heading">
								<div class="row">
									<div class="col-xs-3">
										<i class="fa fa-bar-chart fa-lg" style="line-height: 1.2;"></i>
									</div>
									<div class="text-left">
										<div style="color: white; font-size: 14px; font-weight: 600;">Deposit
											Statement</div>
									</div>
								</div>
							</div>
							<a href="${pageContext.request.contextPath}/getDepositstmt">
								<div class="panel-footer">
									<span class="pull-left" style="font-weight: 500;">View
										Details</span> 
									<div class="clearfix"></div>
								</div>
							</a>
						</div>
					</div>
				</div>

				<!-- Deposit Statement ends here -->                
              
				<div class="col-lg-3 col-md-6 col-sm-6">
              <div class="card card-stats">
                  <div class="card-header card-header-info card-header-icon">
                      <div class="card-icon">
                         <i class="fa fa-files-o fa-lg" style="line-height:1.2;"></i>
                      </div>
                      <p class="card-category"></p>
					  <h4 class="card-title">Old Statement</h4>
                  </div>
                  <div class="card-footer">
                      <div class="stats">
                         <a href="${pageContext.request.contextPath}/getReportByAcc">
                         <span class="pull-left" style="font-weight:500;">View Details</span>
                        <!--  <span class="pull-right"><i class="fa fa-arrow-circle-right"></i></span> -->
                        </a>
                      </div>
                  </div>
              </div>                      
          </div>
				<div class="col-md-3 col-3-width" id="quaryrunner">
					<div class="zoom">
						<div class="panel panel-yellow">
							<div class="panel-heading" style="background: crimson;">
								<div class="row">
									<div class="col-xs-2">
										<i class="fa fa-bar-chart fa-lg" style="line-height: 1.2;"></i>
									</div>
									<div class="col-xs-10 text-left">
										<div style="color: white; font-size: 14px; font-weight: 600;">Quary
											Runner</div>
									</div>
								</div>
							</div>
							<a href="${pageContext.request.contextPath}/predefinereports">
								<div class="panel-footer">
									<span class="pull-left" style="font-weight: 500;">View
										Details</span> <span class="pull-right"><i
										class="fa fa-arrow-circle-right"></i></span>
									<div class="clearfix"></div>
								</div>
							</a>
						</div>
					</div>
				</div>
				<input type="hidden" value="${user }" id="userid"/>
				<input type="hidden" id="tellertype" value=${sessionScope.tellertype } >
                </div>
     <!-------------\\\\ Download record count Status  \\------------------------------------------------------------------------------------------ -->
<%-- 
			<h3>Download History</h3>
			<div class="row">
				<div class="col-xl-3 col-md-6">
					<div class="card card-stats">
						<!-- Card body -->
						<div class="card-body">
							<div class="row">
								<div class="col">
									<h5 class="card-title text-uppercase text-muted mb-0">Total
										Count on Current Date</h5>
									<span class="data">${TotalCountOnDate}</span>
								</div>
								<div class="col-auto">
									<div
										class="icon icon-shape bg-gradient-red text-white rounded-circle shadow">
										<i class="fa fa-arrow-circle-down"></i>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
				<div class="col-xl-3 col-md-6">
					<div class="card card-stats">
						<!-- Card body -->
						<div class="card-body">
							<div class="row">
								<div class="col">
									<h5 class="card-title text-uppercase text-muted mb-0">Total
										Completed Count</h5>
									<span class="data">${completcount}</span>
								</div>
								<div class="col-auto">
									<div
										class="icon icon-shape bg-gradient-orange text-white rounded-circle shadow">
										<i class="fa fa-download"></i>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
				<div class="col-xl-3 col-md-6">
					<div class="card card-stats">
						<!-- Card body -->
						<div class="card-body">
							<div class="row">
								<div class="col">
									<h5 class="card-title text-uppercase text-muted mb-0">Total
										Inprocess Count</h5>
									<span class="data">${inprocesscount}</span>
								</div>
								<div class="col-auto">
									<div
										class="icon icon-shape bg-gradient-green text-white rounded-circle shadow">
										<i class="fa fa-spinner"></i>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
				<div class="col-xl-3 col-md-6">
					<div class="card card-stats">
						<!-- Card body -->
						<div class="card-body">
							<div class="row">
								<div class="col">
									<h5 class="card-title text-uppercase text-muted mb-0">Total
										Downloads Count</h5>
									<span class="data">${TotalCount}</span>
								</div>
								<div class="col-auto">
									<div
										class="icon icon-shape bg-gradient-info text-white rounded-circle shadow">
										<i class="fa fa-cloud-download" aria-hidden="true"></i>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
 --%>
			<div style="width: 85%; margin: 0 auto;">
				<div class="col-md-12">
					<div class="">
						<div class=""></div>
						<!-- /.panel-heading -->
						<div style="width: 100%; overflow: hidden;">
							<div id="invmchart" class="col-md-6" style="height: 400px;"></div>
							<div id="loanchart" class="col-md-6" style="height: 400px;"></div>
						</div>
					</div>

				</div>
			</div>
		</div>
      </div>
                     
                
     <script type="text/javascript" charset="utf-8">
						$(window)
								.on(
										"load",
										function() {
											$
													.ajax({
														type : "POST",
														url : 'showchartResult',
														data : '',
														processData : false,
														contentType : "application/json; charset=utf-8",
														error : function(xhr,
																status, error) {
														},
														success : function(
																response) {
															var list = response;

															var chart = new CanvasJS.Chart(
																	"invmchart",
																	{
																		exportEnabled : false,
																		animationEnabled : true,
																		theme : "light1", // "light1", "light2", "dark1", "dark2"
																		title : {
																			text : "Deposit - Product and Accounts",
																			fontSize : 20,
																			padding : 10
																		},
																		axisY : {
																			title : "Number of Transactions"
																		},
																		axisX : {
																			title : "List of Banks"
																		},
																		data : [ {

																			type : "pie",
																			startAngle : 20,
																			toolTipContent : "<b>Product {label} </b>No of count: {y}",
																			legendText : "{label}",
																			indexLabelFontSize : 12,
																			indexLabel : "{label} - {y}",
																			dataPoints : list
																		} ]
																	});

															chart.render();
															if ($('.canvasjs-chart-credit'))
																$(
																		'.canvasjs-chart-credit')
																		.addClass(
																				'hide');
														},
														beforeSend : function() {

														},
													});
											$
													.ajax({
														type : "POST",
														url : 'loanchartData',
														data : '',
														processData : false,
														contentType : "application/json; charset=utf-8",
														error : function(xhr,
																status, error) {
														},
														success : function(
																response) {
															var list = response;

															var chart = new CanvasJS.Chart(
																	"loanchart",
																	{
																		exportEnabled : false,
																		animationEnabled : true,
																		theme : "light1", // "light1", "light2", "dark1", "dark2"
																		title : {
																			text : "Loan - Product and Accounts",
																			fontSize : 20,
																			padding : 10
																		},
																		axisY : {
																			title : "Number of Transactions"
																		},
																		axisX : {
																			title : "List of Banks"
																		},
																		data : [ {

																			type : "pie",
																			startAngle : 20,
																			toolTipContent : "<b>Product {label} </b>No of count: {y}",
																			legendText : "{label}",
																			indexLabelFontSize : 12,
																			indexLabel : "{label} - {y}",
																			dataPoints : list
																		} ]
																	});

															chart.render();
															if ($('.canvasjs-chart-credit'))
																$(
																		'.canvasjs-chart-credit')
																		.addClass(
																				'hide');
															/* $("#chartContainer").CanvasJSChart(chart); */
														},
														beforeSend : function() {

														},
													});

											$('#dipoststmtdivid').hide();
											$('#quaryrunner').hide();

										});
					</script>
<%@ include file="/WEB-INF/pages/Footer.jsp"%>    