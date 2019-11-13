package com.gl.waveproject.util;

import com.gl.waveproject.view.WaveShowView;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @Author: gl
 * @CreateDate: 2019/11/12
 * @Description: 画曲线的工具类
 */
public class WaveUtil {
    private Timer timer;
    private TimerTask timerTask;
    /**
     * 模拟源源不断的数据源
     */
    public void showWaveData(final WaveShowView waveShowView){
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                waveShowView.showLine(new Random().nextFloat()*(40f)-20f);//取得是-20到20间的浮点数
            }
        };
        //500表示调用schedule方法后等待500ms后调用run方法，50表示以后调用run方法的时间间隔
        timer.schedule(timerTask,500,50);
    }

    /**
     * 停止绘制波形
     */
    public void stop(){
        if(timer != null){
            timer.cancel();
            timer.purge();
            timer = null;
        }
        if(null != timerTask) {
            timerTask.cancel();
            timerTask = null;
        }
    }
}
