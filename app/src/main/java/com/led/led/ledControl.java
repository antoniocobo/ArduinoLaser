package com.led.led;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.MailTo;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.AsyncTask;

import java.io.IOException;
import java.util.List;
import java.util.UUID;


public class ledControl extends ActionBarActivity implements SensorEventListener{

    Button btnOn, btnOff, btnDis;
    SeekBar brightness;
    TextView lumn;
    String address = null;
    private ProgressDialog progress;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    private SensorManager sm;
    private Sensor acelerometro;
    TextView x,y;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Intent newint = getIntent();
        address = newint.getStringExtra(DeviceList.EXTRA_ADDRESS); //recivimos la mac address obtenida en la actividad anterior

        setContentView(R.layout.activity_led_control);

        sm= (SensorManager) getSystemService(SENSOR_SERVICE);
        List<Sensor>listaSensores=sm.getSensorList(Sensor.TYPE_ACCELEROMETER);
        if(!listaSensores.isEmpty()){
            acelerometro=listaSensores.get(0);
            sm.registerListener(this,acelerometro,SensorManager.SENSOR_DELAY_UI);
        }
        x=(TextView)findViewById(R.id.x);
        y=(TextView)findViewById(R.id.y);

        new ConnectBT().execute(); //Call the class to connect

    }
    private void msg(String s)
    {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_led_control, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()){
            case Sensor.TYPE_ACCELEROMETER:
                double x=event.values[0];
                double y=event.values[1];
                double z=event.values[2];
                enviarDatos(Math.round(x),Math.round(y));
                this.x.setText(""+Math.round(x));
                this.y.setText(""+Math.round(y ));
                break;
        }
    }

    private void enviarDatos(float x, float y) {
        System.out.println("X: "+Math.round(x));
        System.out.println("Y: "+Math.round(y));
        String[]datos=new String[2];
        if(x<=9 && x>=-9 && y<=9 && y>=-9) {
            if (x >= 0) {
                datos[0] = Float.toString(Math.round(x) + 10);
            } else {
                datos[0] = Float.toString(Math.round(x)) + " ";
            }
            if (y >= 0) {
                datos[1] = Float.toString(Math.round(y) + 10);
            } else {
                datos[1] = Float.toString(Math.round(y)) + " ";
            }
            if (btSocket!=null)
            {
                try
                {
                    if(btSocket.isConnected()) {
                        for(int i=0;i<datos.length;i++){
                            btSocket.getOutputStream().write(datos[i].charAt(0));
                            btSocket.getOutputStream().write(datos[i].charAt(1));
                        }
                    }
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                    msg("Error 4");
                }
            }
        }



    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private class ConnectBT extends AsyncTask<Void, Void, Void>  // UI thread
    {
        private boolean ConnectSuccess = true;

        @Override
        protected void onPreExecute()
        {
            progress = ProgressDialog.show(ledControl.this, "Connecting...", "Please wait!!!");
        }

        @Override
        protected Void doInBackground(Void... devices)
        {
            try
            {
                if (btSocket == null || !isBtConnected)
                {
                 myBluetooth = BluetoothAdapter.getDefaultAdapter();
                 BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);//conectamos al dispositivo y chequeamos si esta disponible
                 btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);
                 BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                 btSocket.connect();
                }
            }
            catch (IOException e)
            {
                ConnectSuccess = false;
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result)
        {
            super.onPostExecute(result);

            if (!ConnectSuccess)
            {
                msg("Conexión Fallida");
                finish();
            }
            else
            {
                msg("Conectado");
                isBtConnected = true;
            }
            progress.dismiss();
        }
    }
}
