package com.example.basecodelibrary.anrwatchdog;

import android.os.Looper;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class AnrError extends Error {
    private static class $ {
        private final String mName;
        private final StackTraceElement[] mStackTrace;

        private class ThrowableThread extends Throwable {
            private static final long serialVersionUID = 2L;

            private ThrowableThread(ThrowableThread other) {
                super(mName, other);
            }

            @Override
            public Throwable fillInStackTrace() {
                setStackTrace(mStackTrace);
                return this;
            }
        }

        private $(String name, StackTraceElement[] stacktrace) {
            mName = name;
            mStackTrace = stacktrace;
        }
    }

    private static final long serialVersionUID = 1L;
    private final Map<Thread, StackTraceElement[]> mStackTraces;

    private AnrError($.ThrowableThread st, Map<Thread, StackTraceElement[]> stacktraces) {
        super("Application Not Responding", st);
        mStackTraces = stacktraces;
    }

    public Map<Thread, StackTraceElement[]> getStackTraces() {
        return mStackTraces;
    }

    @Override
    public Throwable fillInStackTrace() {
        setStackTrace(new StackTraceElement[]{});
        return this;
    }

    static AnrError New(String prefix, boolean logThreadsWithoutStackTrace) {
        final Thread mainThread = Looper.getMainLooper().getThread();
        final Map<Thread, StackTraceElement[]> stackTraces = new TreeMap<Thread, StackTraceElement[]>(
                new Comparator<Thread>() {
                    @Override
                    public int compare(Thread lhs, Thread rhs) {
                        if (lhs == rhs) {
                            return 0;
                        }
                        if (lhs == mainThread) {
                            return 1;
                        }
                        if (rhs == mainThread) {
                            return -1;
                        }
                        return rhs.getName().compareTo(lhs.getName());
                    }
                });
        for (Map.Entry<Thread, StackTraceElement[]> entry : Thread.getAllStackTraces().entrySet()) {
            if (entry.getKey() == mainThread
                    || (entry.getKey().getName().startsWith(prefix) && (logThreadsWithoutStackTrace || entry.getValue().length > 0))) {
                stackTraces.put(entry.getKey(), entry.getValue());
            }
        }
        $.ThrowableThread tst = null;

        for (Map.Entry<Thread, StackTraceElement[]> entry : stackTraces.entrySet()) {
            tst = new $(entry.getKey().getName(), entry.getValue()).new ThrowableThread(tst);
        }

        return new AnrError(tst, stackTraces);
    }

    static AnrError NewMainOnly() {
        final Thread mainThread = Looper.getMainLooper().getThread();
        final StackTraceElement[] mainStackTrace = mainThread.getStackTrace();
        final HashMap<Thread, StackTraceElement[]> stackTraces = new HashMap<Thread, StackTraceElement[]>(1);
        stackTraces.put(mainThread, mainStackTrace);
        return new AnrError(new $(mainThread.getName(), mainStackTrace).new ThrowableThread(null), stackTraces);
    }

}
