/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package emmanuelapp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 *
 * @author Conecta-J
 */
public class Model {

    String dirXML = "";
    File directorio;
    File[] ficheros;
    private String nombFileSalida = null;
    private String type;
    private String dirSalida = null;

    public String getPathDir() {
        return dirXML;
    }

    public void setPathDir(String pathDir) {
        this.dirXML = pathDir;
    }

    public File getDirectorio() {
        return directorio;
    }

    public void setDirectorio(File directorio) {
        this.directorio = directorio;
    }

    public String preProceso() {
        StringBuilder stringBuilder = new StringBuilder();
        directorio = new File(dirXML);
        if (directorio.isDirectory()) {
            System.out.println("Buscando en Directorio: " + directorio.getName());
            stringBuilder.append("Buscando en Directorio: " + directorio.getName() + "\n");
            FilterXml filtro = new FilterXml("xml");
            ficheros = directorio.listFiles(filtro);
            System.out.println("Se procesará un total de: " + ficheros.length);
            stringBuilder.append("Se procesará un total de: " + ficheros.length);
        }

        return stringBuilder.toString();
    }

    public StringBuilder procesar() {
        StringBuilder sb = new StringBuilder();
        System.out.println("Running");
        System.out.println("Leyendo Archivo XML");

        SAXBuilder builder = new SAXBuilder();

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //File fFactura = ficheros[0];
        List<Comprobante> listComprobantes = new ArrayList<Comprobante>();

        for (File fact : ficheros) {
            System.out.println("Archivo encontrado: " + fact.getName());
            System.out.println("Construyendo XML");

            try {
                /**
                 * Construyendo
                 */
                Document doc = (Document) builder.build(fact);
                System.out.println("XML construido. " + doc);
                Comprobante comprobante = new Comprobante();
                /**
                 * Raiz
                 */
                System.out.println("Extrayedo Raiz");
                Element raiz = doc.getRootElement();
                /**
                 * Attributes
                 */
                System.out.println("Extrayedo atributos");
                List<Attribute> atributos = raiz.getAttributes();
                for (Attribute a : atributos) {
                    System.out.println(a.getName() + ": " + a.getValue());
                    if (a.getName().equalsIgnoreCase("fecha")) {
                        String sFecha = a.getValue();
                        sFecha = sFecha.replaceAll("T", " ");
                        System.out.println(sFecha);
                        try {
                            Date dFecha = df.parse(sFecha);
                            System.out.println("Date: " + dFecha);
                            comprobante.setFecha(dFecha);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    } else if (a.getName().equalsIgnoreCase("folio")) {
                        comprobante.setFolio(a.getValue());
                    } else if (a.getName().equalsIgnoreCase("tipoDeComprobante")) {
                        comprobante.setTipoDeComprobante(a.getValue());
                    } else if (a.getName().equalsIgnoreCase("subTotal")) {
                        comprobante.setSubTotal(BigDecimal.valueOf(Double.parseDouble(a.getValue())));
                    } else if (a.getName().equalsIgnoreCase("total")) {
                        System.out.println("setting total = " + BigDecimal.valueOf(Double.parseDouble(a.getValue())));
                        comprobante.setTotal(BigDecimal.valueOf(Double.parseDouble(a.getValue())));
                    } else if (a.getName().equalsIgnoreCase("LugarExpedicion")) {
                        comprobante.setLugarExpedicion(a.getValue());
                    } else if (a.getName().equalsIgnoreCase("tipoDeComprobante")) {
                        comprobante.setLugarExpedicion(a.getValue());
                    }
                }
                /**
                 * Children
                 */
                List<Element> elementos = raiz.getChildren();
                System.out.println("Extrayedo hijos");
                for (Element e : elementos) {
                    System.out.println("elemento: " + e.getName());
                    if (e.getName().equalsIgnoreCase("Impuestos")) {
                        String s_totalImpuestosTrasladados = e.getAttribute("totalImpuestosTrasladados").getValue();
                        BigDecimal bd_totalImpuestosTrasladados = BigDecimal.valueOf(Double.parseDouble(s_totalImpuestosTrasladados));
                        comprobante.setTotalImpuestosTrasladados(bd_totalImpuestosTrasladados);
                    }
                }

                System.out.println("Mostrando elemento");
                System.out.println("Comprobante creado/agregado: ");
                listComprobantes.add(comprobante);


            } catch (JDOMException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        Workbook workbook = null;
        workbook = new HSSFWorkbook();

        Sheet sheet = workbook.createSheet();

        try {
            Row row = sheet.createRow(0);

            Cell cell = row.createCell(0);
            cell.setCellValue("Fecha");
            Cell cell_1 = row.createCell(1);
            cell_1.setCellValue("Concepto");
            Cell cell_2 = row.createCell(2);
            cell_2.setCellValue("RFC");
            Cell cell_3 = row.createCell(3);
            cell_3.setCellValue("Factura");
            Cell cell_4 = row.createCell(4);
            cell_4.setCellValue("Importe");
            Cell cell_5 = row.createCell(5);
            cell_5.setCellValue("Iva");
            Cell cell_6 = row.createCell(5);
            cell_6.setCellValue("Total");

            int filaSig = 1;
            for (Comprobante c : listComprobantes) {
                Row rowNext = sheet.createRow(filaSig);
                
                Cell celdaFecha = rowNext.createCell(0);
                celdaFecha.setCellValue(df.format(c.getFecha()));
                
                Cell celdaLugarExpedicion = rowNext.createCell(1);
                celdaLugarExpedicion.setCellValue(c.getLugarExpedicion());
                
                Cell celdaTipoDeComprobante = rowNext.createCell(2);
                celdaTipoDeComprobante.setCellValue(c.getTipoDeComprobante());
                
                Cell celdaSubTotal = rowNext.createCell(3);
                celdaSubTotal.setCellValue(c.getSubTotal().doubleValue());
                
                Cell celdaTotal = rowNext.createCell(4);
                celdaTotal.setCellValue(c.getTotal().doubleValue());
                
                Cell celdaTotalImpuestosTrasladados = rowNext.createCell(5);
                celdaTotalImpuestosTrasladados.setCellValue(c.getTotalImpuestosTrasladados().doubleValue());

                filaSig++;
            }

            FileOutputStream fos = new FileOutputStream(dirSalida + "\\" + nombFileSalida + "." + type);
            workbook.write(fos);
            fos.close();
            System.out.println("Archivo Listo en: " + dirSalida + "\\" + nombFileSalida + "." + type);
            sb.append("Procesados ")
                    .append( listComprobantes.size() )
                    .append(" archivos \n");
            sb.append("Archivo Listo en: ")
                    .append(dirSalida)
                    .append("\\")
                    .append(nombFileSalida)
                    .append(".")
                    .append(type);
            
            dirSalida = null;
            nombFileSalida = null;
            
            return sb;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return sb.append(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            return sb.append(e.getMessage());
        }
//        File fFactura = new File( path );

    }

    void setNameArchivo(String text) {
        this.nombFileSalida = text;
    }

    public String getNombFileSalida() {
        return nombFileSalida;
    }

    public String getDirSalida() {
        return dirSalida;
    }

    void setPathOut(String pathPub) {
        this.dirSalida = pathPub;
    }

    void setType(String type) {
        this.type = type;
    }
}
