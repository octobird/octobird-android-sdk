package com.octobird.advertising;

import android.content.Context;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.webkit.WebView;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.ByteArrayBuffer;

import com.octobird.advertising.core.Constants;
import com.octobird.advertising.core.Utils;

import java.io.*;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;

public class ContentManager {
    private static final String INSTALLATION = "INSTALLATION";
    private String autoDetectParameters;
    private String userAgent = Constants.STR_EMPTY;
    private static ContentManager instance;
    private static boolean isSimAvailable;
    private HashMap<AdControl, ContentParameters> senderParameters = new HashMap<AdControl, ContentParameters>();
    private static String id = null;

    static public ContentManager getInstance(Context context) {
        if (instance == null)
            instance = new ContentManager(context.getApplicationContext());

        return instance;
    }

    static public ContentManager getInstance() {
        return instance;
    }

    private ContentManager(final Context context) {
        AdLog.staticLog(AdLog.LOG_LEVEL_CRITICAL, AdLog.LOG_TYPE_WARNING, Constants.STR_CONTENT_MANAGER, String.format(Constants.STR_CONTENT_MANAGER_CREATE,
                Constants.SDK_VERSION));
        userAgent = (new WebView(context)).getSettings().getUserAgentString();

        Thread thread = new Thread() {
            @Override
            public void run() {
                initDefaultParameters(context);
            }
        };
        thread.setName(Constants.STR_THREAD_NAME_INIT);
        thread.start();
    }

    public boolean sendImpr(String uri) {
        DefaultHttpClient client = new DefaultHttpClient();
        HttpGet get = new HttpGet(uri);
        try {
            HttpResponse response = client.execute(get);
            if (response.getStatusLine().getStatusCode() != 200) return false;
        } catch (Exception e) {
            return false;
        }
        return true;
    }


