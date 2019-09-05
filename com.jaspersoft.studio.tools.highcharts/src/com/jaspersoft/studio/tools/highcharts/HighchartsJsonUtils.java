/*******************************************************************************
 * Copyright (C) 2017. TIBCO Software Inc. 
 * All Rights Reserved. Confidential & Proprietary.
 ******************************************************************************/
package com.jaspersoft.studio.tools.highcharts;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jaspersoft.studio.tools.highcharts.JsonPropertyFilter.FilterType;

public class HighchartsJsonUtils {
	
	// Logger
	private static final Logger LOGGER = LoggerFactory.getLogger(HighchartsJsonUtils.class);
	
	// Default file location for the JSON configuration file
	private static final String DEFAULT_JSON_LOCATION = "/highcharts-configuration.json";
	
	// List of filters to be applied to the properties list in order
	// to clean out un-wanted elements
	private static List<JsonPropertyFilter> pFilters;
	
	static {
		pFilters = new ArrayList<>();
		pFilters.add(new JsonPropertyFilter("global",FilterType.STARTS_WITH));
		pFilters.add(new JsonPropertyFilter("lang",FilterType.STARTS_WITH));
		pFilters.add(new JsonPropertyFilter("accessibility",FilterType.STARTS_WITH));
		pFilters.add(new JsonPropertyFilter("annotations",FilterType.STARTS_WITH));
		pFilters.add(new JsonPropertyFilter("boost",FilterType.STARTS_WITH));
		pFilters.add(new JsonPropertyFilter("data",FilterType.STARTS_WITH));
		pFilters.add(new JsonPropertyFilter("defs",FilterType.STARTS_WITH));
		pFilters.add(new JsonPropertyFilter("drilldown",FilterType.STARTS_WITH));
		pFilters.add(new JsonPropertyFilter("exporting",FilterType.STARTS_WITH));
		pFilters.add(new JsonPropertyFilter("drilldown",FilterType.STARTS_WITH));
		pFilters.add(new JsonPropertyFilter("loading",FilterType.STARTS_WITH));
		pFilters.add(new JsonPropertyFilter("navigation",FilterType.STARTS_WITH));
		pFilters.add(new JsonPropertyFilter("noData",FilterType.STARTS_WITH));
		pFilters.add(new JsonPropertyFilter("pane",FilterType.STARTS_WITH));
		pFilters.add(new JsonPropertyFilter("responsive",FilterType.STARTS_WITH));		
		pFilters.add(new JsonPropertyFilter("series",FilterType.STARTS_WITH));
		pFilters.add(new JsonPropertyFilter("time",FilterType.STARTS_WITH));
		pFilters.add(new JsonPropertyFilter("zAxis",FilterType.STARTS_WITH));
		pFilters.add(new JsonPropertyFilter("chart.options3d", FilterType.STARTS_WITH));
		pFilters.add(new JsonPropertyFilter("position3d", FilterType.ENDS_WITH));
		pFilters.add(new JsonPropertyFilter("chart.parallelCoordinates", FilterType.STARTS_WITH));
		pFilters.add(new JsonPropertyFilter("chart.parallelAxes", FilterType.STARTS_WITH));
		
		// requires "series-label.js" module
		pFilters.add(new JsonPropertyFilter("plotOptions.area.label", FilterType.STARTS_WITH));
		pFilters.add(new JsonPropertyFilter("plotOptions.areaspline.label", FilterType.STARTS_WITH));
		pFilters.add(new JsonPropertyFilter("plotOptions.bar.label", FilterType.STARTS_WITH));
		pFilters.add(new JsonPropertyFilter("plotOptions.bubble.label", FilterType.STARTS_WITH));
		pFilters.add(new JsonPropertyFilter("plotOptions.column.label", FilterType.STARTS_WITH));
		pFilters.add(new JsonPropertyFilter("plotOptions.heatmap.label", FilterType.STARTS_WITH));
		pFilters.add(new JsonPropertyFilter("plotOptions.line.label", FilterType.STARTS_WITH));
		pFilters.add(new JsonPropertyFilter("plotOptions.pie.label", FilterType.STARTS_WITH));
		pFilters.add(new JsonPropertyFilter("plotOptions.scatter.label", FilterType.STARTS_WITH));
		pFilters.add(new JsonPropertyFilter("plotOptions.series.label", FilterType.STARTS_WITH));
		pFilters.add(new JsonPropertyFilter("plotOptions.spline.label", FilterType.STARTS_WITH));
		pFilters.add(new JsonPropertyFilter("plotOptions.treemap.label", FilterType.STARTS_WITH));
		
		// not "implemented" types
		pFilters.add(new JsonPropertyFilter("plotOptions.areasplinerange", FilterType.STARTS_WITH));
		pFilters.add(new JsonPropertyFilter("plotOptions.arearange", FilterType.STARTS_WITH));
		pFilters.add(new JsonPropertyFilter("plotOptions.bellcurve", FilterType.STARTS_WITH));
		pFilters.add(new JsonPropertyFilter("plotOptions.boxplot", FilterType.STARTS_WITH));
		pFilters.add(new JsonPropertyFilter("plotOptions.bullet", FilterType.STARTS_WITH));
		pFilters.add(new JsonPropertyFilter("plotOptions.columnpyramid", FilterType.STARTS_WITH));
		pFilters.add(new JsonPropertyFilter("plotOptions.columnrange", FilterType.STARTS_WITH));
		pFilters.add(new JsonPropertyFilter("plotOptions.cylinder", FilterType.STARTS_WITH));
		pFilters.add(new JsonPropertyFilter("plotOptions.errorbar", FilterType.STARTS_WITH));
		pFilters.add(new JsonPropertyFilter("plotOptions.funnel", FilterType.STARTS_WITH));
		pFilters.add(new JsonPropertyFilter("plotOptions.gauge", FilterType.STARTS_WITH));
		pFilters.add(new JsonPropertyFilter("plotOptions.histogram", FilterType.STARTS_WITH));
		pFilters.add(new JsonPropertyFilter("plotOptions.networkgraph", FilterType.STARTS_WITH));
		pFilters.add(new JsonPropertyFilter("plotOptions.packedbubble", FilterType.STARTS_WITH));
		pFilters.add(new JsonPropertyFilter("plotOptions.pareto", FilterType.STARTS_WITH));
		pFilters.add(new JsonPropertyFilter("plotOptions.polygon", FilterType.STARTS_WITH));
		pFilters.add(new JsonPropertyFilter("plotOptions.pyramid", FilterType.STARTS_WITH));
		pFilters.add(new JsonPropertyFilter("plotOptions.sankey", FilterType.STARTS_WITH));
		pFilters.add(new JsonPropertyFilter("plotOptions.scatter3d", FilterType.STARTS_WITH));
		pFilters.add(new JsonPropertyFilter("plotOptions.streamgraph", FilterType.STARTS_WITH));
		pFilters.add(new JsonPropertyFilter("plotOptions.sunburst", FilterType.STARTS_WITH));
		pFilters.add(new JsonPropertyFilter("plotOptions.tilemap", FilterType.STARTS_WITH));
		pFilters.add(new JsonPropertyFilter("plotOptions.variablepie", FilterType.STARTS_WITH));
		pFilters.add(new JsonPropertyFilter("plotOptions.variwide", FilterType.STARTS_WITH));
		pFilters.add(new JsonPropertyFilter("plotOptions.vector", FilterType.STARTS_WITH));
		pFilters.add(new JsonPropertyFilter("plotOptions.venn", FilterType.STARTS_WITH));
		pFilters.add(new JsonPropertyFilter("plotOptions.waterfall", FilterType.STARTS_WITH));
		pFilters.add(new JsonPropertyFilter("plotOptions.windbarb", FilterType.STARTS_WITH));
		pFilters.add(new JsonPropertyFilter("plotOptions.wordcloud", FilterType.STARTS_WITH));
		pFilters.add(new JsonPropertyFilter("plotOptions.xrange", FilterType.STARTS_WITH));
	}
	

