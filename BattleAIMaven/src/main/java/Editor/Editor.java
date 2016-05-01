/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Editor;

import Compiler.SourceCompiler;
import Console.ConsoleFrame;
import Constants.PathConstants;
import Interface.MainFrame;
import Networking.Server.Player;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Calendar;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.filechooser.FileFilter;
import javax.swing.plaf.BorderUIResource;
import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

/**
 * The Editor class represent a frame in witch the user can write
 * his code for an robot
 */
public final class Editor extends JFrame {
    private final MainFrame frame;
    
    private final JMenuBar menuBar;
    
    private final JMenuItem newButton;
    private final JMenuItem openButton;
    private final JMenuItem saveButton;
    private final JMenuItem compileButton;
    
    private String inteligenceTemplate;
    private final RSyntaxTextArea sourceArea;
    private final JScrollPane scrollSourceArea;
    private final JTextArea compileResult;
    private final JScrollPane scrollCompileResult;
    private final JPanel sourcePanel;
    
    private static String sourceName;
    
    /**
     * 
     * @param mainFrame
     */
    public Editor(MainFrame mainFrame){
        super("Editor - (unsaved)");
        
        this.frame = mainFrame;
        inteligenceTemplate = SourceManager.getInstance().getIntelligenceTemplate();
        sourceName = JOptionPane.showInputDialog("Source name: ", "LocalSource");
        
        inteligenceTemplate = inteligenceTemplate.replaceFirst("<name>", sourceName);
        
        sourceArea = new RSyntaxTextArea();
        sourceArea.setDocument(new RSyntaxDocument (RSyntaxTextArea.SYNTAX_STYLE_JAVA));
        sourceArea.setDocument(new RSyntaxDocument(RSyntaxTextArea.SYNTAX_STYLE_JAVA));
        sourceArea.append(inteligenceTemplate);
        sourceArea.setSyntaxEditingStyle(RSyntaxTextArea.SYNTAX_STYLE_JAVA);
        sourceArea.setAutoIndentEnabled(true);
        sourceArea.setCodeFoldingEnabled(true);
        sourceArea.setMarkOccurrences(true);
        sourceArea.setActiveLineRange(3, 10);
        sourceArea.setBorder(new BorderUIResource.LineBorderUIResource(Color.white, 4));
        
        
        scrollSourceArea = new JScrollPane();
        scrollSourceArea.setViewportView(sourceArea);
        scrollSourceArea.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        
        compileResult = new JTextArea();
        compileResult.setEditable(false);
        compileResult.setBorder(new BorderUIResource.LineBorderUIResource(Color.black));
        compileResult.setLineWrap(true);
        compileResult.setBackground(Color.black);
        compileResult.setForeground(Color.GREEN);
        
        scrollCompileResult = new JScrollPane();
        scrollCompileResult.setViewportView(compileResult);
        scrollCompileResult.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        
        sourcePanel = new JPanel(new GridLayout(2,1));
        
        EditorMenuListener editorMenuListener = new EditorMenuListener();
        
        newButton = new JMenuItem("New");
        newButton.setBackground(Color.BLACK);
        newButton.setForeground(Color.WHITE);
        newButton.addMouseListener(new EditorNewListener());
        
        openButton = new JMenuItem("Open");
        openButton.setBackground(Color.BLACK);
        openButton.setForeground(Color.WHITE);
        openButton.addMouseListener(new EditorOpenListener());
        
        saveButton = new JMenuItem("Save");
        saveButton.setBackground(Color.BLACK);
        saveButton.setForeground(Color.WHITE);
        saveButton.addMouseListener(new EditorSaveListener());
        
        compileButton = new JMenuItem("Compile");
        compileButton.setBackground(Color.BLACK);
        compileButton.setForeground(Color.WHITE);
        compileButton.addMouseListener(new EditorCompileListener());
        
        menuBar = new JMenuBar();
        menuBar.setLayout(new GridLayout(1,4));
        menuBar.setBackground(Color.BLACK);

        menuBar.add(newButton);
        menuBar.add(openButton);
        menuBar.add(saveButton);
        menuBar.add(compileButton);
        
        start();
    }
    