    public String getAutoDetectParameters() {
        return autoDetectParameters;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public static boolean isSimAvailable() {
        return isSimAvailable;
    }

    public void sendImpression(final String uri, final AdLog adLog) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                if (!sendImpr(uri))
                    adLog.log(AdLog.LOG_LEVEL_CRITICAL, AdLog.LOG_TYPE_WARNING, Constants.STR_IMPRESSION_NOT_SEND, uri);
            }
        };
        thread.start();
    }

    public void startLoadContent(AdControl sender, String url, String display) {
        if (senderParameters.containsKey(sender))
            stopLoadContent(sender);

        ContentParameters parameters = new ContentParameters();
        parameters.sender = sender;
        parameters.url = url;
        parameters.display = display;

        senderParameters.put(sender, parameters);

        ContentThread cTh = new ContentThread(parameters);
        parameters.cTh = cTh;
        cTh.setName(Constants.STR_THREAD_NAME_LOAD_CONTENT);
        cTh.start();
    }

    public void stopLoadContent(AdControl sender) {
        if (senderParameters.containsKey(sender)) {
            senderParameters.get(sender).sender = null;
            senderParameters.get(sender).cTh.cancel();
            senderParameters.remove(sender);
        }
    }

    private class ContentParameters {
        public String url;
        public String display;
        public AdControl sender;
        ContentThread cTh;
    }

    private class ContentThread extends Thread {
        ContentParameters parameters;
        boolean isCanceled = false;

        public ContentThread(ContentParameters parameters) {
            this.parameters = parameters;
        }

        @Override
        public void run() {
            InputStream inputStream = null;
            BufferedInputStream bufferedInputStream = null;
            try {
                DefaultHttpClient client = new DefaultHttpClient();
                HttpGet get = new HttpGet(parameters.url);
                //get.addHeader("Accept-Encoding", "gzip");
                get.addHeader("Accept", "application/json");
                //get.addHeader("UA-Pixels", parameters.w + "x" + parameters.h);
                get.addHeader("Accept-Language", Locale.getDefault().toString().replace("_", "-"));
                get.addHeader("User-Agent", "OctobirdClient/" + Constants.API_VERSION +
                		" (android-sdk; "+Constants.SDK_VERSION+")");

                HttpConnectionParams.setConnectionTimeout(get.getParams(), Constants.DEFAULT_REQUEST_TIMEOUT * 1000);
                HttpConnectionParams.setSoTimeout(get.getParams(), Constants.DEFAULT_REQUEST_TIMEOUT * 1000);                
                HttpResponse response = client.execute(get);
                HttpEntity entity = response.getEntity();
                String responseValue;
                
                if (isCanceled) {
                	responseValue = Constants.STR_EMPTY;
                }else
                {
	                inputStream = entity.getContent();
	                bufferedInputStream = new BufferedInputStream(inputStream, Constants.BUFFERED_INPUT_STREAM_SIZE);
	                if (!isCanceled) {
	                    responseValue = readInputStream(bufferedInputStream);
	                } else {
	                    responseValue = Constants.STR_EMPTY;
	                }
                }

                if (parameters.sender != null)
                    parameters.sender.requestCallback(responseValue, isCanceled ? "canceled" : null);
            } catch (ClientProtocolException e) {
                if (parameters.sender != null)
                    parameters.sender.requestCallback(null, e.toString() + ": " + e.getMessage());
            } catch (IOException e) {
                if (parameters.sender != null)
                    parameters.sender.requestCallback(null, e.toString() + ": " + e.getMessage());
            } finally {
                if (bufferedInputStream != null)
                    try {
                        bufferedInputStream.close();
                    } catch (IOException e) {
                    	AdLog.staticLog(AdLog.LOG_LEVEL_ALL, AdLog.LOG_TYPE_WARNING, Constants.STR_CONTENT_MANAGER, e.getMessage());
                    }
                if (inputStream != null)
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                    	AdLog.staticLog(AdLog.LOG_LEVEL_ALL, AdLog.LOG_TYPE_WARNING, Constants.STR_CONTENT_MANAGER, e.getMessage());
                    }
            }


            stopLoadContent(parameters.sender);
        }

        public void cancel() {
            isCanceled = true;
        }

        private String readInputStream(BufferedInputStream in) throws IOException {
            byte[] buffer = new byte[Constants.BUFFERED_INPUT_STREAM_SIZE];
            ByteArrayBuffer byteBuffer = new ByteArrayBuffer(1);
            for (int n; (n = in.read(buffer)) != -1; ) {
                if (isCanceled) return Constants.STR_EMPTY;
                byteBuffer.append(buffer, 0, n);
            }
            return new String(byteBuffer.buffer(), 0, byteBuffer.length());
        }
    }

    private void initDefaultParameters(Context context) {
        String deviceIdMd5 = null;
    	try {		
	        String deviceId;
	        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
	        isSimAvailable = tm.getSimState() > TelephonyManager.SIM_STATE_ABSENT;
	        String tempDeviceId = tm.getDeviceId();
	
	        if (null != tempDeviceId) {
	            deviceId = tempDeviceId;
	        } else {
	            tempDeviceId = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
	
	            if (null != tempDeviceId) {
	                deviceId = tempDeviceId;
	            } else {
	                deviceId = null;
	            }
	        }
	
	        if (deviceId == null) {
	            deviceId = getId(context);
	        }
	
	        deviceIdMd5 = Utils.md5(deviceId);
        } catch (Exception e) {
            //TODO 
            AdLog.staticLog(AdLog.LOG_LEVEL_CRITICAL, AdLog.LOG_TYPE_ERROR, Constants.STR_LOG_initDefaultParameters, e.getMessage());
        }
	
	
	        StringBuilder buffer = new StringBuilder();
	        
	        buffer.append("nb=1");
	        if ((deviceIdMd5 != null) && (deviceIdMd5.length() > 0)) {
	        	buffer.append("&did=" + deviceIdMd5);
	        }
	        
	        buffer.append("&devicevendor=" + URLEncoder.encode(android.os.Build.MANUFACTURER));
	        buffer.append("&devicemodel=" + URLEncoder.encode(android.os.Build.MODEL));
	        buffer.append("&deviceos=Android");
	        buffer.append("&deviceosversion=" + URLEncoder.encode(android.os.Build.VERSION.RELEASE));
	        buffer.append("&version=" + Constants.API_VERSION);
	        buffer.append("&tp=all");
	        buffer.append("&format=json");
	        buffer.append("&adhtml=1");
	        
	        //buffer.append("&blocks=1");
	        
	        autoDetectParameters = buffer.toString();
	    	
		//} catch (Exception e) {
			//TODO 
			//AdLog.staticLog(AdLog.LOG_LEVEL_CRITICAL, AdLog.LOG_TYPE_ERROR, Constants.STR_LOG_initDefaultParameters, e.getMessage());
		//}
    }

    private synchronized static String getId(Context context) {
        if (id == null) {
            File installation = new File(context.getFilesDir(), INSTALLATION);
            try {
                if (!installation.exists())
                    writeInstallationFile(installation);
                id = readInstallationFile(installation);
            } catch (Exception e) {
                id = Constants.STR_DEFAULT_ID;
            }
        }
        return id;
    }

    private static String readInstallationFile(File installationFile) throws IOException {
        RandomAccessFile f = new RandomAccessFile(installationFile, "r");
        byte[] bytes = new byte[(int) f.length()];
        f.readFully(bytes);
        f.close();
        return new String(bytes);
    }

    private static void writeInstallationFile(File installationFile) throws IOException {
        FileOutputStream out = new FileOutputStream(installationFile);
        String id = UUID.randomUUID().toString();
        out.write(id.getBytes());
        out.close();
    }

}
