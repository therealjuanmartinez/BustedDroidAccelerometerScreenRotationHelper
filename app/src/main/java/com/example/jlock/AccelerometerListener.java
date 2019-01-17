package com.example.jlock;


public interface AccelerometerListener {
    public void onAccelerationChanged(float x, float y, float z);
    public void onShake(float force);
    public void changeRotation(int rot);
}
