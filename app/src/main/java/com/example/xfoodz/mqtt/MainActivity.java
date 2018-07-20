package com.example.xfoodz.mqtt;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManager;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;

import java.io.IOException;

public class MainActivity extends Activity {
    MQTTHelper mqttHelper;
    private Gpio mLedGpioR;
    private Gpio mLedGpioG;
    private Gpio mLedGpioB;
    private Gpio mLightGpio;
    private boolean mLedState = false;
    private static final int interval = 1000;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            String R = "BCM26";
            String G = "BCM16";
            String B = "BCM12";
            String Light = "BCM6";
            mLedGpioR = PeripheralManager.getInstance().openGpio(R);
            mLedGpioR.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
            mLedGpioG = PeripheralManager.getInstance().openGpio(G);
            mLedGpioG.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
            mLedGpioB = PeripheralManager.getInstance().openGpio(B);
            mLedGpioB.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
            mLightGpio = PeripheralManager.getInstance().openGpio(Light);
            mLightGpio.setDirection(Gpio.DIRECTION_IN);
        }
        catch (IOException e){
            Log.e("Error", "Error on PeripheralIO API", e);
        }
        startMqtt();
    }

    private void startMqtt() {
        mqttHelper = new MQTTHelper(getApplicationContext());
        mqttHelper.setCallback(new MqttCallbackExtended() {
                @Override
            public void connectComplete(boolean b, String s) {
                Log.w("Mqtt", "Connection Complete!");
            }

            @Override
            public void connectionLost(Throwable throwable) {
                Log.w("Mqtt", "Connection Lost!");
            }

            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                String str = new String(mqttMessage.getPayload());
                Log.w("Message", str);
                MqttMessage mess;
                MqttAndroidClient client = mqttHelper.mqttAndroidClient;
                String clientID = mqttHelper.clientId;
                switch (str) {
                    case "on":
                        turnOnLed();
                        mess = new MqttMessage("LED is ON".getBytes());
                        client.publish(topic + " from " + clientID, mess);
                        break;
                    case "on-red":
                        turnOnRedLed();
                        mess = new MqttMessage("Red LED is ON".getBytes());
                        client.publish(topic + " from " + clientID, mess);
                        break;
                    case "on-green":
                        turnOnGreenLed();
                        mess = new MqttMessage("Green LED is ON".getBytes());
                        client.publish(topic + " from " + clientID, mess);
                        break;
                    case "on-blue":
                        turnOnBlueLed();
                        mess = new MqttMessage("Blue LED is ON".getBytes());
                        client.publish(topic + " from " + clientID, mess);
                        break;
                    case "off":
                        turnOffLed();
                        mess = new MqttMessage("LED is OFF".getBytes());
                        client.publish(topic + " from " + clientID, mess);
                        break;
                    case "on-sensor":
                        mHandler.post(mLightRunnable);
                        break;
                    case "off-sensor":
                        mHandler.removeCallbacks(mLightRunnable);
                        break;
                    default:
                }
            }
            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
                Log.w("Message", "Delivery Complete!");
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            mLedGpioR.close();
            mLedGpioG.close();
            mLedGpioB.close();
        } catch (IOException e) {
            Log.e("Error", "Error on PeripheralIO API", e);
        } finally {
            mLedGpioR = null;
            mLedGpioG = null;
            mLedGpioB = null;
        }
    }

    private void turnOnLed(){
        mLedState = true;
        if (mLedGpioR == null) {
            return;
        }
        try {
            mLedGpioR.setValue(mLedState);
        } catch (IOException e) {
            Log.e("Error", "Error on PeripheralIO API", e);
        }
        if (mLedGpioG == null) {
            return;
        }
        try {
            mLedGpioG.setValue(mLedState);
        } catch (IOException e) {
            Log.e("Error", "Error on PeripheralIO API", e);
        }
        if (mLedGpioB == null) {
            return;
        }
        try {
            mLedGpioB.setValue(mLedState);
        } catch (IOException e) {
            Log.e("Error", "Error on PeripheralIO API", e);
        }
    }

    private void turnOnRedLed(){
        if (mLedGpioR == null) {
            return;
        }
        try {
            mLedState = true;
            mLedGpioR.setValue(mLedState);
        } catch (IOException e) {
            Log.e("Error", "Error on PeripheralIO API", e);
        }
    }

    private void turnOnGreenLed(){
        if (mLedGpioG == null) {
            return;
        }
        try {
            mLedState = true;
            mLedGpioG.setValue(mLedState);
        } catch (IOException e) {
            Log.e("Error", "Error on PeripheralIO API", e);
        }
    }

    private void turnOnBlueLed(){
        if (mLedGpioB == null) {
            return;
        }
        try {
            mLedState = true;
            mLedGpioB.setValue(mLedState);
        } catch (IOException e) {
            Log.e("Error", "Error on PeripheralIO API", e);
        }
    }

    private void turnOffLed(){
        mLedState = false;
        if (mLedGpioR == null) {
            return;
        }
        try {
            mLedGpioR.setValue(mLedState);
        } catch (IOException e) {
            Log.e("Error", "Error on PeripheralIO API", e);
        }
        if (mLedGpioG == null) {
            return;
        }
        try {
            mLedGpioG.setValue(mLedState);
        } catch (IOException e) {
            Log.e("Error", "Error on PeripheralIO API", e);
        }
        if (mLedGpioB == null) {
            return;
        }
        try {
            mLedGpioB.setValue(mLedState);
        } catch (IOException e) {
            Log.e("Error", "Error on PeripheralIO API", e);
        }
    }

    private Runnable mLightRunnable = new Runnable() {
        @Override
        public void run() {
            MqttMessage mess;
            MqttAndroidClient client = mqttHelper.mqttAndroidClient;
            try {
                String booMess = String.valueOf(mLightGpio.getValue());
                Log.d("Debug", "Value of sensor: " + booMess);
            } catch (IOException e) {
                e.printStackTrace();
            }
            // String clientID = mqttHelper.clientId;
            if (mLightGpio == null) {
                return;
            }
            try {
                if (!mLightGpio.getValue()) {
                    if(mLedState){
                        mess = new MqttMessage("off".getBytes());
                        client.publish("led/sensor" , mess);
                        mLedState = false;
                    }
                }
                else {
                    if(!mLedState){
                        mess = new MqttMessage("on".getBytes());
                        client.publish("led/sensor" , mess);
                        mLedState = true;
                    }
                }
            } catch (IOException e) {
                Log.e("Error", "Error getting value from sensor", e);
            } catch (MqttPersistenceException e) {
                e.printStackTrace();
            } catch (MqttException e) {
                e.printStackTrace();
            }
            mHandler.postDelayed(mLightRunnable, interval);
        }
    };
}
