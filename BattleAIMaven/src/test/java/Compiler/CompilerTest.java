/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Compiler;

import Editor.Source;
import Editor.SourceManager;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Dragos-Alexandru
 */
public class CompilerTest {
    
    public CompilerTest() {
    }

    /**
     * Test of getInstanceOfSource method, of class Compiler.
     */
    @Test
    public void testGetInstanceOfSource() {
        System.out.println("getInstanceOfSource");
        List<Source> sourceList = SourceManager.getInstance().getSourceList();
        for(Source source:sourceList){
            if(source.getAuthor().equals("GOOD")){
                Object result = SourceCompiler.getInstanceOfSource(source);
                String expResultClassName = source.getName();
                assertEquals(result.getClass().getSimpleName(), expResultClassName);
            }else{
                Object result = SourceCompiler.getInstanceOfSource(source);
                assertNull(result);
            }
        }
    }
    
}
