package com.octobird.advertising.samples;

import java.sql.Date;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

import com.octobird.advertising.AdControl;
import com.octobird.advertising.AdLog;

public class SimpleBanner extends Activity {
	private LinearLayout linearLayout;
	AdControl adserverView;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        ((Button) findViewById(R.id.btnTestOn)).setOnClickListener(new OnClickListener() {
        	@Override
			public void onClick(View v) {
        		adserverView.Test(true);
        	}
        });
        ((Button) findViewById(R.id.btnTestOff)).setOnClickListener(new OnClickListener() {
        	@Override
			public void onClick(View v) {
        		adserverView.Test(false);
        	}
        });
        ((Button) findViewById(R.id.btnRefresh)).setOnClickListener(new OnClickListener() {
        	@Override
			public void onClick(View v) {
        		adserverView.Refresh();
        	}
        });
        
        linearLayout = (LinearLayout) findViewById(R.id.frameAdContent);
        
        AdLog.setDefaultLogLevel(AdLog.LOG_LEVEL_ALL);
        
        adserverView = new AdControl(this,"1234");

        adserverView.setLayoutParams(new ViewGroup.LayoutParams(getDip(300), getDip(50)));
        adserverView.Test(true);

        linearLayout.addView(adserverView);
    }
    
    private int getDip(int pixel)
    {
        float scale = getBaseContext().getResources().getDisplayMetrics().density;
        return (int) (pixel * scale + 0.5f);
    }
}