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
		List<HighchartsJsonProperty> result = new ArrayList<HighchartsJsonProperty>();
		JsonPropertyFilter f1=new JsonPropertyFilter("series<",FilterType.STARTS_WITH);
		JsonPropertyFilter f2=new JsonPropertyFilter("global",FilterType.STARTS_WITH);
		JsonPropertyFilter f3=new JsonPropertyFilter("lang",FilterType.STARTS_WITH);
		for(HighchartsJsonProperty p : properties){
			String fullname = p.getFullname();
			if(!f1.applyFilterTo(fullname)&&!f2.applyFilterTo(fullname)&&!f3.applyFilterTo(fullname)){
				result.add(p);
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
		List<String> result = new ArrayList<String>();
		result.addAll(types);
		return result;
	}
	
	
	public static void main(String args[]) throws MalformedURLException{
		// grab the last available highcharts json file
		listAllPropertyAttributes(new URL("http://api.highcharts.com/highcharts/option/dump.json"));

		// rely on the shipped one
		//listAllPropertyAttributes(null);
		
		// read and filter the properties
		List<HighchartsJsonProperty> properties = readModelAsBeans(null, true);
		properties = filterPropertiesByVersion(properties, "4.2.1");
		properties = filterUselessProperties(properties);
		
		// print the list of return types found
		for(String rt : collectReturnTypes(properties)){
			LOGGER.info("Return type: " + rt);
		}
		
		// write to file the filtered list of properties
		writeToJsonFile(properties, "/tmp/outputHC.json");
		
	}

}
