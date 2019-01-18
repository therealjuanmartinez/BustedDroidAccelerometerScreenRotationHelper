# BustedDroidAccelerometerScreenRotationHelper
Mostly fixes screen rotation failure when an accelerometer's Y axis is broken (rotate with X and Z data only)

One day I found that my Samsung Note 8's accelerometer's Y value was maxed out which signals hardware failure.  Being 5 days out or warranty I figured I'd try for a software solution.

This doesn't fix the accelerometer, but uses X and Z values only to drive screen rotation (landscape vs. portrait).  Without Y, however, the phone can't know if it's upside down, but this is a small problem.

This also doesn't fix apps that require all of X,Y,Z data to operate correctly (some games, 360 camera apps, AR, VR, etc.)



