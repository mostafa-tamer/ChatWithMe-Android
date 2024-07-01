package com.mostafatamer.chatwithme.utils;

import android.os.CountDownTimer;

import androidx.annotation.NonNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CallDecorator<T> {
    private final Callback<T> callback = new Callback<T>() {
        @Override
        public void onResponse(@NonNull Call<T> call, @NonNull Response<T> response) {

//            new Thread(() -> {
//                try {
//                    Thread.sleep(DELAY);
//                } catch (InterruptedException ignored) {
//                }
//
//                new Handler(Looper.getMainLooper()).post(() -> {
//                    onSuccess(response);
//                    onEnd();
//                });
//            }).start();

            onSuccess(response);
            onEnd();
        }

        @Override
        public void onFailure(@NonNull Call<T> call, @NonNull Throwable t) {
            onFail(t);
            onEnd();
        }
    };
    private final Callback<T> safeCallback = new Callback<T>() {
        @Override
        public void onResponse(@NonNull Call<T> call, @NonNull Response<T> response) {
            onSuccess(response);
            countDownTimer.start();
        }

        @Override
        public void onFailure(@NonNull Call<T> call, @NonNull Throwable t) {
            onFail(t);
            countDownTimer.start();
        }
    };
    CountDownTimer countDownTimer = new CountDownTimer(500, 500) {
        @Override
        public void onTick(long millisUntilFinished) {
        }

        @Override
        public void onFinish() {
            onEnd();
        }
    };
    private final Call<T> call;
    private static final int DELAY = 0;
    private boolean isLoading;
    private static boolean isSafeLoading;
    private static int numberOfRunningServices;
    private OnServerResponseSucceed<T> onServiceInteractionSuccess;
    private OnServerResponseFail onServiceInteractionFail;
    private OnStartInteraction onStartInteraction;
    private OnEndInteraction onEndServiceInteraction;
    private LoadingObserver loadingObserver;
    private static LoadingObserver staticLoadingObserver;
    private static OnBusyLoading onBusy;

    public CallDecorator(Call<T> call) {
        this.call = call;
    }

    public static void setOnSafeExecution(OnBusyLoading onBusy) {
        CallDecorator.onBusy = onBusy;
    }

    public CallDecorator<T> setLoadingObserver(LoadingObserver loadingObserver) {
        this.loadingObserver = loadingObserver;
        return this;
    }

    public static void setStaticLoadingObserver(LoadingObserver loadingObserver) {
        CallDecorator.staticLoadingObserver = loadingObserver;
    }

    public CallDecorator<T> setOnStartInterAction(OnStartInteraction onStartInteraction) {
        this.onStartInteraction = onStartInteraction;
        return this;
    }

    public CallDecorator<T> setOnSuccess(OnServerResponseSucceed<T> onServiceInteractionSuccess) {
        this.onServiceInteractionSuccess = onServiceInteractionSuccess;
        return this;
    }

    public CallDecorator<T> setOnServiceInteractionFail(OnServerResponseFail onServiceInteractionFail) {
        this.onServiceInteractionFail = onServiceInteractionFail;
        return this;
    }

    public CallDecorator<T> setOnEndServiceInteraction(OnEndInteraction onEndServiceInteraction) {
        this.onEndServiceInteraction = onEndServiceInteraction;
        return this;
    }

    public boolean isLoading() {
        return isLoading;
    }

    public static int getNumberOfRunningServices() {
        return numberOfRunningServices;
    }

    public void execute(
            OnServerResponseSucceed<T> onServerResponseSucceed
    ) {
        this.setOnSuccess(onServerResponseSucceed);
        execute();
    }

    public void execute() {
        this.run();
    }

    public void execute(
            OnServerResponseSucceed<T> onServerResponseSucceed,
            OnServerResponseFail onServiceInteractionFail
    ) {
        this.setOnServiceInteractionFail(onServiceInteractionFail);
        execute(onServerResponseSucceed);
    }

    public void execute(
            OnStartInteraction onStartInteraction,
            OnServerResponseSucceed<T> onServerResponseSucceed,
            OnServerResponseFail onServiceInteractionFail,
            OnEndInteraction onEndInteraction
    ) {
        this.setOnStartInterAction(onStartInteraction);
        execute(onServerResponseSucceed, onServiceInteractionFail, onEndInteraction);
    }

    public void execute(
            OnServerResponseSucceed<T> onServerResponseSucceed,
            OnServerResponseFail onServiceInteractionFail,
            OnEndInteraction onEndInteraction
    ) {
        this.setOnEndServiceInteraction(onEndInteraction);
        execute(onServerResponseSucceed, onServiceInteractionFail);
    }

    public void safeExecute(
            OnServerResponseSucceed<T> onServerResponseSucceed,
            OnServerResponseFail onServiceInteractionFail,
            OnEndInteraction onEndInteraction
    ) {
        this.setOnEndServiceInteraction(onEndInteraction);
        safeExecute(onServerResponseSucceed, onServiceInteractionFail);
    }

    public void safeExecute(
            OnServerResponseSucceed<T> onServerResponseSucceed
    ) {
        this.setOnSuccess(onServerResponseSucceed);
        this.runSafe();
    }

    public void safeExecute(
            OnServerResponseSucceed<T> onServerResponseSucceed,
            OnServerResponseFail onServiceInteractionFail
    ) {
        this.setOnServiceInteractionFail(onServiceInteractionFail);
        this.safeExecute(onServerResponseSucceed);
    }

    public void unlockSafeLoading() {
        isSafeLoading = false;
    }

    public void run() {
        onStart();
        call.enqueue(callback);
    }

    private void runSafe() {
        if (!isSafeLoading) {
            onStart();
            call.enqueue(safeCallback);
        } else {
            onBusyHandler();
        }
    }

    private void onFail(@NonNull Throwable t) {
        if (onServiceInteractionFail != null) {
            onServiceInteractionFail.onResponse(t);
        }
    }

    private void onStart() {
        setLoadingState(true);
        incrementNumberOfRunningServices();
        onStartEventHandler();
    }

    private void onBusyHandler() {
        if (onBusy != null) {
            onBusy.handleOnBusy();
        }
    }

    private void onStartEventHandler() {
        if (onStartInteraction != null) {
            onStartInteraction.handleEvent();
        }
    }

    private static void incrementNumberOfRunningServices() {
        numberOfRunningServices++;
    }

    private void setLoadingState(boolean isLoading) {
        this.isLoading = isLoading;
        isSafeLoading = isLoading;
        if (loadingObserver != null) {
            loadingObserver.observeLoading(isLoading);
        }
        if (staticLoadingObserver != null) {
            staticLoadingObserver.observeLoading(isLoading);
        }
    }

    private void onEnd() {
        decrementNumberOfRunningServices();
        onEndEventHandler();
        setLoadingState(false);
    }

    private void decrementNumberOfRunningServices() {
        numberOfRunningServices--;
    }

    private void onEndEventHandler() {
        if (onEndServiceInteraction != null) {
            onEndServiceInteraction.handleEvent();
        }
    }

    private void onSuccess(@NonNull Response<T> response) {
        if (onServiceInteractionSuccess != null) {
            onServiceInteractionSuccess.onResponse(response.body());
        }
    }

    public Call<T> getCall() {
        return call;
    }

    @FunctionalInterface
    public interface LoadingObserver {
        void observeLoading(boolean isLoading);
    }

    @FunctionalInterface
    public interface OnStartInteraction {
        void handleEvent();
    }

    @FunctionalInterface
    public interface OnEndInteraction {
        void handleEvent();
    }

    public interface OnServerResponseSucceed<T> {
        void onResponse(T response);
    }

    @FunctionalInterface
    public interface OnServerResponseFail {
        void onResponse(Throwable throwable);
    }

    @FunctionalInterface
    public interface OnBusyLoading {
        void handleOnBusy();
    }
}