package Source;

import java.io.Serializable;
import java.util.Objects;

/**
 *
 * @author Dragos-Alexandru
 */
public class Source implements Serializable {

    static final long serialVersionUID = 1L;

    private final String name;
    private String author;
    private final String content;

    public Source(String content, String name, String author) {
        this.content = content;
        this.name = name;
        this.author = author;
    }

    public Source(String name, String author) {
        this.content = "";
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

    public void setAuthor(String author) {
        this.author = author;
    }

    public String toListString() {
        return name + "/" + author;
    }

    @Override
    public String toString() {
        return "Source{" + "name=" + name + ", author=" + author + ", content=" + content + '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Source) {
            Source sObj = (Source) obj;
            return this.name.equals(sObj.name) && this.author.equals(sObj.author);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(this.name);
        hash = 53 * hash + Objects.hashCode(this.author);
        return hash;
    }

}
