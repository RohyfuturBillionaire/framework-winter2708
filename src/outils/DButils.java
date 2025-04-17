package outils;

import java.io.FileWriter;
import java.io.IOException;

import DAO.DB;

public class DButils {
    public String makeEntete(String separateur,String[]entete){
        StringBuilder entetes= new StringBuilder();
        for (String string : entete) {
            entetes.append(string).append(separateur);
        }
        System.out.println(entetes.substring(0,entetes.length()-1));
        return entetes.substring(0,entetes.length()-1);
    }
    public void ToCSV(DB[] obj,String filename,String separateur,String[]entete) throws Exception{
        try (FileWriter writer = new FileWriter(filename, true)) { 
          	writer.write(makeEntete(separateur, entete));
            writer.write("\n");
            for (DB db : obj) {
                writer.write(db.toString(separateur));
            }
		} 
      	catch (IOException e) {
    		System.out.println("An error occurred while appending " + " to the file: " + e.getMessage());
		}

    }
}
