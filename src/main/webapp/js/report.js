	window.history.forward();
	window.onload = function()
	{
	  window.history.forward();
	};
	window.onunload = function() {
	  null;
	};
$(function() {
	$('#iptext').keyup(function(e) {
		var value = $('#iptext').val();
		var selectedValue = $("#s1").val();
		if ($('#iptext').css('color') == 'rgb(255, 0, 0)') {
			$('#iptext').css('color', 'black');
			$('#btn_transmit').removeClass('disabled');
		}
		if (isCriteriaNumber(selectedValue)) {
			if (!isValidIptext(value)) {
				$('#iptext').addClass('iptext_message');
			} else {
				$('#iptext').removeClass('iptext_message');
			}
		}
	});
});
function blockSpecialChar(e) {
	var k;
	document.all ? k = e.keyCode : k = e.which;
	if ($("#s2").val() == "IN") {
		if (((k > 64 && k < 91) || (k > 96 && k < 123) || k == 8 || k == 32
				|| k == 95 || (k >= 44 && k <= 57))) {
			return true;
		} else {
			$("#errmsg").html("Symbols not allow..").show().fadeOut("slow");
			return false;
		}
	} else {
		if (((k > 64 && k < 91) || (k > 96 && k < 123) || k == 8 || k == 32
				|| k == 95 || (k >= 48 && k <= 57))) {
			return true;
		} else {
			$("#errmsg").html("Symbols not allow..").show().fadeOut("slow");
			return false;

		}
	}
}

function isValidIptext(value) {
	var filter = "";
	if ($("#s2").val() == "IN") {
		filter = /^[0-9-+]+$/;
	} else {
		filter = /^[0-9,-,+]+$/;
	}

	if (filter.test(value)) {
		return true;
	} else {
		return false;
	}
}

function addToFilter() {
	var DD1 = $("#s1").val(), alias = "";
	var DD2 = $("#s2").val(), ipVal = '';
	if ($("#iptext").val()) {
		var tablename = $('#table_select').val();
		if (DD1 == 'ACCT_NO') {
			var temp = $("#iptext").val();
			if(!($("#s2 option:selected").text() == 'Middle Like'))
			//ipVal = temp.substring(0, temp.length - 1).pad('0', 16);
				ipVal = temp.substring(0, temp.length);
				else
				ipVal=temp;
		} else
			ipVal = $("#iptext").val();
	} else if ($("#iptextdate").val()) {
		ipVal = $("#iptextdate").val();
		if (ipVal != '' && ipVal != null && ipVal !== "") {
			
			var tablename1 = $("#table_select option:selected").text().startsWith(
			'NV_');
			if(tablename1)
			ipVal = " to_char(to_date(" + "'" + ipVal + "'"
					+ ",'DD-mm-YY'),'J')-2415020 ";
			else
				ipVal = $("#iptextdate").val();
		}
	}

	var fromValue = $("#from").val(),
	toValue = $("#to").val(), 
	todate = $("#todate").val(), fromdate = $("#fromdate").val();
	var data = $("#criteriaTextArea").val();

	if ((ipVal != "" && ipVal != null) || (fromValue != "" && toValue != null)
			|| (todate != "" && fromdate != null)) {
		if (data == "") {
			addFirstCriteria(DD1, DD2, ipVal, alias);
		} else {
			addMultipleCriteria(data, DD1, DD2, ipVal, alias);
		}
	} else {
		alert("Enter Data for before Criteria");
	}
}

