package com.example.basecodelibrary.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.example.basecodelibrary.R;
import com.example.basecodelibrary.bean.UpdateEvent;
import com.example.basecodelibrary.eventbus.EventBus;
import com.example.basecodelibrary.eventbus.EventThread;
import com.example.basecodelibrary.eventbus.ManagedEventBus;
import com.example.basecodelibrary.eventbus.ObserverCallback;

public class MainActivity extends AppCompatActivity implements ObserverCallback {
    private ManagedEventBus eventbus;
    Button start_tv;
    TextView result_tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        eventbus = new ManagedEventBus(this);
        eventbus.subscribeEvent(this, UpdateEvent.class, EventThread.MAIN_THREAD, this);
        start_tv = findViewById(R.id.start_tv);
        result_tv = findViewById(R.id.event_result_tv);
    }

    @Override
    public void handleBusEvent(Object observer, Object eventObj) {
        if (eventObj instanceof UpdateEvent) {
            EventBus.unsubscribeEvent(this);
            UpdateEvent event = (UpdateEvent) eventObj;
           if(result_tv != null){
               result_tv.setText("事件结果，updateNum:" + event.updateNum);
           }
        }
    }
}
