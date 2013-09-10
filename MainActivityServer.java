package com.kika.mam;


import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ws4d.coap.connection.BasicCoapChannelManager;
import org.ws4d.coap.interfaces.CoapChannelManager;
import org.ws4d.coap.interfaces.CoapMessage;
import org.ws4d.coap.interfaces.CoapRequest;
import org.ws4d.coap.interfaces.CoapServer;
import org.ws4d.coap.interfaces.CoapServerChannel;
import org.ws4d.coap.messages.CoapMediaType;
import org.ws4d.coap.messages.CoapResponseCode;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


public class MainActivity extends Activity implements CoapServer{

	private static final int PORT = 5683;
	static int counter = 0;
	double latitude = 0;
	double logitude = 0;

	//static final LatLng HAMBURG = new LatLng(53.558, 9.927);
	//static final LatLng KIEL = new LatLng(53.551, 9.993);
	private GoogleMap map;


	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		CoapChannelManager channelManager = BasicCoapChannelManager.getInstance();
		channelManager.createServerListener(this, PORT);


		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
				.getMap();

		Timer dodo = new Timer();
		dodo.schedule(new TimerTask() {

			@Override
			public void run() {

				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						LatLng here = new LatLng(latitude, logitude);
						Marker xyz = map.addMarker(new MarkerOptions().position(here).title("something"));
						map.moveCamera(CameraUpdateFactory.newLatLngZoom(here, 20));

						// Zoom in, animating the camera.
						map.animateCamera(CameraUpdateFactory.zoomTo(18), 2000, null);

					}
				});
				// TODO Auto-generated method stub

			}
		}, 0, 5000);


	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}


	@Override
	public CoapServer onAccept(CoapRequest request) {
		// TODO Auto-generated method stub
		//return null;
		System.out.println("Accept connection...");
		return this;
	}


	@Override
	public void onRequest(CoapServerChannel channel, CoapRequest request) {
		// TODO Auto-generated method stub


		//System.out.println("Received message: " + request.toString()+ " URI: " + request.getUriPath());
		String result = new String(request.getPayload());
		System.out.println("Hi Received Response from Client: "+result);


		CoapMessage response = channel.createResponse(request, CoapResponseCode.Content_205);

		//	response.setContentType(CoapMediaType.text_plain);
		response.setContentType(CoapMediaType.json);

		//	coapRequest.setPayload(user1.toString());

		//String res = request.getPayload().toString();
		//response.setPayload("payload...".getBytes());
		//response.setPayload(res);

		//String res = new String(request.getPayload());
		//byte[] b = res.getBytes(); 
		//String s = new String(b); 
		//System.out.println(res);




		String keyword = "deviceId";

		int index = result.indexOf(keyword);
		System.out.println("the value of index: "+index);

		String keyword1 = "values";
		int index1 = result.indexOf(keyword1);
		System.out.println("the value of index of values: "+index1);



		if (index != -1 )
		{
			JSONObject jsonObj;
			try {
				jsonObj = new JSONObject(result);
				JSONArray resultt1 = jsonObj.getJSONArray("sensors");
				String some =  resultt1.getString(0);
				String some1 =  resultt1.getString(1);

				System.out.println(jsonObj);

				System.out.println(resultt1.toString());
				System.out.println(some); 
				System.out.println(some1); 

				JSONObject user = new JSONObject();
				user.put("sensor", some);
				user.put("sampling_interval", 2000);
				user.put("sending_interval", 5000);
				user.put("duration", 10000);

				JSONObject user2 = new JSONObject();
				user2.put("sensor", some1);
				user2.put("sampling_interval", 1000);
				user2.put("sending_interval", 2000);
				user2.put("duration", 10);

				JSONArray notebookUsers = new JSONArray();
				notebookUsers.put(user);
				notebookUsers.put(user2);

				JSONObject user1 = new JSONObject();
				user1.put("subscriptions", notebookUsers);

				System.out.println(user1); 

				response.setPayload(user1.toString());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


		}

		/*  else{

			    	 System.out.println("Sorry no more connection!");
			    	 if(index1 != -1){
			 	    	System.out.println("You will get map values soon");
			 	    }
			 	    else{
			 	    	System.out.println("No map values");
			 	    }
			    } */

		else if(index1 != -1){
			System.out.println("You will get map values soon");

			JSONObject jsonLag;
			try {
				jsonLag = new JSONObject(result);
				JSONArray lagval = jsonLag.getJSONArray("sensorReadings");
				//int someval =  lagval.getInt(0);
				System.out.println("The values of location: "+lagval);

				JSONObject jsonLag1 = lagval.getJSONObject(0);
				JSONArray readings = jsonLag1.getJSONArray("readings");
				JSONObject jsonLag2 = readings.getJSONObject(readings.length()-1);
				JSONArray latlags = jsonLag2.getJSONArray("values");
				latitude = latlags.getDouble(latlags.length()-2);
				logitude = latlags.getDouble(latlags.length()-1);
				System.out.println("latitude: "+latitude);
				System.out.println("longitude: "+logitude);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


		}

		if (request.getObserveOption() != null){
			System.out.println("Client wants to observe this resource.");
		}

		response.setObserveOption(1);

		channel.sendMessage(response);
	}


	@Override
	public void onSeparateResponseFailed(CoapServerChannel channel) {
		// TODO Auto-generated method stub
		System.out.println("Separate response transmission failed.");

	}
}