function addFirstCriteria(DD1, DD2, ipVal, alias) {

	if (isCriteriaString(DD1)
			&& (!$("#s2 option:selected").text() == 'Start Like' || !$(
					"#s2 option:selected").text() == 'End Like' || !$("#s2 option:selected").text() == 'Middle Like')) {
		$("#criteriaTextArea").html(
				alias + DD1 + " " + DD2 + " " + "'" + ipVal + "'");
	} else if (isCriteriaString(DD1)
			&& $("#s2 option:selected").text() == 'Start Like') {
		$("#criteriaTextArea").html(
				alias + DD1 + " " + DD2 + " " + "'" + ipVal + "%'");
	} else if (isCriteriaString(DD1)
			&& $("#s2 option:selected").text() == 'End Like') {
		$("#criteriaTextArea").html(
				alias + DD1 + " " + DD2 + " " + "'%" + ipVal + "'");
	} else if (isCriteriaString(DD1)
			&& $("#s2 option:selected").text() == 'Middle Like') {
		$("#criteriaTextArea").html(
				alias + DD1 + " " + DD2 + " " + "'%" + ipVal + "%'");
	}  
	else if (isCriteriaNumber(DD1) && $('#s2').val() == 'between') {
		$("#criteriaTextArea").html(
				alias + DD1 + " BETWEEN " + $('#from').val() + " AND "
						+ $('#to').val());
	} else if (isCriteriaDate(DD1) && $('#s2').val() == 'Fromto') {
		var tablename1 = $("#table_select option:selected").text().startsWith(
				'NV_');
		var tablename2 = $("#table_select option:selected").text().startsWith(
		'Loan');
		var fromdate='';
		var todate='';
		if (tablename1 || tablename2) {
			  fromdate = " to_char(to_date(" + "' " + $('#fromdate').val()
					+ " '" + ",'DD-mm-YY'),'J')-2415020 ";
			  todate = " to_char(to_date(" + "' " + $('#todate').val() + "' "
					+ ",'DD-mm-YY'),'J')-2415020 ";
			  $("#criteriaTextArea").html(
						alias + DD1 + " BETWEEN " + "   " + fromdate + "   " + " AND "
								+ "  " + todate + "  ");
		}else{
			fromdate= $('#fromdate').val();
			todate= $('#todate').val();
			$("#criteriaTextArea").html(
					alias + DD1 + " BETWEEN " + "  ' " + fromdate + " '  " + " AND "
							+ " ' " + todate + " ' ");
		}
		 
	} else if (isCriteriaNumber(DD1) && $('#s2').val() == 'IN') {
		$("#criteriaTextArea").html(alias + DD1 + " IN " + "(" + ipVal + ")");
	} else if (isCriteriaNumber(DD1)) {
		$("#criteriaTextArea").html(alias + DD1 + " " + DD2 + " " + ipVal);
	} else {
		if (ipVal.indexOf("to_char(to_date") >= 0)
			$("#criteriaTextArea").html(
					alias + DD1 + " " + DD2 + " " + "" + ipVal + "");
		else
			$("#criteriaTextArea").html(
					alias + DD1 + " " + DD2 + " " + "'" + ipVal + "'");
	}
}

function addMultipleCriteria(data, DD1, DD2, ipVal, alias) {

	var newdata;
	if (isCriteriaNumber(DD1)
			&& (!$("#s2 option:selected").text() == 'Start Like' || !$(
					"#s2 option:selected").text() == 'End Like' ||  !$("#s2 option:selected").text() == 'End Like' )) {
		newdata = data + "\n" + " AND" + " " + alias + DD1 + " " + DD2 + " "
				+ "'" + ipVal + "'";
	} else if (isCriteriaString(DD1)
			&& $("#s2 option:selected").text() == 'Start Like') {
		newdata = data + "\n" + " AND" + " " + alias + DD1 + " " + DD2 + " "
				+ "'" + ipVal + "%'";
	} else if (isCriteriaString(DD1)
			&& $("#s2 option:selected").text() == 'End Like') {
		newdata = data + "\n" + " AND" + " " + alias + DD1 + " " + DD2 + " "
				+ "'%" + ipVal + "'";
	} else if (isCriteriaString(DD1)
			&& $("#s2 option:selected").text() == 'Middle Like') {
		newdata = data + "\n" + " AND" + " " + alias + DD1 + " " + DD2 + " "
				+ "'%" + ipVal + "%'";
	}else if (isCriteriaNumber(DD1) && $('#s2').val() == 'between') {
		newdata = data + "\n" + " AND" + " " + alias + DD1 + " BETWEEN " + $('#from').val() + " AND " + $('#to').val();
	} else if (isCriteriaDate(DD1) && $('#s2').val() == 'Fromto') {

		var tablename1 = $("#table_select option:selected").text().startsWith('NV_');
		var tablename2 = $("#table_select option:selected").text().startsWith('Loan');
		var fromdate='';
		var todate='';
		if (tablename1 || tablename2) {
			  fromdate = " to_char(to_date(" + "' " + $('#fromdate').val() + " '" + ",'DD-mm-YY'),'J')-2415020 ";
			  todate = " to_char(to_date(" + "' " + $('#todate').val() + "' " + ",'DD-mm-YY'),'J')-2415020 ";
		}else{
			fromdate= $('#fromdate').val();
			todate= $('#todate').val();
		}
		newdata = data + "\n" + " AND" + " " + alias + DD1 + " BETWEEN " + "'" + $('#fromdate').val() + "'" + " AND " + "'" +$('#todate').val() + "'";
	} else if (isCriteriaNumber(DD1) && $('#s2').val() == 'IN') {
		newdata = data + "\n" + " AND" + " " + alias + DD1 + " IN " + "("
				+ ipVal + ")";
	} else if (isCriteriaNumber(DD1)) {
		newdata = data + "\n" + " AND" + " " + alias + DD1 + " " + DD2 + " "
				+ ipVal;
	} else {
		if (ipVal.indexOf("to_char(to_date") >= 0)
			newdata = data + "\n" + " AND" + " " + alias + DD1 + " " + DD2
					+ " " + "" + ipVal + "";
		else
			newdata = data + "\n" + " AND" + " " + alias + DD1 + " " + DD2
					+ " " + "'" + ipVal + "'";

	}
	$("#criteriaTextArea").html(newdata);

}

