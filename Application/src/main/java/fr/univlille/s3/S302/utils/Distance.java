package fr.univlille.s3.S302.utils;

import fr.univlille.s3.S302.model.Data;

public interface Distance {
    /**
     * @param a une donnée
     * @param b un second donnée
     * @return la distance euclidienne entre les deux données
     */
    double distance(Data a, Data b);
}
