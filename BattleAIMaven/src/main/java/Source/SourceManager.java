package Source;

import Console.ConsoleFrame;
import Constants.PathConstants;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilePermission;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.AccessController;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

/**
 * This class is a singleton and manages all the sources on the local machine
 *
 * @author Dragos-Alexandru
 */
public final class SourceManager {

    private static final String SOURCE_FOLDER_PATH = PathConstants.USER_SOURCES_FOLDER_PATH;
    private static List<Source> sources = new ArrayList<>();
    private static SourceManager instance;

    private static String AI_TEMPLATE_CONTENT = "";

    private SourceManager() throws IOException {

        File sourceFolder = new File(SOURCE_FOLDER_PATH);
        ConsoleFrame.sendMessage(this.getClass().getSimpleName(), "Searching for source folder at " + sourceFolder.getAbsolutePath());

        if (!sourceFolder.exists()) {
            ConsoleFrame.sendMessage(this.getClass().getSimpleName(), "Creating source folder");
            if(sourceFolder.mkdir()){
                ConsoleFrame.sendMessage(this.getClass().getSimpleName(), "Succesfully created source folder");
            }else{
                ConsoleFrame.sendMessage(this.getClass().getSimpleName(), "Failed to create source folder");
            }
            checkReadWrite(sourceFolder);
            File sourceIndex = new File(PathConstants.USER_SOURCES_INDEX_PATH);
            writeSourceFileIndex(sourceIndex);
        } else {
            ConsoleFrame.sendMessage(this.getClass().getSimpleName(), "Source folder exists");
            ConsoleFrame.sendMessage(this.getClass().getSimpleName(), "Searching for source index file");
            File sourceIndex = new File(PathConstants.USER_SOURCES_INDEX_PATH);
            if (!sourceIndex.exists()) {
                writeSourceFileIndex(sourceIndex);
            } else {
                ConsoleFrame.sendMessage(this.getClass().getSimpleName(), "Source index file exists");
                sources = readSourceFileIndex(sourceIndex);
            }
        }
        if (!sources.isEmpty()) {
            ConsoleFrame.sendMessage(this.getClass().getSimpleName(), "Found this sources: ");
            for (Source sourceAux : sources) {
                ConsoleFrame.sendMessage(this.getClass().getSimpleName(), sourceAux.toString());
            }
        }
        readIntelligenceTemplate();
        ConsoleFrame.sendMessage(this.getClass().getSimpleName(), "SourceManager is set to go");
    }

