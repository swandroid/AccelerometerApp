package com.example.accelerometerapp;

import interdroid.swan.ExpressionManager;
import interdroid.swan.SensorInfo;
import interdroid.swan.SwanException;
import interdroid.swan.ValueExpressionListener;
import interdroid.swan.swansong.ExpressionFactory;
import interdroid.swan.swansong.ExpressionParseException;
import interdroid.swan.swansong.TimestampedValue;
import interdroid.swan.swansong.ValueExpression;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	private static final String TAG = "AccelerometerApp";
	SensorInfo swanSensor;
	
	/* name of the sensor */
	final String SENSOR_NAME = "movement";
	
	/* random id */
	public final int REQUEST_CODE = 123;
	
	TextView tv = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initialize();
		
	}
	
	
	public void initialize(){
		
		tv = (TextView) findViewById(R.id.textView1);
		
		try {
			swanSensor = ExpressionManager.getSensor(MainActivity.this, SENSOR_NAME);
		} catch (SwanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/* start activity for configuring sensor */
		startActivityForResult(swanSensor.getConfigurationIntent(), REQUEST_CODE);
		
	}
	
	/* Invoked on pressing back key from the sensor configuration activity */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		super.onActivityResult(requestCode, resultCode, data);
		
		if (resultCode == RESULT_OK) {
			
				if (REQUEST_CODE == requestCode) {
					String myExpression = data.getStringExtra("Expression");
					/*Based on sensor configuration an expression will be created*/
					Log.d(TAG, "expression: " + myExpression);
					
					registerSWANSensor(myExpression);
									
				}
		}

	}
	
	/* Register expression to SWAN */ 
	private void registerSWANSensor(String myExpression){
		
			try {
				ExpressionManager.registerValueExpression(this, String.valueOf(REQUEST_CODE),
				(ValueExpression) ExpressionFactory.parse(myExpression),
				new ValueExpressionListener() {

					/* Registering a listener to process new values from the registered sensor*/
					@Override
					public void onNewValues(String id,
							TimestampedValue[] arg1) {
						if (arg1 != null && arg1.length > 0) {
							String value = arg1[0].getValue().toString();
							tv.setText("Value = "+value);
									
						} else {
							tv.setText("Value = null");
						}

					}
				});
			} catch (SwanException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExpressionParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	

	}
	
	/* Unregister expression from SWAN */
	private void unregisterSWANSensor(){
		
		ExpressionManager.unregisterExpression(this, String.valueOf(REQUEST_CODE));
		
	}
	
	
	@Override
	protected void onPause() {
		super.onPause();
		unregisterSWANSensor();
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterSWANSensor();

	}
	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
