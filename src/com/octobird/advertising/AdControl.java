package com.octobird.advertising;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.octobird.advertising.core.AdCallbackManager;
import com.octobird.advertising.core.AdServerRequest;
import com.octobird.advertising.core.Constants;
import com.octobird.advertising.layouts.AdLayoutVector;

import java.sql.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Viewer of advertising.
 */
public class AdControl extends FrameLayout {
    private Handler handler = new Handler(Looper.getMainLooper());
    protected AdCallbackManager adCallbackManager = new AdCallbackManager(this);
    protected AdServerRequest adserverRequest;
    private AdLog adLog = new AdLog(this);

    private AdControl view;

    private ReloadTask reloadTask;
    private Timer reloadTimer;
    private boolean isManualUpdate = false;

    private boolean autoCollapse = true;
    private AdLayoutVector adLayoutVector;
    private int bgColor;
    private boolean isAutoRefresh = true;
    private boolean isFirstRequestNotDone = true;
    private boolean isFirstAttachNotDone = true;
    private boolean isNeedRequestOnResize = false;
    private boolean isShortTimer = false;

    private static int requestCounter = 0;
    private int rCounterLocal;
    
    public enum Gender{
    	Male,
    	Female
    };

    /**
     * Creation of viewer of advertising. Note, this constructor don't let to set width and height, but they are required.
     * So the control will be not initialized in full with help of this constructor.
     *
     * @param context       - The Context the view is running in, through which it can
     *                      access the current theme, resources, etc.
     * @param sid 			- The application identifier assigned to you during the
     *                      publisher registration process.
     */
    public AdControl(Context context, String sid) {
        super(context);
        adserverRequest = new AdServerRequest(adLog, context);
        autoDetectParameters(context);
        createLayouts(context, sid);
    }

    /**
     * Creation of viewer of advertising.
     *
     * @param context       - The Context the view is running in, through which it can
     *                      access the current theme, resources, etc.
     * @param width         - The width of control (for banner preload).
     * @param height        - The height of control (for banner preload).
     * @param sid			- The application identifier assigned to you during the
     *                      publisher registration process.
     */
    public AdControl(Context context, Integer width, Integer height,
                     String sid) {
        super(context);
        adserverRequest = new AdServerRequest(adLog, context);
        autoDetectParameters(context);
        if ((width != null)&&(height != null))
            adserverRequest.setParameter(AdServerRequest.PARAMETER_DISPLAY,
                    String.valueOf(width)+"x"+String.valueOf(height));
        createLayouts(context, sid);
    }

    /**
     * Creation of viewer of advertising. It is used for element creation in a
     * XML template.
     *
     * @param context  - The Context the view is running in, through which it can
     *                 access the current theme, resources, etc.
     * @param attrs    - The attributes of the XML tag that is inflating the view.
     * @param defStyle - The default style to apply to this view. If 0, no style will
     *                 be applied (beyond what is included in the theme). This may
     *                 either be an attribute resource, whose value will be retrieved
     *                 from the current theme, or an explicit style resource.
     */
    public AdControl(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        adserverRequest = new AdServerRequest(adLog, context);
        autoDetectParameters(context);
        initialize(context, attrs);
    }

    /**
     * Creation of viewer of advertising. It is used for element creation in a
     * XML template.
     *
     * @param context - The Context the view is running in, through which it can
     *                access the current theme, resources, etc.
     * @param attrs   - The attributes of the XML tag that is inflating the view.
     */
    public AdControl(Context context, AttributeSet attrs) {
        super(context, attrs);
        adserverRequest = new AdServerRequest(adLog, context);
        autoDetectParameters(context);
        initialize(context, attrs);
    }

    /**
     * Creation of viewer of advertising. It is used for element creation in a
     * XML template.
     *
     * @param context - The Context the view is running in, through which it can
     *                access the current theme, resources, etc.
     */
    public AdControl(Context context) {
        super(context);
        adserverRequest = new AdServerRequest(adLog, context);
        autoDetectParameters(context);
        initialize(context, null);
    }

