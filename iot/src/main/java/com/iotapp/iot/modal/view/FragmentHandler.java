package com.iotapp.iot.modal.view;

/**
 * Created by kundankumar on 26/09/16.
 */
public class FragmentHandler {
    private static FragmentHandler ourInstance = new FragmentHandler();
    private Object currFragment;
    private String title;
    private Object data;
    private boolean isNextFragmentMove;
    private boolean isBackFragmentMove;
    private Integer scrNo;
    private FragmentHandler() {
    }

    public static FragmentHandler getInstance() {
        return ourInstance;
    }

    public boolean isNextFragmentMove() {
        return isNextFragmentMove;
    }

    public void setIsNextFragmentMove(boolean isNextFragmentMove) {
        this.isNextFragmentMove = isNextFragmentMove;
    }

    public void setIsBackFragmentMove(boolean isBackFragmentMove) {
        this.isBackFragmentMove = isBackFragmentMove;
    }

    public Object getCurrFragment() {
        return currFragment;
    }

    public void setCurrFragment(Object currFragment) {
        this.currFragment = currFragment;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Integer getScrNo() {
        return scrNo;
    }

    public void setScrNo(int scrNo) {
        this.scrNo = scrNo;
    }
}
