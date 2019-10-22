package smartSpaces.Pandora.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;




public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private static final String TAG = "Activity scanner";
    int stableamount = 9;
    double maxValue;
    double maxCount;



    private boolean accChanged, gyroChanged, magnetoChanged;

    List<Double> activitystable = new ArrayList<>();

    private SensorManager sensorManager;
    private Sensor accelerometer, gyroscope;

    //magnetometer could be uncommented
    //private magnetometer;

    private double accXValue, accYValue, accZValue;
    private double gyroXvalue, gyroYValue, gyroZValue;

    // magnetometer could be uncommented
    //private double magnetoXvalue, magnetoYValue, magnetoZValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //magnetometer could be uncommented if it wants to be used
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        //magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        sensorManager.registerListener(this, accelerometer, sensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, gyroscope, sensorManager.SENSOR_DELAY_NORMAL);
        //sensorManager.registerListener(this, magnetometer, sensorManager.SENSOR_DELAY_NORMAL);

    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        checkSensorsChanged(event);
        changedSensorActivity();
    }

    private void changedSensorActivity() {
        if (accChanged && gyroChanged ){
            Double[] array = getSensorData();

            try{
                classifyData(array);
            }catch (Exception e){
                System.out.println("and it stopped working");
            }
        }
    }

    private void classifyData(Double[] array) throws Exception {
        double result = WekaClassifier.classify(array);

        String textresult;

        activitystable.add(result);
        if(activitystable.size() == stableamount){


            result = getBestClassifiedActivity();

            if(result == 0.0){

                textresult = "you are Flag";

                activitystable.clear();
            }else if(result == 1.0) {
                textresult = "you are Still";

                activitystable.clear();
            }else if(result == 2.0) {
                textresult = "you are Shaking";

                activitystable.clear();
            }else if(result == 3.0) {
                textresult = "you are pirhouette";

                activitystable.clear();
            }else{
                textresult = "you are doing nothing";

                activitystable.clear();
            }

            TextView placeText = (TextView)findViewById(R.id.textresult);
            placeText.setText(textresult);

        }
    }

    private double getBestClassifiedActivity() {
        double result;
        for (int i = 0; i < activitystable.size(); ++i) {
            int count = 0;
            for (int j = 0; j < activitystable.size(); ++j) {
                if (activitystable.get(j) == activitystable.get(i)) ++count;
            }
            if (count > maxCount) {
                maxCount = count;
                maxValue = activitystable.get(i);
            }
        }

        result = maxValue;
        maxValue = -1;
        maxCount = -1;
        return result;
    }

    private Double[] getSensorData() {
        accChanged = false;
        gyroChanged = false;
        //should be 9 if the magnetometer is uncommented
        Double array [] = new Double[6];
        array[0] = accXValue;
        array[1] = accYValue;
        array[2] = accZValue;
        array[3] = gyroXvalue;
        array[4] = gyroYValue;
        array[5] = gyroZValue;

        //could be uncommented if the magnetometer on someone's phone works properly
        //magnetoChanged = false;
        //array[6] = magnetoXvalue;
        //array[7] = magnetoYValue;
        //array[8] = magnetoZValue;
        return array;
    }

    private void checkSensorsChanged(SensorEvent event) {
        Sensor sensor = event.sensor;

        Log.d(TAG, "onSensorChanged: sensor: " + Sensor.TYPE_ACCELEROMETER);
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){

            this.accXValue = event.values[0];
            this.accYValue = event.values[1];
            this.accZValue = event.values[2];
            accChanged = true;
            Log.d(TAG, "startTracking: sensordata changed: x: " + this.accXValue + ", y: " + this.accYValue + ", z: " + this.accZValue);
        } else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            this.gyroXvalue = event.values[0];
            this.gyroYValue = event.values[1];
            this.gyroZValue = event.values[2];
            gyroChanged = true;
        }
        //could be uncommented if the magnetometer is used
        /*
        else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){
            this.magnetoXvalue = event.values[0];
            this.magnetoYValue = event.values[1];
            this.magnetoZValue = event.values[2];
            magnetoChanged = true;
        }
*/
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


}