function addINToFilter() {

	var DD1 = $("#s1").val();
	var DD2 = $("#s2").val();
	var ipVal = $("#iptext").val();
	var Data = $("#criteriaTextArea").val();
	if (ipVal != "" && ipVal != null) {
		if (Data == "") {
			$("#criteriaTextArea").html(DD1 + " " + DD2 + " " + ipVal);
		} else {
			var newdata = Data + "\n" + "AND" + " " + DD1 + " " + DD2 + " "
					+ ipVal;
			$("#criteriaTextArea").html(newdata);
		}
	} else {
		alert("Enter Data for before Criteria");
	}
}

function addORToFilter() {
	var DD1 = $("#s1").val(), alias = "";
	var DD2 = $("#s2").val();
	var ipVal = $("#iptext").val(), fromValue = $("#from").val(), toValue = $(
			"#to").val();
	todate = $("#todate").val(), fromdate = $("#fromdate").val();
	var data = $("#criteriaTextArea").val();
	if ((ipVal != "" && ipVal != null) || (fromValue != "" && toValue != null)
			|| (todate != "" || fromdate != null)) {
		if (data == "") {
			addFirstCriteria(DD1, DD2, ipVal, alias);
		} else {
			var newdata;
			if (isCriteriaNumber(DD1)
					&& (!$("#s2 option:selected").text() == 'Start Like' || !$(
							"#s2 option:selected").text() == 'End Like'||!$("#s2 option:selected").text() == 'Middle Like' )) {
				newdata = data + "\n" + " OR" + " " + alias + DD1 + " " + DD2
						+ " " + "'" + ipVal + "'";
			} else if (isCriteriaString(DD1)
					&& $("#s2 option:selected").text() == 'Start Like') {
				newdata = data + "\n" + " OR" + " " + alias + DD1 + " " + DD2
						+ " " + "'" + ipVal + "%'";
			} else if (isCriteriaString(DD1)
					&& $("#s2 option:selected").text() == 'End Like') {
				newdata = data + "\n" + " OR" + " " + alias + DD1 + " " + DD2
						+ " " + "'%" + ipVal + "'";
			} else if (isCriteriaString(DD1)
					&& $("#s2 option:selected").text() == 'Middle Like') {
				newdata = data + "\n" + " OR" + " " + alias + DD1 + " " + DD2
						+ " " + "'%" + ipVal + "%'";
			} else if (isCriteriaNumber(DD1) && $('#s2').val() == 'between') {
				newdata = data + "\n" + " OR" + " " + alias + DD1 + " BETWEEN "
						+ $('#from').val() + " AND " + $('#to').val();
			} else if (isCriteriaDate(DD1) && $('#s2').val() == 'Fromto') {
				newdata = data + "\n" + " OR" + " " + alias + DD1 + " BETWEEN "
						+ "'" + $('#fromdate').val() + "'" + " AND " + "'"
						+ $('#todate').val() + "'";
			} else if (isCriteriaNumber(DD1) && $('#s2').val() == 'IN') {
				newdata = data + "\n" + " OR" + " " + alias + DD1 + " IN "
						+ "(" + ipVal + ")";
			} else if (isCriteriaNumber(DD1)) {
				newdata = data + "\n" + " OR" + " " + alias + DD1 + " " + DD2
						+ " " + ipVal;
			} else {
				newdata = data + "\n" + " OR" + " " + alias + DD1 + " " + DD2
						+ " " + "'" + ipVal + "'";

			}
			$("#criteriaTextArea").html(newdata);
		}
	} else {
		alert("Enter Data for before Criteria");
	}
}

function isCriteriaNumber(DD1) {
	var response = JSON.parse(localStorage.getItem("dbColumns"));
	var flag = false;
	$.each(response, function(i, value) {
		if (DD1 == value.name) {
			flag = isNumber(value);
		}
	});
	return flag;
}

function isCriteriaDate(DD1) {
	var response = JSON.parse(localStorage.getItem("dbColumns"));
	var flag = false;
	$.each(response, function(i, value) {
		if (DD1 == value.name) {
			flag = isDate(value);
		}
	});
	return flag;
}

function isCriteriaString(DD1) {
	var response = JSON.parse(localStorage.getItem("dbColumns"));
	var flag = false;
	$.each(response, function(i, value) {
		if (DD1 == value.name) {
			flag = isString(value);
		}
	});
	return flag;
}

function hideDivs() {
	document.getElementById('fromToDiv').style.display = 'none';
	document.getElementById('iptextdateDiv').style.display = 'none';
	document.getElementById('betweenDiv').style.display = 'none';
}

