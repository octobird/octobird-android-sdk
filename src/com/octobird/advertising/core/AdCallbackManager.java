package com.octobird.advertising.core;

import com.octobird.advertising.AdControl;
import com.octobird.advertising.OnAdEventHandler;

import android.os.Handler;
import android.os.Looper;

public class AdCallbackManager {
    private Handler handler = new Handler(Looper.getMainLooper());
    private OnAdEventHandler onAdEventHandler;
    private AdControl sender;

    public AdCallbackManager(AdControl sender) {
        this.sender = sender;
    }

    public void setOnAdEventHandler(OnAdEventHandler onAdEventHandler) {
        this.onAdEventHandler = onAdEventHandler;
    }

    public OnAdEventHandler getOnAdEventHandler() {
        return onAdEventHandler;
    }

    public void error(final String error) {
        if (onAdEventHandler != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    onAdEventHandler.error(sender, error);
                }
            });
        }
    }

    public void engagedChange(final boolean engaged) {
        if (onAdEventHandler != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    onAdEventHandler.engagedChange(sender, engaged);
                }
            });
        }
    }

    public void refresh() {
        if (onAdEventHandler != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    onAdEventHandler.refresh(sender);
                }
            });
        }
    }

}
