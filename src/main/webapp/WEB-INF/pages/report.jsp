<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ include file="/WEB-INF/pages/header.jsp"%>
<link rel="stylesheet" href="css/style.css">
<style type="text/css">
.select2-results li {
	color: black;
	font-weight: 500;
	font-size: 12px;
}

#errmsg {
	color: red;
}
#errormsg {
	color: red;
}
#serverValidation {
	color: red;
	margin-top: 48px;
}
#message {
	margin-left: 85px;
	color: red;
}
.table_select_error {
	border-color: red;
}
#drag_message {
	color: red;
}
.iptext_message {
	color: red !important;
}
ul>li, ol>li {
	line-height: 1.4;
	padding-left: 0;
	/* border-bottom: 1px solid #ccc; */
}
.btn {text-align: left;}
.scrollable{padding-top:15px;}
.table_field a {
	margin-right: 5px;
	margin-bottom: 5px;
}
/* .btn-success:focus{background-color: #E94057;border-color: #E94057;} */
.btn-success:hover {
	background-color: #339bb7 !important;
	border-color: #339bb7 !important;
}
.header-color {
	line-height: 10px !important;margin:0;}
.fa-file:before{color:#000;}
.select2-container .select2-choice {
	padding: 0 0 0 5px;
	background: none;
	border: none;
}
.select2-container .select2-choice .select2-arrow {
	background: none;
	border: 0;
	border-radius: 0;
}
.select2-results .select2-highlighted .select2-result-label {
	color: #fff !important;}
.font-icon{margin-top: 0.5%;border-bottom: 1px solid #f1f1f1;margin-bottom: 0.5%;}
.dataTables_wrapper .dataTables_filter{padding:0;}
.loan_label-15{padding:0 10px; background: #fff; box-shadow: 0 0 4px rgba(0, 0, 0, 0.5);margin: 5px 0 0;
overflow:hidden;}
.select2-results li{border-bottom: 1px solid #ccc;}
</style>

	<div id="mySidenav" class="sidenav"
		style="border-right: 1px solid #ccc;">
		<input type="text" id="myInput" onkeyup="myFunction()"
			placeholder="Search for names.." title="Type in a name"> <a
			href="javascript:void(0)" class="closebtn" onclick="closeNav()"
			style="line-height: 1;">&times;</a>
		<nav id="sidebar">
		<ul id="columns" class="list-unstyled components">
		</ul>
		</nav>
	</div>

	<div id="main">
		<!-- <div class="header-color"><span style="font-size:25px;cursor:pointer" onclick="openNav()">&#9776;</span></div> -->
		<div class="scrollable">
			<div class="report_bg_patch">
				<div class="row">
					<div class="col-md-12 font-icon">
						<div class="pull-right">
							<a class="file_action" href="help"><i
								class="glyphicon glyphicon-question-sign"></i><span><label
									style="cursor: pointer;">Help</label></span></a> <a class="file_action"
								href="#"><i class="fa fa-edit"></i><span><label
									style="cursor: pointer;">Graphs</label></span></a> <a class="file_action"
								href="${pageContext.request.contextPath}/showFavouriteList"><i
								class="glyphicon glyphicon-star-empty"></i><span><label
									style="cursor: pointer;">Favourite List</label></span></a> <a
								class="file_action" href="#"> <!-- <i id="groupby" class="fa fa-edit"> </i>-->
								<input style="float: left; margin: 4px 4px 0 0;" id="groupby"
								type="checkbox"><span><label
									style="cursor: pointer;">Groups by</label></span></a>
						</div>
					</div>
					<!-- <h4 class="text-center bold b-b report_head m-t-0"
						style="height: 45px; background: #fff; border-radius: 0;"></h4> -->
				</div>
				<div class="row">
					<div class="col-md-12">
						<div class="col-md-10 no-padding">
							<label class="pull-left fs-14" style="margin: 5px 15px 0 0;">Table
								Name</label> <select id="table_select" data-init-plugin="select2"
								class="form_input pull-left half_width"
								style="width: 40%; cursor: pointer; margin-right: 15px; height: 28px;">
								<c:forEach items="${tables}" var="table">
									<option value="<c:out value="${table.tableName}"></c:out>"><c:out
											value="${table.name}"></c:out></option>
								</c:forEach>
								<option value="<c:out value="${favouriteLists.table}"></c:out>"
									selected="selected"><c:out
										value="${favouriteLists.table}"></c:out></option>
							</select>
							<button id="tables" class="btn btn-success report-btn">
								<i class="glyphicon glyphicon-file"></i>&nbsp;Get Tables
							</button>
							<button style="display: none" id=""
								class="favourite btn btn-success report-btn" data-toggle="modal"
								data-target="#saveFavourite">
								<span class="glyphicon glyphicon-file"></span>&nbsp;Save
							</button>

							<button id="clear_btn_all" class="btn btn-success report-btn">
								<i class="glyphicon glyphicon-remove-sign"></i>&nbsp;Clear All
							</button>

                         <!-- Queue download page Button -->
						<%--   <a href="${pageContext.request.contextPath}/downloadreportPage"><button id="dwld" style="margin-top:3px;" class="btn btn-success">
						 <i class="glyphicon glyphicon-download-alt"></i>&nbsp;Downloads
						 </button>
						  </a> --%>

							<div class="clearfix"></div>
							<span id="message"></span>
						</div>
						<div class="clearfix"></div>
						<div style="display: none;" id='table_field' class="drag_drop_div">
							<label class="field_head">Select Reports Fields</label>&nbsp;&nbsp;<span
								class="fs-12">(Please click on field)</span>
							<div class="clearfix"></div>
							<div class="hide col-md-6 p-r-5 p-l-0" id="div1"
								ondrop="drop(event)" ondragover="allowDrop(event)">
								<div class="table_field b-grey" id="drop"></div>
							</div>
							<div class="col-md-12 p-l-5 p-r-0" id="div2" ondrop="drop(event)"
								ondragover="allowDrop(event)">
								<div class="table_field b-grey" id="drag">
									<%-- <c:out value="${favouriteLists.parameter}"></c:out> --%>
								</div>
								<span id="drag_message"></span>
							</div>
						</div>
					</div>
				</div>
<%-- 				<div class="row" style="margin: 10px 0;">
						<div class="col-md-12 font-icon">
						<div class="pull-right">
							<a class="file_action" href="#"><i class="fa fa-edit"></i><span><label>Graphs</label></span></a> <a class="file_action" href="${pageContext.request.contextPath}/showFavouriteList"><i
								class="glyphicon glyphicon-star-empty"></i><span><label style="cursor: pointer">Favourite List</label></span></a> <a class="file_action" href="#"> <!-- <i id="groupby" class="fa fa-edit"> </i>-->
								<input style="float: left; margin: 4px 4px 0 0;" id="groupby" type="checkbox"><span><label>Groups by</label></span></a>
						</div>
					</div>
				</div> --%>
				<div class="row">
					<div class="col-md-12">
						<div class="loan_label-15">
							<p class="field_head">Table Criteria
							</p>
							
							<div class="m-t-10"  id="m10">
								<div class="filter_row1">
									<label class="">Filter</label> <select class="form_input m-r-5"
										id="s1" style="width: 25.7%; border-radius: 0; padding: 4px;">
										<option value=""></option>
									</select> <select class="form_input m-r-5" id="s2"
										style="width: 7%; border-radius: 0; padding: 4px;">
									</select> <input type="text" id="iptext" class="form_input m-r-5"
										onkeypress="return blockSpecialChar(event)"
										style="width: 23.3%; display: block" />

									<div id="iptextdateDiv" style="display: none; padding-left: 0;"
										class='col-md-2'>
										<div class="form-group">
											<div class='input-group date'>
												<input type='text' id="iptextdate"
													class="form_input_date m-r-5" placeholder=" Select date" />
												<span class="input-group-addon"> <span
													class="glyphicon glyphicon-calendar"></span>
												</span>
											</div>
										</div>
									</div>

									<div id="fromToDiv" style="display: none">
										<div class='col-md-2'
											style="padding-left: 0; padding-right: 5px;">
											<div class="form-group">
												<div class='input-group date'>
													<input type='text' id="fromdate"
														class="form_input_date m-r-5" placeholder=" Start date" />
													<span class="input-group-addon"> <span
														class="glyphicon glyphicon-calendar"></span>
													</span>
												</div>
											</div>
										</div>
										<div class='col-md-2'
											style="padding-right: 5px; padding-left: 0;">
											<div class="form-group">
												<div class='input-group date'>
													<input type='text' id="todate"
														class="form_input_date m-r-5" placeholder=" End date" />
													<span class="input-group-addon"> <span
														class="glyphicon glyphicon-calendar"></span>
													</span>
												</div>
											</div>
										</div>
									</div>

									<div id="dateDiv" style="display: none">
										<div class='col-md-2'>
											<div class="form-group">
												<div class='input-group date' id='datetimepicker6'>
													<input type='text' id="iptextdate"
														class="form_input_date m-r-5" /> <span
														class="input-group-addon"> <span
														class="glyphicon glyphicon-calendar"></span>
													</span>
												</div>
											</div>
										</div>
									</div>

									<div id="betweenDiv" style="display: none">
										<input type="text" id="from" class="form_input m-r-5"
											onkeypress="return blockSpecialChar(event)"
											style="width: 15.3%;" /> <label class="bold">To</label> <input
											type="text" id="to" class="form_input m-r-5"
											onkeypress="return blockSpecialChar(event)"
											style="width: 15.3%;" />
									</div>


									<button class="btn btn-success report-btn addto">
										<i class="	glyphicon glyphicon-filter"></i>&nbsp;Add to Filter
									</button>
									<button class="btn btn-success report-btn orto">
										<i class="glyphicon glyphicon-filter"></i>&nbsp;Or to Filter
									</button>

									<span id="errmsg"></span>
									<div id="errormsg">
										<c:out value='${errormsg}' />
									</div>

									<!-- <div id="serverValidation" style="display: none" class="alert alert-danger"></div> -->

									</br>
									<div class="" style="margin-top: 10px;">
										<span>Note:</span><span>1.Red color fields are
											mendatory <!-- 2.black color fields optional -->
										</span>
									</div>
								</div>

								<div class="">
									<textarea disabled rows="3" cols="132"
										style="width: 775px; margin-top: 10px;" class=""
										id="criteriaTextArea"><c:out
											value="${favouriteLists.query}"></c:out></textarea>
								</div>
								<div style="margin-top:5px;">
									<button class="btn btn-success report-btn submitCf"
										data-toggle="modal" data-target="#myModal" id="btn_transmit">
										<i class="glyphicon glyphicon-share-alt"></i>&nbsp;Transmit
									</button>
									<!-- btn-primary new_btn_style  -->
									<button class="submitClear btn btn-success report-btn">
										<i class="glyphicon glyphicon-remove-sign"></i>&nbsp;Clear
									</button>
									<p style="text-align: center; color: green">
										<b id="favouriteMsg"></b>
									</p>
										
										<div class="hide">
										<form id="pdfForm" method="post"
											action="${pageContext.request.contextPath}/createPdf">
											<input type="hidden" name="pdfJson" id="pdf" value="">
											<button class="pdfByItext" type="submit">PDF</button>
										</form>
									</div>
									<div class="hide">
										<form id="excelForm" method="post"
											action="${pageContext.request.contextPath}/createExcel">
											<input type="hidden" name="excelJson" id="excel" value="">
											<button class="excel" type="submit">Excel</button>
										</form>
									</div>
									<div class="hide">
										<form id="csvForm" method="post"
											action="${pageContext.request.contextPath}/createCsv">
											<input type="hidden" name="csvJson" class="csv" value="">
											<button class="csv" type="submit">CSV</button>
										</form>
									</div>
									<div class="hide">
										<form id="pipecsvForm" method="post"
											action="${pageContext.request.contextPath}/createPipeCsv">
											<input type="hidden" name="csvJson" class="csv" value="">
											<button class="csv" type="submit">CSV PIPE</button>
										</form>
									</div>
									<div id="serverValidation" style="display: none"
										class="alert alert-danger"></div>
								</div>
							</div>
						</div>
					</div>
					<div class="col-md-12 m-t-10 m-b-10">
						<!--   <button type="button" id="any_button">CSV</button> -->
						<div class="row b-b b-l b-r b-t b-grey" style="background: #fff;">
							<div style="margin: 10px;overflow:auto;">
								<div id="menu4">
									<!-- Adding dynamic data table here -->
								</div>
								<br>
								<div class="clearfix"></div>
								<div class="row text-center"></div>
							</div>
						</div>
					</div>
				</div>


			</div>
		</div>
	</div>
	<div id="fade"></div>
	<div id="modal"
		style="height: auto; width: auto; padding: 0; border-radius: 0;">
		<img id="loader" style="width: 150px;" src="images/loading5.gif" />
	</div>

	<!-- -----------------------Add To Favourite popup----------------------------------- -->

	<!-- Modal -->
	<div class="modal" id="saveFavourite" tabindex="-1" role="dialog"
		aria-labelledby="myModalLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content animate">
				<!-- Modal Header -->
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span aria-hidden="true">&times;</span> <span class="sr-only">Close</span>
					</button>
					<h4 class="modal-title" id="myModalLabel">Add Favourite
						Details</h4>
				</div>

				<!-- Modal Body -->
				<div class="modal-body">

					<form id="formdata" role="form">
						<div class="form-group">
							<label for="qryTitle">Query Title</label> <input type="text"
								name="qryTitle" class="form-control desc" id="qryTitle"
								placeholder="Enter query title" />
						</div>
						<div class="form-group">
							<label for="purpose">Description</label>
							<textarea name="description" class="form-control desc"
								id="description" placeholder="Enter description"></textarea>
						</div>
						<div class="form-group">
							<label for="purpose">Purpose</label>
							<textarea name="purpose" class="form-control desc" id="purpose"
								placeholder="Enter purpose"></textarea>
						</div>
						<button id="favouriteForm" style="width: 80px" type="button"
							class="btn btn-success report-btn">Submit</button>
					</form>
				</div>

				<!-- Modal Footer -->
				<div class="modal-footer">
					<button type="button" class="btn btn-success report-btn"
						data-dismiss="modal">Close</button>
				</div>
			</div>
		</div>

	</div>
	
	

	<!-- -------------------------------Add To Favourite popup End---------------------------- -->


	<script>
	
	$(".desc").keypress(function (e) {
	     //if the letter is not digit then display error and don't type anything
	    var regex = new RegExp("^[a-zA-Z0-9]+$");
    	var key = String.fromCharCode(!event.charCode ? event.which : event.charCode);
		    if (!regex.test(key)) {
		       event.preventDefault();
		       return false;
		    }
	   });
	
		function myFunction() {
			var input, filter, ul, li, a, i;
			input = document.getElementById("myInput");
			filter = input.value.toUpperCase();
			ul = document.getElementById("columns");
			li = ul.getElementsByTagName("li");
			for (i = 0; i < li.length; i++) {
				a = li[i].getElementsByTagName("a")[0];
				if (a && a.innerHTML.toUpperCase().indexOf(filter) > -1) {
					li[i].style.display = "";
				} else {
					li[i].style.display = "none";

				}
			}
		}

		function openNav() {
			document.getElementById("mySidenav").style.width = "250px";
			document.getElementById("main1").style.marginLeft = "250px";
			document.getElementById("main").style.marginLeft = "250px";
		}

		function closeNav() {
			document.getElementById("mySidenav").style.width = "0";
			document.getElementById("main").style.marginLeft = "0";
			document.getElementById("main1").style.marginLeft = "0";
		}

		function allowDrop(ev) {
			ev.preventDefault();
		}
		function drag(ev) {
			ev.dataTransfer.setData("text", ev.target.id);
		}
		function drop(ev) {
			ev.preventDefault();
			var data = ev.dataTransfer.getData("text");
			var dragValue = document.getElementById(data).value;
			isCriteriaNumber(dragValue);
			ev.target.appendChild(document.getElementById(data));

		}
	</script>

	<script>
		$(window).load(function() {
			retriveColumns();
		});

		$(document).ready(function() {
			$(function() {
				$("#table_select").select2();
				$("#fromdate").datepicker({
					dateFormat : 'd-M-y'
				});
				$("#todate").datepicker({
					dateFormat : 'd-M-y'
				});
				$("#iptextdate").datepicker({
					dateFormat : 'd-M-y'
				});

			});
			$('#any_button').click(function(e) {
				createCsv();
			});

			$('#btn_transmit').click(function(e) {
				checkbankcode();
				submitCf(e);
			});

			$(".addto").on("click", function() {
				addToFilter();
			});

			$(".orto").on("click", function() {
				addORToFilter();
			});
			$('.submitClear').click(function(e) {
				document.getElementById("criteriaTextArea").innerHTML = "";
			});

			$('#s1').change(function(e) {
				validateSelectedValue();
			});

			$('#tables').click(function(e) {
				callMetaDataTable();
			});

			$('#table_select').change(function(e) {
				var tablename = $('#table_select').val();
				retriveColumnAjaxCall(tablename);
			});

			$('#clear_btn_all').click(function(e) {
				clearAllFields();
			});
			$('#s2').click(function(e) {
				addBetweenFilter();
			});
			$('#groupby').click(function(e) {
				groupBy(e);
			});
			$('#favouriteForm').click(function(e) {
				saveFavouriteQuery(e);
			});

		});
		$("#tables").click(function(e){
			document.getElementById("errormsg").style.display = "none";
		})
		history.pushState(null, null, location.href);
    	window.onpopstate = function () {
        history.go(1);
    };
	</script>
<%@ include file="/WEB-INF/pages/Footer.jsp"%>