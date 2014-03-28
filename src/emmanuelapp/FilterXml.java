/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package emmanuelapp;

import java.io.File;
import java.io.FilenameFilter;

/**
 *
 * @author Conecta-J
 */
public class FilterXml implements FilenameFilter {
    private String ext = "xml";

    public FilterXml(String e) {
        ext = e;
    }

    @Override
    public boolean accept(File d, String n){
        return n.endsWith(ext);
    }
}
