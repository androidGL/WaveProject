package com.gl.waveproject.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.gl.waveproject.R;
import com.gl.waveproject.util.WaveUtil;
import com.gl.waveproject.view.WaveShowView;

public class MainActivity extends AppCompatActivity {
    private WaveUtil waveUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        waveUtil = new WaveUtil();
    }

    //开始绘制波形
    public void start(View view) {
        WaveShowView waveShowView = findViewById(R.id.waveview);
        waveUtil.showWaveData(waveShowView);

    }
    //停止绘制波形
    public void stop(View view) {
        waveUtil.stop();
    }
}
