
package com.ben.opinionslice.utils;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;

import com.ben.opinionslice.R;
import com.ben.opinionslice.application.OSApplication;
import com.ben.opinionslice.application.OSConfig;

import android.content.res.Resources;

public class Utils {
	public static String language;
    public static void CopyStream(InputStream is, OutputStream os)
    {
        final int buffer_size = 1024;
        try
        {
            byte[] bytes = new byte[buffer_size];
            for (;;)
            {
                int count = is.read(bytes, 0, buffer_size);
                if (count == -1)
                    break;
                os.write(bytes, 0, count);
            }
        } catch (Exception ex) {
        }
    }
    public static int calculatePercentage(int val1, int val2){
    	int total = val1 + val2;
    	float p = (float)val1 / (float)total;
    	return Math.round(p * 100);
    }
    public static String formatDuration(long duration, String language){
    	String formated_duration = "";
    	
    	if(duration < 60)
    		formated_duration = Math.round(duration) + _("duration_second", language);
    	else if(duration < (60 * 60))
    		formated_duration = Math.round(duration/60.0) + _("duration_minute", language);
    	else if(duration < (60 * 60 * 24))
    		formated_duration = Math.round(duration/60.0/60.0) + _("duration_hour", language);
    	else if(duration < (60 * 60 * 24 * 30))
    		formated_duration = Math.round(duration/60.0/60.0/24.0) + _("duration_day", language);
    	else if(duration < (60 * 60 * 24 * 365))
    		formated_duration = Math.round(duration/60.0/60.0/24.0/30.0) + _("duration_month", language);
    	else
    		formated_duration = Math.round(duration/60.0/60.0/24.0/365.0) + _("duration_year", language);
    	
    	return formated_duration;
    }
    public static String _(String text, String language){
    	String translated_text = "";
    	Resources resource = OSApplication.getMyResources();
    	int resource_id = resource.getIdentifier(text + "_" + language, "string", OSConfig.PACKAGE_NAME);
    	if(resource_id != 0)
    		translated_text = resource.getString(resource_id);
    	return translated_text;
    }
	
	public static String _(String text){
    	String translated_text = "";
    	Resources resource = OSApplication.getMyResources();
    	int resource_id = resource.getIdentifier(text + "_" + language, "string", OSConfig.PACKAGE_NAME);
    	if(resource_id != 0)
    		translated_text = resource.getString(resource_id);
    	else
    		translated_text = text;
    	return translated_text;
    }
}
