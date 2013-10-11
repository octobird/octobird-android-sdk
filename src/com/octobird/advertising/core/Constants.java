package com.octobird.advertising.core;

public class Constants {
    public static final String SDK_VERSION = "1.0.0000";
    public static final String API_VERSION = "300";
    public static final int DEFAULT_COLOR = 0;

    public static final int UPDATE_TIME_INTERVAL = 30; //in seconds
    public static final int UPDATE_SHORT_TIME_INTERVAL = 5; //in seconds
    public static final int DEFAULT_REQUEST_TIMEOUT = 20; //in seconds
    public static final String ADSERVER_URL = "http://show.octobird.com/";//*/"http://show.octobird.com/android.php";//"http://mobileads.msn.com/v3/Delivery/Placement";

    public final static int BUFFERED_INPUT_STREAM_SIZE = 1024;
    public final static int ANIMATION_TIME_FRAME = 500;//in ms
    public final static int ANIMATION_TIME_AD = 1000;//in ms
    public final static int ANIMATION_TIME_AD_WAIT = 500;
    public final static int FRAME_TIME = 4000;//in ms


    public static final String STR_THREAD_NAME_INIT = "[ContentManager] InitDefaultParameters";
    public static final String STR_THREAD_NAME_LOAD_CONTENT = "[ContentManager] LoadContent";
    public static final String STR_EMPTY = "";
    public static final String STR_DEFAULT_ID = "1234567890";

    public static final String STR_ACTION_TYPE_PHONE = "tel:%s";
    public static final String STR_CONTENT_MANAGER = "ContentManager";
    public static final String STR_CONTENT_MANAGER_CREATE = "New instance was created on SDK version[%s]";
    public static final String STR_VISIBILITY_CHANGET = "visibility[%d]";
    public static final String STR_TIMER_STOPPED = "Stop update timer";
    public static final String STR_IMPRESSION_NOT_SEND = "Impression is not sent";

    public static final String STR_LOG_FORMAT_TAG = "[%s]%s";
    public static final String STR_LOG_FORMAT_MESSAGE = "%s ";
    public static final String STR_LOG_LEVEL_1 = "LOG_LEVEL_CRITICAL";
    public static final String STR_LOG_LEVEL_2 = "LOG_LEVEL_NORMAL";
    public static final String STR_LOG_LEVEL_3 = "LOG_LEVEL_ALL";
    public static final String STR_LOG_LEVEL_NONE = "LOG_LEVEL_NONE";

    public static final String STR_LOG_SDK_VERSION = "adSDK version = %s; DeviceModel = %s; DeviceOsVersion = %s; PackageName = %s; versionName= %s";
    public static final String STR_LOG_ADCONTROL_autoDetectParameters = "AdControl.autoDetectParameters";
    public static final String STR_LOG_ADCONTROL_setBackgroundColor = "AdControl.setBackgroundColor";
    public static final String STR_LOG_ADCONTROL_onAttachedToWindow = "AdControl.onAttachedToWindow";
    public static final String STR_LOG_ADCONTROL_onDetachedFromWindow = "AdControl.onDetachedFromWindow";
    public static final String STR_LOG_ADCONTROL_onWindowVisibilityChanged = "AdControl.onWindowVisibilityChanged";
    public static final String STR_LOG_ADCONTROL_onVisibilityChanged = "AdControl.onVisibilityChanged";
    public static final String STR_LOG_ADCONTROL_update = "AdControl.update";
    public static final String STR_LOG_ADCONTROL_startLoadContent = "AdControl.startLoadContent";
    public static final String STR_LOG_ADCONTROL_requestCallback = "requestCallback";
    public static final String STR_LOG_ADCONTROL_startTimer = "AdControl.startTimer";
    public static final String STR_LOG_ADACTION = "AdAction";
    public static final String STR_LOG_ADLOG_setLogLevel = "setLogLevel";
    public static final String STR_LOG_initDefaultParameters = "initDefaultParameters";

    public static final String STR_LOG_ADLAYOUT_AdJSON_parse = "AdJSON.parse";
    public static final String STR_LOG_ADLAYOUT_AdJSON_parse_message = "Code=%s message=%s";
    public static final String STR_LOG_ADLAYOUT_AdJSON_INVALID_PROTOCOL = "Invalid protocol version. Protocol should have version 3.0";

    public static final String STR_LOG_REQUEST_GET = "requestGet[%d]";
    public static final String STR_LOG_REQUEST_GET_RESULT = "requestGet result[%d]";
    public static final String STR_LOG_REQUEST_GET_RESULT_ERROR = "requestGet result[%d][ERROR]";
    public static final String STR_LOG_CREATED = "Created";
    public static final String STR_LOG_CANT_SEND_REQUEST = "[cant send request] %s";
    public static final String STR_LOG_WRONG_DISPLAY = "Wrong width or height";
    public static final String STR_LOG_WRONG_WIDTH = "Wrong width";
    public static final String STR_LOG_WRONG_HEIGHT = "Wrong height";
    public static final String STR_LOG_WRONG_IDS = "Wrong ApplicationId or AdUnitId";
    
