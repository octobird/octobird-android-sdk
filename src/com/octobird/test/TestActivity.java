package com.octobird.test;

import java.sql.Date;

import com.octobird.advertising.AdControl;
import com.octobird.advertising.AdLog;
import com.octobird.advertising.OnAdEventHandler;
import com.octobird.advertising.core.Constants;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.app.Instrumentation.ActivityMonitor;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.Window.Callback;
import android.view.WindowManager.LayoutParams;
import android.view.accessibility.AccessibilityEvent;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;

public class TestActivity extends Activity {
	
	private LinearLayout linearLayout;
	AdControl adserverView;
	//byte[] b = new byte[12 * 1000 * 1000];
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
       
        linearLayout = (LinearLayout) findViewById(R.id.frameAdContent);
        /*ArrayList<String> t = new ArrayList<String>();
        t.add("1");
        t.add("2");
        t.add("3");
        t.add("4");        
        t.add("5");
        t.add("6");
        
        Iterator<String> it = t.iterator();
        while(it.hasNext())
        {
        	String str = it.next();
        	it.remove();
        }*/
        
        ((Button) findViewById(R.id.btn2)).setOnClickListener(new OnClickListener() {
        	@Override
			public void onClick(View v) {
        		adserverView.Refresh();
        	}
        });
        
