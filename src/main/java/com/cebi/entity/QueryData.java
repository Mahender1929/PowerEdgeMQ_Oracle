package com.cebi.entity;

import static javax.persistence.GenerationType.IDENTITY;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "cesys003")
public class QueryData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4155788594048164116L;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id")
	private Integer id;

	@Column(name = "parameter")
	private String parameter;

	@Column(name = "columnnames")
	private String columnNames;

	@Column(name = "qry")
	private String query;

	@Column(name = "tbl")
	private String table;

	@Column(name = "groupby")
	private String groupby;

	@Column(name = "curr_date")
	private String currentDate;

	@Column(name = "final_qry")
	private String finalQry;

	@Column(name = "bank_code")
	private String bankCode;
	@Transient
	private String reportDetails;

	@Column(name = "qry_title")
	private String qryTitle;

	@Column(name = "description")
	private String description;

	@Column(name = "purpose")
	private String purpose;
	@Transient
	private String reporttype = "Simple";
	@Transient
	private int reportDataId;

	public String getQryTitle() {
		return qryTitle;
	}

	public void setQryTitle(String qryTitle) {
		this.qryTitle = qryTitle;
	}

	public String getPurpose() {
		return purpose;
	}

	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getParameter() {
		return parameter;
	}

	public void setParameter(String parameter) {
		this.parameter = parameter;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public String getReportDetails() {
		return reportDetails;
	}

	public void setReportDetails(String reportDetails) {
		this.reportDetails = reportDetails;
	}

	public String getColumnNames() {
		return columnNames;
	}

	public void setColumnNames(String columnNames) {
		this.columnNames = columnNames;
	}

	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public String getGroupby() {
		return groupby;
	}

	public void setGroupby(String groupby) {
		this.groupby = groupby;
	}

	public String getCurrentDate() {
		return currentDate;
	}

	public void setCurrentDate(String currentDate) {
		this.currentDate = currentDate;
	}

	public String getFinalQry() {
		return finalQry;
	}

	public void setFinalQry(String finalQry) {
		this.finalQry = finalQry;
	}

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getReporttype() {
		return reporttype;
	}

	public void setReporttype(String reporttype) {
		this.reporttype = reporttype;
	}

	public int getReportDataId() {
		return reportDataId;
	}

	public void setReportDataId(int reportDataId) {
		this.reportDataId = reportDataId;
	}

}