	// Singleton
	private HighchartsJsonUtils(){
	}
	
	/**
	 * Scans the JSON Highcharts configuration file in order to determine
	 * which are the possible attributes for an Highcharts property.
	 * <p>
	 * 
	 * NOTE: Pay attention to nested objects. Review the list of produced items.
	 * 
	 * @param fileLocation the location of the JSON file 
	 * @return a list of the possible attributes of an Highcharts property
	 */
	public static List<String> listAllPropertyAttributes(URL fileLocation){
		List<String> results = new ArrayList<String>();
		InputStream jsonStream = null;
		try {
			if(fileLocation==null){
				jsonStream = HighchartsJsonUtils.class.getResourceAsStream(DEFAULT_JSON_LOCATION);
			}
			else {		
				HttpURLConnection conn = (HttpURLConnection) fileLocation.openConnection();
				conn.setRequestProperty("User-Agent",
						"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36");
				jsonStream = conn.getInputStream();
			}
			JsonFactory factory = new JsonFactory();
			JsonParser parser = factory.createParser(jsonStream);
			while(!parser.isClosed()){
				JsonToken currToken = parser.nextToken();
				if(JsonToken.FIELD_NAME.equals(currToken)){
					String currFieldName = parser.getCurrentName();
					if(!results.contains(currFieldName)) {
						results.add(currFieldName);
					}
				}
			}
		} catch (IOException e) {
			LOGGER.error("Error occurred while reading the file",e);
		} finally {
			IOUtils.closeQuietly(jsonStream);
		}
		if(!results.isEmpty()){
			LOGGER.info("List of attributes - Pay attention to possible nested objects and review the list.");
			for(String item : results) {
				LOGGER.info(item);
			}
		}
		else {
			LOGGER.warn("No attributes were found scanning the configuration file.");
		}
		return results;
	}
	
