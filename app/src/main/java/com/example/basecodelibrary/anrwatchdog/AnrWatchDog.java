package com.example.basecodelibrary.anrwatchdog;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

public class AnrWatchDog extends Thread {
    public interface ANRListener {
        public void onAppNotResponding(AnrError error);
    }

    public interface InterruptionListener {
        public void onInterrupted(InterruptedException exception);
    }

    /**
     * 无响应时长5s
     */
    private static final int DEFAULT_ANR_TIMEOUT = 5000;
    private static final ANRListener DEFAULT_ANR_LISTENER = new ANRListener() {
        @Override
        public void onAppNotResponding(AnrError error) {
            throw error;
        }
    };
    private static final InterruptionListener DEFAULT_INTERRUPTION_LISTENER = new InterruptionListener() {
        @Override
        public void onInterrupted(InterruptedException exception) {
            Log.w("ANRWatchdog", "Interrupted: " + exception.getMessage());
        }
    };
    private ANRListener _anrListener = DEFAULT_ANR_LISTENER;
    private InterruptionListener _interruptionListener = DEFAULT_INTERRUPTION_LISTENER;
    private final Handler _uiHandler = new Handler(Looper.getMainLooper());
    private final int _timeoutInterval;
    private String _namePrefix = "";
    private boolean _logThreadsWithoutStackTrace = false;
    private volatile int _tick = 0;
    private final Runnable _ticker = new Runnable() {
        @Override
        public void run() {
            _tick = (_tick + 1) % 10;
        }
    };
    public AnrWatchDog() {
        this(DEFAULT_ANR_TIMEOUT);
    }

    public AnrWatchDog(int timeoutInterval) {
        super();
        _timeoutInterval = timeoutInterval;
    }

    public AnrWatchDog setANRListener(ANRListener listener) {
        if (listener == null) {
            _anrListener = DEFAULT_ANR_LISTENER;
        } else {
            _anrListener = listener;
        }
        return this;
    }

    public AnrWatchDog setInterruptionListener(InterruptionListener listener) {
        if (listener == null) {
            _interruptionListener = DEFAULT_INTERRUPTION_LISTENER;
        } else {
            _interruptionListener = listener;
        }
        return this;
    }

    public AnrWatchDog setReportThreadNamePrefix(String prefix) {
        if (prefix == null) {
            prefix = "";
        }
        _namePrefix = prefix;
        return this;
    }

    public AnrWatchDog setReportMainThreadOnly() {

        _namePrefix = null;
        return this;

    }

    public void setLogThreadsWithoutStackTrace(boolean logThreadsWithoutStackTrace) {

        _logThreadsWithoutStackTrace = logThreadsWithoutStackTrace;
    }

    @Override
    public void run() {
        setName("|ANR-WatchDog|");
        int lastTick;
        while (!isInterrupted()) {
            lastTick = _tick;
            _uiHandler.post(_ticker);
            try {
                Thread.sleep(_timeoutInterval);
            } catch (InterruptedException e) {
                _interruptionListener.onInterrupted(e);
                return;
            }
            if (_tick == lastTick) {
                AnrError error;
                if (_namePrefix != null) {
                    error = AnrError.New(_namePrefix, _logThreadsWithoutStackTrace);
                } else {
                    error = AnrError.NewMainOnly();
                }
                _anrListener.onAppNotResponding(error);
            }
        }
    }
}
