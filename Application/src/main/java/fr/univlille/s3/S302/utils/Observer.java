package fr.univlille.s3.S302.utils;

public abstract class Observer {

    protected abstract void update(Observable ob);

    protected abstract void update(Observable ob, Object elt);
}
