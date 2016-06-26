package Compiler;

import Console.ConsoleFrame;
import Constants.PathConstants;
import java.io.*;

/**
 * Class that can be used to dynamically load a class, and then reload an
 * updated version This is set up to only load classes whose names don't contain
 * "User_Sources." or they are equal to "Intelligence.IntelligenceTemplate"
 *
 */
public class SourceClassLoader extends ClassLoader {

    @Override
    public synchronized Class loadClass(String typeName, boolean resolveIt) throws ClassNotFoundException {

        // See if type has already been loaded by
        // this class loader
        Class result = findLoadedClass(typeName);
        if (result != null) {
            // Return an already-loaded class
            return result;
        }

        if (typeName.equals("Intelligence.IntelligenceTemplate") || !typeName.contains("User_Sources.")) {
            try {
                result = super.findSystemClass(typeName);
                // Return a system class
                return result;
            } catch (ClassNotFoundException e) {
                ConsoleFrame.showError("Failde to find unreloadable class");
                System.exit(-1);
            }
        }

        // Try to load it
        byte typeData[] = getData(typeName);
        if (typeData == null) {
            throw new ClassNotFoundException();
        }

        // Parse it
        if (typeName.contains("User_Sources.")) {
            result = defineClass(typeName, typeData, 0, typeData.length);
        }
        if (result == null) {
            throw new ClassFormatError();
        }

        if (resolveIt) {
            resolveClass(result);
        }

        // return class
        return result;
    }

    /**
     * Function which returns the class data of the reloadable class in bytes
     *
     * @param typeName
     * @return
     */
    private byte[] getData(String typeName) {
        FileInputStream fis;
        String fileName = typeName.replace("User_Sources.", "").replace('.', File.separatorChar) + ".class";
        try {
            File sourceClass = new File(PathConstants.USER_SOURCES_FOLDER_PATH + fileName);
            fis = new FileInputStream(sourceClass);
        } catch (FileNotFoundException e) {
            ConsoleFrame.showError("Failed to find source .class file");
            System.exit(-1);
            return null;
        }

        BufferedInputStream bis = new BufferedInputStream(fis);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            int c = bis.read();
            while (c != -1) {
                out.write(c);
                c = bis.read();
            }
        } catch (IOException e) {
            ConsoleFrame.showError("Failed to read from class file");
            System.exit(-1);
        }
        return out.toByteArray();
    }
}
