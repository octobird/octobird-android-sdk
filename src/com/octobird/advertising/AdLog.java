package com.octobird.advertising;

import com.octobird.advertising.core.Constants;

import android.util.Log;

public class AdLog {
    public static final int LOG_LEVEL_NONE = 0;
    public static final int LOG_LEVEL_CRITICAL = 1;
    public static final int LOG_LEVEL_NORMAL = 2;
    public static final int LOG_LEVEL_ALL = 3;

    public static final int LOG_TYPE_ERROR = 1;
    public static final int LOG_TYPE_WARNING = 2;
    public static final int LOG_TYPE_INFO = 3;

    private static int defaultLogLevel = LOG_LEVEL_NORMAL;
    private int currentLogLevel = LOG_LEVEL_NONE;
    private AdControl adView;

    public AdLog(AdControl adView) {
        this.adView = adView;
        setLogLevel(defaultLogLevel);
    }

    public static void setDefaultLogLevel(int logLevel) {
        defaultLogLevel = logLevel;
    }

    public void log(int level, int type, String tag, String msg) {
        String resultTag = String.format(Constants.STR_LOG_FORMAT_TAG, Integer.toHexString(adView.hashCode()), tag);

        if (level <= currentLogLevel) {
            switch (type) {
                case LOG_TYPE_ERROR:
                    Log.e(resultTag, String.format(Constants.STR_LOG_FORMAT_MESSAGE, msg));
                    break;
                case LOG_TYPE_WARNING:
                    Log.w(resultTag, String.format(Constants.STR_LOG_FORMAT_MESSAGE, msg));
                    break;
                default:
                    Log.i(resultTag, String.format(Constants.STR_LOG_FORMAT_MESSAGE, msg));
            }
        }
    }

    public static void staticLog(int level, int type, String tag, String msg) {
        if (level <= defaultLogLevel) {
            switch (type) {
                case LOG_TYPE_ERROR:
                    Log.e(tag, String.format(Constants.STR_LOG_FORMAT_MESSAGE, msg));
                    break;
                case LOG_TYPE_WARNING:
                    Log.w(tag, String.format(Constants.STR_LOG_FORMAT_MESSAGE, msg));
                    break;
                default:
                    Log.i(tag, String.format(Constants.STR_LOG_FORMAT_MESSAGE, msg));
            }
        }
    }

    public void setLogLevel(int logLevel) {
        currentLogLevel = logLevel;
        switch (logLevel) {
            case LOG_LEVEL_CRITICAL:
                log(LOG_LEVEL_CRITICAL, LOG_TYPE_INFO, Constants.STR_LOG_ADLOG_setLogLevel, Constants.STR_LOG_LEVEL_1);
                break;
            case LOG_LEVEL_NORMAL:
                log(LOG_LEVEL_CRITICAL, LOG_TYPE_INFO, Constants.STR_LOG_ADLOG_setLogLevel, Constants.STR_LOG_LEVEL_2);
                break;
            case LOG_LEVEL_ALL:
                log(LOG_LEVEL_CRITICAL, LOG_TYPE_INFO, Constants.STR_LOG_ADLOG_setLogLevel, Constants.STR_LOG_LEVEL_3);
                break;
            default:
                log(LOG_LEVEL_CRITICAL, LOG_TYPE_INFO, Constants.STR_LOG_ADLOG_setLogLevel, Constants.STR_LOG_LEVEL_NONE);
        }
    }

    public int getLogLevel() {
        return currentLogLevel;
    }
}
