package com.kochava.sdk;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.apache.cordova.*;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.webkit.WebView;

import com.kochava.android.tracker.*;

public class KochavaEvents extends CordovaPlugin
{
	private static Feature kochavaObject;
	private Context ctx;
	
	private static final String SENDONSTART = "sendonstart";
		
	private static final String LOGTAG = "KochavaEvents";
	
	private Handler attribution_handler;
	
	/*  Example of creating a map object in javascript.
	 * 
	 * 	var a = {};
		a["key1"] = "value1";
		a["key2"] = "value2";
	 * 
	 * 
	 */
	
	@Override
	public void initialize(CordovaInterface cordova, CordovaWebView webView) 
	{
	    super.initialize(cordova, webView);
	    // your init code here
	    if(ctx == null)
			ctx = cordova.getActivity().getApplicationContext();	    
		
	    Log.i(LOGTAG,"Plugin Initialization");
	}
	
	@Override
    public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException 
    {
        Log.i(LOGTAG,"action is: " + action + " and there are " + args.length() + " args");
        
				
		if (action.equals("initKochava"))
        {
			HashMap<String, Object> options = new HashMap<String,Object>();
			
			
            JSONObject datamap = null;
            // check to make sure they passed a JSONObject as the first argument
            try
        	{
            	datamap = (JSONObject) args.get(0);			
        	}
        	catch(Exception e)
        	{
        		Log.i(LOGTAG,"Error parsing datamap: " + e.toString());
        		return false;
        	}
			
			      
            String partner_name = null;           
            try
            {
            	partner_name = datamap.getString("partner_name");
            }
            catch(JSONException je)
            {}
            
            
            String app_id = null;
            try
            {
            	app_id = datamap.getString("kochava_android_app_id");
            }
            catch(JSONException je)
            {
				Log.i(LOGTAG,"Error parsing datamap: " + je.toString());
			}
            
            //check to make sure they passed in either a partner id and partner name or an app id            
            if((partner_name == null || partner_name.trim().length() == 0))
	    	{
	    		// if both of these are null, we know they are not using this option, so
	    		// now we need to check for an app id
	    		
	    		if(app_id == null || app_id.trim().length() == 0 )
	    		{
	    			// if the app id is null we have a problem. Log the error for the user to see
	    			// and exit the function
	    			Log.i(LOGTAG,"Error - you need to pass either a kochava app id, or a partner id and a partner name (non-blank) into the datamap during initialization.");
	        		return false;
	    		}  
	    		else
	    		{
	    			Log.i(LOGTAG,"Got app id: " + app_id);
					options.put(Feature.INPUTITEMS.KOCHAVA_APP_ID, app_id);	    			
	    		}
	    		
	    	}
	    	else if(partner_name == null || partner_name.trim().length() == 0)
	    	{
	    		// user entered only one of the two required partner parameters
	    		Log.i(LOGTAG,"The partner name you passed was null or empty. Cannot initialize.");
	    		return false;    		
	    	}
	    	else
	    	{
	    		Log.i(LOGTAG,"putting partner name in " + partner_name);
	    		options.put(Feature.INPUTITEMS.PARTNER_NAME, partner_name);
	    	}
            
            try
            {
            	if(datamap.has(Feature.INPUTITEMS.CURRENCY))
				{
					options.put(Feature.INPUTITEMS.CURRENCY, datamap.getString(Feature.INPUTITEMS.CURRENCY));

					Log.i(LOGTAG,"got CURRENCY");
				}
            }
            catch(JSONException je)
            {
            	Log.i(LOGTAG,"The currency option key you passed was not a string!");
            }
			
			try
            {
            	if(datamap.has(Feature.INPUTITEMS.DEBUG_ON))
				{
					options.put(Feature.INPUTITEMS.DEBUG_ON, datamap.getBoolean(Feature.INPUTITEMS.DEBUG_ON)); 
					Log.i(LOGTAG,"got DEBUG_ON");	
				}					
            }
            catch(JSONException je)
            {
            	Log.i(LOGTAG,"The debug option key you passed was not a boolean!");
            }

			try
            {
            	if(datamap.has(Feature.INPUTITEMS.IDENTITY_LINK))
				{
					options.put(Feature.INPUTITEMS.IDENTITY_LINK, toMap(datamap.getJSONObject(Feature.INPUTITEMS.IDENTITY_LINK)));
					Log.i(LOGTAG,"got IDENTITY_LINK");
				}					
            }
            catch(Exception je)
            {
            	Log.i(LOGTAG,"Error parsing identity link options: " + je);
            }

			
			try
            {
            	if(datamap.has(Feature.INPUTITEMS.REQUEST_ATTRIBUTION))
				{
					options.put(Feature.INPUTITEMS.REQUEST_ATTRIBUTION, datamap.getBoolean(Feature.INPUTITEMS.REQUEST_ATTRIBUTION));
					Log.i(LOGTAG,"got REQUEST_ATTRIBUTION");
					
					if(attribution_handler == null)
					{
						attribution_handler = new Handler()
						{
							@Override
							public void handleMessage(final Message msg)
							{
								final String attrString = msg.getData().getString(Feature.ATTRIBUTION_DATA);
								cordova.getActivity().runOnUiThread(new Runnable()
																	{
									public void run()
									{
										webView.loadUrl("javascript:window.attributionNotification.notificationCallback('"+attrString+"');");
									}
								});
							}
						};
					}
					
					Feature.setAttributionHandler(attribution_handler);

				}
            }
            catch(Exception je)
            {
            	Log.i(LOGTAG,"Error parsing REQUEST_ATTRIBUTION: " + je);
            }
			
			
			try
            {
            	if(datamap.has("override_automatic_sessions"))
				{
					options.put("override_automatic_sessions", datamap.getBoolean("override_automatic_sessions"));
					Log.i(LOGTAG,"got override_automatic_sessions");
				}					
            }
            catch(Exception je)
            {
            	Log.i(LOGTAG,"Error parsing identity link options: " + je);
            } 
			
			
			try
            {
            	if(datamap.has("suppress_adid_gather"))
				{
					options.put("suppress_adid_gather", datamap.getBoolean("suppress_adid_gather"));
					Log.i(LOGTAG,"got suppress_adid_gather");
				}					
            }
            catch(Exception je)
            {
            	Log.i(LOGTAG,"Error parsing identity link options: " + je);
            } 
			
			try
			{
				if(datamap.has("app_limit_tracking") && datamap.get("app_limit_tracking").getClass().equals(Boolean.class) )
				{
					options.put("app_limit_tracking", ((Boolean) datamap.get("app_limit_tracking")));
				}
			}
			catch(Exception je)
			{
				Log.i(LOGTAG,"Error parsing app_limit_tracking: " + je);
			}
			
			try
            {
            	if(datamap.has("flush_rate"))
				{
					options.put("flush_rate", datamap.getInt("flush_rate"));
					Log.i(LOGTAG,"got flush_rate");
				}					
            }
            catch(Exception je)
            {
            	Log.i(LOGTAG,"Error parsing flush rate: " + je);
            } 
			
			
			
			
			options.put("version_extension","-cordova");
            this.init(ctx,options);
            
            return true;
        }
        if (action.equals("EnableLogging")) 
        {
        	boolean debug;
        	try
        	{
        		debug = args.getBoolean(0);
        		Log.i(LOGTAG,"settings EnableLogging " + debug);
        	}
        	catch(Exception e)
        	{
        		Log.i(LOGTAG,"Error in \"EnableLogging\" call: " + e.toString());
        		return false;
        	}
        	
        	Feature.enableDebug(debug);
            return true;
        }
        if (action.equals("TrackEvent")) 
        {
        	if(kochavaObject == null)
    		{	
        		Log.i(LOGTAG,"(TrackEvent call) Kochava library has not been initialized yet");
        		return false;        		
    		}
        	
        	String tempeventname = "";
        	String tempeventdata = "";
        	
        	try
        	{
        		tempeventname = args.getString(0);
        		tempeventdata = args.getString(1);
        	}
        	catch(Exception e)
        	{
        		callbackContext.error("Error in \"TrackEvent\" call: " + e.toString());
        		return false;
        	}
        	
        	
        	final String eventname = tempeventname;
        	final String eventdata = tempeventdata;
        	
        	cordova.getThreadPool().execute(new Runnable()
        	{
        		@Override
        		public void run()
        		{
        			kochavaObject.event(eventname, eventdata);
        		}
        	});
            return true;
        }
        if (action.equals("SpatialEvent")) 
        {
        	if(kochavaObject == null)
    		{	
        		Log.i(LOGTAG,"(SpatialEvent call) Kochava library has not been initialized yet");
        		return false;        		
    		}
        	
        	String tempeventName;
        	double tempx;
        	double tempy;
        	double tempz;
        	String eventData;
        	
        	try
        	{
        		tempeventName = args.getString(0);
        		tempx = args.getDouble(1);
        		tempy = args.getDouble(2);
        		tempz = args.getDouble(3);
        	}
        	catch(Exception e)
        	{
        		Log.i(LOGTAG,"Error in \"SpatialEvent\" call: " + e.toString());
        		return false;
        	}
        	
			final String eventName = tempeventName;
        	final double x = tempx;
        	final double y = tempy;
        	final double z = tempz;
			
			cordova.getThreadPool().execute(new Runnable()
        	{
        		@Override
        		public void run()
        		{
        			kochavaObject.eventSpatial(eventName, x, y, z, null);
        		}
        	});
        	
            return true;
        }
        if (action.equals("IdentityLink")) 
        {
        	
        	
        	if(kochavaObject == null)
    		{	
        		Log.i(LOGTAG,"(IdentityLink call) Kochava library has not been initialized yet");
        		return false;        		
    		}
        	
        	JSONObject identityMap;
        	try
        	{
        		identityMap = (JSONObject) args.get(0);        		
        	}
        	catch(Exception e)
        	{
        		Log.i(LOGTAG,"You tried to call \"IdentityLink\" but the key/value dictionary object you passed was not the right type. Please refer to documentation for how to correctly create the needed object. Error: " + e.toString());
        		return false;
        	}
        	
			final Map<String, String> idLinkData = toMap(identityMap);
			
			cordova.getThreadPool().execute(new Runnable()
        	{
        		@Override
        		public void run()
        		{
        			kochavaObject.linkIdentity(idLinkData);
        		}
        	});
        	
            return true;
        }
        if (action.equals("StartSession"))
        {
        	if(kochavaObject == null)
    		{	
        		Log.i(LOGTAG,"(StartSession call) Kochava library has not been initialized yet");
        		return false;        		
    		}
        	
        	kochavaObject.startSession();
        	return true;
        }
        if (action.equals("EndSession"))
        {
        	if(kochavaObject == null)
    		{	
        		Log.i(LOGTAG,"(EndSession call) Kochava library has not been initialized yet");
        		return false;        		
    		}
        	
        	kochavaObject.endSession();
        	return true;
        }
        if(action.equals("LimitAdTracking"))
        {
        	if(kochavaObject == null)
    		{	
        		Log.i(LOGTAG,"(LimitAdTracking call) Kochava library has not been initialized yet");
        		return false;        		
    		}
        	
        	boolean templimittracking = false;
        	try
        	{
        		templimittracking = args.getBoolean(0);
				Log.i(LOGTAG,"limittracking set to: " + templimittracking);
        	}
	    	catch(Exception e)
	    	{
	    		Log.i(LOGTAG,"Error in \"limittracking\" call: " + e.toString());
	    		return false;
	    	}
        	
			final boolean limittracking = templimittracking;
			
			cordova.getThreadPool().execute(new Runnable()
        	{
        		@Override
        		public void run()
        		{
        			kochavaObject.setAppLimitTracking(limittracking);
        		}
        	});
        	return true;
        }
        if(action.equals("RetrieveAttribution"))
        {
        	if(kochavaObject == null)
    		{	
        		Log.i(LOGTAG,"(RetrieveAttribution call) Kochava library has not been initialized yet");
        		return false;        		
    		}
        	
        	callbackContext.success(Feature.getAttributionData());
        	return true;
        }
		
		if(action.equals("GetKochavaDeviceId"))
		{
			if(kochavaObject == null)
			{
				Log.i(LOGTAG,"(GetKochavaDeviceId call) Kochava library has not been initialized yet");
				return false;
			}
			
			callbackContext.success(Feature.getKochavaDeviceId());
			return true;
		}
		
        if(action.equals("EnableErrorLogging"))
        {
        	if(kochavaObject == null)
    		{	
        		Log.i(LOGTAG,"(EnableErrorLogging call) Kochava library has not been initialized yet");
        		return false;        		
    		}
        	
        	boolean errordebug;
        	try
        	{
        		errordebug = args.getBoolean(0);
        	}
        	catch(Exception e)
        	{
        		Log.i(LOGTAG,"Error in \"EnableErrorLogging\" call: " + e.toString());
        		return false;
        	}
        	
        	Feature.setErrorDebug(errordebug);        	
        	return true;
        }
        if (action.equals("DeeplinkEvent")) 
        {
        	if(kochavaObject == null)
    		{	
        		Log.i(LOGTAG,"(DeepLinkEvent call) Kochava library has not been initialized yet");
        		return false;        		
    		}
			
        	String tempuri = null;
        	
        	try
        	{
        		tempuri = args.getString(0);
        	}
        	catch(Exception e)
        	{
        		Log.i(LOGTAG,"Error in \"DeepLinkEvent\" call: " + e.toString());
        		return false;
        	}
			
			final String uri = tempuri;
        	
			cordova.getThreadPool().execute(new Runnable()
        	{
        		@Override
        		public void run()
        		{
        			kochavaObject.deepLinkEvent(uri);
        		}
        	});
        	
            return true;
        }
        
        Log.i(LOGTAG,"Invalid action");
        return false;
    }

    
    private void init(final Context context, final HashMap<String, Object> options)
    {    	
		kochavaObject = new Feature(context,options);		
    }
    
    private Map<String, String> toMap(JSONObject object) throws JSONException 
    {
    	Map<String, String> map = new HashMap();
    	Iterator keys = object.keys();
    	while (keys.hasNext()) 
    	{
	    	String key = (String) keys.next();
	    	map.put(key, object.getString(key));
    	}
    	return map;
    }
}
