package com.dara.testclient;


import java.net.InetAddress;

import java.net.UnknownHostException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ws4d.coap.Constants;
import org.ws4d.coap.connection.BasicCoapChannelManager;
import org.ws4d.coap.interfaces.CoapChannelManager;
import org.ws4d.coap.interfaces.CoapClient;
import org.ws4d.coap.interfaces.CoapClientChannel;
import org.ws4d.coap.interfaces.CoapRequest;
import org.ws4d.coap.interfaces.CoapResponse;
import org.ws4d.coap.messages.CoapMediaType;
import org.ws4d.coap.messages.CoapRequestCode;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings.Secure;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements CoapClient, SensorEventListener, LocationListener{

	private static final String SERVER_ADDRESS = "172.17.72.16";
	private static final int PORT = Constants.COAP_DEFAULT_PORT;
	static int counter = 0;
	CoapChannelManager channelManager = null;
	CoapClientChannel clientChannel = null;

	private TextView num, list;
	private SensorManager mSensorManager;
	String temp = "";
	int type;
	String android_id = "";
	private Handler handy;

	float lat;
	float lng;

	JSONObject obj = new JSONObject();
	JSONArray JA = new JSONArray();

	JSONArray Val = new JSONArray();
	JSONArray Val1 = new JSONArray();
	JSONArray Val2 = new JSONArray();
	JSONObject rea1 = new JSONObject();

	JSONArray rea2 = new JSONArray();
	JSONObject rea3 = new JSONObject();
	JSONObject rea4 = new JSONObject();


	Sensor accelerometer, proxSensor, mLight, tempSensor, gravSensor, magSensor, gyroSensor, accSensor, rotSensor, oriSensor, humSensor, ambSensor, presSensor;;
	private float axisX;
	private float axisY;
	private float axisZ;

	private float proxX;
	private float lightX;
	private float gravX;
	private float gravY;
	private float gravZ;
	private float tempX;
	private float magX;
	private float magY;
	private float magZ;
	private float gyroX;
	private float gyroY;
	private float gyroZ;
	private float oriX;
	private float oriY;
	private float oriZ;
	private float accX;
	private float accY;
	private float accZ;
	private float rotX;
	private float rotY;
	private float rotZ;
	private float humX;
	private float ambX;
	private float presX;

	Timer time = new Timer(); 
	String sensorname1 = "";
	int samplinginterval1;
	int duration1;
	int sendinterval1;
	String jo;

	SensorManager sm;
	TextView acceleration, proxText, lightText, tempText, gravText, magText, gyroText, accText, rotText, oriText, humText, ambText, prestext;

	private TextView latituteField;
	private TextView longitudeField;
	private LocationManager locationManager;
	private String provider;   

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		num = (TextView) findViewById(R.id.textView2);
		list = (TextView) findViewById(R.id.textView3);
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		List<Sensor> deviceSensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);

		JA.put("location");

		for(int i=0; i< deviceSensors.size(); i++)
		{

			type = deviceSensors.get(i).getType();

			if(type == 1){
				temp = "accelerometer";
			}else if (type == 13){
				temp = "ambient temperature";
			}else if(type == 9){
				temp = "gravity";
			}else if (type == 4){
				temp = "gyroscope";
			}else if(type == 5){
				temp = "light";
			}else if (type == 10){
				temp = "linear acceleration";
			}else if(type == 2){
				temp = "magnetic field";
			}else if(type == 3){
				temp = "orientation";
			}else if (type == 6){
				temp = "pressure";
			}else if(type == 8){
				temp = "proximity";
			}else if(type == 12){
				temp = "relative humidity";
			}else if (type == 11){
				temp = "rotation vector";
			}else if(type == 7){
				temp = "temperature";
			}

			JA.put(temp);

		}

		android_id = Secure.getString(getBaseContext().getContentResolver(),
				Secure.ANDROID_ID);

		try {
			obj.put("deviceId", android_id);
			obj.put("sensors", JA);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		sm = (SensorManager)getSystemService(SENSOR_SERVICE);

		if(sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)!= null)
		{
			accelerometer = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		}


		if(sm.getDefaultSensor(Sensor.TYPE_PROXIMITY)!= null)
		{
			proxSensor = sm.getDefaultSensor(Sensor.TYPE_PROXIMITY);
		}

		if(sm.getDefaultSensor(Sensor.TYPE_LIGHT) != null ) 
		{
			mLight = sm.getDefaultSensor(Sensor.TYPE_LIGHT);
		} 


		if(sm.getDefaultSensor(Sensor.TYPE_TEMPERATURE)!= null)
		{
			tempSensor = sm.getDefaultSensor(Sensor.TYPE_TEMPERATURE);
		}

		if(sm.getDefaultSensor(Sensor.TYPE_GRAVITY)!= null)
		{
			gravSensor = sm.getDefaultSensor(Sensor.TYPE_GRAVITY);
		}

		if(sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)!= null)
		{
			magSensor = sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		}

		if(sm.getDefaultSensor(Sensor.TYPE_GYROSCOPE)!= null)
		{
			gyroSensor = sm.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
		}

		if(sm.getDefaultSensor(Sensor.TYPE_PRESSURE)!= null)
		{
			presSensor = sm.getDefaultSensor(Sensor.TYPE_PRESSURE);
		}

		if(sm.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)!= null)
		{
			accSensor = sm.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
		}

		if(sm.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)!= null)
		{
			rotSensor = sm.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
		}

		if(sm.getDefaultSensor(Sensor.TYPE_ORIENTATION)!= null)
		{
			oriSensor = sm.getDefaultSensor(Sensor.TYPE_ORIENTATION);
		}

		if(sm.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY)!= null)
		{
			humSensor = sm.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
		}

		if(sm.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)!= null)
		{
			ambSensor = sm.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
		} 


		// Get the location manager
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		// Define the criteria how to select the locatioin provider -> use
		// default
		Criteria criteria = new Criteria();
		provider = locationManager.getBestProvider(criteria, false);
		Location location = locationManager.getLastKnownLocation(provider);

		// Initialize the location fields
		if (location != null) 
		{
			System.out.println("Provider " + provider + " has been selected.");
			onLocationChanged(location);
		} 

		else {
			System.out.println("Location not available");   
		}

		//Creating Button variable
		Button button = (Button) findViewById(R.id.button1);      

		//Adding Listener to button
		button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub         
				channelManager = BasicCoapChannelManager.getInstance();
				runTestClient();
				//Creating TextView Variable
				TextView text = (TextView) findViewById(R.id.textView1);               
				//Sets the new text to TextView (runtime click event)
				text.setText("You Have clicked the button to send message to server");
			}
		});
	}


	public void runTestClient(){
		try {

			clientChannel = channelManager.connect(this, InetAddress.getByName(SERVER_ADDRESS), PORT);
			//           CoapRequest coapRequest = clientChannel.createRequest(true, CoapRequestCode.GET);
			CoapRequest coapRequest = clientChannel.createRequest(true, CoapRequestCode.POST);
			coapRequest.setContentType(CoapMediaType.json);
			//	        coapRequest.setContentType(CoapMediaType.text_plain);
			//	        coapRequest.setToken("ABCD".getBytes());
			//		coapRequest.setUriHost("123.123.123.123");
			//		coapRequest.setUriPort(1234);
			//		coapRequest.setUriPath("/sub1/sub2/sub3/");
			//		coapRequest.setUriQuery("a=1&b=2&c=3");
			//		        coapRequest.setProxyUri("http://proxy.org:1234/proxytest");	
			//                  coapRequest.setPayload(v.toString());
			coapRequest.setPayload(obj.toString());
			clientChannel.sendMessage(coapRequest);			
			System.out.println("Sent Request");
		} 

		catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}


	public void runTest2(){
		try {
			clientChannel = channelManager.connect(this, InetAddress.getByName(SERVER_ADDRESS), PORT);
			//CoapRequest coapRequest = clientChannel.createRequest(true, CoapRequestCode.GET);
			final CoapRequest coapRequest = clientChannel.createRequest(true, CoapRequestCode.PUT);
			coapRequest.setContentType(CoapMediaType.json);
			//	coapRequest.setContentType(CoapMediaType.text_plain);
			//	coapRequest.setToken("ABCD".getBytes());
			//	coapRequest.setUriHost("123.123.123.123");
			//	coapRequest.setUriPort(1234);
			//	coapRequest.setUriPath("/sub1/sub2/sub3/");
			//		coapRequest.setUriQuery("a=1&b=2&c=3");
			//		coapRequest.setProxyUri("http://proxy.org:1234/proxytest");
			//           coapRequest.setPayload(v.toString());

			coapRequest.setPayload(rea4.toString());

			clientChannel.sendMessage(coapRequest);

			System.out.println("Sent Request");
		} 

		catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}


	public void startThread2(){
		time.schedule(new TimerTask() {
			private int numberoftimes = 0;
			public void run() {	    
				numberoftimes = numberoftimes+ 1000;
				if (numberoftimes <= duration1) 
				{	 
					runTest2();   
				}

				else{
					time.cancel();
				}
			}}, 0, sendinterval1);	    
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onResponse(CoapClientChannel channel, CoapResponse response) {
		// TODO Auto-generated method stub
		System.out.println("Connection established");
		String sevres =	new String (response.getPayload());
		Log.d("Success ", sevres);


		JSONObject sevobj;
		try {
			sevobj = new JSONObject(sevres);
			JSONArray result1 = sevobj.getJSONArray("subscriptions");
			String some2 =  result1.getString(0);

			Log.d("Server response", sevobj.toString());

			Log.d("Json array", result1.toString());
			Log.d("The first value of Json array", some2); 

			JSONObject res2 = new JSONObject(some2);
			Log.d("The first Json object", some2);    

			sensorname1 = res2.getString("sensor");
			Log.d("first sensor name is ", sensorname1);

			duration1 = res2.getInt("duration");
			sendinterval1 = res2.getInt("sending_interval");
			samplinginterval1 = res2.getInt("sampling_interval");

			startThread();
			startThread2();


		} 

		catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}



		public void startThread(){
			time.schedule(new TimerTask() {
				private int numberoftimes = 0;
				public void run() {
					numberoftimes = numberoftimes+ 1000;
					if (numberoftimes <= duration1) {
						if(sensorname1.equalsIgnoreCase("accelerometer"))
						{
							long dtMili = System.currentTimeMillis();	

							LinkedHashMap lm = new LinkedHashMap();
							lm .put("time", dtMili);
							lm .put("Xvalue", axisX);
							lm .put("Yvalue", axisY);
							lm .put("Zvalue", axisZ);

							Object objt = lm.get("time");
							Object obj1 = lm.get("Xvalue");
							Object obj2 = lm.get("Yvalue");
							Object obj3 = lm.get("Zvalue");

							/*Exercise 3 - way to send json values		
		Val.put(obj);
		Val.put(obj1);
		Val.put(obj2);
		Val.put(obj3);
		//Val.put(lm.values());

		 JSONObject rea = new JSONObject();
		  try {
			rea.put("sensor", sensorname1);   
			rea.put("readings", Val);
			} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			}
		       Val1.put(rea);
		       // JSONObject rea1 = new JSONObject();
		       try {
			rea1.put("sensorReadings", Val1);
			} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			}
			String pp = "Json values"+rea1;
		     // Log.d("Json values", pp);  */

							Val.put(obj1);
							Val.put(obj2);
							Val.put(obj3);

							JSONObject rea = new JSONObject();
							try {
								rea.put("time", objt);   
								rea.put("values", Val);
							} 

							catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}	

							rea2.put(rea);

							try {
								rea3.put("sensor", sensorname1);   
								rea3.put("readings", rea2);
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

							Val2.put(rea3);   

							try {
								rea4.put("sensorReadings", Val2);
							} 
							catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}


						else if(sensorname1.equalsIgnoreCase("proximity"))
						{

							long dtMili = System.currentTimeMillis();	

							LinkedHashMap lm = new LinkedHashMap();
							lm .put("time", dtMili);
							lm .put("Xvalue", proxX);

							Object objt = lm.get("time");
							Object obj1 = lm.get("Xvalue");

							Val.put(obj1);

							JSONObject rea = new JSONObject();
							try {
								rea.put("time", objt);   
								rea.put("values", Val);
							} 
							catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}	

							rea2.put(rea);

							try {
								rea3.put("sensor", sensorname1);   
								rea3.put("readings", rea2);
							} 
							catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							Val2.put(rea3);     			 


							try {
								rea4.put("sensorReadings", Val2);
							} 
							catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}  

						}

						else if(sensorname1.equalsIgnoreCase("light"))
						{

							long dtMili = System.currentTimeMillis();	

							LinkedHashMap lm = new LinkedHashMap();
							lm .put("time", dtMili);
							lm .put("Xvalue", lightX);

							Object objt = lm.get("time");
							Object obj1 = lm.get("Xvalue");

							Val.put(obj1);

							JSONObject rea = new JSONObject();
							try {
								rea.put("time", objt);   
								rea.put("values", Val);
							} 
							catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}	

							rea2.put(rea);		

							try {
								rea3.put("sensor", sensorname1);   
								rea3.put("readings", rea2);
							} 
							catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							Val2.put(rea3);   

							try {
								rea4.put("sensorReadings", Val2);
							} 
							catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}  					  			  		
						}



						else if(sensorname1.equalsIgnoreCase("temperature"))
						{	   			 
							long dtMili = System.currentTimeMillis();	

							LinkedHashMap lm = new LinkedHashMap();
							lm .put("time", dtMili);
							lm .put("Xvalue", tempX);

							Object objt = lm.get("time");
							Object obj1 = lm.get("Xvalue");

							Val.put(obj1);

							JSONObject rea = new JSONObject();
							try {
								rea.put("time", objt);   
								rea.put("values", Val);
							}
							catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}	

							rea2.put(rea);


							try {
								rea3.put("sensor", sensorname1);   
								rea3.put("readings", rea2);
							} 
							catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

							Val2.put(rea3);   							  			 							  			 
							try {
								rea4.put("sensorReadings", Val2);
							} 
							catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}  						  			  		
						}

						else if(sensorname1.equalsIgnoreCase("magnetic field"))
						{

							long dtMili = System.currentTimeMillis();	

							LinkedHashMap lm = new LinkedHashMap();
							lm .put("time", dtMili);
							lm .put("Xvalue", magX);
							lm .put("Yvalue", magY);
							lm .put("Zvalue", magZ);

							Object objt = lm.get("time");
							Object obj1 = lm.get("Xvalue");
							Object obj2 = lm.get("Yvalue");
							Object obj3 = lm.get("Zvalue");

							/*Exercise 3 - way to send json values		
			  			Val.put(obj);
			  			Val.put(obj1);
			  			Val.put(obj2);
			  			Val.put(obj3);
			  			//Val.put(lm.values());



			  			   JSONObject rea = new JSONObject();
			  			   try {
			  				rea.put("sensor", sensorname1);   
							rea.put("readings", Val);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
			  			   Val1.put(rea);
			  			// JSONObject rea1 = new JSONObject();
			  			 try {
							rea1.put("sensorReadings", Val1);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
			  		String pp = "Json values"+rea1;
			  	//	Log.d("Json values", pp);  */

							Val.put(obj1);
							Val.put(obj2);
							Val.put(obj3);

							JSONObject rea = new JSONObject();
							try {
								rea.put("time", objt);   
								rea.put("values", Val);
							} 
							catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}	

							rea2.put(rea);

							try {
								rea3.put("sensor", sensorname1);   
								rea3.put("readings", rea2);
							} 
							catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							Val2.put(rea3);   


							try {
								rea4.put("sensorReadings", Val2);
							} 
							catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}

						else	if(sensorname1.equalsIgnoreCase("gravity"))
						{
							String buffer5 = "Gravity X value: "+gravX+
									"\nGravity Y value: "+gravY+
									"\nGravity Z value: "+gravZ; 
							Log.d("light value", buffer5);

							long dtMili = System.currentTimeMillis();	
							LinkedHashMap lm = new LinkedHashMap();
							lm .put("time", dtMili);
							lm .put("Xvalue", gravX);
							lm .put("Yvalue", gravY);
							lm .put("Zvalue", gravZ);	 

							Object objt = lm.get("time");
							Object obj1 = lm.get("Xvalue");
							Object obj2 = lm.get("Yvalue");
							Object obj3 = lm.get("Zvalue");

							/*Exercise 3 - way to send json values		
			  			Val.put(obj);
			  			Val.put(obj1);
			  			Val.put(obj2);
			  			Val.put(obj3);
			  			//Val.put(lm.values());



			  			   JSONObject rea = new JSONObject();
			  			   try {
			  				rea.put("sensor", sensorname1);   
							rea.put("readings", Val);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
			  			   Val1.put(rea);
			  			// JSONObject rea1 = new JSONObject();
			  			 try {
							rea1.put("sensorReadings", Val1);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
			  		String pp = "Json values"+rea1;
			  	//	Log.d("Json values", pp);  */

							Val.put(obj1);
							Val.put(obj2);
							Val.put(obj3);

							JSONObject rea = new JSONObject();
							try {
								rea.put("time", objt);   
								rea.put("values", Val);
							} 
							catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}	

							rea2.put(rea);			

							try {
								rea3.put("sensor", sensorname1);   
								rea3.put("readings", rea2);
							} 
							catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							Val2.put(rea3);   


							try {
								rea4.put("sensorReadings", Val2);
							} 
							catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}



						else	if(sensorname1.equalsIgnoreCase("orientation"))
						{

							long dtMili = System.currentTimeMillis();
							LinkedHashMap lm = new LinkedHashMap();
							lm .put("time", dtMili);
							lm .put("Xvalue", oriX);
							lm .put("Yvalue", oriY);
							lm .put("Zvalue", oriZ);

							Object objt = lm.get("time");
							Object obj1 = lm.get("Xvalue");
							Object obj2 = lm.get("Yvalue");
							Object obj3 = lm.get("Zvalue");

							/*Exercise 3 - way to send json values		
			  			Val.put(obj);
			  			Val.put(obj1);
			  			Val.put(obj2);
			  			Val.put(obj3);
			  			//Val.put(lm.values());



			  			   JSONObject rea = new JSONObject();
			  			   try {
			  				rea.put("sensor", sensorname1);   
							rea.put("readings", Val);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
			  			   Val1.put(rea);
			  			// JSONObject rea1 = new JSONObject();
			  			 try {
							rea1.put("sensorReadings", Val1);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
			  		String pp = "Json values"+rea1;
			  	//	Log.d("Json values", pp);  */

							Val.put(obj1);
							Val.put(obj2);
							Val.put(obj3);

							JSONObject rea = new JSONObject();
							try {
								rea.put("time", objt);   
								rea.put("values", Val);
							} 
							catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}	

							rea2.put(rea); 			


							try {
								rea3.put("sensor", sensorname1);   
								rea3.put("readings", rea2);
							} 
							catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							Val2.put(rea3);   			 

							try {
								rea4.put("sensorReadings", Val2);
							} 
							catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}



						else	if(sensorname1.equalsIgnoreCase("pressure"))
						{
							long dtMili = System.currentTimeMillis();	

							LinkedHashMap lm = new LinkedHashMap();
							lm .put("time", dtMili);
							lm .put("Xvalue", presX);

							Object objt = lm.get("time");
							Object obj1 = lm.get("Xvalue");

							Val.put(obj1);

							JSONObject rea = new JSONObject();
							try {
								rea.put("time", objt);   
								rea.put("values", Val);
							} 
							catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}	

							rea2.put(rea);


							try {
								rea3.put("sensor", sensorname1);   
								rea3.put("readings", rea2);
							} 
							catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

							Val2.put(rea3);   


							try {
								rea4.put("sensorReadings", Val2);
							} 
							catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}  

						}


						else	if(sensorname1.equalsIgnoreCase("relative humidity"))
						{

							long dtMili = System.currentTimeMillis();	

							LinkedHashMap lm = new LinkedHashMap();
							lm .put("time", dtMili);
							lm .put("Xvalue", humX);

							Object objt = lm.get("time");
							Object obj1 = lm.get("Xvalue");							  									 			
							Val.put(obj1);

							JSONObject rea = new JSONObject();
							try {
								rea.put("time", objt);   
								rea.put("values", Val);
							} 
							catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}	

							rea2.put(rea);				  			

							try {
								rea3.put("sensor", sensorname1);   
								rea3.put("readings", rea2);
							} 
							catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

							Val2.put(rea3);   


							try {
								rea4.put("sensorReadings", Val2);
							} 
							catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}  

						}


						else	if(sensorname1.equalsIgnoreCase("rotation vector"))
						{			   			  
							long dtMili = System.currentTimeMillis();	

							LinkedHashMap lm = new LinkedHashMap();
							lm .put("time", dtMili);
							lm .put("Xvalue", rotX);
							lm .put("Yvalue", rotY);
							lm .put("Zvalue", rotZ);

							Object objt = lm.get("time");
							Object obj1 = lm.get("Xvalue");
							Object obj2 = lm.get("Yvalue");
							Object obj3 = lm.get("Zvalue");

							/*Exercise 3 - way to send json values		
			  			Val.put(obj);
			  			Val.put(obj1);
			  			Val.put(obj2);
			  			Val.put(obj3);
			  			//Val.put(lm.values());



			  			   JSONObject rea = new JSONObject();
			  			   try {
			  				rea.put("sensor", sensorname1);   
							rea.put("readings", Val);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
			  			   Val1.put(rea);
			  			// JSONObject rea1 = new JSONObject();
			  			 try {
							rea1.put("sensorReadings", Val1);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
			  		String pp = "Json values"+rea1;
			  	//	Log.d("Json values", pp);  */

							Val.put(obj1);
							Val.put(obj2);
							Val.put(obj3);

							JSONObject rea = new JSONObject();
							try {
								rea.put("time", objt);   
								rea.put("values", Val);
							} 
							catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}	

							rea2.put(rea);


							try {
								rea3.put("sensor", sensorname1);   
								rea3.put("readings", rea2);
							} 
							catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							Val2.put(rea3);   


							try {
								rea4.put("sensorReadings", Val2);
							} 
							catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}

						else if(sensorname1.equalsIgnoreCase("gyroscope"))
						{			   			  
							long dtMili = System.currentTimeMillis();
							LinkedHashMap lm = new LinkedHashMap();
							lm .put("time", dtMili);
							lm .put("Xvalue", gyroX);
							lm .put("Yvalue", gyroY);
							lm .put("Zvalue", gyroZ);

							Object objt = lm.get("time");
							Object obj1 = lm.get("Xvalue");
							Object obj2 = lm.get("Yvalue");
							Object obj3 = lm.get("Zvalue");

							/*Exercise 3 - way to send json values		
			  			Val.put(obj);
			  			Val.put(obj1);
			  			Val.put(obj2);
			  			Val.put(obj3);
			  			//Val.put(lm.values());



			  			   JSONObject rea = new JSONObject();
			  			   try {
			  				rea.put("sensor", sensorname1);   
							rea.put("readings", Val);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
			  			   Val1.put(rea);
			  			// JSONObject rea1 = new JSONObject();
			  			 try {
							rea1.put("sensorReadings", Val1);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
			  		String pp = "Json values"+rea1;
			  	//	Log.d("Json values", pp);  */

							Val.put(obj1);
							Val.put(obj2);
							Val.put(obj3);

							JSONObject rea = new JSONObject();
							try {
								rea.put("time", objt);   
								rea.put("values", Val);
							} 
							catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}	

							rea2.put(rea);


							try {
								rea3.put("sensor", sensorname1);   
								rea3.put("readings", rea2);
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							Val2.put(rea3);   


							try {
								rea4.put("sensorReadings", Val2);
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}

						else	if(sensorname1.equalsIgnoreCase("linear acceleration")){

							long dtMili = System.currentTimeMillis();	


							LinkedHashMap lm = new LinkedHashMap();
							lm .put("time", dtMili);
							lm .put("Xvalue", accX);
							lm .put("Yvalue", accY);
							lm .put("Zvalue", accZ);
							// lm.clear();

							Object objt = lm.get("time");
							Object obj1 = lm.get("Xvalue");
							Object obj2 = lm.get("Yvalue");
							Object obj3 = lm.get("Zvalue");

							/*Exercise 3 - way to send json values		
			  			Val.put(obj);
			  			Val.put(obj1);
			  			Val.put(obj2);
			  			Val.put(obj3);
			  			//Val.put(lm.values());



			  			   JSONObject rea = new JSONObject();
			  			   try {
			  				rea.put("sensor", sensorname1);   
							rea.put("readings", Val);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
			  			   Val1.put(rea);
			  			// JSONObject rea1 = new JSONObject();
			  			 try {
							rea1.put("sensorReadings", Val1);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
			  		String pp = "Json values"+rea1;
			  	//	Log.d("Json values", pp);  */

							Val.put(obj1);
							Val.put(obj2);
							Val.put(obj3);

							JSONObject rea = new JSONObject();
							try {
								rea.put("time", objt);   
								rea.put("values", Val);
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}	

							rea2.put(rea);


							try {
								rea3.put("sensor", sensorname1);   
								rea3.put("readings", rea2);
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							Val2.put(rea3);   


							try {
								rea4.put("sensorReadings", Val2);
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}

						else	if(sensorname1.equalsIgnoreCase("ambient temperature")){

							long dtMili = System.currentTimeMillis();	

							LinkedHashMap lm = new LinkedHashMap();
							lm .put("time", dtMili);
							lm .put("Xvalue", ambX);

							Object objt = lm.get("time");
							Object obj1 = lm.get("Xvalue");


							Val.put(obj1);

							JSONObject rea = new JSONObject();
							try {
								rea.put("time", objt);   
								rea.put("values", Val);
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}	

							rea2.put(rea);


							try {
								rea3.put("sensor", sensorname1);   
								rea3.put("readings", rea2);
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							Val2.put(rea3);   


							try {
								rea4.put("sensorReadings", Val2);
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}  

						}

						else	if(sensorname1.equalsIgnoreCase("location")){

							long dtMili = System.currentTimeMillis();	


							LinkedHashMap lm = new LinkedHashMap();
							lm .put("time", dtMili);
							lm .put("Xvalue", lat);
							lm .put("Yvalue", lng);

							// lm.clear();

							Object objt = lm.get("time");
							Object obj1 = lm.get("Xvalue");
							Object obj2 = lm.get("Yvalue");


							/*Exercise 3 - way to send json values		
		  			Val.put(obj);
		  			Val.put(obj1);
		  			Val.put(obj2);
		  			Val.put(obj3);
		  			//Val.put(lm.values());



		  			   JSONObject rea = new JSONObject();
		  			   try {
		  				rea.put("sensor", sensorname1);   
						rea.put("readings", Val);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		  			   Val1.put(rea);
		  			// JSONObject rea1 = new JSONObject();
		  			 try {
						rea1.put("sensorReadings", Val1);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		  		String pp = "Json values"+rea1;
		  	//	Log.d("Json values", pp);  */

							Val.put(obj1);
							Val.put(obj2);


							JSONObject rea = new JSONObject();
							try {
								rea.put("time", objt);   
								rea.put("values", Val);
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}	

							rea2.put(rea);


							try {
								rea3.put("sensor", sensorname1);   
								rea3.put("readings", rea2);
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							Val2.put(rea3);  
							// startThread2();


							try {
								rea4.put("sensorReadings", Val2);
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}



					} else {
						this.cancel();
						//this.time.cancel();
						String bug1 = "Timer stoppes";
						Log.d("Timer stopped ", bug1);
					}

				}}, 0, samplinginterval1); 
		}





		@Override
		public void onConnectionFailed(CoapClientChannel channel,
				boolean notReachable, boolean resetByServer) {
			// TODO Auto-generated method stub
			//TextView textfail = (TextView) findViewById(R.id.textView3);
			System.out.println("Connection Failed");
			/*handy.post(new Runnable() {


			@Override
			public void run() {
				// TODO Auto-generated method stub
				list.append("Connection failed");
			}
		}); */
			//Sets the new text to TextView (runtime click event)
			// textfail.setText("Sorry connection establishment failed!");
		}


		@Override
		public void onAccuracyChanged(Sensor arg0, int arg1) {
			// TODO Auto-generated method stub

		}

		protected void onResume() {
			super.onResume();
			// register this class as a listener for the orientation and
			// accelerometer sensors

			locationManager.requestLocationUpdates(provider, 400, 1, this);

			if(sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)!= null)
			{
				sm.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
			}


			if(sm.getDefaultSensor(Sensor.TYPE_PROXIMITY)!= null)
			{ 
				sm.registerListener(this, proxSensor, SensorManager.SENSOR_DELAY_NORMAL);
			}

			if(sm.getDefaultSensor(Sensor.TYPE_LIGHT) != null ) {
				sm.registerListener(this, mLight, SensorManager.SENSOR_DELAY_NORMAL);
			} 


			if(sm.getDefaultSensor(Sensor.TYPE_TEMPERATURE)!= null)
			{

				sm.registerListener(this, tempSensor, SensorManager.SENSOR_DELAY_NORMAL);
			}

			if(sm.getDefaultSensor(Sensor.TYPE_GRAVITY)!= null)
			{

				sm.registerListener(this, gravSensor, SensorManager.SENSOR_DELAY_NORMAL);
			}

			if(sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)!= null)
			{

				sm.registerListener(this, magSensor, SensorManager.SENSOR_DELAY_NORMAL);
			}

			if(sm.getDefaultSensor(Sensor.TYPE_GYROSCOPE)!= null)
			{

				sm.registerListener(this, gyroSensor, SensorManager.SENSOR_DELAY_NORMAL);
			}


			if(sm.getDefaultSensor(Sensor.TYPE_PRESSURE)!= null)
			{
				sm.registerListener(this, presSensor, SensorManager.SENSOR_DELAY_NORMAL);
			}

			if(sm.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)!= null)
			{
				sm.registerListener(this, accSensor, SensorManager.SENSOR_DELAY_NORMAL);
			}

			if(sm.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)!= null)
			{
				sm.registerListener(this, rotSensor, SensorManager.SENSOR_DELAY_NORMAL);
			}

			if(sm.getDefaultSensor(Sensor.TYPE_ORIENTATION)!= null)
			{

				sm.registerListener(this, oriSensor, SensorManager.SENSOR_DELAY_NORMAL);
			}

			if(sm.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY)!= null)
			{

				sm.registerListener(this, humSensor, SensorManager.SENSOR_DELAY_NORMAL);
			}

			if(sm.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)!= null)
			{
				sm.registerListener(this, ambSensor, SensorManager.SENSOR_DELAY_NORMAL);
			}



		}




		@Override
		protected void onPause() {
			// unregister listener
			super.onPause();
			locationManager.removeUpdates(this);
			sm.unregisterListener(this);
		}


		@Override
		public void onSensorChanged(SensorEvent event) {
			// TODO Auto-generated method stub

			Sensor sensor = event.sensor;
			if(sensor.getType() == Sensor.TYPE_ACCELEROMETER){	
				//		acceleration.setText("Accelerometer X value: "+event.values[0]+
				//			"\nAccelerometer Y value: "+event.values[1]+
				//				"\nAccelerometer Z value: "+event.values[2]); 
				axisX = event.values[0];
				axisY = event.values[1];
				axisZ = event.values[2];


			}

			else if(sensor.getType() == Sensor.TYPE_PROXIMITY){	
				//proxText.setText("X: "+event.values[0]); 
				proxX = event.values[0];

			}

			else if(sensor.getType() == Sensor.TYPE_LIGHT){
				lightX = event.values[0];

			}

			else if(sensor.getType() == Sensor.TYPE_TEMPERATURE){	

				tempX = event.values[0];

			}

			else if(sensor.getType() == Sensor.TYPE_GRAVITY){	
				gravX = event.values[0];
				gravY = event.values[1];
				gravZ = event.values[2]; 
			}

			else if(sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){	
				magX = event.values[0];
				magY = event.values[1];
				magZ = event.values[2]; 
			}

			else if(sensor.getType() == Sensor.TYPE_GYROSCOPE){	
				gyroX = event.values[0];
				gyroY = event.values[1];
				gyroZ = event.values[2]; 
			}

			else if(sensor.getType() == Sensor.TYPE_PRESSURE){	
				//proxText.setText("X: "+event.values[0]); 
				presX = event.values[0];

			}

			else if(sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION){	
				accX = event.values[0];
				accY = event.values[1];
				accZ = event.values[2]; 

			}

			else if(sensor.getType() == Sensor.TYPE_ROTATION_VECTOR){	
				rotX = event.values[0];
				rotY = event.values[1];
				rotZ = event.values[2]; 

			}

			else if(sensor.getType() == Sensor.TYPE_ORIENTATION){	
				oriX = event.values[0];
				oriY = event.values[1];
				oriZ = event.values[2]; 

			}

			else if(sensor.getType() == Sensor.TYPE_RELATIVE_HUMIDITY){	
				humX = event.values[0]; 

			}

			else if(sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE){	
				ambX = event.values[0]; 

			}

		}


		@Override
		public void onLocationChanged(Location location) {
			// TODO Auto-generated method stub
			lat =  (float) (location.getLatitude());
			lng = (float) location.getLongitude();
			// latituteField.setText(String.valueOf(lat));
			// longitudeField.setText(String.valueOf(lng));

		}


		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			Toast.makeText(this, "Disabled provider " + provider,
					Toast.LENGTH_SHORT).show();

		}


		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			Toast.makeText(this, "Enabled new provider " + provider,
					Toast.LENGTH_SHORT).show();

		}


		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub

		}
	}
