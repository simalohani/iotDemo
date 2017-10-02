package com.iotapp.iot.fragment;

import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;


public class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {
    private static final long DELAY_MILLIS = 100;
    private OnItemClickListener mListener;
    private RecyclerView mRecyclerView;
    private GestureDetector mGestureDetector;
    private boolean mIsPrepressed = false;
    private boolean mIsShowPress = false;
    private View mPressedView = null;

    public RecyclerItemClickListener(RecyclerView recyclerView, OnItemClickListener listener) {
        mListener = listener;
        mRecyclerView = recyclerView;
        mGestureDetector = new GestureDetector(recyclerView.getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                mIsPrepressed = true;
                mPressedView = mRecyclerView.findChildViewUnder(e.getX(), e.getY());
                super.onDown(e);
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {
                if (mIsPrepressed && mPressedView != null) {
                    mPressedView.setPressed(true);
                    mIsShowPress = true;
                }
                super.onShowPress(e);
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                if (mIsPrepressed && mPressedView != null) {
                    mPressedView.setPressed(true);
                    final View pressedView = mPressedView;
                    pressedView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            pressedView.setPressed(false);
                        }
                    }, DELAY_MILLIS);
                    mIsPrepressed = false;
                    mPressedView = null;
                }
                return true;
            }

        });
    }



    @Override
    public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
        View childView = view.findChildViewUnder(e.getX(), e.getY());
        if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
            mListener.onItemClick(childView, view.getChildLayoutPosition(childView));
        } else if (e.getActionMasked() == MotionEvent.ACTION_UP && mPressedView != null && mIsShowPress) {
            mPressedView.setPressed(false);
            mIsShowPress = false;
            mIsPrepressed = false;
            mPressedView = null;
        }
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView view, MotionEvent motionEvent) {
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }

}
