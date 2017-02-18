package com.ben.opinionslice.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Countries {
	static HashMap<String, String> countryHash = new HashMap<String, String>();
	
	static {
        
    }
	public static int count(){
		return countryHash.size();
	}
	public static void clear(){
		countryHash.clear();
	}
	public static void addCountry(String code, String name){
		countryHash.put(name, code);
	}
	public static ArrayList<String> getAllCountryNames(){
		ArrayList<String> countryNames = new ArrayList<String>();
		for (String name: countryHash.keySet()) {
		    countryNames.add(name);
		}
		Collections.sort(countryNames, String.CASE_INSENSITIVE_ORDER);
		return countryNames;
	}
	public static String getCodeFromCountryName(String country_name){
		return countryHash.get(country_name);
	}
	public static String getNameFromCountryCode(String country_code){
		
        String country_name = "";
        for(Map.Entry entry: countryHash.entrySet()){
            if(country_code.equals(entry.getValue())){
            	country_name = (String) entry.getKey();
                break; //breaking because its one to one map
            }
        }
		return country_name;
	}
}
