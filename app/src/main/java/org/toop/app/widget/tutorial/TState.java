package org.toop.app.widget.tutorial;

public class TState {

    private int current;
    private int total;

    public TState(int total) {
        this.total = total;
        this.current = 0;
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public void next() {
        current = current + 1;
    }

    public void previous() {
        current = current - 1;
    }

    public boolean hasNext() {
        return current < total - 1;
    }

    public boolean hasPrevious() {
        return current > 0;
    }
}
