/*******************************************************************************
 * Copyright (C) 2017. TIBCO Software Inc. 
 * All Rights Reserved. Confidential & Proprietary.
 ******************************************************************************/
package com.jaspersoft.studio.tools.highcharts;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.fasterxml.jackson.annotation.JsonSetter;

/**
 * Bean representing a property read from the Highcharts JSON model file.
 * 
 * @author Massimo Rabbi (mrabbi@users.sourceforge.net)
 *
 */
@JsonIgnoreProperties(value={
		"extends","isParent","seeAlso","context","demo","deprecated"})
public class HighchartsJsonProperty implements Cloneable {

	private String name;
	private String returnType;
	@JsonProperty(access=Access.WRITE_ONLY)
	private String since;
	private String description;
	private String fullname;
	private String title;
	private String parent;
	@JsonProperty(access=Access.WRITE_ONLY)
	private List<String> products;
	private String defaults;
	private String values;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getReturnType() {
		return returnType;
	}

	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}

	public String getSince() {
		return since;
	}

	public void setSince(String since) {
		this.since = since;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getFullname() {
		return fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@JsonGetter(value = "parent")
	public String getParent() {
		return parent;
	}

	@JsonSetter(value = "parent")
	public void setParent(String parent) {
		this.parent = parent;
	}

	public List<String> getProducts() {
		return products;
	}

	public void setProducts(List<String> products) {
		this.products = products;
	}

	public String getDefaults() {
		return defaults;
	}

	public void setDefaults(String defaults) {
		this.defaults = defaults;
	}

	public String getValues() {
		return values;
	}

	public void setValues(String values) {
		this.values = values;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}