    public static final String STR_LOG_PROPERTY_SID = "Sid";
    public static final Object STR_LOG_PROPERTY_Blocks = "Blocks";
    //public static final String STR_LOG_PROPERTY_CountryOrRegion = "CountryOrRegion";
    public static final String STR_LOG_PROPERTY_Keywords = "Keywords";    
    public static final Object STR_LOG_PROPERTY_Birthday = "Birthday";
    public static final Object STR_LOG_PROPERTY_City = "City";
    //public static final String STR_LOG_PROPERTY_PostalCode = "PostalCode";
    public static final String STR_LOG_INVALID_PROPERTY_MSG = "Invalid property %s value. The value of the property must not be empty.";
    public static final String STR_LOG_INVALID_PROPERTY_MSG_LONGITUDE = "Invalid property Longitude value. The value is outside the range of -180.0 degrees to 180.0 degrees.";
    public static final String STR_LOG_INVALID_PROPERTY_MSG_LATITUDE = "Invalid property Latitude value. The value is outside the range of -90.0 degrees to 90.0 degrees.";
    public static final String STR_LOG_INVALID_PROPERTY_MSG_MAX_WIDTH = "Invalid property MaxWidth value. The value is outside the range of 168 to 5120 pixels.";
    public static final String STR_LOG_INVALID_PROPERTY_MSG_MAX_HEIGHT = "Invalid property MaxHeight value. The value is outside the range of 28 to 5120 pixels.";
    public static final String STR_LOG_INVALID_PROPERTY_MSG_AGE = "Invalid property Age value. The value is outside the range of 0 to 100.";
	
    public static final String STR_LOG_INVALID_PROPERTY_TAG = "Invalid property.";

    public static final String XML_LOG_LEVEL = "logLevel";
    public static final String XML_COUNTRY_OR_REGION = "countryOrRegion";
    public static final String XML_AUTO_REFRESH = "autoRefresh";
    //public static final String XML_LATITUDE = "latitude";
    //public static final String XML_LONGITUDE = "longitude";
    public static final String XML_KEYWORDS = "keywords";
    //public static final String XML_POSTAL_CODE = "postalCode";
    public static final String XML_SID = "sid";
    //public static final String XML_AD_UNIT_ID = "adUnitId";

    public static final String BUTTON_BROWSE_TITLE = "Browse";
    public static final String BUTTON_CALL_TITLE = "Call %s";

    public static final String ADVERTISEMENT_TITLE = "Advertisement";
    public static final String ORMMA_PLAYER_TRANSIENT_TEXT = "Loading. Please Wait..";

    /*public static final String STR_ORMMA_ERROR_RESIZE = "Error was happened: (resize: Cannot resize an ad that is not in the default state.)";
    public static final String STR_ORMMA_ERROR_HIDE = "Error was happened: (hide: Cannot hide an ad that is not in the default state.)";
    public static final String STR_ORMMA_ERROR_EXPAND = "Error was happened: (expand: Cannot expand an ad that is not in the default state.)";
    public static final String STR_ORMMA_ERROR_LOCATION_IS_DISABLED = "Location provider is disabled";
    public static final String STR_ORMMA_ERROR_LOCATION_IS_OUT_OF_SERVICE = "Location provider is out of service";
    public static final String STR_ORMMA_ERROR_ASSET_FILE_IS_NOT_SAVED = "File is not saved in cache";
    public static final String STR_ORMMA_ERROR_ASSET_MAKE_SCREENSHOT = "It is impossible to make a screenshot";
    public static final String STR_ORMMA_ERROR_ASSET_PHOTO_ALBUM = "File is not saved in the device's photo album";
    public static final String STR_ORMMA_ERROR_OPEN_URL = "Cannot open this URL";
    public static final String STR_ORMMA_ERROR_WRONG_NUMBER = "Wrong number";
    public static final String STR_ORMMA_ERROR_WRONG_JSON_FORMAT = "Wrong JSON format";
    public static final String STR_ORMMA_ERROR_NOT_READ_URI = "Could not read uri content";
    public static final String STR_ORMMA_ERROR_SMS_NOT_AVAILABLE = "SMS not available";
    public static final String STR_ORMMA_ERROR_EMAIL_CLIENT_NOT_AVAILABLE = "Email client not available";
    public static final String STR_ORMMA_ERROR_EMAIL_NOT_AVAILABLE = "Email not available";
    public static final String STR_ORMMA_ERROR_BAD_PHONE_NUMBER = "Bad Phone Number";
    public static final String STR_ORMMA_ERROR_CALLS_NOT_AVAILABLE = "CALLS not available";
    public static final String STR_ORMMA_ERROR_NOT_FIND_CALENDAR = "Could not find a local calendar";
    public static final String STR_ORMMA_ERROR_CALENDAR_NOT_AVAILABLE = "Calendar not available";*/

    public static final String STR_ERROR_NO_GOOGLE_API = "Error: no Google Api or error in parameters";
    public static final String STR_ERROR_INCORRECT_ARGUMENT = "Incorrect argument";
    public static final String STR_ERROR_NO_FREE_MEMORY = "No free memory";
    public static final String STR_ERROR_FILE_NOT_EXISTS = "File not exists";
    public static final String STR_ERROR_INTERNAL_ERROR = "Internal error";
    public static final String STR_ERROR_SECURITY_ERROR = "Security error";
    public static final String STR_ERROR_RESPONSE_EMPTY = "Server response with empty body";
    public static final String STR_ERROR_PERMISSION_READ_PHONE_STATE = "Requires READ_PHONE_STATE: Neither user nor current process has android.permission.READ_PHONE_STATE.";
    public static final String STR_ERROR_PERMISSION_INTERNET = "Requires INTERNET: Neither user nor current process has android.permission.INTERNET.";
	
		
	
	

}
