/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Editor;

import java.io.Serializable;

/**
 *
 * @author Dragos-Alexandru
 */
public class Source implements Serializable{
    
    static final long serialVersionUID = 1L;
    
    private final String name;
    private final String author;
    private final String content;

    public Source(String content, String name, String author) {
        this.content = content;
        this.name = name;
        this.author = author;
    }
    
    public Source(String name, String author) {
        String generatedContent = "package User_Sources;\n\n";
        generatedContent += "   public class "+name+"{\n";
        generatedContent += "       static { System.out.println(\"Hello "+author+"\"); }\n";
        generatedContent += "       public "+name+"(){\n";
        generatedContent += "           System.out.println(\"I have been instantieted\");\n";
        generatedContent += "       }\n";
        generatedContent += "   }\n";
        this.content = generatedContent;
        this.name = name;
        this.author = author;
    }
    public String getContent() {
        return content;
    }

    public String getName() {
        return name;
    }

    public String getAuthor() {
        return author;
    }
    
    public String toListString(){
        return name+"/"+author;
    }
    
    @Override
    public String toString() {
        return "Source{" + "name=" + name + ", author=" + author + ", content=" + content + '}';
    }
}
