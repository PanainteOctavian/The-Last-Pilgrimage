package Tiles;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

public class XmlToMatrix {
    public static int[][] GetIdMatrix(String xmlFilePath) {
        try {
            File xmlFile = new File(xmlFilePath);
            if (!xmlFile.exists()) {
                System.err.println("Fișierul XML nu a fost găsit!");
                return null;
            }

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            Node layerNode = doc.getElementsByTagName("layer").item(0);
            if (layerNode.getNodeType() == Node.ELEMENT_NODE) {
                Element layerElement = (Element) layerNode;
                String csvData = layerElement.getElementsByTagName("data").item(0).getTextContent().trim();
                String[] rows = csvData.split("\n");
                int rowCount = rows.length;
                int colCount = rows[0].split(",").length;

                int[][] matrix = new int[rowCount][colCount];
                for (int i = 0; i < rowCount; i++) {
                    String[] elements = rows[i].split(",");
                    for (int j = 0; j < colCount; j++) {
                        try {
                            matrix[i][j] = Integer.parseInt(elements[j].trim());
                        } catch (NumberFormatException e) {
                            System.err.println("Eroare la conversia elementului: " + elements[j]);
                            matrix[i][j] = 0;
                        }
                    }
                }
                return matrix;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}