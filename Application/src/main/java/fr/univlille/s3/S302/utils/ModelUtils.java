package fr.univlille.s3.S302.utils;

import fr.univlille.s3.S302.model.Data;
import fr.univlille.s3.S302.model.DataManager;
import javafx.util.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModelUtils {
    /**
     * Renvoie le pourcentage le plus haut  parmis les plus proches voisins et choisissant le nombre de voisin le plus aproprier
     * @param dm DataManager
     * @param listeData la liste de données
     * @param distance la distance
     * @return Le pourcentage Maximal de robustesse
     */
    public static Pair<Integer,Double> Robustesse(DataManager<Data> dm, List<Data> listeData, Distance distance ){
        int bestNBVoisin=0;
        double pourcentageMax=0.0;
        int cpt=0;
        int size= listeData.size();
        for (int i=1;i<(dm.getDataList().size())/2;i++){
            for (Data d : listeData){
                String cate=d.getCategory();

                String catDetermine=getMostRepresentedCategorie(dm.getNearestDatas(d,distance,i));
                if (cate.equals(catDetermine)){
                    cpt++;
                }
            }
           if((cpt /(double) size)>pourcentageMax){
               pourcentageMax = cpt /(double) size;
               bestNBVoisin=i;
           }
           cpt=0;
        }
        return new Pair<>(bestNBVoisin,pourcentageMax);
    }

    /**
     * Renvoie la catégorie la plus prés,ete dans la liste de données en parametre et si il y a une égalité, la première rencontrée
     * @param listeData la liste de données
     * @return La catégorie la plus présente
     */
    public static String getMostRepresentedCategorie(List<Data> listeData){
        Map<String,Integer> mapCategorie=new HashMap<String,Integer>();
        for(Data d:listeData){
           mapCategorie.putIfAbsent(d.getCategory(), mapCategorie.getOrDefault(d.getCategory(), 0)+1);
        }
        int intMax=0;
        String keyMax="";
        for (Map.Entry<String, Integer> m : mapCategorie.entrySet()){
            if(m.getValue()>intMax){
                intMax=m.getValue();
                keyMax=m.getKey();
            }
        }
        return keyMax;
    }
}
