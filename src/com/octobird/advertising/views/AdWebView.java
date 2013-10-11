package com.octobird.advertising.views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.view.*;
import android.webkit.*;
import android.widget.*;

import com.octobird.advertising.AdControl;
import com.octobird.advertising.AdLog;
import com.octobird.advertising.core.Utils;
import com.octobird.advertising.layouts.AdLayoutVector;

import java.net.HttpURLConnection;
import java.net.URL;

public class AdWebView extends WebView {
    public enum ACTION {PLAY_AUDIO, PLAY_VIDEO}

    private enum ViewState {DEFAULT, RESIZED, EXPANDED, HIDDEN}

    public static final String ACTION_KEY = "action";
    public static final String PLAYER_PROPERTIES = "player_properties";
    public static final String EXPAND_URL = "expand_url";
    public static final String DIMENSIONS = "expand_dimensions";
    public static final String EXPAND_SET_SIZE_MANUAL = "expand_set_size_manual";
    private Handler handler = new Handler(Looper.getMainLooper());
    private AdLog adLog;
    private OnOrmmaListener ormmaListener;
    
    private static String mScriptPath;
    private boolean isExpanded = false;
    private AdWebView expandParent;
    private ViewState mViewState = ViewState.DEFAULT;
    private int lastX;
    private int lastY;
    public AdControl adControl;
    private Button buttonClose;
    private OpenUrlThread openUrlThread;
    private boolean isFirstLoad = true;
    private AdLayoutVector adLayoutVector;
    
    public AdWebView(Context context, AdControl adControl, AdLayoutVector adLayoutVector) {
        super(context);
	    this.adLayoutVector = adLayoutVector;    
        initialize(context, adControl);
    }

    public AdWebView(Context context, AdControl adControl, boolean expanded, AdWebView expandParent,
    		AdLayoutVector adLayoutVector) {
        super(context);
        this.adLayoutVector = adLayoutVector;
        initialize(context, adControl);
        isExpanded = expanded;
        this.expandParent = expandParent;
        mViewState = ViewState.EXPANDED;
    }

    private void initialize(Context context, AdControl adControl) {
        this.adControl = adControl;
        WebSettings webSettings = getSettings();
        webSettings.setSavePassword(false);
        webSettings.setSaveFormData(false);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(false);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);

        setWebViewClient(new AdWebViewClient());
        setWebChromeClient(new AdWebChromeClient());
        setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        setBackgroundColor(adControl.getBackgroundColor() == 0 ? Color.WHITE : adControl.getBackgroundColor());

