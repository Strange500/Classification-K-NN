package fr.univlille.s3.S302.model;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;

import java.io.*;
import java.util.List;

public class DataLoader {

    /**
     * Charge un fichier CSV et le transforme en liste d'objets FormatDonneeBrut
     * 
     * @param fileName le nom du fichier à charger
     * @return la liste d'objets FormatDonneeBrut
     * @throws FileNotFoundException si le fichier n'existe pas
     */
    public static List<FormatDonneeBrut> charger(String fileName) throws FileNotFoundException {
        System.out.println("Chargement du fichier " + fileName);
        InputStream input = DataLoader.class.getResourceAsStream(fileName);
        if (input == null) {
            if (new File(fileName).exists()) {
                input = new FileInputStream(fileName);
            } else {
                throw new FileNotFoundException("Fichier non trouvé");
            }
        }
        return csvToList(input);

    }

    /**
     * Transforme un fichier CSV en liste d'objets FormatDonneeBrut
     * 
     * @param input le fichier CSV à transformer
     * @return la liste d'objets FormatDonneeBrut
     * @throws FileNotFoundException si le fichier n'existe pas
     */
    private static List<FormatDonneeBrut> csvToList(InputStream input) throws FileNotFoundException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {

            CsvToBean<FormatDonneeBrut> csvToBean = new CsvToBeanBuilder<FormatDonneeBrut>(reader).withSeparator(',')
                    .withType(FormatDonneeBrut.class).build();

            List<FormatDonneeBrut> records = csvToBean.parse();
            return records;
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new FileNotFoundException("Fichier non trouvé");
    }

    /**
     * Crée un objet Iris à partir d'un objet FormatDonneeBrut
     * 
     * @param f l'objet FormatDonneeBrut
     * @return l'objet Iris
     */
    public static Iris createObject(FormatDonneeBrut f) {
        return new Iris(f.sepalLength, f.sepalWidth, f.petalLength, f.petalWidth, f.species);
    }
}
