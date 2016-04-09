
package Editor;

import Interface.MainFrame;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

/**
 * The Editor class represent a frame in witch the user can write
 * his code for an robot
 */
public final class Editor extends JFrame {
    private MainFrame frame;
    
    private JMenuBar menuBar;
    
    private JMenuItem newButton;
    private JMenuItem openButton;
    private JMenuItem saveButton;
    private JMenuItem compileButton;
    
    private RSyntaxTextArea sourceArea;
    
    /**
     * 
     * @param mainFrame
     */
    public Editor(MainFrame mainFrame){
        super("editor");
        EditorMenuListener editorMenuListener = new EditorMenuListener();
        
        this.setSize(800, 800);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setLayout(new BorderLayout());
        
        this.frame = mainFrame;
        this.addWindowListener(new WindowAdapter(){
            @Override
            public void windowClosing(WindowEvent e){
                super.windowClosing(e);
                frame.setVisible(true);
        }
        });
        
        newButton = new JMenuItem("New");
        newButton.setBackground(Color.BLACK);
        newButton.setForeground(Color.WHITE);
        newButton.addMouseListener(editorMenuListener);
        
        openButton = new JMenuItem("Open");
        openButton.setBackground(Color.BLACK);
        openButton.setForeground(Color.WHITE);
        openButton.addMouseListener(editorMenuListener);
        
        saveButton = new JMenuItem("Save");
        saveButton.setBackground(Color.BLACK);
        saveButton.setForeground(Color.WHITE);
        saveButton.addMouseListener(editorMenuListener);
        
        compileButton = new JMenuItem("Compile");
        compileButton.setBackground(Color.BLACK);
        compileButton.setForeground(Color.WHITE);
        compileButton.addMouseListener(editorMenuListener);
        
        menuBar = new JMenuBar();
        menuBar.setLayout(new GridLayout(1,4));
        menuBar.setBackground(Color.BLACK);
        menuBar.add(newButton);
        menuBar.add(openButton);
        menuBar.add(saveButton);
        menuBar.add(compileButton);
        
        sourceArea = new RSyntaxTextArea();
        sourceArea.setDocument(new RSyntaxDocument (RSyntaxTextArea.SYNTAX_STYLE_JAVA));
        sourceArea.setSyntaxEditingStyle(RSyntaxTextArea.SYNTAX_STYLE_JAVA);
        sourceArea.setAutoIndentEnabled(true);
        sourceArea.setCodeFoldingEnabled(true);
        sourceArea.setMarkOccurrences(true);
        sourceArea.setActiveLineRange(3, 10);
        
        this.setVisible(true);
        this.add(menuBar, BorderLayout.NORTH);
        this.add(sourceArea, BorderLayout.CENTER);
    }
    
    
}