    private void autoDetectParameters(Context context) {
    	
    	if (context.checkCallingOrSelfPermission(android.Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
    		adLog.log(AdLog.LOG_LEVEL_CRITICAL, AdLog.LOG_TYPE_ERROR, "ERROR", Constants.STR_ERROR_PERMISSION_INTERNET);
    		adCallbackManager.error(Constants.STR_ERROR_PERMISSION_INTERNET);    		
    	}
    	if (context.checkCallingOrSelfPermission(android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
    		adLog.log(AdLog.LOG_LEVEL_CRITICAL, AdLog.LOG_TYPE_ERROR, "ERROR", Constants.STR_ERROR_PERMISSION_READ_PHONE_STATE);
    		adCallbackManager.error(Constants.STR_ERROR_PERMISSION_READ_PHONE_STATE);
    	}
    	
        setVisibility(View.INVISIBLE);

        setBackgroundColor(Constants.DEFAULT_COLOR);
        
        String versionName = Constants.STR_EMPTY;
        try {
            PackageInfo pinfo;
            pinfo = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
            if (pinfo != null) {
                versionName = pinfo.versionName;
            }
        } catch (Exception e) {
            adLog.log(AdLog.LOG_LEVEL_NORMAL, AdLog.LOG_TYPE_ERROR,
                    Constants.STR_LOG_ADCONTROL_autoDetectParameters, e.getMessage());
        }

        String logString = String.format(Constants.STR_LOG_SDK_VERSION, Constants.SDK_VERSION, android.os.Build.MODEL,
                android.os.Build.VERSION.RELEASE, context.getPackageName(), versionName);

        adLog.log(AdLog.LOG_LEVEL_CRITICAL, AdLog.LOG_TYPE_INFO, Constants.STR_LOG_CREATED,
                logString);

        ContentManager cm = ContentManager.getInstance(context);
        adserverRequest.setParameter(AdServerRequest.PARAMETER_USER_AGENT, cm.getUserAgent());
    }

    /**
     * Get AdLog object.
     *
     * @return AdLog object.
     */
    public AdLog getAdLog() {
        return adLog;
    }

    /**
     * Sets the application identifier assigned to you during the publisher
     * registration process. This property is set when the AdControl object is
     * instantiated.
     *
     * @param sid - Application identifier.
     */
    public void Sid(String sid) {
    	if(TextUtils.isEmpty(sid))
    		adLog.log(AdLog.LOG_LEVEL_NORMAL, AdLog.LOG_TYPE_WARNING, Constants.STR_LOG_INVALID_PROPERTY_TAG, 
    				String.format(Constants.STR_LOG_INVALID_PROPERTY_MSG, Constants.STR_LOG_PROPERTY_SID ));
    	else
        adserverRequest.setParameter(AdServerRequest.PARAMETER_SID,
                sid);
    }

    /**
     * Gets the application identifier assigned to you during the publisher
     * registration process. This property is set when the AdControl object is
     * instantiated.
     *
     * @return Application identifier.
     */
    public String Sid() {
        return adserverRequest
                .getParameter(AdServerRequest.PARAMETER_SID);
    }

    public void Blocks(String blocks){
    	if(TextUtils.isEmpty(blocks))
    		adLog.log(AdLog.LOG_LEVEL_NORMAL, AdLog.LOG_TYPE_WARNING, Constants.STR_LOG_INVALID_PROPERTY_TAG, 
    				String.format(Constants.STR_LOG_INVALID_PROPERTY_MSG, Constants.STR_LOG_PROPERTY_Blocks ));
    	else
        adserverRequest.setParameter(
                AdServerRequest.PARAMETER_BLOCKS, blocks);
    }
    
    public String Blocks(){
    	return adserverRequest
                .getParameter(AdServerRequest.PARAMETER_BLOCKS);
    }
    
    public void MaxWidth(Integer maxWidth){
    	if((maxWidth==null) || (maxWidth>=5120) || (maxWidth<=168))
    		adLog.log(AdLog.LOG_LEVEL_NORMAL, AdLog.LOG_TYPE_WARNING, Constants.STR_LOG_INVALID_PROPERTY_TAG, 
    				Constants.STR_LOG_INVALID_PROPERTY_MSG_MAX_WIDTH);
    	else adserverRequest.setParameter(
                AdServerRequest.PARAMETER_MAX_WIDHT, maxWidth.toString());
    }
    
    public Integer MaxWidth(){
    	return getIntParameter(AdServerRequest.PARAMETER_MAX_WIDHT);
    }
    
    public void MaxHeight(Integer maxHeight){
    	if((maxHeight==null) || (maxHeight>=5120) || (maxHeight<=28))
    		adLog.log(AdLog.LOG_LEVEL_NORMAL, AdLog.LOG_TYPE_WARNING, Constants.STR_LOG_INVALID_PROPERTY_TAG, 
    				Constants.STR_LOG_INVALID_PROPERTY_MSG_MAX_HEIGHT);
    	else adserverRequest.setParameter(
                AdServerRequest.PARAMETER_MAX_HEIGHT, maxHeight.toString());
    }
    
    public Integer MaxHeight(){
    	return getIntParameter(AdServerRequest.PARAMETER_MAX_HEIGHT);
    }
    
    public void Test(boolean test){
    	adserverRequest.setParameter(
                AdServerRequest.PARAMETER_TEST, test ? "1" : "0");
    }
    
    public boolean Test(){
    	String result = adserverRequest
    			.getParameter(AdServerRequest.PARAMETER_TEST);
    	return "1".equals(result);
    }
    
    public void Gender(Gender gender){
    	adserverRequest.setParameter(
                AdServerRequest.PARAMETER_GENDER, gender == Gender.Male ? "m" : "f");
    }
    
    public Gender Gender(){
    	String result = adserverRequest
    			.getParameter(AdServerRequest.PARAMETER_TEST);
    	return "m".equals(result) ? Gender.Male : Gender.Female;
    }
    
    public void Age(Integer age){
    	if((age==null) || (age>=100) || (age<=0))
    		adLog.log(AdLog.LOG_LEVEL_NORMAL, AdLog.LOG_TYPE_WARNING, Constants.STR_LOG_INVALID_PROPERTY_TAG, 
    				Constants.STR_LOG_INVALID_PROPERTY_MSG_AGE);
    	else adserverRequest.setParameter(
                AdServerRequest.PARAMETER_AGE, age.toString());
    }
    
    public Integer Age(){
    	return getIntParameter(AdServerRequest.PARAMETER_AGE);
    }
    
    public void Birthday(Date birthday){
    	if(birthday == null) 
    		adLog.log(AdLog.LOG_LEVEL_NORMAL, AdLog.LOG_TYPE_WARNING, Constants.STR_LOG_INVALID_PROPERTY_TAG, 
    				String.format(Constants.STR_LOG_INVALID_PROPERTY_MSG, Constants.STR_LOG_PROPERTY_Birthday ));
    	else adserverRequest.setParameter(
                AdServerRequest.PARAMETER_BIRTHDAY, birthday.toString().replace("-", ""));
    }
    
    public Date Birthday(){
    	String result = adserverRequest
    			.getParameter(AdServerRequest.PARAMETER_BIRTHDAY);
    	String result2 = result.subSequence(0, 4) + "-" + result.subSequence(4, 6) + "-" + result.subSequence(6, 8);
    	return Date.valueOf(result2) ;//result == null ? null : Date.parse(result);
    }
    
    public void City(String city){
    	if(city == null) 
    		adLog.log(AdLog.LOG_LEVEL_NORMAL, AdLog.LOG_TYPE_WARNING, Constants.STR_LOG_INVALID_PROPERTY_TAG, 
    				String.format(Constants.STR_LOG_INVALID_PROPERTY_MSG, Constants.STR_LOG_PROPERTY_City ));
    	else adserverRequest.setParameter(
                AdServerRequest.PARAMETER_CITY, city);
    }
    
    public String City(){
    	return adserverRequest
    			.getParameter(AdServerRequest.PARAMETER_CITY);
    }
    
    
    public void setRequestParam(String name, String value){
    	adserverRequest.setParameter(name, value);
    }
    
    public String getRequestParam(String name){
    	return adserverRequest
    			.getParameter(name);
    }

    /**
     * Enables or disables automatic refresh and presentation of advertisements.
     *
     * @param autoRefresh - Automatic refresh flag (default=true)
     */
    public void AutoRefresh(boolean autoRefresh) {
        isAutoRefresh = autoRefresh;

        if (autoRefresh)
        	startTimer(getContext(), this, Constants.UPDATE_TIME_INTERVAL * 1000);
        else if (reloadTask != null) {
            reloadTask.cancel();
            reloadTask = null;
        }
    }

    /**
     * Is Enables or disables automatic refresh and presentation of
     * advertisements.
     *
     * @return Automatic refresh flag (default=true)
     */
    public boolean AutoRefresh() {
        return isAutoRefresh;
    }

    /**
     * Sets the value that indicates whether the AdControl will automatically
     * hide itself if no ad is available or an error occurs.
     *
     * @param autoCollapse - Automatic collapse flag (default=true).
     */
    public void AutoCollapse(boolean autoCollapse) {
        this.autoCollapse = autoCollapse;
    }

    /**
     * Gets the value that indicates whether the AdControl will automatically
     * hide itself if no ad is available or an error occurs.
     *
     * @return Automatic collapse flag (default=true).
     */
    public boolean AutoCollapse() {
        return autoCollapse;
    }

    /**
     * Call to this method directs the AdControl to show the next ad when a new
     * ad becomes available.
     */
    public void Refresh() {
        update(true);
    }

    /**
     * Gets a value that indicates whether the user is interacting with an
     * advertisement.
     *
     * @return Ad interacting indicator.
     */
    public boolean IsEngaged() {
        return adLayoutVector.isEngaged();
    }

   
    /**
     * Sets the latitude and longitude of the location.
     *
     * @param latitude - Latitude.
     * @param longitude - Longitude.
     */
    public void GPS(Double latitude, Double longitude) {
    	if((latitude==null) || (latitude>90) || (latitude<-90))
    		adLog.log(AdLog.LOG_LEVEL_NORMAL, AdLog.LOG_TYPE_WARNING, Constants.STR_LOG_INVALID_PROPERTY_TAG, 
    				Constants.STR_LOG_INVALID_PROPERTY_MSG_LATITUDE);
    	else
    		if((longitude==null) || (longitude>180) || (longitude<-180))
        		adLog.log(AdLog.LOG_LEVEL_NORMAL, AdLog.LOG_TYPE_WARNING, Constants.STR_LOG_INVALID_PROPERTY_TAG, 
        				Constants.STR_LOG_INVALID_PROPERTY_MSG_LONGITUDE);
        	else
    		
    		adserverRequest.setParameter(AdServerRequest.PARAMETER_GPS,
        		latitude.toString().replace(',', '.')+','+longitude.toString().replace(',', '.'));
    }
    
    /**
     * Gets the latitude and longitude of the location.
     *
     * @return "latitude,longitude".
     */
    public String GPS(){
    	try {
			return adserverRequest.getParameter(AdServerRequest.PARAMETER_GPS);    	
	    } catch (Exception e) {
			return "";
		}
    }
    
    /**
     * Sets the comma-separated keywords that target the audience for advertisements.
     *
     * @param keywords - Keywords.
     */
    public void Keywords(String keywords) {
    	if(TextUtils.isEmpty(keywords))
    		adLog.log(AdLog.LOG_LEVEL_NORMAL, AdLog.LOG_TYPE_WARNING, Constants.STR_LOG_INVALID_PROPERTY_TAG, 
    				String.format(Constants.STR_LOG_INVALID_PROPERTY_MSG, Constants.STR_LOG_PROPERTY_Keywords ));
    	else
        adserverRequest.setParameter(AdServerRequest.PARAMETER_KEYWORDS,
                keywords);
    }

    /**
     * Gets the comma-separated keywords that target the audience for advertisements.
     *
     * @return Keywords.
     */
    public String Keywords() {
        return adserverRequest.getParameter(AdServerRequest.PARAMETER_KEYWORDS);
    }


    /**
     * Sets the LogLevel value that determines log level.
     *
     * @param logLevel - Log level (default=LOG_LEVEL_NORMAL).
     */
    public void LogLevel(int logLevel) {
        adLog.setLogLevel(logLevel);
    }

    /**
     * Gets the LogLevel value that determines log level.
     *
     * @return Log level (default=LOG_LEVEL_NORMAL).
     */
    public int LogLevel() {
        return adLog.getLogLevel();
    }

    /**
     * Sets the interface to handle advertising.
     *
     * @param onAdEventHandler - Interface to handle advertising.
     */
    public void OnAdEventHandler(OnAdEventHandler onAdEventHandler) {
        adCallbackManager.setOnAdEventHandler(onAdEventHandler);
    }

    /**
     * Gets the interface to handle advertising.
     *
     * @return Interface to handle advertising.
     */
    public OnAdEventHandler OnAdEventHandler() {
        return adCallbackManager.getOnAdEventHandler();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        adserverRequest.setParameter(AdServerRequest.PARAMETER_DISPLAY,
                String.valueOf(w)+"x"+String.valueOf(h));
        
        if(isNeedRequestOnResize)
        {
        	isNeedRequestOnResize = false;
        	startLoadContent(getContext(), view);
        }
    }

    /**
     * Optional. Set Background color of advertising in HEX.
     *
     * @param backgroundColor - Background color of advertising in HEX (default=transparent).
     */
    @Override
    public void setBackgroundColor(int backgroundColor) {
        try {
            bgColor = backgroundColor;
            super.setBackgroundColor(backgroundColor);
        } catch (Exception e) {
            adLog.log(AdLog.LOG_LEVEL_CRITICAL, AdLog.LOG_TYPE_ERROR,
                    Constants.STR_LOG_ADCONTROL_setBackgroundColor, e.getMessage());
        }
    }

    /**
     * Optional. Get Background color of advertising in HEX.
     *
     * @return Background color of advertising in HEX (default=transparent).
     */
    public int getBackgroundColor() {
        return bgColor;
    }

    private void initialize(Context context, AttributeSet attrs) {
        if (attrs != null) {
            Integer logLevel = getIntParameter(attrs.getAttributeValue(null,
                    Constants.XML_LOG_LEVEL));
            if (logLevel != null)
                LogLevel(logLevel);

            Boolean bv = getBooleanOrNull(attrs.getAttributeValue(null,
                    Constants.XML_AUTO_REFRESH));
            if (bv != null)
                AutoRefresh(bv);
            Keywords(attrs.getAttributeValue(null, Constants.XML_KEYWORDS));
            
            createLayouts(
                    context,
                    attrs.getAttributeValue(null, Constants.XML_SID));
            update(false);
        } else {        	
            createLayouts(context, null);
            update(false);
        }
    }

    private static Boolean getBooleanOrNull(String stringValue) {
        if (stringValue == null) return null;
        return Boolean.parseBoolean(stringValue);
    }

    Integer getIntParameter(String stringValue) {
            try {
                return Integer.parseInt(stringValue);
            } catch (Exception e) {
                return null;
            }
    }
    
    Double getDoubleParameter(String stringValue) {
        try {
            return Double.parseDouble(stringValue);
        } catch (Exception e) {
            return null;
        }
}

    private void createLayouts(Context context, String sid) {
        Sid(sid);
        adLayoutVector = new AdLayoutVector(context, this);
        adLayoutVector.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.FILL_PARENT));
        addView(adLayoutVector);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        
        if(isFirstAttachNotDone && (adserverRequest.getParameter(AdServerRequest.PARAMETER_DISPLAY)==null)
        		)
        	isNeedRequestOnResize = true;
        isFirstAttachNotDone = false;
        
        adLog.log(AdLog.LOG_LEVEL_NORMAL, AdLog.LOG_TYPE_INFO,
                Constants.STR_LOG_ADCONTROL_onAttachedToWindow, Constants.STR_EMPTY);
        reloadTimer = new Timer();
        startTimer(getContext(), this, Constants.UPDATE_TIME_INTERVAL * 1000);
        if (adLayoutVector.isEmpty())
            startLoadContent(getContext(), this);
    }

