package com.octobird.advertising.layouts;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.*;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.webkit.*;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.octobird.advertising.*;
import com.octobird.advertising.core.AdAction;
import com.octobird.advertising.core.AdCallbackManager;
import com.octobird.advertising.core.Constants;
import com.octobird.advertising.views.AdWebView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

class AdLayout extends FrameLayout {
    private ArrayList<AdAction> actions = new ArrayList<AdAction>();
    private ArrayList<String> impressions = new ArrayList<String>();
    private Animation animationBegin;
    private Animation animationEnd;
    private OnClickListener clickListener;
    private AdControl adControl;
    private AdLayout instance;
    private int pos;
    private AdWebView adWebView = null;
    private  AdLayoutVector instanceVector;

    public AdLayout(Context context, AdControl adControl, OnClickListener clickListener) {
        super(context);
        setBackgroundColor(Color.TRANSPARENT);
        instance = this;
        this.adControl = adControl;
        this.clickListener = clickListener;
        animationBegin = new AlphaAnimation(0, 1);
        animationBegin.setDuration(Constants.ANIMATION_TIME_FRAME);
        animationBegin.setFillAfter(true);

        animationEnd = new AlphaAnimation(1, 0);
        animationEnd.setDuration(Constants.ANIMATION_TIME_FRAME);
        animationEnd.setFillAfter(true);
    }

    public boolean parseJson(String data, AdLayoutVector instanceVector, AdCallbackManager event) {
    	this.instanceVector = instanceVector;
    	JSONArray arr = null;
        String title = null;
        String descr = null;
        String url = null;
        String phone = null;
        String image = null;
        String html = null;

        try {
            data = "[" + data + "]";
            arr = new JSONArray(data);
            JSONObject obj = (JSONObject) arr.get(0);
            //obj = (JSONObject) obj.get("adrsp");
            if ("error".equals(obj.get("status"))) {
                /*adControl.getAdLog().log(AdLog.LOG_LEVEL_CRITICAL, AdLog.LOG_TYPE_ERROR,
                        Constants.STR_LOG_ADLAYOUT_AdJSON_parse, String.format(Constants.STR_LOG_ADLAYOUT_AdJSON_parse_message,
                        obj.get("code"), obj.get("message")));
                if (event != null)
                    event.error(Constants.STR_LOG_ADLAYOUT_AdJSON_parse + " " + String.format(Constants.STR_LOG_ADLAYOUT_AdJSON_parse_message,
                            obj.get("code"), obj.get("message")));*/
                return false;
            }
            
            /*if(!"3.0".equals(obj.get("version")))
            {
            	 adControl.getAdLog().log(AdLog.LOG_LEVEL_CRITICAL, AdLog.LOG_TYPE_ERROR,
                         Constants.STR_LOG_ADLAYOUT_AdJSON_parse, Constants.STR_LOG_ADLAYOUT_AdJSON_INVALID_PROTOCOL);
                 if (event != null)
                     event.error(Constants.STR_LOG_ADLAYOUT_AdJSON_parse + " " + Constants.STR_LOG_ADLAYOUT_AdJSON_INVALID_PROTOCOL);
            	return false;
            }*/

            obj = (JSONObject) obj.get("blocks");
            //obj = (JSONObject) obj.get("ad");
            //obj = (JSONObject) obj.get("cr");
            arr = (JSONArray) obj.get("main");
            
            for (int x = 0; x < arr.length(); x++) {
                JSONObject ob = arr.getJSONObject(x);
                if(ob.get("html")!=null)
                {
                	html = ob.get("html").toString();
                }
                /*if (ob.get("type").toString().equals("uri")) {
                    url = ob.optString("label");
                    String value = ob.getString("value");
                    addAction(AdAction.TYPE_URL, url, value);
                }
                if (ob.get("type").toString().equals("phone")) {
                    phone = ob.optString("label");
                    String value = ob.getString("value");
                    if (ContentManager.isSimAvailable())
                        addAction(AdAction.TYPE_PHONE, phone, value);
                }
                if (ob.get("type").toString().equals("text")) {
                    if (ob.get("name").toString().equals("title"))
                        title = ob.get("value").toString();
                    if (ob.get("name").toString().equals("description"))
                        descr = ob.get("value").toString();
                }
                if (ob.get("type").toString().equals("image")) {
                    image = ob.get("uri").toString();
                }
                if (ob.get("type").toString().equals("content")) {
                    html = ob.get("value").toString();
                }*/
            }

            if (image != null)
                addImage(image, instanceVector);
            else {
                if (descr != null)
                    addDescription(descr);
                if (url != null)
                    addUrl(title, url);
                if (phone != null)
                    addUrl(title, phone);
                if (html != null)
                    addHTML(html);

            }

            {
                Object o = obj.opt("evt");
                if (o != null) {
                    if (o.getClass().getName()
                            .equals(JSONObject.class.getName())) {
                        JSONObject ob = (JSONObject) o;
                        if (ob.get("action").toString().equals("impr")) {
                            String uri = ob.get("uri").toString();
                            impressions.add(uri);
                        }
                    } else {
                        JSONArray obArr = (JSONArray) o;
                        for (int x = 0; x < obArr.length(); x++) {
                            JSONObject ob = obArr.getJSONObject(x);
                            if (ob.get("action").toString().equals("impr")) {
                                String uri = ob.get("uri").toString();
                                impressions.add(uri);
                            }
                        }
                    }
                }
            }

            //if (image == null) instanceVector.startAnimation();
        } catch (Exception e) {
            adControl.getAdLog().log(AdLog.LOG_LEVEL_CRITICAL, AdLog.LOG_TYPE_ERROR,
                    Constants.STR_LOG_ADLAYOUT_AdJSON_parse, e.getMessage());
            if (event != null) event.error(Constants.STR_LOG_ADLAYOUT_AdJSON_parse + " " + e.getMessage());
            return false;
        }


        pos = getChildCount() - 1;
        return true;
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        sendImpressions();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        sendImpressions();
    }

