/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Editor;

import Console.ConsoleFrame;
import Constants.PathConstants;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Dragos-Alexandru
 */
public final class SourceManager {
    
    private static final String SOURCE_FOLDER_PATH = PathConstants.USER_SOURCES_FOLDER_PATH;
    private static List<Source> sources = new ArrayList<>();
    private static SourceManager instance;
    
    private static String AI_TEMPLATE_CONTENT = "";
    
    private SourceManager() throws IOException{
        
        File sourceFolder = new File(SOURCE_FOLDER_PATH);
        ConsoleFrame.sendMessage(this.getClass().getSimpleName(), "Searching for source folder at "+sourceFolder.getAbsolutePath());
        
        if(!sourceFolder.exists()){
            ConsoleFrame.sendMessage(this.getClass().getSimpleName(), "Creating source folder");
            sourceFolder.mkdir();
            checkReadWrite(sourceFolder);
            File sourceIndex = new File(PathConstants.USER_SOURCES_INDEX_PATH);
            writeSourceFileIndex(sourceIndex);
        }else{
            ConsoleFrame.sendMessage(this.getClass().getSimpleName(), "Source folder exists");
            ConsoleFrame.sendMessage(this.getClass().getSimpleName(), "Searching for source index file");
            File sourceIndex = new File(PathConstants.USER_SOURCES_INDEX_PATH);
            if(!sourceIndex.exists()){
                writeSourceFileIndex(sourceIndex);
            }else{
                ConsoleFrame.sendMessage(this.getClass().getSimpleName(), "Source index file exists");
                sources = readSourceFileIndex(sourceIndex);
            }
        }
        if(!sources.isEmpty()){
            ConsoleFrame.sendMessage(this.getClass().getSimpleName(), "Found this sources: ");
            for(Source sourceAux:sources){
                ConsoleFrame.sendMessage(this.getClass().getSimpleName(), sourceAux.toString());
            }
        }
        
        ConsoleFrame.sendMessage(this.getClass().getSimpleName(), "SourceManager is set to go");
    }
    
    public static SourceManager getInstance(){
        if(instance == null){
            try {
                ConsoleFrame.sendMessage(SourceManager.class.getSimpleName(), "Started SourceManager");
                instance = new SourceManager();
            } catch (IOException ex) {
                ConsoleFrame.sendMessage(SourceManager.class.getSimpleName(), ex.getMessage());
                System.exit(-1);
            }
        }
        return instance;
    }
    
    private void checkReadWrite(File sourceFolder) throws IOException{
        ConsoleFrame.sendMessage(this.getClass().getSimpleName(), "Testing read/write permissions source folder");
        if(!sourceFolder.canRead()){
            throw new IOException("Can't read from designated folder!");
        }
        if(!sourceFolder.canWrite()){
            throw new IOException("Can't write to designated folder!");
        }
    }
    
    private List<Source> readSourceFileIndex(File sourceIndex) throws IOException{
        List<Source> auxSource = null;
        try (FileInputStream fInput = new FileInputStream(sourceIndex);
                ObjectInputStream oInput = new ObjectInputStream(fInput)){
            ConsoleFrame.sendMessage(this.getClass().getSimpleName(), "Reading from source index file");
            auxSource = (ArrayList<Source>)oInput.readObject();

        } catch (ClassNotFoundException ex) {
            ConsoleFrame.sendMessage(this.getClass().getSimpleName(), "Couldn't read from source index");
        }
        return auxSource;
    }
    
    private void writeSourceFileIndex(File sourceIndex) throws IOException{
        ConsoleFrame.sendMessage(this.getClass().getSimpleName(), "Creating source index file");
        if(sourceIndex.createNewFile()){
            try (FileOutputStream fOutput = new FileOutputStream(sourceIndex); 
                    ObjectOutputStream oOutput = new ObjectOutputStream(fOutput)) {
                ConsoleFrame.sendMessage(this.getClass().getSimpleName(), 
                        "Writing empty source list to source index file");
                sources.add(new Source("Test test test", "Test1", "Dragos"));
                sources.add(new Source("Test test test", "Test2", "Tanase"));
                sources.add(new Source("Test test test", "Test3", "Alex"));
                sources.add(new Source("Test test test", "Test4", "Liviu"));
                sources.add(new Source("Test test test", "Test5", "Alexandra"));
                oOutput.writeObject(sources);
            }
        }
    }
    
    public List<Source> getSourceList(){
        File sourceIndex = new File(PathConstants.USER_SOURCES_INDEX_PATH);
        try {
            return readSourceFileIndex(sourceIndex);
        } catch (IOException ex) {
            ConsoleFrame.sendMessage(this.getClass().getSimpleName(), "Failed to read source list "+ex.getMessage());
            ConsoleFrame.showError("Failed to read source list");
            return (new ArrayList<>());
        }
    }
    
    /**
     * Moves files to the source folder
     * @param file 
     */
    public void moveFileToSourceFolder(File file){
        File newSource = new File(SOURCE_FOLDER_PATH+file.getName());
        try {
            newSource.createNewFile();
            try (FileReader fileReader = new FileReader(file); 
                    BufferedReader reader = new BufferedReader(fileReader); 
                    FileWriter fileWriter = new FileWriter(newSource); 
                    BufferedWriter writer = new BufferedWriter(fileWriter)) {
                
                String stringAux;
                stringAux = reader.readLine();
                while(stringAux != null){
                    writer.write(stringAux);
                }
            }
        } catch (IOException ex) {
            ConsoleFrame.sendMessage(this.getClass().getSimpleName(), "Failed to create file "+file.getName());
            ConsoleFrame.showError("Failed to create file "+file.getName());
        }
    }
    
    /**
     * This method returns the content of the predefined AI template
     * @return inteligenceTemplate
     */
    public String getInteligenceTemplate(){
        File template = new File(PathConstants.AI_TEMPLATE);
        if(template.exists()){
            try (FileReader fileReader = new FileReader(template);
                    BufferedReader bufferedReader = new BufferedReader(fileReader)){
                while(bufferedReader.ready()){
                    AI_TEMPLATE_CONTENT += bufferedReader.readLine()+"\n";
                }
            }catch (IOException ex){
                    ConsoleFrame.sendMessage(this.getClass().getSimpleName(), "Failed to read template");
                    ConsoleFrame.showError("Failed to read AI template");
            }
        }else{
            ConsoleFrame.sendMessage(this.getClass().getSimpleName(), "Could not find AI template at " + template.getAbsolutePath());
            ConsoleFrame.showError("Failed to find AI template");
        }
        return AI_TEMPLATE_CONTENT;
    }
    
    
}