	/**
	 * Reads the configuration files and produce a list of property beans representing
	 * different items collected from the JSON file.
	 * 
	 * @param fileLocation the location of the JSON file
	 * @param applySort flag to decide if the items should be sorted
	 * @return the list of properties read from the configuration file
	 */
	public static List<HighchartsJsonProperty> readModelAsBeans(URL fileLocation, boolean applySort){
		List<HighchartsJsonProperty> results = new ArrayList<>();
		InputStream jsonStream = null;
		ObjectMapper objMapper = new ObjectMapper();
		try {
			if(fileLocation==null){
				jsonStream = HighchartsJsonUtils.class.getResourceAsStream(DEFAULT_JSON_LOCATION);
			}
			else {		
				HttpURLConnection conn = (HttpURLConnection) fileLocation.openConnection();
				conn.setRequestProperty("User-Agent",
						"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36");
				jsonStream = conn.getInputStream();
			}
			results = objMapper.readValue(jsonStream, new TypeReference<List<HighchartsJsonProperty>>(){});
			if(applySort){
				Collections.sort(results, new Comparator<HighchartsJsonProperty>() {
					@Override
					public int compare(HighchartsJsonProperty o1, HighchartsJsonProperty o2) {
						return o1.getName().compareTo(o2.getName());
					}
				});			
			}
		} catch (IOException e) {
			LOGGER.error("Error occurred while reading the file",e);
		} finally{
			IOUtils.closeQuietly(jsonStream);
		}
		return results;
	}
	
	/**
	 * Filters the list of properties getting rid of those ones 
	 * that were introduced in later versions compared to the specified one.
	 * 
	 * @param properties the original list of properties
	 * @param version the maximum version allowed
	 * @return the filtered list of properties
	 */	
	public static List<HighchartsJsonProperty> filterPropertiesByVersion(
			List<HighchartsJsonProperty> properties, String version){
		List<HighchartsJsonProperty> result = new ArrayList<HighchartsJsonProperty>();
		JsonPropertyFilter filter = new JsonPropertyFilter(version, FilterType.MINOR_EQ);
		for(HighchartsJsonProperty p : properties){
			if(filter.applyFilterTo(p.getSince())){
				result.add(p);
			}
		}
		return result;
	}
	
	/**
	 * Filters the list of properties getting rid of those ones that are not 
	 * interesting for a possible Highcharts component.
	 * 
	 * @param properties the original list of properties
	 * @return the filtered list of properties
	 */
	public static List<HighchartsJsonProperty> filterUselessProperties(
			List<HighchartsJsonProperty> properties){
		List<HighchartsJsonProperty> result = new ArrayList<>();
		for(HighchartsJsonProperty p : properties){
			if(p.getProducts().contains("highcharts")) {
				String fullname = p.getFullname();
				boolean validProperty = true;
				for(JsonPropertyFilter f:pFilters) {
					if(f.applyFilterTo(fullname)) {
						// at least one filter applied,
						// no need to apply the others
						validProperty = false;
						break;
					}
				}
				if(validProperty) {
					result.add(p);					
				}
			}
		}
		return result;
	}
	
	/**
	 * Writes the list of properties to the specified target JSON file.
	 * 
	 * @param properties the list of properties to write
	 * @param targetFileLocation the target file location
	 */
	public static void writeToJsonFile(List<HighchartsJsonProperty> properties, String targetFileLocation){
		try {
			ObjectMapper objMapper = new ObjectMapper();
			objMapper.writeValue(new File(targetFileLocation), properties);
		} catch (IOException e) {
			LOGGER.error("An error occurred while trying to write the output JSON file",e);
		}
	}
	
	/**
	 * Scans the list of properties and determines which are the return types available.
	 * 
	 * @param properties the list of properties
	 * @return the list of return types found
	 */
	public static List<String> collectReturnTypes(List<HighchartsJsonProperty> properties){
		Set<String> types=new TreeSet<>();
		for(HighchartsJsonProperty p : properties){
			String rt = p.getReturnType();
			if(!StringUtils.isEmpty(rt)){
				types.add(rt);
			}
		}
		List<String> result = new ArrayList<>();
		result.addAll(types);
		return result;
	}
	
	
	public static void main(String args[]) throws MalformedURLException{
		// grab the last available highcharts json file
		//listAllPropertyAttributes(new URL("http://api.highcharts.com/highcharts/option/dump.json"));

		// rely on the shipped one
		listAllPropertyAttributes(null);
		
		// read and filter the properties
		List<HighchartsJsonProperty> properties = readModelAsBeans(null, true);
		properties = filterUselessProperties(properties);
		properties = filterPropertiesByVersion(properties, "6.1.1");
		
		// print the list of return types found
		for(String rt : collectReturnTypes(properties)){
			LOGGER.info("Return type: " + rt);
		}
		
		// write to file the filtered list of properties
		writeToJsonFile(properties, "/tmp/outputHC.json");
		
	}

}
