package fr.univlille.s3.S302.model;

import fr.univlille.s3.S302.utils.Observer;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe permettant de gérer les observateurs d'une donnée.
 */
public class DataObserverManager<E extends Data> {

    private final List<Observer<E>> observers;

    /**
     * Constructeur de la classe DataObserverManager.
     */
    public DataObserverManager() {
        this.observers = new ArrayList<>();
    }

    /**
     * Permet d'ajouter un observateur à la liste des observateurs.
     * @param ob l'observateur à ajouter.
     */
    public void attach(Observer<E> ob) {
        this.observers.add(ob);
    }

    /**
     * Permet de supprimer un observateur de la liste des observateurs.
     * @param ob l'observateur à supprimer.
     */
    public void detach(Observer<E> ob) {
        this.observers.remove(ob);
    }

    /**
     * Permet de notifier tous les observateurs.
     * @param elt l'élément à notifier.
     */
    public void notifyAllObservers(E elt) {
        ArrayList<Observer<E>> tmp = new ArrayList<>(this.observers);
        for (Observer<E> ob : tmp) {
            ob.update(null, elt);
        }
    }

    /**
     * Permet de notifier tous les observateurs.
     */
    public void notifyAllObservers() {
        ArrayList<Observer<E>> tmp = new ArrayList<>(this.observers);
        for (Observer<E> ob : tmp) {
            ob.update(null);
        }
    }
}