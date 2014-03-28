/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package emmanuelapp;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author Conecta-J
 */
public class ControlEmmanuel implements ActionListener{
    
    Frame frame;
    private Model model;
    String pathPub = "";

    public ControlEmmanuel(Frame f) {
        this.frame = f;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println("e.getActionCommand(): " + e.getActionCommand());
        if(e.getActionCommand().equals(Frame.BUSCAR)){
            frame.getjTextArea_resumen().setText("");
            System.out.println("buscando...");
            JFileChooser jfc = new JFileChooser();
            jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            jfc.setDialogTitle("Buscando el archivo ARA");
            int result = jfc.showOpenDialog(frame);
            //
            if( result == JFileChooser.APPROVE_OPTION){
                String pathPub = jfc.getSelectedFile().getPath();
                frame.getjTextField_dir().setText(pathPub);
                model.setPathDir(pathPub);
                frame.getjTextArea_resumen().setText( model.preProceso() );
            }
        }
        if(e.getActionCommand().equals(Frame.SALIDA)){
           
            JFileChooser jfc = new JFileChooser();
            jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            jfc.setDialogTitle("Buscando el archivo ARA");
            int result = jfc.showOpenDialog(frame);
            //
            if( result == JFileChooser.APPROVE_OPTION){
                pathPub = jfc.getSelectedFile().getPath();
//                frame.getjTextField_nomExcel().setText(pathPub);
                frame.getjTextArea_resumen().append( "\n" );
                frame.getjTextArea_resumen().append( "Directorio de salida: " );
                frame.getjTextArea_resumen().append( pathPub );
            }
            
        }
        if(e.getActionCommand().equals(Frame.GENERAR)){
            String type = "xls";
            if( frame.getjRadioButton_97().isSelected() ){
                type = "xls";
            }else if( frame.getjRadioButton_2010().isSelected() ){
                type = "xlsx";
            }
            
            model.setPathOut( pathPub );
            model.setType( type );
            
            if ( frame.getjTextField_nomExcel().getText().equals("Nombre del excel ...")
                    || frame.getjTextField_nomExcel().getText().equals("") 
                    || model.getDirSalida() == null) {
                
                JOptionPane.showMessageDialog(frame, "Por favor colocar un nombre para archivo de Excel");
                
            }else{
                
                if( frame.getjTextField_nomExcel().getText().equals("") ){
                    String nArchivo = JOptionPane.showInputDialog(frame, "Nombre del archivo Excel");
                    model.setNameArchivo( nArchivo );
                }else{
                    model.setNameArchivo(frame.getjTextField_nomExcel().getText());
                    frame.getjTextArea_resumen().append( model.procesar().toString() );
                    JOptionPane.showMessageDialog(frame, "El archivo se generó en: " + model.getDirSalida());
                    
                }
                
                
            }
        }
    }

    void init() {
        frame.pack();
        frame.setTitle("Transformando a Excel");
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setResizable(false);
    }

    void setModel(Model m) {
        this.model = m;
    }
    
}
