package com.cebi.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "cebiaudithistory")
public class AuditHistory implements java.io.Serializable {

	private static final long serialVersionUID = -6540785696744783149L;
	private Integer id;
	private Integer tellerId;
	private Long branchId;
	private String bankCode;
	private String query;
	private String audDate;
	private String table;
	private String dburl;
	
	@Column(name = "dburl")
	public String getDburl() {
		return dburl;
	}

	public void setDburl(String dburl) {
		this.dburl = dburl;
	}

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "teller_id")
	public Integer getTellerId() {
		return this.tellerId;
	}

	public void setTellerId(Integer tellerId) {
		this.tellerId = tellerId;
	}

	@Column(name = "branch_id")
	public Long getBranchId() {
		return this.branchId;
	}

	public void setBranchId(Long branchId) {
		this.branchId = branchId;
	}

	@Column(name = "bank_code", length = 10)
	public String getBankCode() {
		return this.bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	@Column(name = "query", length = 4000)
	public String getQuery() {
		return this.query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	@Column(name = "aud_date", length = 10)
	public String getAudDate() {
		return this.audDate;
	}

	public void setAudDate(String audDate) {
		this.audDate = audDate;
	}

	@Column(name = "table_nm")
	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}

}
