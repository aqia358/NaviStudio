package com.mapabc.android.activity.listener;

public abstract interface CrossingZoomListener
{
  public static final int CROSSING_ZOOM_DISAVISIBLE_STATUS = 0;
  public static final int CROSSING_ZOOM_VISIBLE_STATUS = 1;

  public abstract void onStatusedChange(int paramInt);
}