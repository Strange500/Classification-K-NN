package fr.univlille.s3.S302.model;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.reflections.Reflections;

import java.io.*;
import java.util.*;

/**
 * Classe permettant de charger un fichier CSV et de le transformer en liste d'objets
 */
public class DataLoader {

    private static final Map<String, Class<? extends Data>> headerToClassMap = new HashMap<>();
    private static final char SEPARATOR = ',';

    /**
     * Charge un fichier CSV et le transforme en liste d'objets FormatDonneeBrut
     * 
     * @param fileName le nom du fichier à charger
     * @return la liste d'objets FormatDonneeBrut
     * @throws FileNotFoundException si le fichier n'existe pas
     */
    public static List<? extends Data> charger(String fileName) throws FileNotFoundException {
        if (fileName == null) {
            throw new FileNotFoundException("check is null : Fichier non trouvé");
        }
        InputStream input = DataLoader.class.getResourceAsStream(fileName);
        Class<? extends Data> clazz;
        if (input == null) {
            if (new File(fileName).exists()) {
                input = new FileInputStream(fileName);
                clazz = getClassFromHeader(new FileReader(fileName));
            } else {
                throw new FileNotFoundException("check filename exist : Fichier non trouvé: " + fileName);
            }
        } else{
            clazz = getClassFromHeader(new InputStreamReader(Objects.requireNonNull(DataLoader.class.getResourceAsStream(fileName))));
        }
        return csvToList(input, clazz);
    }

    /**
     * Transforme un fichier CSV en liste d'objets FormatDonneeBrut
     * 
     * @param input le fichier CSV à transformer
     * @return la liste d'objets FormatDonneeBrut
     * @throws FileNotFoundException si le fichier n'existe pas
     */
    private static List<? extends Data> csvToList(InputStream input, Class<? extends Data> clazz) throws FileNotFoundException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
            if (clazz == null) {
                throw new IllegalStateException("Entête non reconnue");
            }
            CsvToBean<Data> csvToBean = new CsvToBeanBuilder<Data>(reader).withSeparator(SEPARATOR)
                    .withType(clazz).build();
            return csvToBean.parse();
        } catch (IOException e) {
            System.err.println("Erreur lors de la lecture du fichier:" + e.getMessage());
        }
        throw new FileNotFoundException("Fichier non trouvé" );
    }

    /**
     * Récupère la classe correspondant à l'entête du fichier CSV
     *
     * @param fileReader le fichier CSV
     * @return la classe correspondant à l'entête
     */
    private static Class<? extends  Data> getClassFromHeader(Reader fileReader) {
        preLoadClasses();
        try (BufferedReader reader = new BufferedReader(fileReader)) {
            String header = reader.readLine();

            return headerToClassMap.getOrDefault(header, null);
        } catch (IOException e) {
            System.err.println("Erreur lors de la lecture du fichier:" + e.getMessage());
        }
        return null;
    }

    /**
     * Enregistre une correspondance entre un entête et une classe
     *
     * @param clazz la classe
     * @param header l'entête
     */
    public static void registerHeader(Class<? extends Data> clazz, String header) {
        headerToClassMap.put(header, clazz);
    }

    /**
     * Charge toutes les classes héritant de Data
     */
    static void preLoadClasses() {
        try {
            Set<Class<? extends Data>> allClasses = new Reflections("fr.univlille.s3.S302.model").getSubTypesOf(Data.class);
            System.out.println("Liste des classe a charger : " + allClasses);
            for (Class<? extends Data> clazz : allClasses) {
                Class.forName(clazz.getName());
                System.out.println("Chargement de la classe " + clazz.getName());
            }
        } catch (ClassNotFoundException e) {
            System.err.println("Impossible de charger les classes");
        }
    }

}