function validateSelectedValue() {
	var response = JSON.parse(localStorage.getItem("dbColumns"));
	var selectedValue = $('#s1').val();
	$('#s2').val('');
	$('#iptext').val('');
	$
			.each(
					response,
					function(i, value) {
						if (selectedValue == value.name) {
							if (isNumber(value)) {
								hideDivs();
								document.getElementById('iptext').style.display = 'block';
								addNumberSymbol();
							} else if (isString(value)) {
								hideDivs();
								document.getElementById('iptext').style.display = 'block';
								addStringSymbol();
							} else if (isDate(value)) {
								document.getElementById('iptextdateDiv').style.display = 'block';
								document.getElementById('iptext').style.display = 'none';
								document.getElementById('betweenDiv').style.display = 'none';
								addDateSymbol();
							}
						}
					});

}

function addDateSymbol() {

	$('#s2').empty().append($('<option>', {
		value : '=',
		text : '='
	}));
	$('#s2').append($('<option>', {
		value : '>',
		text : '>'
	}));
	$('#s2').append($('<option>', {
		value : '<',
		text : '<'
	}));
	$('#s2').append($('<option>', {
		value : '>=',
		text : '>='
	}));
	$('#s2').append($('<option>', {
		value : '<=',
		text : '<='
	}));
	$('#s2').append($('<option>', {
		value : 'Fromto',
		text : 'From to'
	}));
}

function addNumberSymbol() {

	$('#s2').empty().append($('<option>', {
		value : '=',
		text : '='
	}));
	$('#s2').append($('<option>', {
		value : '>',
		text : '>'
	}));
	$('#s2').append($('<option>', {
		value : '<',
		text : '<'
	}));
	$('#s2').append($('<option>', {
		value : '>=',
		text : '>='
	}));
	$('#s2').append($('<option>', {
		value : '<=',
		text : '<='
	}));
	$('#s2').append($('<option>', {
		value : 'IN',
		text : 'In'
	}));
	$('#s2').append($('<option>', {
		value : 'between',
		text : 'between'
	}));
}

function addStringSymbol() {
	$('#s2').empty().append($('<option>', {
		value : 'like',
		text : 'Like'
	}));
	$('#s2').append($('<option>', {
		value : 'like',
		text : 'Start Like'
	}));
	$('#s2').append($('<option>', {
		value : 'like',
		text : 'End Like'
	}));
	$('#s2').append($('<option>', {
		value : 'like',
		text : 'Middle Like'
	}));
}

function isDate(value) {
	var isDate = false;
	if ('DATE' == value.dataType) {
		isDate = true;
	}
	return isDate;
}

function isNumber(value) {
	var isNumber = false;
	if ('decimal' == value.dataType) {
		isNumber = true;
	} else if ('int' == value.dataType) {
		isNumber = true;
	} else if ('FLOAT' == value.dataType) {
		isNumber = true;
	} else if ('NUMBER' == value.dataType) {
		isNumber = true;
	}
	return isNumber;
}

function isString(value) {
	var isString = false;

	if (value && value.required == 'M') {
		isString = false;
	} else if ('VARCHAR2' == value.dataType) {
		isString = true;
	} else if ('CHAR' == value.dataType) {
		isString = true;
	}
	return isString;
}
function checkMandatory(value) {
	var isString = false;

	if (value && value.required != 'M') {
		isString = true;
	} else {
		isString = false;
	}
	return isString;
}
function validateRequiredFields(e) {
	if ($("#table_select option:selected").val() == "") {
		$("#message").html("Please select the table");
		$("#table_select").addClass("table_select_error");
	} else {
		$("#message").html("");
	}
}

function isAggregate(value) {
	var isAggregate = false;
	if (value.includes('AVG') || value.includes('MIN') || value.includes('MAX')) {
		isAggregate = true;
	}
	return isAggregate;
}

