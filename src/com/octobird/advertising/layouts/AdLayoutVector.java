package com.octobird.advertising.layouts;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;

import com.octobird.advertising.AdControl;
import com.octobird.advertising.core.AdAction;
import com.octobird.advertising.core.AdCallbackManager;
import com.octobird.advertising.core.Constants;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class AdLayoutVector extends FrameLayout implements OnClickListener {
    private Handler handler = new Handler();
    private Timer animationTimer = new Timer();
    private AdLayout currentAd = null;
    private AdLayout newAd = null;
    private Animation animationBegin;
    private Animation animationEnd;
    private Animation animationWait;
    private AdLayoutVector instance;
    private AdControl adControl;
    private AlertDialog currentDialog;

    public AdLayoutVector(Context context, AdControl adControl) {
        super(context);
        instance = this;
        this.adControl = adControl;

        setBackgroundColor(Color.TRANSPARENT);
        
        /*animationBegin = new AlphaAnimation(0f, 1f);
        animationBegin.setDuration(Constants.ANIMATION_TIME_AD);
        animationBegin.setFillAfter(true);

        animationEnd = new AlphaAnimation(1.0f, 0.0f);
        animationEnd.setDuration(Constants.ANIMATION_TIME_AD);
        animationEnd.setFillAfter(true);*/
        
        animationWait = new AlphaAnimation(0.03f, 0.03f);
        animationWait.setDuration(Constants.ANIMATION_TIME_AD_WAIT);
        animationWait.setFillAfter(true);
        animationWait.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {				
				try{					
					if (currentAd != null) {
	                    currentAd.close();
	                    currentAd.startAnimation(animationEnd);
	                }
	
					if(newAd!=null) newAd.startAnimation(animationBegin);
				}catch(Exception e){};
			}
		});
        
        animationBegin = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, -1.0f, Animation.RELATIVE_TO_SELF,
                0.0f);
        animationBegin.setDuration(Constants.ANIMATION_TIME_AD);
        animationBegin.setFillAfter(true);

        animationEnd = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 1.0f);
        animationEnd.setDuration(Constants.ANIMATION_TIME_AD);
        animationEnd.setFillAfter(true);

        animationBegin.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (currentAd != null)
                            removeView(currentAd);
                        currentAd = newAd;
                        newAd = null;
                    }
                });
            }
        });

        animationTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        animateBanners();
                    }
                });
            }
        }, Constants.FRAME_TIME, Constants.FRAME_TIME);
    }

    public boolean isEmpty() {
        return (newAd == null) && (currentAd == null);
    }

    public boolean isEngaged() {
        return (currentDialog != null) && currentDialog.isShowing();
    }

    public void addBanner(final String data, final AdCallbackManager event) {
       /* synchronized(this.data){
        	this.data = data;
        	this.event = event;
        	addBannerToScreen();
        }*/
    	handler.post(new Runnable() {
            @Override
            public void run() {
	                AdLayout adLayout = new AdLayout(getContext(), adControl, instance);
	                adLayout.setLayoutParams(new ViewGroup.LayoutParams(
	                        ViewGroup.LayoutParams.FILL_PARENT,
	                        ViewGroup.LayoutParams.FILL_PARENT));
	                adLayout.setId(1);
	
	                if (newAd!=null)
	                {
	                	newAd.setVisibility(View.INVISIBLE);
	                	removeView(newAd);	                	
	                	newAd = null;
	                }
	                
	                newAd = adLayout;
	                adLayout.setVisibility(View.INVISIBLE);
	
	                if (adLayout.parseJson(data, instance, event)) {
	                    addView(adLayout);
	                    if (event != null) event.refresh();
	                    requestLayout();
	
	                } else newAd = null;
            	}
        });
    }
    
    /*private String data;
    private AdCallbackManager event;
    private boolean isAnimationPlayind;
    
    private void addBannerToScreen(){
    	if(data==null) return;
    	
    	handler.post(new Runnable() {
            @Override
            public void run() {
            	synchronized(data){
	                AdLayout adLayout = new AdLayout(getContext(), adControl, instance);
	                adLayout.setLayoutParams(new ViewGroup.LayoutParams(
	                        ViewGroup.LayoutParams.FILL_PARENT,
	                        ViewGroup.LayoutParams.FILL_PARENT));
	                adLayout.setId(1);
	
	                if (newAd!=null)
	                {
	                	removeView(newAd);
	                	newAd = null;
	                }
	                
	                newAd = adLayout;
	                adLayout.setVisibility(View.INVISIBLE);
	
	                if (adLayout.parseJson(data, instance, event)) {
	                    addView(adLayout);
	                    if (event != null) event.refresh();
	                    requestLayout();
	
	                } else newAd = null;
            	}
            }
        });
    }*/

    public void startAnimation() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (newAd != null) {
                    newAd.setVisibility(View.VISIBLE);
                    /*//newAd.refreshDrawableState();
                    //animationWait.cancel();
                    //animationWait.reset();
                    //animationEnd.cancel();
                    animationEnd.reset();
                    //animationBegin.cancel();
                    animationBegin.reset();//*/
                    newAd.startAnimation(animationWait);
                    /*if (currentAd != null) {
                        currentAd.close();
                        currentAd.startAnimation(animationEnd);
                    }
                    newAd.startAnimation(animationBegin);*/
                }
            }
        });
    }

    public ArrayList<AdAction> getAdActions() {
        if (newAd != null)
            return newAd.getAdActions();

        return null;
    }

    private void animateBanners() {
        if (currentAd != null)
            currentAd.animateBanner();
    }

    @Override
    public void onClick(View v) {
        if (currentAd == v) {
            if (currentAd.getAdActions().size() > 0) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (currentAd.getAdActions().size() == 1) {
                            currentAd.getAdActions().get(0).run(getContext());
                            adControl.onAdClickWithoutDialog();
                        } else if (currentAd.getAdActions().size() > 1) {
                            CharSequence[] strs = new CharSequence[currentAd.getAdActions().size()];

                            for (int x = 0; x < currentAd.getAdActions().size(); x++) {
                                strs[x] = currentAd.getAdActions().get(x).toString();
                            }

                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            builder.setTitle(Constants.ADVERTISEMENT_TITLE);
                            builder.setCancelable(true);
                            builder.setItems(strs, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    currentAd.getAdActions().get(which).run(getContext());
                                    adControl.onEngagedChange(false);
                                }
                            });
                            builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    adControl.onEngagedChange(false);
                                }
                            });
                            currentDialog = builder.show();
                            currentDialog.setCanceledOnTouchOutside(false);
                            adControl.onEngagedChange(true);
                        }
                    }
                });
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (animationTimer != null) {
            animationTimer.cancel();
        }
    }

    public boolean isExpanded() {
        return ((currentAd != null) && currentAd.isExpanded());
    }

}