    public void sendImpressions() {
        if ((impressions.size() > 0) && isShown()) {
            for (String impression : impressions) {
                ContentManager.getInstance().sendImpression(impression, adControl.getAdLog());
            }
            impressions.clear();
        }
    }

    private void addAction(int type, String name, String action) {
        AdAction act = new AdAction();
        act.type = type;
        act.name = name;
        act.action = action;
        actions.add(act);
    }

    public void animateBanner() {
        int count = getChildCount();
        if (count > 1) {
            int beginI = pos + 1 == count ? 0 : pos + 1;
            int endI = pos;
            for (int x = 0; x < count; x++) {
                if ((x == beginI) || (x == endI)) {
                    getChildAt(x).setVisibility(View.VISIBLE);
                } else {
                    getChildAt(x).clearAnimation();
                    getChildAt(x).setVisibility(View.INVISIBLE);
                }
            }
            getChildAt(beginI).startAnimation(animationBegin);
            getChildAt(endI).startAnimation(animationEnd);
            pos = beginI;
        }
    }

    public ArrayList<AdAction> getAdActions() {
        return actions;
    }

    private LinearLayout createLayout() {
        LinearLayout ll = new LinearLayout(getContext());
        ll.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.FILL_PARENT));
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setPadding((int) (5 * getContext().getResources()
                .getDisplayMetrics().density), 0, (int) (5 * getContext()
                .getResources().getDisplayMetrics().density), 0);
        ll.setBackgroundColor(Color.BLACK);
        ll.setGravity(Gravity.CENTER);
        ll.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onClick(instance);
            }
        });
        return ll;
    }

    private void addHTML(final String content) {
        final LinearLayout ll = createLayout();
        ll.setPadding(0, 0, 0, 0);
        ll.setBackgroundColor(Color.TRANSPARENT);
        //ll.setBackgroundColor(Color.GREEN);

        adWebView = new AdWebView(getContext(), adControl, instanceVector);
        adWebView.loadContent(content);
        adWebView.setAdLog(adControl.getAdLog());        
        adWebView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.FILL_PARENT));
        ll.addView(adWebView);
        addView(ll);
        
        /*ViewTreeObserver vto = adWebView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				//adWebView.loadContent(content);
			}
        
        });*/
        //adWebView.loadContent(content);
        
        //instanceVector.startAnimation(); 
    }

    private void addImage(final String url, final AdLayoutVector instanceVector) {
        final LinearLayout ll = createLayout();
        ll.setPadding(0, 0, 0, 0);

        WebView image = new WebView(getContext());

        image.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.FILL_PARENT));
        ll.addView(image);
        addView(ll);

        WebSettings webSettings = image.getSettings();
        webSettings.setSavePassword(false);
        webSettings.setSaveFormData(false);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(false);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        image.setVerticalScrollBarEnabled(false);
        image.setHorizontalScrollBarEnabled(false);


        String data = "<html><head>"
                + "<style>*{margin:0;padding:0}</style>"
                + "<script language=\"javascript\">"
                + "function ScaleSize(){"
                + "var dx = document.body.clientWidth / document.getElementById(\"imgTag\").naturalWidth;"
                + "var dy = document.body.clientHeight/ document.getElementById(\"imgTag\").naturalHeight;"
                + "var dd = dx; if(dx>dy) dd = dy;"
                + "if (dd<1 && dd!=0) {"
                + "document.getElementById(\"imgTag\").style.width = document.getElementById(\"imgTag\").naturalWidth * dd;"
                + "document.getElementById(\"imgTag\").style.height = document.getElementById(\"imgTag\").naturalHeight * dd;"
                + "}"
                + "}"
                + "</script>"
                + "</head>"
                + "<body onload=\"javascript:ScaleSize();\" onresize=\"javascript:ScaleSize()\" style=\"background-color:#000000; margin: 0px; padding: 0px; width: 100%; height: 100%\">"
                + "<table height=\"100%\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\"><tr><td style=\"text-align:center;vertical-align:middle;\">"
                + "<img id=\"imgTag\" src=\"" + url + "\"/>"
                + "</td></tr></table></body></html>";

        image.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        image.loadDataWithBaseURL(null, data, "text/html", "UTF-8", null);

        image.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN)
                    clickListener.onClick(instance);
                return false;
            }
        });

        WebChromeClient mWebChromeClient = new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message,
                                     JsResult result) {
                return super.onJsAlert(view, url, message, result);
            }
        };

        image.setWebChromeClient(mWebChromeClient);
        image.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                instanceVector.startAnimation();
            }
        });
    }

    private void addUrl(final String title, final String url) {
        final LinearLayout ll = createLayout();

        ViewTreeObserver vto = ll.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (url.equals("")) {
                    AutoResizeTextView txt = fitToLine(ll, title, 17, 12,
                            getHeight(), true, 2);
                    txt.setTextColor(Color.WHITE);
                    txt.setGravity(Gravity.CENTER);
                    txt.setTextColor(Color.argb(255, 20, 184, 105));

                } else {
                    final AutoResizeTextView txt = fitToLine(ll, title, 17, 13,
                            getHeight() / 2, true, 1);
                    txt.setTypeface(Typeface.DEFAULT_BOLD);
                    txt.setTextColor(Color.argb(255, 20, 184, 105));
                    final AutoResizeTextView txt2 = fitToLine(ll, url, 15, 12,
                            getHeight() / 2, false, 1);
                    txt2.setTextColor(Color.argb(255, 73, 119, 193));
                    txt.setOnResizeListener(new AutoResizeTextView.OnTextResizeListener() {
                        @Override
                        public void onTextResize(TextView textView, float oldSize, float newSize) {
                            txt.setGravity(Gravity.CENTER);
                            if (txt2.getTextSize() > txt.getTextSize())
                                txt2.setTextSize(txt.getTextSize());
                            txt2.setGravity(Gravity.CENTER);
                        }
                    });
                }
                ViewTreeObserver obs = ll.getViewTreeObserver();
                obs.removeGlobalOnLayoutListener(this);
            }
        });
        addView(ll);
    }

    private void addDescription(final String descr) {
        final LinearLayout ll = createLayout();

        ViewTreeObserver vto = ll.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                AutoResizeTextView txt = fitToLine(ll, descr, 15, 12,
                        getHeight(), false, 2);
                txt.setTextColor(Color.WHITE);
                txt.setGravity(Gravity.CENTER);

                ViewTreeObserver obs = ll.getViewTreeObserver();
                obs.removeGlobalOnLayoutListener(this);
            }
        });
        addView(ll);
    }

    private AutoResizeTextView fitToLine(LinearLayout ll, String text,
                                         final int maxSize, final int minSize, final int maxHeight,
                                         boolean isBold, int maxLines) {
        AutoResizeTextView txt = new AutoResizeTextView(getContext());
        txt.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.FILL_PARENT, maxHeight));
        txt.setTextSize(maxSize);
        if (isBold)
            txt.setTypeface(null, Typeface.BOLD);
        txt.setMinTextSize(minSize);
        txt.setMaxTextSize(maxSize);
        txt.setMaxLine(maxLines);
        txt.setText(text);
        txt.setTextColor(Color.BLACK);
        ll.addView(txt);
        return txt;
    }

    public void close() {
        for (int x = 0; x < getChildCount(); x++) {
            Object obj = getChildAt(x);
            if (obj.getClass().getName().equals(AdWebView.class.getName())) {
                AdWebView adWeb = ((AdWebView) obj);
                if (adWeb.isExpanded()) adWeb.close();
            }
        }
    }

    public boolean isExpanded() {
        return ((adWebView != null) && adWebView.isExpanded());
    }
}
