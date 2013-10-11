package com.octobird.advertising.core;

import android.content.Context;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.octobird.advertising.AdLog;
import com.octobird.advertising.ContentManager;


public class AdServerRequest {
	public final static String PARAMETER_SID = "sid";
    //public final static String PARAMETER_AD_UNIT_ID = "pid";
    //public final static String PARAMETER_COUNTRY_OR_REGION = "ctry";
    public final static String PARAMETER_GPS = "gps";
    //public final static String PARAMETER_LATITUDE = "lat";
    //public final static String PARAMETER_LONGITUDE = "long";
    public final static String PARAMETER_KEYWORDS = "kws";
    //public final static String PARAMETER_POSTAL_CODE = "zip";
    public final static String PARAMETER_USER_AGENT = "device";
    //public final static String PARAMETER_WIDTH = "w";
    //public final static String PARAMETER_HEIGHT = "h";
    public final static String PARAMETER_DISPLAY = "display";
	public final static String PARAMETER_BLOCKS = "blocks";
	public static final String PARAMETER_MAX_WIDHT = "width";
	public static final String PARAMETER_MAX_HEIGHT = "height";
	public static final String PARAMETER_TEST = "test";
	public static final String PARAMETER_GENDER = "gender";
	public static final String PARAMETER_AGE = "age";
	public static final String PARAMETER_BIRTHDAY = "dob";
	public static final String PARAMETER_CITY = "city";
    //private final static String PARAMETER_ADM = "adm";
    private Map<String, String> parameters = new HashMap<String, String>();
    private Context context;

    public AdServerRequest(AdLog AdLog, Context context) {
        this.context = context;
    }

    public AdServerRequest setParameter(String name, String value) {
        if ((name != null) && (value != null)) {
            parameters.put(name, value);
        }
        return this;
    }

    public String getParameter(String name) {
        return parameters.get(name);
    }

    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append(Constants.ADSERVER_URL);
        buffer.append("?");
        buffer.append(ContentManager.getInstance(context).getAutoDetectParameters());
        appendParametersToBuffer(buffer, parameters);

        return buffer.toString();
    }

    private void appendParametersToBuffer(StringBuilder builderToString, Map<String, String> parameters) {
        if (parameters != null) {
            for (Entry<String, String> entry : parameters.entrySet()) {
                String param = entry.getKey();
                String value = entry.getValue();

                if (value != null) {
                    builderToString.append("&");
                    builderToString.append(URLEncoder.encode(param));
                    builderToString.append("=");
                    builderToString.append(URLEncoder.encode(value));
                }
            }
        }
    }

}
