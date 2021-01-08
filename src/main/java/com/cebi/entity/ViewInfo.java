package com.cebi.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "cesys005")
public class ViewInfo {

	@Id
	@Column(name = "id")
	private Integer id;
	@Column(name = "view_name")
	private String viewName;
	@Column(name = "view_")
	private String view;
	@Column(name = "status")
	private String status;
	@Column(name = "type")
	private String type;
	@Column(name = "access_")
	private Integer access;
	@Column(name = "description")
	private String description;
	
	@Transient
	private List<ApplicationLabel> appLabels=new ArrayList<>();

	public ViewInfo() {
		super();
	}

	public ViewInfo(Integer id, String viewName, String view, String status, String type, Integer access, String description) {
		super();
		this.id = id;
		this.viewName = viewName;
		this.view = view;
		this.status = status;
		this.type = type;
		this.access = access;
		this.description = description;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getViewName() {
		return viewName;
	}

	public void setViewName(String viewName) {
		this.viewName = viewName;
	}

	public String getView() {
		return view;
	}

	public void setView(String view) {
		this.view = view;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Integer getAccess() {
		return access;
	}

	public void setAccess(Integer access) {
		this.access = access;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<ApplicationLabel> getAppLabels() {
		return appLabels;
	}

	public void setAppLabels(List<ApplicationLabel> appLabels) {
		this.appLabels = appLabels;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((view == null) ? 0 : view.hashCode());
		result = prime * result + ((access == null) ? 0 : access.hashCode());
		result = prime * result + ((viewName == null) ? 0 : viewName.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ViewInfo other = (ViewInfo) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		if (view == null) {
			if (other.view != null)
				return false;
		} else if (!view.equals(other.view))
			return false;
		if (viewName == null) {
			if (other.viewName != null)
				return false;
		} else if (!viewName.equals(other.viewName))
			return false;
		if (access == null) {
			if (other.access != null)
				return false;
		}
		if (description == null) {
			if (other.description != null) {
				return false;
			}
		}
		return true;
	}

	@Override
	public String toString() {
		return "ViewInfo [id=" + id + ", viewName=" + viewName + ", view=" + view + ", status=" + status + ", type=" + type + ", access=" + access + ", description=" + description + "]";
	}
}