    /**
     * Starts the editor
     * I put this here because all those functions cannot be called from the
     * constructor
     */
    public final void start(){
        
        this.setSize(800, 600);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setLayout(new BorderLayout());
        
        this.addWindowListener(new WindowAdapter(){
            @Override
            public void windowClosing(WindowEvent e){
                super.windowClosing(e);
                frame.setVisible(true);
        }
        });
        
        this.add(menuBar, BorderLayout.NORTH);
        sourcePanel.add(scrollSourceArea,BorderLayout.CENTER);
        sourcePanel.add(scrollCompileResult,BorderLayout.SOUTH);
        this.add(sourcePanel, BorderLayout.CENTER);
        
        this.setVisible(true);
    }
    
    private final class EditorNewListener extends EditorMenuListener{
        
        @Override
        public void mouseClicked(MouseEvent e) {
            String inteligenceTemplate = SourceManager.getInstance().getIntelligenceTemplate();
            sourceName = JOptionPane.showInputDialog("Source name: ", "LocalSource");

            inteligenceTemplate = inteligenceTemplate.replaceFirst("<name>", sourceName);

            sourceArea.setText("");

            sourceArea.append(inteligenceTemplate);
        }
    }
    private final class EditorOpenListener extends EditorMenuListener{

        @Override
        public void mouseClicked(MouseEvent e) {
            
            int external;
            external = JOptionPane.showConfirmDialog(null, "Is this file outside of our application?", "File chooser", JOptionPane.YES_NO_OPTION);
            JFileChooser fileChooser;
            if(external == JOptionPane.NO_OPTION){
                fileChooser = new JFileChooser(new File(PathConstants.USER_SOURCES_INDEX_PATH));
            }else{
                fileChooser = new JFileChooser(new File(System.getProperty("user.home"), "Desktop"));
            }
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.setFileFilter(new FileFilter() {
                @Override
                public boolean accept(File f) {
                    return f.getAbsolutePath().endsWith(".java") ||
                            f.getAbsolutePath().endsWith(".txt") ||
                            !f.getAbsolutePath().contains(".");
                }

                @Override
                public String getDescription() {
                    return "Java source,text file (*.java;*.txt)";
                }
            });

            int resultVal = fileChooser.showOpenDialog(null);
            if(resultVal == JFileChooser.APPROVE_OPTION){
                File openedFile = fileChooser.getSelectedFile();
                String sourceNameAux = openedFile.getName();

                if(sourceNameAux.endsWith(".java")){
                    sourceName = sourceNameAux.replaceFirst(".java", "");
                }else{
                    sourceName = sourceNameAux.replaceFirst(".txt", "");
                }

                String fileContent = "";
                try(FileReader fileReader = new FileReader(openedFile);
                        BufferedReader reader = new BufferedReader(fileReader)){
                    while(reader.ready()){
                        fileContent += "\n"+reader.readLine();
                    }
                }catch(IOException ex) {
                    ConsoleFrame.sendMessage(this.getClass().getSimpleName(), "Failed to read from opened source");
                }
                sourceArea.setText(fileContent);
            }
            saveButton.setForeground(Color.red);
        }
    }
    private final class EditorSaveListener extends EditorMenuListener{
        
        @Override
        public void mouseClicked(MouseEvent e) {

            Source source = new Source(sourceArea.getText(), sourceName, Player.getInstance().getUsername());

            boolean success = SourceManager.getInstance().saveFileToSourceFolder(source);

            if(success){
                JOptionPane.showMessageDialog(null, "Save succesfull");
            }else{
                JOptionPane.showMessageDialog(null, "Save failed");
            }
            saveButton.setForeground(Color.WHITE);
        }
    }
    private final class EditorCompileListener extends EditorMenuListener{
        
        @Override
        public void mouseClicked(MouseEvent e) {
            Calendar timePressed = Calendar.getInstance();

            Source source = new Source(sourceArea.getText(),sourceName, Player.getInstance().getUsername());
            Object sourceObject = SourceCompiler.getInstanceOfSource(source);

            boolean success = false;
            if(sourceObject != null){
                success = true;
            }

            compileResult.setText(" Compile result for " +
                    sourceName + ".java at <" + timePressed.getTime().toString() + "> : \n");
            if(success){
                compileResult.append("\nCompilation was successfull");
            }else{
                compileResult.append("\nCompilation failed: \n"+SourceCompiler.getLastError());
            }
        }
    }
    
}
