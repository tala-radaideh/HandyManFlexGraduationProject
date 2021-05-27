package com.example.handymanflexpartserver.Common;


import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Property;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.database.ValueEventListener;

import java.util.logging.LogRecord;

public class MarkerAnimation {
    public static void MarkerAnimationtoGb(final Marker marker, LatLng finalposition, LatLngInterpolator latLngInterpolator) {
        LatLng startposition = marker.getPosition();
        Handler handler = new Handler();
        long start = SystemClock.uptimeMillis();
        Interpolator interpolator = new AccelerateDecelerateInterpolator();
        float DurationinMs = 3000;
        handler.post(new Runnable() {
            long elapsed;
            float t, v;

            @Override
            public void run() {
                elapsed = SystemClock.uptimeMillis() - start;
                t = elapsed / DurationinMs;
                v = interpolator.getInterpolation(t);
                marker.setPosition(latLngInterpolator.interpolate(v, startposition, finalposition));
                if (t < 1) {
                    handler.postDelayed(this, 16);
                }
            }
        });


    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static void MarkerAnimationtoHC(final Marker marker, LatLng finalposition, LatLngInterpolator latLngInterpolator) {
        LatLng startLocation = marker.getPosition();
        ValueAnimator valueAnimator = new ValueAnimator();
        valueAnimator.addUpdateListener(animation -> {
            float v = valueAnimator.getAnimatedFraction();
            LatLng newposition = latLngInterpolator.interpolate(v, startLocation, finalposition);

        });

        valueAnimator.setFloatValues(0, 1);
        valueAnimator.setDuration(3000);
        valueAnimator.start();

    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public static void MarkerAnimationtoICS(final Marker marker, LatLng finalposition, LatLngInterpolator latLngInterpolator) {
        TypeEvaluator<LatLng> typeEvaluator = new TypeEvaluator<LatLng>() {
            @Override
            public LatLng evaluate(float fraction, LatLng startValue, LatLng endValue) {
                return latLngInterpolator.interpolate(fraction, startValue, endValue);
            }
        };
        Property<Marker, LatLng> property = Property.of(Marker.class, LatLng.class, "position");
        ObjectAnimator animator = ObjectAnimator.ofObject(marker, property, typeEvaluator, finalposition);
        animator.setDuration(3000);
        animator.start();
    }
}