    public void stopLoadContent() {
        ContentManager.getInstance().stopLoadContent(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        adLog.log(AdLog.LOG_LEVEL_NORMAL, AdLog.LOG_TYPE_INFO,
                Constants.STR_LOG_ADCONTROL_onDetachedFromWindow, Constants.STR_EMPTY);
        ContentManager.getInstance().stopLoadContent(this);
        if (reloadTimer != null) {
            try {
                reloadTimer.cancel();
                reloadTimer = null;
            } catch (Exception e) {
                adLog.log(AdLog.LOG_LEVEL_CRITICAL, AdLog.LOG_TYPE_ERROR,
                        Constants.STR_LOG_ADCONTROL_onDetachedFromWindow, e.getMessage());
            }
        }

        super.onDetachedFromWindow();
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        adLog.log(AdLog.LOG_LEVEL_NORMAL, AdLog.LOG_TYPE_INFO,
                Constants.STR_LOG_ADCONTROL_onWindowVisibilityChanged,
                String.format(Constants.STR_VISIBILITY_CHANGET, visibility));
        if (visibility == View.VISIBLE) {
            startTimer(getContext(), this, Constants.UPDATE_TIME_INTERVAL * 1000);
        } else if (reloadTask != null) {
            reloadTask.cancel();
            reloadTask = null;
        }
    }
    
    @Override
    public void setVisibility(int visibility) {
    	if(getVisibility() != visibility ){    		
	    	adLog.log(AdLog.LOG_LEVEL_NORMAL, AdLog.LOG_TYPE_INFO,
	                Constants.STR_LOG_ADCONTROL_onVisibilityChanged,
	                String.format(Constants.STR_VISIBILITY_CHANGET, visibility));
	        if (visibility == View.VISIBLE) {
	            startTimer(getContext(), this, Constants.UPDATE_TIME_INTERVAL * 1000);
	        } else if (reloadTask != null) {
	            reloadTask.cancel();
	            reloadTask = null;
	        }
    	}
    	super.setVisibility(visibility);    	
    }
    
    /*@Override
    protected void onVisibilityChanged(View changedView, int visibility) {
    	//super.onVisibilityChanged(changedView, visibility);
    	adLog.log(AdLog.LOG_LEVEL_NORMAL, AdLog.LOG_TYPE_INFO,
                Constants.STR_LOG_ADCONTROL_onVisibilityChanged,
                String.format(Constants.STR_VISIBILITY_CHANGET, visibility));
        if (visibility == View.VISIBLE) {
            startTimer(getContext(), this, Constants.UPDATE_TIME_INTERVAL * 1000);
        } else if (reloadTask != null) {
            reloadTask.cancel();
            reloadTask = null;
        }
    }*/

    /**
     * Immediately update banner contents.
     *
     * @param isManual
     */
    private void update(boolean isManual) {
        if (isShown() || isManual) {
            adLog.log(AdLog.LOG_LEVEL_ALL, AdLog.LOG_TYPE_INFO, Constants.STR_LOG_ADCONTROL_update, Constants.STR_EMPTY);
            isManualUpdate = isManual;
            startLoadContent(getContext(), this);
        }
    }

    private void startLoadContent(Context context, AdControl view) {
        try {
            if (adserverRequest.getParameter(AdServerRequest.PARAMETER_DISPLAY) == null)
                throw new AdControlException(Constants.STR_LOG_WRONG_DISPLAY);

            if ((adserverRequest.getParameter(AdServerRequest.PARAMETER_SID) == null)
                    || (adserverRequest.getParameter(AdServerRequest.PARAMETER_SID).equals(Constants.STR_EMPTY))) {

                startTimer(getContext(), view, Constants.UPDATE_TIME_INTERVAL * 1000);
                adCallbackManager.error(Constants.STR_LOG_WRONG_IDS);
                throw new AdControlException(Constants.STR_LOG_WRONG_IDS);                

            } else if (((getVisibility() != View.VISIBLE) || !isShown() || adLayoutVector
                    .isExpanded()) && (!isFirstRequestNotDone && !isManualUpdate)) {
                startTimer(getContext(), view,
                        isShortTimer ? Constants.UPDATE_SHORT_TIME_INTERVAL * 1000
                                : Constants.UPDATE_TIME_INTERVAL * 1000);
                return;
            }
            String[] size = adserverRequest.getParameter(AdServerRequest.PARAMETER_DISPLAY).split("x");
            int w = Integer.parseInt(size[0]);
            int h = Integer.parseInt(size[1]);
            if (w<300)
            {
            	adCallbackManager.error(Constants.STR_LOG_WRONG_WIDTH);
            	throw new AdControlException(Constants.STR_LOG_WRONG_WIDTH);
            }
            
            if (h<50)
            {
            	adCallbackManager.error(Constants.STR_LOG_WRONG_HEIGHT);
            	throw new AdControlException(Constants.STR_LOG_WRONG_HEIGHT);
            }


            isManualUpdate = false;
            isFirstRequestNotDone = false;

            //TODO above
            requestCounter++;
            rCounterLocal = requestCounter;
            String url = adserverRequest.toString();
            adLog.log(AdLog.LOG_LEVEL_ALL, AdLog.LOG_TYPE_INFO, String.format(Constants.STR_LOG_REQUEST_GET, requestCounter), url);
            ContentManager.getInstance(context).startLoadContent(this, url,
            		adserverRequest.getParameter(AdServerRequest.PARAMETER_DISPLAY)
                    );
        } catch (AdControlException e) {
            adLog.log(AdLog.LOG_LEVEL_NORMAL, AdLog.LOG_TYPE_ERROR, Constants.STR_LOG_ADCONTROL_startLoadContent,
                    String.format(Constants.STR_LOG_CANT_SEND_REQUEST, e.getMessage()));

        }
    }

    protected void requestCallback(String result, final String error) {
        if (error != null) {
            adLog.log(AdLog.LOG_LEVEL_ALL, AdLog.LOG_TYPE_ERROR,
                    String.format(Constants.STR_LOG_REQUEST_GET_RESULT_ERROR, rCounterLocal), error);
            if (!adLayoutVector.isEmpty() && autoCollapse)
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        setVisibility(View.INVISIBLE);
                    }
                });
            adCallbackManager.error(error);

