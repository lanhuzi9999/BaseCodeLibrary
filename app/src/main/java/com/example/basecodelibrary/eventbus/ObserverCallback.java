package com.example.basecodelibrary.eventbus;

public interface ObserverCallback {
    public void handleBusEvent(Object observer, Object event);
}
