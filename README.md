# WaveProject
Android端绘制曲线图，折线图，心电图等
详细说明请见：
https://blog.csdn.net/jiujiaopanduola/article/details/103042552

首先自定义View，WaveShowView,重写其onLayout，onDraw方法，注释写的很清楚了：

public class WaveShowView extends View {
 
    private float mWidth = 0,mHeight = 0;//自身大小
    private int mBackGroundColor = Color.BLACK;
    private Paint mLinePaint;//画笔
    private Paint mWavePaint;//心电图的折现
    private Path mPath;//心电图的路径
 
    private ArrayList refreshList = new ArrayList();//后加的数据点
    private int row;//背景网格的行数和列数
 
    //心电
    private float MAX_VALUE = 20;
    private float WAVE_LINE_STROKE_WIDTH = 2;
    private int mWaveLineColor = Color.parseColor("#EE4000");//波形颜色
    private  float nowX,nowY;//目前的xy坐标
 
    //网格
    private final int GRID_SMALL_WIDTH = 10;//每一个网格的宽度和高度,包括线
    private final int GRID_BIG_WIDTH = 50;//每一个大网格的宽度和高度,包括线
    private int xSmallNum,ySmallNum,xBigNum,yBigNum;//小网格的横格，竖格，大网格的横格，竖格数量
    private final int GRID_LINE_WIDTH=2;//网格的线的宽度
    private int mWaveSmallLineColor = Color.parseColor("#092100");//小网格颜色
    private int mWaveBigLineColor = Color.parseColor("#1b4200");//小网格颜色
 
    public WaveShowView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }
 
    public WaveShowView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
 
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mWidth = w;
        mHeight = h;
        super.onSizeChanged(w, h, oldw, oldh);
    }
 
    private void init() {
        mLinePaint = new Paint();
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setStrokeWidth(GRID_LINE_WIDTH);
        mLinePaint.setAntiAlias(true);//抗锯齿效果
 
        mWavePaint = new Paint();
        mWavePaint.setStyle(Paint.Style.STROKE);
        mWavePaint.setColor(mWaveLineColor);
        mWavePaint.setStrokeWidth(WAVE_LINE_STROKE_WIDTH);
        mWavePaint.setAntiAlias(true);//抗锯齿效果
 
        mPath = new Path();
 
    }
 
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mWidth = getMeasuredWidth();//获取view的宽
        mHeight = getMeasuredHeight();//获取view的高
 
        row= (int) (mWidth/(GRID_SMALL_WIDTH));//获取行数
 
        //小网格
        xSmallNum = (int) (mHeight/GRID_SMALL_WIDTH);//横线个数=总高度/小网格高度
        ySmallNum = (int) (mWidth/GRID_SMALL_WIDTH);//竖线个数=总宽度/小网格宽度
        //大网格
        xBigNum = (int) (mHeight/GRID_BIG_WIDTH);//横线个数
        yBigNum = (int) (mWidth/GRID_BIG_WIDTH);//竖线个数
    }
 
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //绘制网格
         drawGrid(canvas);
        //绘制波形
        drawWaveLine(canvas);
    }
 
    /**
     * 画折线
     * @param canvas
     */
    private void drawWaveLine(Canvas canvas) {
        if(null == refreshList || refreshList.size()<=0){
            return;
        }
        mPath.reset();
        mPath.moveTo(0f,mHeight/2);
        for (int i = 0;i<refreshList.size();i++){
            nowX = i* GRID_SMALL_WIDTH;
            float dataValue = (float) refreshList.get(i);
            if(dataValue>0){
                if(dataValue>MAX_VALUE * 0.8){
                    dataValue = MAX_VALUE * 0.8f;
                }
            }else {
                if(dataValue< -MAX_VALUE * 0.8){
                    dataValue = -MAX_VALUE * 0.8f;
                }
            }
            nowY = mHeight/2 + dataValue *(mHeight/(MAX_VALUE*2));
            mPath.lineTo(nowX,nowY);
        }
        canvas.drawPath(mPath, mWavePaint);
        if(refreshList.size()>row){
            refreshList.remove(0);
        }
    }
 
    //画网格
    private void drawGrid(Canvas canvas){
 
        canvas.drawColor(mBackGroundColor);
        //画小网格
        mLinePaint.setColor(mWaveSmallLineColor);
        //画横线
        for(int i = 0;i < xSmallNum + 1;i++){
           canvas.drawLine(0,i*GRID_SMALL_WIDTH,
                   mWidth, i*GRID_SMALL_WIDTH, mLinePaint);
        }
        //画竖线
        for(int i = 0;i < ySmallNum+1;i++){
            canvas.drawLine(i*GRID_SMALL_WIDTH,0,
                    i*GRID_SMALL_WIDTH,mHeight, mLinePaint);
        }
        //画大网格
        mLinePaint.setColor(mWaveBigLineColor);
        //画横线
        for(int i = 0;i < xBigNum + 1;i++){
            canvas.drawLine(0,i*GRID_BIG_WIDTH,
                    mWidth, i*GRID_BIG_WIDTH, mLinePaint);
        }
        //画竖线
        for(int i = 0;i < yBigNum+1;i++){
            canvas.drawLine(i*GRID_BIG_WIDTH,0,
                    i*GRID_BIG_WIDTH,mHeight, mLinePaint);
        }
    }
    public void showLine(float line) {
        refreshList.add(line);
        postInvalidate();
    }
    //重置折现的坐标集合
    public void resetCanavas() {
        refreshList.clear();
    }
}
然后写个使用的类，WaveUtil类，非常简单：

Timer是一种定时器，而TimerTask是一个抽象类，表示被Timer计划的任务，实现了Runnable接口。

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
最后在activity页面调用，也非常简单，为了避免内存泄漏，退出activity时必须停止绘制：

//开始绘制波形
public void start(View view) {
    WaveShowView waveShowView = findViewById(R.id.waveview);
    waveUtil.showWaveData(waveShowView);
 
}
//停止绘制波形
public void stop(View view) {
    waveUtil.stop();
}
————————————————
版权声明：本文为CSDN博主「Android小兰」的原创文章，遵循 CC 4.0 BY-SA 版权协议，转载请附上原文出处链接及本声明。
原文链接：https://blog.csdn.net/jiujiaopanduola/article/details/103042552