        /*buttonClose = new Button(context);
        buttonClose.setLayoutParams(getLayoutParamsByDrawableSize("b_close.png"));
        buttonClose.setBackgroundDrawable(Utils.GetSelector(context, "b_close.png", "b_close.png", "b_close.png"));
        buttonClose.setVisibility(View.INVISIBLE);
        buttonClose.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        {
                            //injectJavaScript("ormma.close();");
                            buttonClose.setVisibility(View.INVISIBLE);
                        }
                    }
                });
            }
        });

        LinearLayout ll = new LinearLayout(context);
        ll.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
        ll.setGravity(Gravity.RIGHT);
        ll.addView(buttonClose);
        addView(ll);*/
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    @Override
    protected void onSizeChanged(int w, int h, int ow, int oh) {
        String script = String.format(
                "Ormma.fireEvent(ORMMA_EVENT_SIZE_CHANGE, {dimensions : {width : %d, height: %d}});", w, h);
        //injectJavaScript(script);
        super.onSizeChanged(w, h, ow, oh);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mViewState == ViewState.EXPANDED) {
                if (expandParent != null) {
                    //expandParent.injectJavaScript("ormma.close();");
                } else {
                    //injectJavaScript("ormma.close();");
                }
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    public void loadContent(String content) {
        requestFocus();
        StringBuilder data = new StringBuilder();
        data.append("<html><head>");
        data.append("<style>*{margin:0;padding:0}</style>");
       // data.append("<script src=\"file://");
       // data.append(mScriptPath);
       // data.append("\" type=\"text/javascript\"></script>");
        data.append("<meta name=\"viewport\" content=\"target-densitydpi=device-dpi, initial-scale=1.0, maximum-scale=1.0, user-scalable=no\"/></head>");
        data.append("<body style=\"margin: 0px; padding: 0px; width: 100%; height: 100%\">");
        data.append(content);
        data.append("</body></html>");

        loadDataWithBaseURL(null, data.toString(), "text/html", "UTF-8", null);

        handler.post(new Runnable() {
            @Override
            public void run() {
                getLayoutParams().width = lastX;
                getLayoutParams().height = lastY;
                requestLayout();
            }
        });
    }

    public void setLayoutParams(ViewGroup.LayoutParams params) {
        lastX = params.width;
        lastY = params.height;
        super.setLayoutParams(params);
    }

    private ViewGroup.LayoutParams getLayoutParamsByDrawableSize(String fileName) {
        int sizeDrawableWidth = ViewGroup.LayoutParams.WRAP_CONTENT;
        int sizeDrawablehHeight = ViewGroup.LayoutParams.WRAP_CONTENT;

        try {
            BitmapDrawable sizeDrawable = (BitmapDrawable) Utils.GetDrawable(getContext(), fileName);
            if (sizeDrawable != null) {
                sizeDrawableWidth = sizeDrawable.getBitmap().getWidth();
                sizeDrawablehHeight = sizeDrawable.getBitmap().getHeight();
            }
        } catch (Exception e) {
            if (adLog != null) {
                adLog.log(AdLog.LOG_LEVEL_NORMAL, AdLog.LOG_TYPE_ERROR, "AdWebView.getLayoutParamsByDrawableSize", e.getMessage());
            }
        }

        return new ViewGroup.LayoutParams(sizeDrawableWidth, sizeDrawablehHeight);
    }

     public String getState() {
        return mViewState.toString().toLowerCase();
    }

    public boolean isInterstitial() {
        return isExpanded;
    }

    public void useCloseButton(final boolean show) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (show) {
                    buttonClose.setVisibility(View.VISIBLE);
                } else {
                    buttonClose.setVisibility(View.GONE);
                }
            }
        });
    }

    protected void onPageFinished() {
    	if (isFirstLoad){
    		isFirstLoad = false;
    		adLayoutVector.startAnimation();
    	}
        //injectJavaScript("Ormma.ready();");
    }

    boolean ormaEnabled = false;

    public void ormmaEvent(String name, String params) {
        if (ormmaListener != null) {
            if (!ormaEnabled)
                ormmaListener.event(this, "ormmaenabled", "");
            ormaEnabled = true;
            if (params != null)
                params = params.replace(";", "&");
            ormmaListener.event(this, name, params);
        }
    }

    /*public void injectJavaScript(String str) {
        try {
            super.loadUrl(String.format("javascript:%s", str));
        } catch (Exception e) {
            if (adLog != null) {
                adLog.log(AdLog.LOG_LEVEL_CRITICAL, AdLog.LOG_TYPE_ERROR, "AdWebView.injectJavaScript", e.getMessage());
            }
        }
    }*/

    public OnOrmmaListener getOnOrmmaListener() {
        return ormmaListener;
    }

    public void setOnOrmmaListener(OnOrmmaListener ormmaListener) {
        this.ormmaListener = ormmaListener;
    }

    public interface OnOrmmaListener {
        public void event(AdWebView sender, String name, String params);
    }

    private class AdWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (adLog != null) {
                adLog.log(AdLog.LOG_LEVEL_ALL, AdLog.LOG_TYPE_INFO,
                        "OverrideUrlLoading", url);
            }

            openUrlInExternalBrowser(url);
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            ((AdWebView) view).onPageFinished();
        }

        @Override
        public void onReceivedError(WebView view, int errorCode,
                                    String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
        }
    }

    private class AdWebChromeClient extends WebChromeClient {

        public AdWebChromeClient() {
        }

        @Override
        public boolean onJsAlert(WebView view, String url, String message,
                                 JsResult result) {
            return super.onJsAlert(view, url, message, result);
        }

        @Override
        public void onShowCustomView(final View view, final CustomViewCallback callback) {
            super.onShowCustomView(view, callback);

            if (view instanceof FrameLayout) {
                int contentViewTop = ((Activity) getContext()).getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();

                final RelativeLayout fullscreenFrame = new RelativeLayout(getContext());
                fullscreenFrame.setLayoutParams(new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
                fullscreenFrame.setPadding(0, contentViewTop, 0, 0);
                fullscreenFrame.setBackgroundColor(Color.BLACK);
                fullscreenFrame.addView(view, new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT, Gravity.CENTER));

                Button buttonClose = new Button(getContext());
                buttonClose.setBackgroundDrawable(Utils.GetSelector(getContext(), "b_close.png", "b_close.png", "b_close.png"));
                buttonClose.setLayoutParams(getLayoutParamsByDrawableSize("b_close.png"));
                buttonClose.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                fullscreenFrame.setVisibility(View.GONE);
                                callback.onCustomViewHidden();
                            }
                        });
                    }
                });
                LinearLayout ll = new LinearLayout(getContext());
                ll.setLayoutParams(new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
                ll.setGravity(Gravity.RIGHT);
                ll.addView(buttonClose);
                fullscreenFrame.addView(ll);

                ((ViewGroup) ((Activity) getContext()).getWindow().getDecorView())
                        .addView(fullscreenFrame, new FrameLayout.LayoutParams(
                                ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT, Gravity.CENTER));
            }
        }

        @Override
        public void onHideCustomView() {
            super.onHideCustomView();
        }
    }

    private class OpenUrlThread extends Thread {
        private AdWebView adView;
        private Context context;
        private String url;

        public OpenUrlThread(Context context, AdWebView adView, String url) {
            this.adView = adView;
            this.context = context;
            this.url = url;
        }

        @Override
        public void run() {
            adView.openUrlActivity(context, url);
        }
    }

    private void openUrlInExternalBrowser(String url) {
        if (url != null) {
            if ((openUrlThread == null) || (openUrlThread.getState().equals(Thread.State.TERMINATED))) {
                openUrlThread = new OpenUrlThread(getContext(), this, url);
                openUrlThread.start();
            } else if (openUrlThread.getState().equals(Thread.State.NEW)) {
                openUrlThread.start();
            }
        }
    }

    private void openUrlActivity(Context context, String url) {
        String lastUrl = null;
        String newUrl = url;
        URL connectURL;

        while (!newUrl.equals(lastUrl)) {
            lastUrl = newUrl;
            try {
                connectURL = new URL(newUrl);
                HttpURLConnection conn = (HttpURLConnection) connectURL.openConnection();
                newUrl = conn.getHeaderField("Location");
                if (newUrl == null) newUrl = conn.getURL().toString();
            } catch (Exception e) {
                newUrl = lastUrl;
            }
        }

        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(newUrl));
            context.startActivity(intent);
        } catch (Exception e) {
            if (adLog != null) {
                adLog.log(AdLog.LOG_LEVEL_CRITICAL, AdLog.LOG_TYPE_ERROR, "openUrlActivity",
                        String.format("url=%s; error=%s", newUrl, e.getMessage()));
            }
        }
    }

    public AdLog getLog() {
        return adLog;
    }

    public void setAdLog(AdLog adLog) {
        this.adLog = adLog;
    }

    public boolean isExpanded() {
        return (mViewState == ViewState.EXPANDED) || (mViewState == ViewState.RESIZED);
    }

	public void close() {
		
	}

}