function submitCf(e) {
	validateRequiredFields(e);
	if ($('#table_select').val() && $('#drag').text()) {
		var json = formatData();
		var jsonObject = JSON.parse(json);
		var parameter = jsonObject['parameter'];
		/*
		 * if (isAggregate(parameter) && !$('#groupby').is(':checked')) {
		 * $("#drag_message").html("Please select Group by.."); return false; }
		 */
		var url = 'getTableData';
		$
				.ajax({
					type : "POST",
					url : url,
					data : json,
					dataType : "json",
					contentType : "application/json; charset=utf-8",
					error : function(xhr, status, error) {
						closeModal();
						var err = xhr.responseText;
						alert("Session Time Out");
						if (err.toLowerCase().indexOf("session_timed_out") >= 0) {
							window.location = "login.html?statusCheck=SessionExpired";
						}
					},
					success : function(response) {
						closeModal();
						$('#serverValidation').empty();
						$('#serverValidation').hide();
						if (response.length > 0 && response[0].appMessage) {
							var divContainer = document.getElementById("menu4");
							divContainer.innerHTML = "";
							$.each(response[0].appMessage,
									function(i, message) {
										$('#serverValidation').show();
										$('#serverValidation').append(
												"<i class='glyphicon glyphicon-warning-sign'></i>&nbsp;&nbsp;<span>"
														+ message.description
														+ "</span><br/>");
									});
						} else {
							$('.favourite').show();
							CreateTableFromJSON(response);
							$('.report_field_table')
									.DataTable(
											{
												dom : 'Bfrtip',
												buttons : [
														{
															text : '<i class="fa fa-file-excel-o"></i> Excel',
															action : function(
																	e, dt,
																	node,
																	config) {
																e.preventDefault();
																//value="excel";
																//click :createQueue(value);
																$("#excelForm").submit();
															}
														},
														{
															text : '<i class="fa fa-file-text-o"></i> CSV',
															action : function(
																	e, dt,
																	node,
																	config) {
																e.preventDefault();
																//value="csv";
																//click :createQueue(value);
																$("#csvForm").submit();
															}
														},
														{
															text : '<i class="fa fa-file-text-o"></i> CSV PIPE',
															action : function(
																	e, dt,
																	node,
																	config) {
																e.preventDefault();
																//value="csvpipe";
																//click :createQueue(value);
																$("#pipecsvForm").submit();
															}
														},
														{
															text : '<i class="fa fa-file-pdf-o"></i> TEXT',
															action : function(
																	e, dt,
																	node,
																	config) {
																e.preventDefault();
																//value="text";
																//click :createQueue(value);
																$("#pdfForm").submit();
															}
														},
														{
															extend : 'copyHtml5',
															text : '<i class="fa fa-file"></i> Copy',
															titleAttr : 'Copy'
														} ]
											});
						}
					},
					beforeSend : function() {
						$("#drag_message").html("");
						if ($("#drag").hasClass("table_select_error"))
							$("#drag").removeClass("table_select_error");
						if ($("#table_select").hasClass("table_select_error"))
							$("#table_select")
									.removeClass("table_select_error");
						openModal();
					},
				});

	} else {
		if (!$('#table_select').val()) {
		} else if (!$('#drag').text()) {
			$("#drag_message").html("Please select the columns");
			$("#drag").addClass("table_select_error");
		}
	}
}

function openModal() {
	document.getElementById('modal').style.display = 'block';
	document.getElementById('fade').style.display = 'block';
}

function closeModal() {
	document.getElementById('modal').style.display = 'none';
	document.getElementById('fade').style.display = 'none';
}

function formatData() {
	var parameter = "", columnNames = "", checkBox = "";
	$('a', $('#drag')).each(function() {
		if (parameter != "") {
			parameter = parameter + $(this).attr('value') + ",";
			columnNames = columnNames + $(this).text() + ",";
		} else {
			parameter = $(this).attr('value') + ",";
			columnNames = columnNames + $(this).text() + ",";
		}
	});
	if ($('#groupby').is(':checked')) {
		$('.groupbycolumns').each(function() {
			if (checkBox != "") {
				checkBox = checkBox + $(this).attr("title") + ",";
			} else {
				checkBox = $(this).attr("title") + ",";
			}
		});
	}
	var criteria = $("#criteriaTextArea").val(), table = $("#table_select")
			.val();
	var jsonObject = {};
	jsonObject["table"] = table;
	jsonObject["parameter"] = parameter;
	jsonObject["columnNames"] = columnNames;
	jsonObject["query"] = criteria;
	jsonObject["groupby"] = checkBox;
	var json = JSON.stringify(jsonObject);
	$('#column').val(columnNames);
	$("#pdf").val(json);
	$("#excel").val(json);
	$(".csv").val(json);
	return json;
}

var col = [], colCode = [];

function CreateTableFromJSON(results) {
	// var columns = JSON.parse(localStorage.getItem("dbColumns"));
	col = [];
	$.each(results[0].names, function(i, names) {
		if (col.indexOf(names.name) === -1) {
			col.push(names.name);
			// colCode.push(getAppLabelCode(results[0].appLabels,names.name));
		}
	});

	// CREATE DYNAMIC TABLE.
	var table = document.createElement("table");
	var thead = table.createTHead();
	table.classList.add("report_field_table", "display", "nowrap");
	var tblBody = document.createElement("tbody");
	table.appendChild(tblBody);

	var tr = thead.insertRow(-1);
	for (var i = 0; i < col.length; i++) {
		var th = document.createElement("th"); // TABLE HEADER.
		th.innerHTML = col[i];
		tr.appendChild(th);
	}

	for (var i = 0; i < results[0].names.length; i++) {
		tr = tblBody.insertRow(-1);
		for (var j = 0; j < col.length; j++) {
			var tabCell = tr.insertCell(-1);
			if ((col.length - 1) != j)
				tabCell.innerHTML = results[0].names[i++].field;
			if ((col.length - 1) == j)
				tabCell.innerHTML = results[0].names[i].field;
		}
	}

	// FINALLY ADD THE NEWLY CREATED TABLE WITH JSON DATA TO A CONTAINER.
	var divContainer = document.getElementById("menu4");
	divContainer.innerHTML = "";
	divContainer.appendChild(table);
}

