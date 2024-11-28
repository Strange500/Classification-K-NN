package fr.univlille.s3.S302.utils;

import java.util.ArrayList;
import java.util.List;

public abstract class Observable {

    private final List<Observer> observers;

    public Observable() {
        this.observers = new ArrayList<>();
    }

    public List<Observer> getObservers() {
        return new ArrayList<>(this.observers);
    }

    public void attach(Observer ob) {
        this.observers.add(ob);
    }

    public void detach(Observer ob) {
        this.observers.remove(ob);
    }

    public void notifyAllObservers(Object elt) {
        ArrayList<Observer> tmp = new ArrayList<>(this.observers);
        for (Observer ob : tmp) {
            ob.update(this, elt);
        }
    }

    public void notifyAllObservers() {
        ArrayList<Observer> tmp = new ArrayList<>(this.observers);
        for (Observer ob : tmp) {
            ob.update(this);
        }
    }

}
