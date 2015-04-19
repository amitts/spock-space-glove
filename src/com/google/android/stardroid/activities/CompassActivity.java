package com.google.android.stardroid.activities;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class CompassActivity extends Activity implements OnClickListener, SensorEventListener {

	protected static final int REQUEST_OK = 1;
	   TextToSpeech ttobj;


	   int flag=0;
	/*
	private BroadcastReceiver uiUpdated= new BroadcastReceiver() {

	    @Override
	    public void onReceive(Context context, Intent intent) {

	    	((TextView)findViewById(R.id.text1)).setText(intent.getExtras().getString("speech"));

	    }
	};
	*/
	   
	 

	   Float azimut;  // View to draw a compass
	   float pitch;
	   float roll;
	   
	   public class CustomDrawableView extends View {
	     Paint paint = new Paint();
	     public CustomDrawableView(Context context) {
	       super(context);
	       paint.setColor(0xff00ff00);
	       paint.setStyle(Style.STROKE);
	       paint.setStrokeWidth(2);
	       paint.setAntiAlias(true);
	     };
	  
	     protected void onDraw(Canvas canvas) {
	       int width = getWidth();
	       int height = getHeight();
	       int centerx = width/2;
	       int centery = height/2;
	       canvas.drawLine(centerx, 0, centerx, height, paint);
	       canvas.drawLine(0, centery, width, centery, paint);
	       // Rotate the canvas with the azimut      
	       if (azimut != null)
	         canvas.rotate(-azimut*360/(2*3.14159f), centerx, centery);
	       paint.setColor(0xff0000ff);
	       canvas.drawLine(centerx, -1000, centerx, +1000, paint);
	       canvas.drawLine(-1000, centery, 1000, centery, paint);
	       canvas.drawText("N", centerx+5, centery-10, paint);
	       canvas.drawText("S", centerx-10, centery+15, paint);
	       paint.setColor(0xff00ff00);
	     }
	   }
	  
	   CustomDrawableView mCustomDrawableView;
	   private SensorManager mSensorManager;
	   Sensor accelerometer;
	   Sensor magnetometer;
	   
	   @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_main);
		  mCustomDrawableView = new CustomDrawableView(this);
		    setContentView(mCustomDrawableView);    // Register the sensor listeners
		//findViewById(R.id.button1).setOnClickListener(this);
		
		 mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
	      accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
	    magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		
		//write = (EditText)findViewById(R.id.editText1);
	      ttobj=new TextToSpeech(getApplicationContext(), 
	      new TextToSpeech.OnInitListener() {
	      @Override
	      public void onInit(int status) {
	         if(status != TextToSpeech.ERROR){
	             ttobj.setLanguage(Locale.US);
	            }				
	         }
	      });
		
	   /* Intent service = new Intent(this, MyService.class);
	    startService(service);
	    mBindFlag = Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH ? 0 : Context.BIND_ABOVE_CLIENT;

		registerReceiver(uiUpdated, new IntentFilter("Text_updated"));
		*/


	}
	   

	   protected void onResume() {
	     super.onResume();
	     mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
	     mSensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);
	   }
	  
	   protected void onPause() {
	     super.onPause();
	     mSensorManager.unregisterListener(this);
	   }
	  
	
	   public void onAccuracyChanged(Sensor sensor, int accuracy) {  }
	   
	   float[] mGravity;
	   float[] mGeomagnetic;
	   public void onSensorChanged(SensorEvent event) {
	     if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
	       mGravity = event.values;
	     if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
	       mGeomagnetic = event.values;
	     if (mGravity != null && mGeomagnetic != null) {
	       float R[] = new float[9];
	       float I[] = new float[9];
	       boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
	       if (success) {
	         float orientation[] = new float[3];
	         
	         SensorManager.getOrientation(R, orientation);
	         azimut = orientation[0]; // orientation contains: azimut, pitch and roll
	       }
	     }
	     mCustomDrawableView.invalidate();
	   }
	   
	@Override
	public void onClick(View v) {
		
		 Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
         i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");
        // i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);

         //i.putExtra(RecognizerIntent., value)
        
         
         try {
             startActivityForResult(i, REQUEST_OK);
             
            	 
         } 
               
         catch (Exception e) {
        	 Toast.makeText(this, "Error initializing speech to text engine.", Toast.LENGTH_LONG).show();
         }

	}
	
	//New Func:
	public void click() {
		
		 Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");
       // i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);

        //i.putExtra(RecognizerIntent., value)
       
        
        try {
            startActivityForResult(i, REQUEST_OK);
            
           	 
        } 
              
        catch (Exception e) {
       	 Toast.makeText(this, "Error initializing speech to text engine.", Toast.LENGTH_LONG).show();
        }

	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==REQUEST_OK  && resultCode==RESULT_OK) {
        	ArrayList<String> thingsYouSaid = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
        	
        	//((TextView)findViewById(R.id.text1)).setText(thingsYouSaid.get(0));
            
        	Toast.makeText(getApplicationContext(), thingsYouSaid.get(0), Toast.LENGTH_SHORT).show();
        	
        	if(thingsYouSaid.get(0).contains("spock") || thingsYouSaid.get(0).contains("spoke") || thingsYouSaid.get(0).contains("spike")){
        		
        		if(thingsYouSaid.get(0).contains("click") || thingsYouSaid.get(0).contains("picture")  ){
        			//speakText("As its name suggests, CTX provides the wider context for the data collected by the other two instruments. Scientists examine details of rocks and mineral fields with the other instruments, while CTX provides a bigger-picture view of the terrain. From 400 kilometers (250 miles) above Mars, CTX takes images spanning 30 kilometers (almost 19 miles) of terrain. The camera has a resolution of 6 meters per pixel.");
        			//openFrontFacingCameraGingerbread();
        			Intent i=new Intent(this, CameraDemoActivity.class);
        			startActivity(i);
        			
        		}
        		else
        		if(thingsYouSaid.get(0).contains("camera") || thingsYouSaid.get(0).contains("ctx")){
        			speakText("As its name suggests, CTX provides the wider context for the data collected by the other two instruments. Scientists examine details of rocks and mineral fields with the other instruments, while CTX provides a bigger-picture view of the terrain. From 400 kilometers (250 miles) above Mars, CTX takes images spanning 30 kilometers (almost 19 miles) of terrain. The camera has a resolution of 6 meters per pixel.");
        			
        		}
        		else
        			if(thingsYouSaid.get(0).contains("antenna")){
        				speakText("The high-gain antenna is a 3 meter- (10 foot-) diameter dish antenna for sending and receiving data at high rates. The high-gain antenna must be pointed accurately and is therefore steered using the gimbal mechanism.");
        				
        			}
        			else
        				if(thingsYouSaid.get(0).contains("amplifier")){
        					
        					speakText("Located on the back side of the high-gain antenna is the enclosure for the Traveling Wave Tube Amplifiers, which boost the power of the spacecraft's radio signals so they are strong enough to be detected by the Deep Space Network antennas.");
        				}
        		
        				else
        					if(thingsYouSaid.get(0).contains("twitter") || thingsYouSaid.get(0).contains("tweet")){
        						
        					    Intent intent = new Intent(getApplicationContext(), WebViewActivity.class);
        					    startActivity(intent);

        						
        					}
        				
            				
        		else
        			speakText("Sorry! No command followed.");
        	}
        	   	
        	
        }
        
        	else if(resultCode == RecognizerIntent.RESULT_AUDIO_ERROR){
        	    showToastMessage("Audio Error");
        	   }else if(resultCode == RecognizerIntent.RESULT_CLIENT_ERROR){
        	    showToastMessage("Client Error");
        	   }else if(resultCode == RecognizerIntent.RESULT_NETWORK_ERROR){
        	    showToastMessage("Network Error");
        	   }else if(resultCode == RecognizerIntent.RESULT_NO_MATCH){
        	    showToastMessage("No Match");
        	   }else if(resultCode == RecognizerIntent.RESULT_SERVER_ERROR){
        	    showToastMessage("Server Error");
        	   }
        	  super.onActivityResult(requestCode, resultCode, data);
    }
	
	 void showToastMessage(String message){
		  Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
		 }

	
	
	public void speakText(String toSpeak){
	      //String toSpeak = write.getText().toString();
	      Toast.makeText(getApplicationContext(), toSpeak, 
	      Toast.LENGTH_SHORT).show();
	      
	      ttobj.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);

	   }
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	 switch(keyCode){
	 
	   case KeyEvent.KEYCODE_VOLUME_UP:
		   //findViewById(R.id.button1).performClick();
		   click();
		   
	     return true;
	   case KeyEvent.KEYCODE_VOLUME_DOWN:
		   //findViewById(R.id.button1).performClick();
		   click();

	     return false;
	 }
	 return super.onKeyDown(keyCode, event);
	}

}
