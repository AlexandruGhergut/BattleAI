package Compiler;

import Console.ConsoleFrame;
import Editor.Source;
import Editor.SourceManager;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Scanner;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

/**
 * This class handles all your user compilation and instantiation needs.
 * @author Dragos-Alexandru
 */
public abstract class SourceCompiler {
    
    private final static JavaCompiler COMPILER = ToolProvider.getSystemJavaCompiler();
    private static URLClassLoader CLASS_LOADER;
    
    private static String lastError = "";
    
    /**
     * Compiles a source object and returns the .java file. If deleteJava is
     * true then it deletes the .java file and if deleteClass is true than it 
     * deletes the .class files generated
     * @param source
     * @param deleteJava
     * @param deleteClass
     * @return A .java file that represents the uncompiled file of the given source
     */
    public static File compileSource(Source source, boolean deleteJava, boolean deleteClass){
        File sourceFile = SourceManager.getInstance().createSourceFile(source);
        ByteArrayOutputStream errorOStream = new ByteArrayOutputStream();
        int fail = COMPILER.run(null, null, errorOStream, sourceFile.getPath());
        if(fail != 0){
            SourceManager.getInstance().deleteFile(sourceFile);
            ByteArrayInputStream errorIStream = new ByteArrayInputStream(errorOStream.toByteArray());
            Scanner s = new Scanner(errorIStream);
            lastError = "";
            while(s.hasNextLine()){
                lastError += "\n" + s.nextLine();
            }
            return null;
        }else{
            if(deleteJava){
                SourceManager.getInstance().deleteFile(sourceFile);
            }
            if(deleteClass){
                File compiledSource = getClassFromJavaFile(sourceFile);
                SourceManager.getInstance().deleteFile(compiledSource);
            }
        }
        return sourceFile;
    }
    
    /**
     * Function that returns an instance of the given source object
     * @param source
     * @return A instance of the given source object
     */
    public static Object getInstanceOfSource(Source source){
        Object sourceInstance = null;
        File sourceFile = compileSource(source,true,false);
        if(sourceFile != null){
            try {
                
                CLASS_LOADER = URLClassLoader.newInstance(new URL[] { sourceFile.toURI().toURL() });
                Class<?> sourceClass = Class.forName("User_Sources."+source.getName(),true,CLASS_LOADER);
                sourceInstance = sourceClass.newInstance();
                ConsoleFrame.sendMessage(SourceCompiler.class.getSimpleName(), "Created instance of "+sourceInstance.getClass().getSimpleName());
                
            } catch (MalformedURLException ex) {
                ConsoleFrame.sendMessage(SourceCompiler.class.getSimpleName(), "Failed to create new URLClassLoader");
            } catch (ClassNotFoundException ex) {
                ConsoleFrame.sendMessage(SourceCompiler.class.getSimpleName(), "Failed to get class of compiled source");
            } catch (InstantiationException | IllegalAccessException ex) {
                ConsoleFrame.sendMessage(SourceCompiler.class.getSimpleName(), "Failed to get instantiate source class");
            }finally{
                File compiledSourceFile = getClassFromJavaFile(sourceFile);
                SourceManager.getInstance().deleteFile(compiledSourceFile);
            }
        }
        
        return sourceInstance;
        
    }
    
    /**
     * Receives a .java file and returns a .class file<br>
     * <b>Does not check if returned file exists</b>
     * @param javaFile
     * @return A .class file if the given file is .java or null otherwise
     */
    public static File getClassFromJavaFile(File javaFile){
        File classFile = null;
        if(javaFile.getAbsolutePath().endsWith(".java")){
            classFile = new File(javaFile.getAbsolutePath().replaceFirst(".java", ".class"));
        }else{
            ConsoleFrame.sendMessage(SourceCompiler.class.getSimpleName(), "Given file is not a .java file");
        }
        return classFile;
    }
    
    /**
     * Returns the compilation error
     * @return Returns the error status of the last compilation attempt
     */
    public static String getLastError(){
        return lastError;
    }
    
}
