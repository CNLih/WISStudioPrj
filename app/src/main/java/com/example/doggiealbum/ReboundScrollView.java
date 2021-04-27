package com.example.doggiealbum;

import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.text.method.MovementMethod;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.Toast;

public class ReboundScrollView extends ScrollView{
    private boolean enableTopRebound = true;
    private boolean enableBottomRebound = true;
    private OnReboundEndListener mOnReboundEndListener;
    private Button button;

    private View contentView;
    private Rect rect = new Rect();

    public ReboundScrollView(Context context) {
        super(context);
    }

    public ReboundScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ReboundScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ReboundScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
    public void setEnableTopRebound(boolean enableTopRebound){
        this.enableTopRebound = enableTopRebound;
    }

    public void setEnableBottomRebound(boolean enableBottomRebound) {
        this.enableBottomRebound = enableBottomRebound;
    }

    public ReboundScrollView setOnReboundEndListener(OnReboundEndListener onReboundEndListener){
        this.mOnReboundEndListener = onReboundEndListener;
        return this;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        contentView = getChildAt(0);
        button = findViewById(R.id.btn_new_img);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if(contentView != null){
            rect.set(contentView.getLeft(), contentView.getTop(), contentView.getRight(), contentView.getBottom() + getViewHeight(button));
        }
    }

    private int lastY;
    private boolean rebound = false;
    private int reboundDirection = 0;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if(contentView == null){
            return super.dispatchTouchEvent(ev);
        }

        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                lastY = (int) ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                if(!isScrollToTop() && !isScrollToBottom()){
                    lastY = (int) ev.getY();
                    break;
                }
                int deltaY = (int) (ev.getY() - lastY);
                if((!enableTopRebound && deltaY > 0) || (!enableBottomRebound && deltaY > 0)){
                    break;
                }
                int offset = (int)(120 * Math.atan(deltaY * 0.02));     //hhh自己想出来的阻尼曲线，拖拉到一定程度停止拖动
                contentView.layout(rect.left, rect.top + offset, rect.right, rect.bottom);
                rebound = true;
                break;
            case MotionEvent.ACTION_UP:
                if(!rebound){
                    break;
                }
                reboundDirection = contentView.getTop() - rect.top;
                TranslateAnimation animation = new TranslateAnimation(0, 0, contentView.getTop(), rect.top);
                animation.setDuration(300);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        if (mOnReboundEndListener != null){
                            if (reboundDirection > 0){
                                mOnReboundEndListener.onReboundTopComplete();
                            }
                            if (reboundDirection < 0){
                                mOnReboundEndListener.onReboundBottomComplete();
                            }
                            reboundDirection = 0;
                        }
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                contentView.startAnimation(animation);
                contentView.layout(rect.left, rect.top, rect.right, rect.bottom);
                rebound = false;
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    private boolean isScrollToTop(){
        return getScrollY() == 0;
    }

    private boolean isScrollToBottom(){
        return contentView.getHeight() <= getHeight() + getScrollY();
    }

    public interface OnReboundEndListener{     //提供给外部的接口
        void onReboundTopComplete();
        void onReboundBottomComplete();
    }

    public static int getViewHeight(View view) {
        if (view == null) return 0;
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(h, 0);
        return view.getMeasuredHeight();
    }
}
