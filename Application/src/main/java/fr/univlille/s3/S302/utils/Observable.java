package fr.univlille.s3.S302.utils;

public interface Observable<E> {

    void attach(Observer<E> ob);

    void notifyAllObservers(E elt);

    void notifyAllObservers();

}
