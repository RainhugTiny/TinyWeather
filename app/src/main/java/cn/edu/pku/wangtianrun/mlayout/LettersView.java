package cn.edu.pku.wangtianrun.mlayout;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.jar.Attributes;

import cn.edu.pku.wangtianrun.tinyweather.R;

public class LettersView extends View {
    //TAG
    private static final String TAG="LettersView";
    //导航栏的索引项
    private  String[] strChars={"A", "B", "C", "D", "E", "F", "G", "H",
            "I", "J","K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W",
            "X", "Y", "Z", "#"};
    //画笔
    private Paint mPaint;
    //选中字母的下标
    private int checkIndex;
    //构造方法
    public LettersView(Context context) {
        super(context);
        initView();
    }
    public  LettersView(Context context, AttributeSet attrs){
        super(context,attrs);
        initView();
    }
    public LettersView(Context context, AttributeSet attrs,int defStyleAttr){
        super(context,attrs,defStyleAttr);
        initView();
    }
    //初始化
    private void initView(){
        //实例化画笔
        mPaint=new Paint();
        //设置style
        mPaint.setTypeface(Typeface.DEFAULT_BOLD);
        //设置抗锯齿
        mPaint.setAntiAlias(true);
        //设置被点击索引项的初始值
        checkIndex=-1;
    }
    //绘制函数
    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        /**
         *
         获取View的宽高用以排列索引项
         */
        int viewWidth=getWidth();
        int viewHeight=getHeight();
        int singleHeight=viewHeight/strChars.length;//索引项高度
        //绘制字母
        for (int i=0;i<strChars.length;i++){
            //设置选中索引项的颜色与字体大小
            if(i==checkIndex){
                mPaint.setColor(Color.WHITE);
                mPaint.setTextSize(50);
            }else {
                mPaint.setColor(Color.BLACK);
                mPaint.setTextSize(40);
            }
            //获得字母的横向位置
            float lettersX=(viewWidth-mPaint.measureText(strChars[i]))/2;
            //获得字母的纵向位置
            float lettersY=singleHeight*i+singleHeight;
            //绘制字母
            canvas.drawText(strChars[i],lettersX,lettersY,mPaint);
        }
    }
    private OnLettersListViewListener onLettersListViewListener;
    //获得onLettersListViewListener
    public OnLettersListViewListener getOnLettersListViewListener() {
        return onLettersListViewListener;
    }
    //设置onLettersListViewListener
    public void setOnLettersListViewListener(OnLettersListViewListener onLettersListViewListener) {
        this.onLettersListViewListener = onLettersListViewListener;
    }
    //定义接口
    public interface OnLettersListViewListener{
        public void onLettersListener(String s);
    }
    /*
    * 设置触摸事件
    * */
    @Override
    public boolean dispatchTouchEvent(MotionEvent event){
        //判断手势
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                setBackgroundResource(R.color.colorAccent);
                //获取点击的Y坐标
                float y=event.getY();
                //第一次被选中的下标
                int oldCheckIndex=checkIndex;
                /*
                * 计算对应位置的索引项
                * */
                int c=(int) (y/getHeight()*strChars.length);
                Log.i(TAG,"c"+c);
                //判断移动
                if(oldCheckIndex!=c){
                    //越界判断
                    if(c>=0&&c<strChars.length){
                        //效果联动
                        if(onLettersListViewListener!=null){
                            onLettersListViewListener.onLettersListener(strChars[c]);
                        }
                    }
                    checkIndex=c;
                    //更新View
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                //设置透明背景
                setBackgroundResource(android.R.color.transparent);
                //恢复不选中
                checkIndex=-1;
                invalidate();
                break;
        }
        return true;
    }
}
