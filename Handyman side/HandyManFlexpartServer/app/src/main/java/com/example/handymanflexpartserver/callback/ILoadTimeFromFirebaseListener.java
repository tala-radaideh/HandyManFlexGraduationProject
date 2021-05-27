package com.example.handymanflexpartserver.callback;

public interface ILoadTimeFromFirebaseListener {
    void onLoadOnlyTimeSuccess(long estimateTimeInMs);
    void onLoadTimeFailed(String message);
}