        ((Button) findViewById(R.id.interstitialAd)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//String str = ((Button) findViewById(R.id.interstitialAd)).toString();
				//str = "";
				//adserverView.setVisibility(View.INVISIBLE);//Refresh();
				/*int x = (int)(Math.random()*200) + 200;               
				int y = (int)(Math.random()*200) + 50;
				ViewGroup.LayoutParams lp = adserverView.getLayoutParams();
				lp.height = y;
				lp.width = x;
				adserverView.requestLayout();*/
			
				/*if(adserverView.getParent() ==null)
				{
					adserverView.setLayoutParams(new ViewGroup.LayoutParams((int)(320*getApplicationContext().getResources().getDisplayMetrics().density),
					        	        		(int)(100*getApplicationContext().getResources().getDisplayMetrics().density)));
					linearLayout.addView(adserverView);
				}
				else
					linearLayout.removeView(adserverView);*/
				
				//adserverView.setVisibility(View.INVISIBLE);
				adserverView.Refresh();
				
				//adserverView.setVisibility(adserverView.getVisibility() == View.VISIBLE ? View.INVISIBLE : View.VISIBLE);
			}
		});
		
        AdLog.setDefaultLogLevel(AdLog.LOG_LEVEL_ALL);
        Log.e("START","<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
        
        //http://mobileads.si.msn-int.com/v3/Delivery/Placement/?&pubid=2ac11907-8e3b-4b9b-b803-2ac834fa559a&
        //	pid=p:2954455&adm=1&cfmt=html5richmedia&sft=jpeg,png,gif&w=480&h=80&fmt=json&cltp=app&dim=le&kw=freecell&loc=39.788428~-89.665028&rad=100&loptin=1&ctry=us&lc=en-US&idtp=mid&uid=275abe6a-f437-4c41-b4fb-ae0b00d5854x
        
        //http://mobileads.si.msn-int.com/v3/Delivery/Placement/?&adm=2&cltp=app&cfmt=image,text&ua=dummy&fmt=json&pubid=2ac11907-8e3b-4b9b-b803-2ac834fa559a&pid=p%3A2954455
        
        //http://mobileads.si.msn-int.com/v3/Delivery/Placement/?&pubid=2ac11907-8e3b-4b9b-b803-2ac834fa559a&pid=p:2954455&adm=1&cfmt=image,text,html5richmedia&fmt=json
        
        //adserverView.setAdserverURL("http://mobileads.si.msn-int.com/v3/Delivery/Placement/");
        //adserverView = new AdControl(this,"accba1e3-1a77-4032-a77f-b35e42fe4ebc","p:01003");
        //adserverView = new AdControl(this,"accba1e3-1a77-4032-a77f-b35e42fe4ebc","p:11001");
       // Constants.ADSERVER_URL = "http://188.187.188.71:8080/new_map/request.php";
        
       //adserverView = new AdControl(this,null,100,"113","0");
        //adserverView = new AdControl(this,"113","333");
        //adserverView = new AdControl(this,null,100,"accba1e3-1a77-4032-a77f-b35e42fe4ebc","p:11003");
        //adserverView = new AdControl(this,"accba1e3-1a77-4032-a77f-b35e42fe4ebc","p:22002");
        //adserverView = new AdControl(this);
        //adserverView.ApplicationId("113");
        //adserverView.AdUnitId("1");
        //adserverView.ApplicationId("accba1e3-1a77-4032-a77f-b35e42fe4ebc");
        //adserverView.AdUnitId("p:22002");        
        
        //https://mobileads.msn.com/v3/Delivery/Placement?adm=1&idtp=ANID&pubid=accba1e3-1a77-4032-a77f-b35e42fe4ebc&pid=p:11001&ua=&rurl=&uid=e0acdbe8ba0f4428a80c724cb94c9892&adlt=a1&kwh=&w=500&h=480&dim=le&loptin=1&ctry=&state=&city=&zip=&nct=1&cltp=app&cfmt=text,image,html5richmedia&fmt=json
        //adserverView.ApplicationId("accba1e3-1a77-4032-a77f-b35e42fe4ebc");
        //adserverView.AdUnitId("p:11001");
        
        adserverView = new AdControl(this,"1234");
        
        adserverView.setMinimumHeight(100);
        //adserverView.setLayoutParams(new ViewGroup.LayoutParams((int)(320*getApplicationContext().getResources().getDisplayMetrics().density),
        //adserverView.setLayoutParams(new ViewGroup.LayoutParams((int)(300*getApplicationContext().getResources().getDisplayMetrics().density),
        //	        		(int)(100*getApplicationContext().getResources().getDisplayMetrics().density)));
        //adserverView.setLayoutParams(new ViewGroup.LayoutParams((int)(300*getApplicationContext().getResources().getDisplayMetrics().density),
        //		(int)(50*getApplicationContext().getResources().getDisplayMetrics().density)));
        //adserverView.setLayoutParams(new ViewGroup.LayoutParams(450, 75));
        //adserverView.getLayoutParams().
        //adserverView.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
        adserverView.setLayoutParams(new ViewGroup.LayoutParams(300,50));
        adserverView.setMinimumHeight(100);
        adserverView.GPS(45.54544,21.54645);
        //adserverView.Blocks("n:top");
        adserverView.MaxWidth(300);
        adserverView.MaxHeight(50);
        adserverView.Test(true);
        adserverView.Gender(AdControl.Gender.Male);
        adserverView.Age(66);
        adserverView.Birthday(Date.valueOf("1980-01-03"));
        Date date = adserverView.Birthday();
        adserverView.City("ÌîñêâàÎéëà");
        //adserverView.setBackgroundColor(Color.RED);
        //adserverView.AutoRefresh(false);
        adserverView.Keywords("sss sss%+2#3");
        
        adserverView.OnAdEventHandler(new OnAdEventHandler() {
			
			@Override
			public void refresh(AdControl sender) {
				Log.e("refresh", "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
			}
			
			@Override
			public void error(AdControl sender, String error) {
				Log.e("error", error);
				
			}
			
			@Override
			public void engagedChange(AdControl sender, boolean engaged) {
				Log.e("engagedChange", engaged ? "true" : "false");				
			}
		});
        //adserverView.setUpdateTime(60);
        //adserverView = new AdServerView(this,"111","3");
        //adserverView.setAdserverURL("http://192.168.1.162/map/map.php");
        //adserverView = new AdServerView(this,"test_client","image480_80");
        linearLayout.addView(adserverView);
        
        /*WebView wv = new WebView(getApplicationContext());
    	//wv.setBackgroundColor(0);
    	wv.setBackgroundResource(R.drawable.icon);
    	wv.setBackgroundColor(0);
    	 String  content = "<body style=\"background-color: #FF000000 ;margin: 0px; padding: 0px; width: 100%; height: 100%\"><table height=\"100%\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\"><tr><td style=\"text-align:center;vertical-align:middle;\">" + "ssdssss" + "</td></tr></table></body></html>";
    	// String  content = "<body style=\"background-color:#ff0000\"> ddddd </body>";
    	//String content = "ssss";
    	wv.loadDataWithBaseURL(null, content, "text/html", "UTF-8", null);
    	 
    	 wv.setLayoutParams(new ViewGroup.LayoutParams((int)(320*getApplicationContext().getResources().getDisplayMetrics().density),
    		        	        		(int)(100*getApplicationContext().getResources().getDisplayMetrics().density)));
    	linearLayout.addView(wv);*/
        
        //setContentView(adserverView);
        /*(new Timer()).schedule(new TimerTask() {
			
			@Override
			public void run() {
				adserverView.Refresh();
			}
		}, 100,100);*/
    }
    
    @Override
    protected void onDestroy() {    	
    	super.onDestroy();
    }
    
   
}