function createPdf() {
	if ($('#table_select').val() && $('#drag').text()) {
		var json = formatData();
		$.ajax({
			type : "POST",
			url : 'createPdf',
			data : json,
			processData : false,
			contentType : "application/json; charset=utf-8",
			error : function(xhr, status, error) {
				closeModal();
			},
			success : function(response) {
				closeModal();
			},
			beforeSend : function() {
				openModal();
			},
		});
	} else {
		if (!$('#table_select').val()) {
			alert("please select the table");
		} else if (!$('#drag').text()) {
			alert("please select the column");
		}
	}

}

function saveByteArray(pdfName, byte) {
	var blob = new Blob([ byte ], {
		type : "application/pdf"
	});
	var link = document.createElement('a');
	link.href = window.URL.createObjectURL(blob);
	var fileName = pdfName + ".pdf";
	link.download = fileName;
	link.click();
};

function callMetaDataTable() {
	$("#message").html("");
	$("#grag_message").html("");
	if ($("#table_select").hasClass('table_select_error')) {
		$("#table_select").removeClass("table_select_error");
	}
	if ($("#drag").hasClass('table_select_error')) {
		$("#drag").removeClass("table_select_error");
	}
	$.ajax({
		type : "GET",
		url : 'tables',
		dataType : "json",
		contentType : "application/json; charset=utf-8",
		error : function(xhr, status, error) {
			closeModal();
		},
		success : function(response) {
			closeModal();
			$('#table_select').empty();
			$.each(response, function(i, value) {
				$('#table_select').append(
						$('<option>').text(
								getAppLabel(response, value.tableName)).attr(
								'value', value.tableName));
			});
			retriveColumnAjaxCall();
		},
		beforeSend : function() {
			openModal();
		},
	});
}

function retriveColumnAjaxCall(tablename) {
	columnbox();
	$("#columns").empty();
	$
			.ajax({
				type : "POST",
				url : 'retriveColumns',
				dataType : "json",
				data : tablename,
				contentType : "application/json; charset=utf-8",
				error : function(xhr, status, error) {
					closeModal();
				},
				beforeSend : function() {
					if ($("#table_select").hasClass("table_select_error"))
						$("#table_select").removeClass("table_select_error");
					openModal();
				},
				success : function(response) {
					clearAllFields();
					closeModal();
					localStorage.setItem("dbColumns", JSON.stringify(response));
					$
							.each(
									response,
									function(i, value) {

										/*
										 * if(isNumber(value)){
										 * $('#columns').append($("<li class = 'active'>").append($("<a
										 * class='btn btn-success'
										 * data-toggle='collapse'
										 * aria-expanded='false'>").text(getAppLabel(response,
										 * value.name)).attr({
										 * href:'#'+value.name, value :
										 * value.name, //onclick
										 * :'moveButton(this)', //id : "drag" +
										 * i, })).append($("<ul class='collapse list-unstyled'>").attr('id',value.name)
										 * .append($("<li>") .append($("<a
										 * class='btn
										 * btn-success'>").text(getAppLabel(response,
										 * value.name)).attr({value:value.name,onclick
										 * :'moveButton(this)'}) )) .append($("<li>")
										 * .append($("<a class='btn
										 * btn-success'>").text('AVG
										 * ('+getAppLabel(response,
										 * value.name)+')').attr({value:'AVG('+value.name+')
										 * As '+value.name,onclick
										 * :'moveButton(this)'}) )) .append($("<li>")
										 * .append($("<a class='btn
										 * btn-success'>").text('MIN
										 * ('+getAppLabel(response,
										 * value.name)+')').attr({value:'MIN('+value.name+')
										 * As '+value.name,onclick
										 * :'moveButton(this)'}) )) .append($("<li>")
										 * .append($("<a class='btn
										 * btn-success'>").text('MAX
										 * ('+getAppLabel(response,
										 * value.name)+')').attr({value:'MAX('+value.name+')
										 * As '+value.name,onclick
										 * :'moveButton(this)'}) )) )); }else{
										 */
										$('#columns')
												.append(
														$(
																"<li class = 'active'>")
																.append(
																		$(
																				"<a class='btn btn-success' data-toggle='collapse'>")
																				.text(
																						getAppLabel(
																								response,
																								value.name))
																				.attr(
																						{
																							href : '#'
																									+ value.name,
																							value : value.name,
																							onclick : 'moveButton(this)',
																							id : "drag"
																									+ i,
																						})));

										/* } */

										// ADD ALL COLUMNS TO THE SELECT OPTIONS
										// FOR CRITERIA
										if (isRequired(value)) {
											$('#s1')
													.append(
															$(
																	'<option></option>')
																	.text(
																			getAppLabel(
																					response,
																					value.name))
																	.attr(
																			{
																				value : value.name,
																				style : 'color:red;',
																			}));
										} else if (isString(value)) {
											$('#s1')
													.append(
															$(
																	'<option></option>')
																	.text(
																			getAppLabel(
																					response,
																					value.name))
																	.attr(
																			{
																				value : value.name,
																			/*
																			 * style
																			 * :'color:#919be6;'
																			 */
																			}));
										} else if (checkMandatory(value)) {
											$('#s1')
													.append(
															$(
																	'<option></option>')
																	.text(
																			getAppLabel(
																					response,
																					value.name))
																	.attr(
																			{
																				value : value.name,
																			}));
										}
									});
					validateSelectedValue();
					openNav();
				},

			});
}

