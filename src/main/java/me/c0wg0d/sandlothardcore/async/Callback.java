package me.c0wg0d.sandlothardcore.async;

/**
 * Runnable with state.
 */
public abstract class Callback<T> implements Runnable {
    private volatile T state;
    public void setState(T state) {
        this.state = state;
    }
    public T getState() {
        return state;
    }
}