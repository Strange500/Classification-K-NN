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
            throw new FileNotFoundException("Fichier non trouvé");
        }
        InputStream input = DataLoader.class.getResourceAsStream(fileName);
        Class<? extends Data> clazz = null;
        if (input == null) {
            if (new File(fileName).exists()) {
                input = new FileInputStream(fileName);
                clazz = getClassFromHeader(new FileReader(fileName));
            } else {
                throw new FileNotFoundException("Fichier non trouvé");
            }
        } else{
            clazz = getClassFromHeader(new InputStreamReader(DataLoader.class.getResourceAsStream(fileName)));
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
            List<? extends Data> records = csvToBean.parse();
            System.out.println(records);
            return records;
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new FileNotFoundException("Fichier non trouvé");
    }

    private static Class<? extends  Data> getClassFromHeader(Reader fileReader) {
        preLoadClasses();
        try (BufferedReader reader = new BufferedReader(fileReader)) {
            String header = reader.readLine();

            return headerToClassMap.getOrDefault(header, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }



    public static void registerHeader(Class<? extends Data> clazz, String header) {
        headerToClassMap.put(header, clazz);
    }

    private static void preLoadClasses() {
        try {
            Set<Class<? extends Data>> allClasses = new Reflections("fr.univlille.s3.S302.model").getSubTypesOf(Data.class);
            for (Class<? extends Data> clazz : allClasses) {
                Class.forName(clazz.getName());
            }
        } catch (ClassNotFoundException e) {
            System.err.println("Impossible de charger les classes");
        }
    }

}