function moveButton(elem) {
	var regExp = /\(([^)]+)\)/;
	var matches = regExp.exec($(event.target).text());
	if ($(elem).parent().attr("id") == "drag" && matches == null) {
		$($("<li class = 'active'>").append(elem)).detach()
				.appendTo('#columns');
	} else if ($(elem).parent().attr("id") == "drag" && matches != null) {
		$($("<li class = ''>").append(elem)).detach()
				.appendTo('#' + matches[1]);
	} else {
		$(elem).detach().appendTo('#drag');

	}
}

function getAppLabel(response, value) {
	var appLabel = value;
	$.each(response[0].appLabels, function(i, label) {
		if (label && label.labelCode == appLabel)
			appLabel = label.appLabel;
		return appLabel;
	});
	return appLabel;
}
/*
 * function getAppLabelCode(response, value) { var appLabel = value;
 * $.each(response, function(i, label) { if (label && label.appLabel ==
 * appLabel){ appLabel = label.labelCode; return label.labelCode; } }); return
 * appLabel; }
 */
function isRequired(value) {
	var isActive = false;
	if (value && value.required == 'Y') {
		return isActive = true;
	}
	return isActive;
}

function clearAllFields() {
	var divCoulmnsContainer = document.getElementsByClassName("table_field");
	for (var i = 0; i < divCoulmnsContainer.length; i++) {
		divCoulmnsContainer[i].innerHTML = "";
	}
	document.getElementById("criteriaTextArea").innerHTML = "";
	var divContainer = document.getElementById("menu4");
	divContainer.innerHTML = "";

	var table = $('.report_field_table').DataTable();
	table.clear();
	document.getElementById("s1").innerHTML = "";
}

function addBetweenFilter() {
	if ((isCriteriaDate($('#s1').val()) && ($('#s2').val() != "Fromto"))) {
		$('#iptext').hide();
		$('#fromToDiv').hide();
		$('#iptextdateDiv').show();
	} else if ($('#s2').val() == "between") {
		$('#iptextdateDiv').hide();
		$('#iptext').hide();
		$('#betweenDiv').show();
	} else if ($('#s2').val() == "Fromto") {
		$('#iptextdateDiv').hide();
		$('#iptext').hide();
		$('#fromToDiv').show();
	} else {
		$('#iptextdateDiv').hide();
		$('#iptext').show();
		$('#betweenDiv').hide();
		$('#iptext').show();
		$('#fromToDiv').hide();
	}
}

function groupBy(e) {
	if ($(e.target).is(':checked')) {
		$('a', $('#drag')).each(
				function() {
					if (!isAggregate($(this).attr('value'))) {
						var html = '<span class="groupbycolumns" title="'
								+ $(this).attr('value') + '"/>';
						$('#groupby').append(html);
					}
				});
	}
}

function saveFavouriteQuery(e) {
	var obj = JSON.parse($('#pdf').val());
	obj.qryTitle = $('#qryTitle').val();
	obj.purpose = $('#purpose').val();
	obj.description = $('#description').val();
	var json = JSON.stringify(obj);
	$.ajax({
		type : 'POST',
		url : 'favourite',
		data : json,
		dataType : "json",
		contentType : "application/json; charset=utf-8",
		error : function(xhr, status, error) {
			console.log(xhr.responseText);
			var err = xhr.responseText;
			if (err.toLowerCase().indexOf("session_timed_out") >= 0) {
				window.location = "login.html?statusCheck=SessionExpired";
			}
		},
		beforeSend : function() {

		},
		success : function(response) {
			alert(response.saved);
			$('#saveFavourite').hide();
			$('.modal-backdrop').hide();
		},
		complete : function() {
		}
	});
}

function columnbox() {
	var data = $('#table_select').val();
	if (data) {
		$("#table_field").show();
		$("#table_field1").show();
	} else {
		$("#table_field").hide();
		$("#table_field1").show();
	}
}

