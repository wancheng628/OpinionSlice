package com.ben.opinionslice.listener;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ben.opinionslice.application.OSApplication;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;


public class GooglePlacesAutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {
    private ArrayList<String> resultList;

    private static final String LOG_TAG = "ExampleApp";

    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";

    private static final String API_KEY = "AIzaSyBIiry0bXkL_PCXCsqYe3WLeEFmhVPvkhk";
    private Context mContext;
    
    public GooglePlacesAutoCompleteAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        mContext = context;
    }
    
    private ArrayList<String> autocomplete(String input) {
        ArrayList<String> resultList = null;

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
        	/*
            StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
            sb.append("?key=" + API_KEY);
            sb.append("&input=" + URLEncoder.encode(input, "utf8"));
            sb.append("&types=" + "geocode");
            Log.d("Google Place URL",sb.toString());
            */
        	
            //URL url = new URL(sb.toString());
        	
        	URL url = new URL("http://maps.googleapis.com/maps/api/geocode/json?address="+URLEncoder.encode(input, "utf8")+"&region=&components=&sensor=false");
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error processing Places API URL", e);
            return resultList;
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error connecting to Places API", e);
            return resultList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            String Status = jsonObj.getString("status");
	        if (Status.equalsIgnoreCase("OK")) {
	        	/*
	            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");
	
	            // Extract the Place descriptions from the results
	            resultList = new ArrayList<String>(predsJsonArray.length());
	            for (int i = 0; i < predsJsonArray.length(); i++) {
	                resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
	            }
	            */
	        	JSONArray Results = jsonObj.getJSONArray("results");
	        	resultList = new ArrayList<String>(Results.length()+1);
	        	for (int i = 0; i < Results.length(); i++) {
	                resultList.add(Results.getJSONObject(i).getString("formatted_address"));
	            }
	        }
            String current_language = ((OSApplication)((Activity)mContext).getApplication()).getLanguage();
            if(current_language.equalsIgnoreCase("fr")){
            	resultList.add("International");
            }else{
            	resultList.add("World");
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Cannot process JSON results", e);
        }

        return resultList;
    }
    
    @Override
    public int getCount() {
        return resultList.size();
    }

    @Override
    public String getItem(int index) {
        return resultList.get(index);
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    // Retrieve the autocomplete results.
                    resultList = autocomplete(constraint.toString());

                    // Assign the data to the FilterResults
                    filterResults.values = resultList;
                    filterResults.count = resultList.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged();
                }
                else {
                    notifyDataSetInvalidated();
                }
            }};
        return filter;
    }
}