    /**
     * Gets instance of the singleton class, if is null then it instantiates it
     *
     * @return The static instance of the SourceManager
     */
    public static SourceManager getInstance() {
        if (instance == null) {
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

    /**
     * Checks if the application has read/write permissions on the local machine
     *
     * @param sourceFolder
     * @throws IOException
     */
    private void checkReadWrite(File sourceFolder) throws IOException {
        ConsoleFrame.sendMessage(this.getClass().getSimpleName(), "Testing read/write permissions source folder at "+sourceFolder.getAbsolutePath());
        if (!sourceFolder.canRead()) {
            sourceFolder.setReadable(true);
            if(!sourceFolder.canRead()){
                throw new IOException("Can't read from designated folder "+sourceFolder.getCanonicalPath());
            }
        }
        if (!sourceFolder.canWrite()) {
            sourceFolder.setWritable(true);
            if(!sourceFolder.canWrite()){
                throw new IOException("Can't write to designated folder "+sourceFolder.getCanonicalPath());
            }
        }
    }

    /**
     * Reads the list from the sourceIndex file
     *
     * @param sourceIndex
     * @return A list of sources in the sourceIndex
     * @throws IOException
     */
    private List<Source> readSourceFileIndex(File sourceIndex) throws IOException {
        List<Source> auxSource = null;
        try (FileInputStream fInput = new FileInputStream(sourceIndex);
                ObjectInputStream oInput = new ObjectInputStream(fInput)) {
            ConsoleFrame.sendMessage(this.getClass().getSimpleName(), "Reading from source index file");
            auxSource = (ArrayList<Source>) oInput.readObject();
        } catch (ClassNotFoundException ex) {
            ConsoleFrame.sendMessage(this.getClass().getSimpleName(), "Couldn't read from source index");
        }
        return auxSource;
    }

    /**
     * Creates the sourceIndex file and populates it with dummy sources
     *
     * @param sourceIndex
     * @throws IOException
     */
    private void writeSourceFileIndex(File sourceIndex) throws IOException {
        ConsoleFrame.sendMessage(this.getClass().getSimpleName(), "Creating source index file");
        if (sourceIndex.createNewFile()) {
            try (FileOutputStream fOutput = new FileOutputStream(sourceIndex);
                    ObjectOutputStream oOutput = new ObjectOutputStream(fOutput)) {
                ConsoleFrame.sendMessage(this.getClass().getSimpleName(),
                        "Writing source list to source index file");
                oOutput.writeObject(sources);
            }
        } else {
            try (FileOutputStream fOutput = new FileOutputStream(sourceIndex);
                    ObjectOutputStream oOutput = new ObjectOutputStream(fOutput)) {
                ConsoleFrame.sendMessage(this.getClass().getSimpleName(),
                        "Writing source list to source index file");
                oOutput.writeObject(sources);
            }
        }
    }

    /**
     * Gets the source list from the sourceIndex file
     *
     * @return A list of sources on the local machine
     */
    public List<Source> getSourceList() {
        File sourceIndex = new File(PathConstants.USER_SOURCES_INDEX_PATH);
        try {
            return readSourceFileIndex(sourceIndex);
        } catch (IOException ex) {
            ConsoleFrame.sendMessage(this.getClass().getSimpleName(), "Failed to read source list " + ex.getMessage());
            ConsoleFrame.showError("Failed to read source list");
            return (new ArrayList<>());
        }
    }

    /**
     * Saves file to the source folder
     *
     * @param source
     * @return true if save was successful and false otherwise
     */
    public boolean saveFileToSourceFolder(Source source) {
        File newSource = new File(SOURCE_FOLDER_PATH + source.getName());
        try {
            if (newSource.exists()) {
                int replace = JOptionPane.showConfirmDialog(null, "Replace " + source.getName() + " ?", "Replace source", JOptionPane.YES_NO_OPTION);
                if (replace == JOptionPane.NO_OPTION) {
                    return false;
                }
            } else {
                newSource.createNewFile();
            }
            try (FileWriter fileWriter = new FileWriter(newSource);
                    BufferedWriter writer = new BufferedWriter(fileWriter)) {
                writer.write(source.getContent());
                if (sources.contains(source)) {
                    sources.remove(source);
                }
                sources.add(source);
                File sourceIndex = new File(PathConstants.USER_SOURCES_INDEX_PATH);
                writeSourceFileIndex(sourceIndex);
            } catch (IOException ex) {
                ConsoleFrame.sendMessage(this.getClass().getSimpleName(), "Failed to create file " + source.getName());
                ConsoleFrame.showError("Failed to create file " + source.getName());
            }
            return true;
        } catch (IOException ex) {
            ConsoleFrame.sendMessage(this.getClass().getSimpleName(), "Failed to create file " + source.getName());
            ConsoleFrame.showError("Failed to create file " + source.getName());
            return false;
        }
    }

    /**
     * Creates a source file in the Compiler package
     *
     * @param source
     * @return The created file from the given source
     */
    public File createSourceFile(Source source) {
        File sourceFile = new File(SOURCE_FOLDER_PATH + source.getName() + ".java");
        ConsoleFrame.sendMessage(this.getClass().getSimpleName(), "Creating file at " + sourceFile.getAbsolutePath());
        try {
            sourceFile.createNewFile();
            try (FileWriter fileWriter = new FileWriter(sourceFile);
                    BufferedWriter writer = new BufferedWriter(fileWriter)) {

                String stringAux;
                stringAux = source.getContent();
                writer.write(stringAux);
            }
            return sourceFile;
        } catch (IOException ex) {
            ConsoleFrame.sendMessage(this.getClass().getSimpleName(), "Failed to create file " + source.getName());
            ConsoleFrame.showError("Failed to create file " + source.getName());
        }
        return null;
    }

    /**
     * Deletes a given file
     *
     * @param source
     * @return true if deletion was successful and false otherwise
     */
    public boolean deleteFile(File source) {
        boolean success = true;
        ConsoleFrame.sendMessage(this.getClass().getSimpleName(), "Deleting file at " + source.getAbsolutePath());
        if (source.exists()) {
            success = source.delete();
        }
        return success;
    }

    /**
     * This method returns the content of the predefined AI template
     *
     * @return inteligenceTemplate
     */
    private String readIntelligenceTemplate() {
        File template = new File(PathConstants.AI_TEMPLATE);
        if (template.exists()) {
            try (FileReader fileReader = new FileReader(template);
                    BufferedReader bufferedReader = new BufferedReader(fileReader)) {
                while (bufferedReader.ready()) {
                    AI_TEMPLATE_CONTENT += bufferedReader.readLine() + "\n";
                }
            } catch (IOException ex) {
                ConsoleFrame.sendMessage(this.getClass().getSimpleName(), "Failed to read template");
                ConsoleFrame.showError("Failed to read AI template");
            }
        } else {
            ConsoleFrame.sendMessage(this.getClass().getSimpleName(), "Could not find AI template at " + template.getAbsolutePath());
            ConsoleFrame.showError("Failed to find AI template");
        }
        return AI_TEMPLATE_CONTENT;
    }

    /**
     * Gets the intelligence template that will appear in the editor
     *
     * @return The intelligence template in a String
     */
    public String getIntelligenceTemplate() {
        if (AI_TEMPLATE_CONTENT.isEmpty()) {
            readIntelligenceTemplate();
        }
        return AI_TEMPLATE_CONTENT;
    }

}
