/*******************************************************************************
 * Copyright (C) 2017. TIBCO Software Inc. 
 * All Rights Reserved. Confidential & Proprietary.
 ******************************************************************************/
package com.jaspersoft.studio.tools.highcharts;

import org.apache.commons.lang3.StringUtils;

/**
 * Filter to get rid of possible useless/not allowed Highcharts properties.
 * 
 * @author Massimo Rabbi (mrabbi@users.sourceforge.net)
 *
 */
public class JsonPropertyFilter {

	public enum FilterType {
		CONTAINS, STARTS_WITH, ENDS_WITH, MAJOR, MAJOR_EQ, EQUALS, MINOR, MINOR_EQ
	}

	private FilterType type;
	private String text;
	private boolean discardEmptyNull;

	public JsonPropertyFilter(String text, FilterType type) {
		this.type = type;
		this.text = text;
		this.discardEmptyNull = false;
	}
	
	public JsonPropertyFilter(String text, FilterType type, boolean considerEmptyNull) {
		this.type = type;
		this.text = text;
		this.discardEmptyNull = considerEmptyNull;
	}

	public FilterType getType() {
		return type;
	}

	public void setType(FilterType type) {
		this.type = type;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public boolean isDiscardEmptyNull() {
		return discardEmptyNull;
	}

	public void setDiscardEmptyNull(boolean considerEmptyNull) {
		this.discardEmptyNull = considerEmptyNull;
	}
	
	/**
	 * Applies the filter to the specified text item.
	 * 
	 * @param itemText the text possibly interested by the filtering
	 * @return <code>true</code> if the applied filter is coherent, <code>false</code> otherwise
	 */
	public boolean applyFilterTo(String itemText) {
		if(StringUtils.isEmpty(itemText)){
			return !isDiscardEmptyNull();
		}
		else{
			String conditionStr = getText();
			int compareResult = itemText.compareTo(conditionStr);
			switch (getType()) {
				case CONTAINS:
					return itemText.contains(conditionStr);
				case ENDS_WITH:
					return itemText.endsWith(conditionStr);
				case STARTS_WITH:
					return itemText.startsWith(conditionStr);
				case EQUALS:
					return compareResult == 0;
				case MAJOR:
					return compareResult > 0;
				case MAJOR_EQ:
					return compareResult >= 0;
				case MINOR:
					return compareResult < 0;
				case MINOR_EQ:
					return compareResult <= 0;
				default:
					return false;
			}
		}
	}
	
}