            startTimer(getContext(), view,
                    isShortTimer ? Constants.UPDATE_SHORT_TIME_INTERVAL * 1000
                            : Constants.UPDATE_TIME_INTERVAL * 1000);
            return;
        }

        isShortTimer = false;
        adLog.log(AdLog.LOG_LEVEL_ALL, AdLog.LOG_TYPE_INFO,
                String.format(Constants.STR_LOG_REQUEST_GET_RESULT, rCounterLocal), result);

        try {
            if ((result != null) && (result.length() > 0)) {
                if (autoCollapse && adLayoutVector.isEmpty())
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            setVisibility(View.VISIBLE);
                        }
                    });
                adLayoutVector.addBanner(result, adCallbackManager);
                startTimer(getContext(), view,
                        Constants.UPDATE_TIME_INTERVAL * 1000);
            } else {
            	adLog.log(AdLog.LOG_LEVEL_CRITICAL, AdLog.LOG_TYPE_ERROR,
                        Constants.STR_LOG_ADCONTROL_requestCallback, Constants.STR_ERROR_RESPONSE_EMPTY); 
            	if(adCallbackManager!=null) adCallbackManager.error(Constants.STR_LOG_ADCONTROL_requestCallback + " " + Constants.STR_ERROR_RESPONSE_EMPTY); 
            }
        } catch (Exception e) {
            adLog.log(AdLog.LOG_LEVEL_CRITICAL, AdLog.LOG_TYPE_ERROR,
                    Constants.STR_LOG_ADCONTROL_requestCallback, e.getMessage());
            startTimer(getContext(), view,
                    Constants.UPDATE_TIME_INTERVAL * 1000);
        }
    }

    private void startTimer(Context context, AdControl view, int adReloadPeriod) {
        {
            try {
                if (!isAutoRefresh
                        && ((Constants.UPDATE_SHORT_TIME_INTERVAL * 1000) != adReloadPeriod))
                    return;

                if ((Constants.UPDATE_SHORT_TIME_INTERVAL * 1000) == adReloadPeriod)
                    isShortTimer = true;

                if (reloadTask != null) {
                    reloadTask.cancel();
                    reloadTask = null;
                }
                
                if(IsEngaged() || !isShown()) return;

                ReloadTask newReloadTask = new ReloadTask(context, view);

                if (adReloadPeriod >= 0) {
                    if (adReloadPeriod > 0) {
                        adLog.log(AdLog.LOG_LEVEL_ALL, AdLog.LOG_TYPE_INFO,
                                Constants.STR_LOG_ADCONTROL_startTimer,
                                String.valueOf(adReloadPeriod / 1000));
                        reloadTimer.schedule(newReloadTask, adReloadPeriod);
                    } else {
                        adLog.log(AdLog.LOG_LEVEL_ALL, AdLog.LOG_TYPE_INFO,
                                Constants.STR_LOG_ADCONTROL_startTimer, Constants.STR_TIMER_STOPPED);
                    }
                } else {
                    reloadTimer.schedule(newReloadTask,
                            Constants.UPDATE_TIME_INTERVAL * 1000);
                    adLog.log(AdLog.LOG_LEVEL_ALL, AdLog.LOG_TYPE_INFO,
                            Constants.STR_LOG_ADCONTROL_startTimer,
                            String.valueOf(Constants.UPDATE_TIME_INTERVAL));
                }

                reloadTask = newReloadTask;
            } catch (Exception e) {
                adLog.log(AdLog.LOG_LEVEL_ALL, AdLog.LOG_TYPE_ERROR,
                        Constants.STR_LOG_ADCONTROL_startTimer, e.getMessage());
            }
        }
    }

    public void onEngagedChange(boolean engaged) {
        //if (OnAdEventHandler() != null)
        //	OnAdEventHandler().engagedChange(this, engaged);
        adCallbackManager.engagedChange(engaged);
        if (engaged) {
            if (reloadTask != null) {
                reloadTask.cancel();
                reloadTask = null;
            }
        } else
            startTimer(getContext(), view,
                    Constants.UPDATE_SHORT_TIME_INTERVAL * 1000);
    }

    public void onAdClickWithoutDialog() {
        startTimer(getContext(), view,
                Constants.UPDATE_SHORT_TIME_INTERVAL * 1000);
    }

    private class ReloadTask extends TimerTask {
        private Context context;
        private AdControl view;

        public ReloadTask(Context context, AdControl view) {
            this.context = context;
            this.view = view;
        }

        @Override
        public void run() {
            startLoadContent(context, view);
        }
    }

}