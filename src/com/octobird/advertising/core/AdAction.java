package com.octobird.advertising.core;


import com.octobird.advertising.AdLog;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class AdAction {
    public static final int TYPE_URL = 1;
    public static final int TYPE_PHONE = 2;
    public int type;
    public String name;
    public String action;

    @Override
    public String toString() {
        switch (type) {
            case TYPE_URL:
                return Constants.BUTTON_BROWSE_TITLE;
            case TYPE_PHONE:
                return String.format(Constants.BUTTON_CALL_TITLE, action);
        }

        return Constants.STR_EMPTY;
    }

    public void run(Context context) {
        try {
            Intent intent;
            switch (type) {
                case TYPE_URL:
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse(action));
                    context.startActivity(intent);
                    break;
                case TYPE_PHONE:
                    intent = new Intent(Intent.ACTION_CALL, Uri.parse(String.format(Constants.STR_ACTION_TYPE_PHONE, action)));
                    context.startActivity(intent);
                    break;
            }
        } catch (Exception e) {
            AdLog.staticLog(AdLog.LOG_LEVEL_NORMAL, AdLog.LOG_TYPE_ERROR, Constants.STR_LOG_ADACTION, e.getMessage());
        }
    }
}