function processLoginForm() {
	openModal();
	var name = $('#namkName').val();
	if (name == 'Please Select Bank Name' && $('#tellerid').val() == '501') {
		alert("please select the bank");
		closeModal();
		return false;
	} else {
		encryptpwd();
		$("#loginform1").submit();
		return true;
	}

}

function hideBranchDiv(e) {
	if ($('#selectall').val() == 'isSelectall'
			|| $('#consolidated').val() == 'isConsolidated') {
		$('.branchdiv').hide();
		$('.branchrangediv').hide();

	} else {
		$('.branchdiv').show();
	}
}

function addRangeDiv(e) {
	if ($('#rangeid').val() == 'isRangetrue') {
		$('.branchrangediv').show();
		$('.branchdiv').show();

	} else {

		$('#branchcd').attr('value', '');
		$('.branchrangediv').hide();
	}
	document.getElementById('tobranchcddiv').style.display = 'block';
}

function submitstaticForm() {
	if (!$('#bankcd').val() == '') {
		if ($('#selectall').is(':checked')) {
			$('#errMessage').html("");
			$('#branchcd').val('');
		}
		if ($('#branchcd').val() == '' && !$('#selectall').is(':checked')
				&& !$('#consolidated').is(':checked')) {
			$('#errMessage').html('*Please enter branch code');
			return false;
		}

		if ($('#rangeid').is(':checked')) {
			if ($('#tobranchcd').val() == '') {
				$('#errMessage').html("*Please enter tobranch code");
				return false;
			}
		}

		if (($('#textid').is(':checked')) || ($('#pdfid').is(':checked'))) {
			$('#errMessage').html("");
			$("#staticformid").submit();
		} else {
			$('#errMessage').html("*Please select file format");
		}
	}
	$('#staticformid').each(function() {
		if ($('#bankcd').val() == '') {
			this.reset();
		}
	});
}

function getViewDetails(e) {
	$.ajax({
		type : "POST",
		url : 'viewDeatils',
		processData : false,
		contentType : "application/json; charset=utf-8",
		error : function(xhr, status, error) {
		},
		success : function(response) {
			for (var i = 0; i < response.length; i++) {
				$('#viewdtls').append(
						'<tr><td>'
								+ getAppLabel(response, response[i].viewName) /* response[i].viewName */
								+ '</td><td>' + response[i].description
								+ '</td>');
			}
		},
		beforeSend : function() {
		},
	});
}

String.prototype.pad = function(_char, len, to) {
	if (!this || !_char || this.length >= len) {
		return this;
	}
	to = to || 0;

	var ret = this;

	var max = (len - this.length) / _char.length + 1;
	while (--max) {
		ret = (to) ? ret + _char : _char + ret;
	}

	return ret;
};
function createCsv() {

}

function encryptpwd()
{
	var val=$("#pwd").val();
	var rkEncryptionKey = CryptoJS.enc.Base64.parse('u/Gu5posvwDsXUnV5Zaq4g==');
	 var rkEncryptionIv = CryptoJS.enc.Base64.parse('5D9r9ZVzEYYgha93/aUK2w==');
	 var utf8Stringified = CryptoJS.enc.Utf8.parse(val);
	 var encrypted = CryptoJS.AES.encrypt(utf8Stringified.toString(), rkEncryptionKey, 
	{mode: CryptoJS.mode.CBC, padding: CryptoJS.pad.Pkcs7, iv: rkEncryptionIv});
	 $("#pwd").val(encrypted.ciphertext.toString(CryptoJS.enc.Base64))
}

function createQueue(value){
	if ($('#table_select').val() && $('#drag').text()){
		
		var json; var obj;
		
		if(value=="excel"){
			  obj = JSON.parse($('#excel').val());
			  obj["reporttype"] = "xls";
		}else if(value=="csv"){
			 obj = JSON.parse($('.csv').val());
			 obj["reporttype"] = "csv";
		}else if(value=="csvpipe"){
			 obj = JSON.parse($('.csv').val());
			 obj["reporttype"] = "csvpipe";
		}else if(value=="text"){
			 obj = JSON.parse($('#pdf').val());
			 obj["reporttype"] = "text";
		}
		    json=JSON.stringify(obj);
		    
		$.ajax({
			type : "POST",
			url : 'createQueue',
			data : json,
			dataType : "json",
			contentType : "application/json; charset=utf-8",
			error : function(xhr, status, error) {
				closeModal();
			},
			success : function(response) {
				closeModal();
				alert(response.msg);
			},
			beforeSend : function() {
				openModal();
			},
		});
	} else {
		if (!$('#table_select').val()) {
			alert("please select the table!");
		} else if (!$('#drag').text()) {
			alert("please select the column!");
		}
	}
}
