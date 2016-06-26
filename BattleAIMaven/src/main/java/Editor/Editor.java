package Editor;

import Source.SourceManager;
import Source.Source;
import Compiler.SourceCompiler;
import Console.ConsoleFrame;
import Constants.PathConstants;
import Interface.MainFrame;
import Networking.Server.Player;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
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
 * The Editor class represent a frame in witch the user can write his code for
 * an robot
 */
public final class Editor extends JFrame {

    private final MainFrame frame;

    private final JMenuBar menuBar;

    private final JMenuItem newButton;
    private final JMenuItem openButton;
    private final JMenuItem saveButton;
    private final JMenuItem compileButton;
    private final JMenuItem backButton;

    private String inteligenceTemplate;
    private final RSyntaxTextArea sourceArea;
    private final JScrollPane scrollSourceArea;
    private final JTextArea compileResult;
    private final JScrollPane scrollCompileResult;
    private final JPanel sourcePanel;

    private static String sourceName;

    private boolean saved = false;

    /**
     *
     * @param mainFrame
     */
    public Editor(MainFrame mainFrame) {
        super("Editor - (unsaved)");

        this.frame = mainFrame;
        inteligenceTemplate = SourceManager.getInstance().getIntelligenceTemplate();
        sourceName = "LocalSource";
        if (sourceName != null) {
            inteligenceTemplate = inteligenceTemplate.replaceAll("<name>", sourceName);
        }
        sourceArea = new RSyntaxTextArea();
        sourceArea.setDocument(new RSyntaxDocument(RSyntaxTextArea.SYNTAX_STYLE_JAVA));
        sourceArea.setDocument(new RSyntaxDocument(RSyntaxTextArea.SYNTAX_STYLE_JAVA));
        sourceArea.append(inteligenceTemplate);
        sourceArea.setSyntaxEditingStyle(RSyntaxTextArea.SYNTAX_STYLE_JAVA);
        sourceArea.setAutoIndentEnabled(true);
        sourceArea.setCodeFoldingEnabled(true);
        sourceArea.setMarkOccurrences(true);
        sourceArea.setActiveLineRange(3, 10);
        sourceArea.setBorder(new BorderUIResource.LineBorderUIResource(Color.white, 4));
        sourceArea.addKeyListener(new InputKeyListener());

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

        sourcePanel = new JPanel(new GridLayout(2, 1));

        newButton = new JMenuItem("New");
        newButton.setBackground(Color.BLACK);
        newButton.setForeground(Color.WHITE);
        newButton.addMouseListener(new EditorNewListener(this));
        newButton.setName("newButton");

        openButton = new JMenuItem("Open");
        openButton.setBackground(Color.BLACK);
        openButton.setForeground(Color.WHITE);
        openButton.addMouseListener(new EditorOpenListener(this));
        openButton.setName("openButton");

        saveButton = new JMenuItem("Save");
        saveButton.setBackground(Color.BLACK);
        saveButton.setForeground(Color.RED);
        saveButton.addMouseListener(new EditorSaveListener(this));
        saveButton.setName("saveButton");

        compileButton = new JMenuItem("Compile");
        compileButton.setBackground(Color.BLACK);
        compileButton.setForeground(Color.WHITE);
        compileButton.addMouseListener(new EditorCompileListener(this));
        compileButton.setName("compileButton");

        backButton = new JMenuItem("Back");
        backButton.setBackground(Color.BLACK);
        backButton.setForeground(Color.WHITE);
        backButton.addMouseListener(new EditorBackListener(this));
        backButton.setName("backButton");

        menuBar = new JMenuBar();
        menuBar.setLayout(new GridLayout(1, 4));
        menuBar.setBackground(Color.BLACK);

        menuBar.add(newButton);
        menuBar.add(openButton);
        menuBar.add(saveButton);
        menuBar.add(compileButton);
        menuBar.add(backButton);

        start();
    }

    /**
     * Starts the editor I put this here because all those functions cannot be
     * called from the constructor
     */
    public final void start() {

        this.setSize(800, 600);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setLayout(new BorderLayout());

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                frame.setVisible(true);
            }
        });

        this.add(menuBar, BorderLayout.NORTH);
        sourcePanel.add(scrollSourceArea, BorderLayout.CENTER);
        sourcePanel.add(scrollCompileResult, BorderLayout.SOUTH);
        this.add(sourcePanel, BorderLayout.CENTER);

        this.setVisible(true);
        if (sourceName == null) {
            frame.setVisible(true);
            this.dispose();
        }
    }

    private final class EditorNewListener extends EditorMenuListener {

        public EditorNewListener(Editor parent) {
            super(parent);
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            String inteligenceTemplate = SourceManager.getInstance().getIntelligenceTemplate();
            sourceName = JOptionPane.showInputDialog("Source name: ", "LocalSource");

            inteligenceTemplate = inteligenceTemplate.replaceAll("<name>", sourceName);

            sourceArea.setText("");

            sourceArea.append(inteligenceTemplate);
        }
    }

    private final class EditorOpenListener extends EditorMenuListener {

        public EditorOpenListener(Editor parent) {
            super(parent);
        }

        @Override
        public void mouseClicked(MouseEvent e) {

            int external;
            external = JOptionPane.showConfirmDialog(null, "Is this file outside of our application?", "File chooser", JOptionPane.YES_NO_OPTION);
            JFileChooser fileChooser;
            if (external == JOptionPane.NO_OPTION) {
                fileChooser = new JFileChooser(new File(PathConstants.USER_SOURCES_INDEX_PATH));
            } else {
                fileChooser = new JFileChooser(new File(System.getProperty("user.home"), "Desktop"));
            }
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.setFileFilter(new FileFilter() {
                @Override
                public boolean accept(File f) {
                    return f.getAbsolutePath().endsWith(".java")
                            || f.getAbsolutePath().endsWith(".txt")
                            || !f.getAbsolutePath().contains(".");
                }

                @Override
                public String getDescription() {
                    return "Java source,text file (*.java;*.txt)";
                }
            });

            int resultVal = fileChooser.showOpenDialog(null);
            if (resultVal == JFileChooser.APPROVE_OPTION) {
                File openedFile = fileChooser.getSelectedFile();
                String sourceNameAux = openedFile.getName();

                if (sourceNameAux.endsWith(".java")) {
                    sourceName = sourceNameAux.replaceFirst(".java", "");
                } else {
                    sourceName = sourceNameAux.replaceFirst(".txt", "");
                }

                String fileContent = "";
                try (FileReader fileReader = new FileReader(openedFile);
                        BufferedReader reader = new BufferedReader(fileReader)) {
                    while (reader.ready()) {
                        fileContent += "\n" + reader.readLine();
                    }
                } catch (IOException ex) {
                    ConsoleFrame.sendMessage(this.getClass().getSimpleName(), "Failed to read from opened source");
                }
                sourceArea.setText(fileContent);
            }
            saveButton.setForeground(Color.red);
        }
    }

    private final class EditorSaveListener extends EditorMenuListener {

        public EditorSaveListener(Editor parent) {
            super(parent);
        }

        @Override
        public void mouseClicked(MouseEvent e) {

            Calendar timePressed = Calendar.getInstance();

            Source source = new Source(sourceArea.getText(), sourceName, Player.getInstance().getUsername());
            Object sourceObject = SourceCompiler.getInstanceOfSource(source);

            boolean success = false;
            if (sourceObject != null) {
                success = true;
            }

            compileResult.setText(" Compile result for "
                    + sourceName + ".java at <" + timePressed.getTime().toString() + "> : \n");
            if (success) {
                compileResult.append("\nCompilation was successfull");
            } else {
                compileResult.append("\nCompilation failed: \n" + SourceCompiler.getLastError());
            }

            if (!success) {
                JOptionPane.showMessageDialog(null, "File does not compile", "Compilation error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            success = SourceManager.getInstance().saveFileToSourceFolder(source);

            if (success) {
                JOptionPane.showMessageDialog(null, "Save succesfull");
                saved = true;
            } else {
                JOptionPane.showMessageDialog(null, "Save failed", "Save error", JOptionPane.ERROR_MESSAGE);
            }
            saveButton.setForeground(Color.WHITE);
        }
    }

    private final class EditorCompileListener extends EditorMenuListener {

        public EditorCompileListener(Editor parent) {
            super(parent);
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            Calendar timePressed = Calendar.getInstance();

            Source source = new Source(sourceArea.getText(), sourceName, Player.getInstance().getUsername());
            Object sourceObject = SourceCompiler.getInstanceOfSource(source);

            boolean success = false;
            if (sourceObject != null) {
                success = true;
            }

            compileResult.setText(" Compile result for "
                    + sourceName + ".java at <" + timePressed.getTime().toString() + "> : \n");
            if (success) {
                compileResult.append("\nCompilation was successfull");
            } else {
                compileResult.append("\nCompilation failed: \n" + SourceCompiler.getLastError());
            }
        }
    }

    private final class EditorBackListener extends EditorMenuListener {

        public EditorBackListener(Editor parent) {
            super(parent);
        }

        @Override
        public void mouseClicked(MouseEvent e) {

            if (!saved) {
                int result = JOptionPane.showConfirmDialog(null,
                        "        You have unsaved changes,\n"
                        + "are you sure you want to close the editor?", "Unsaved changes", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (result == JOptionPane.NO_OPTION) {
                    return;
                }
            }

            parentEditor.getMainFrame().setVisible(true);
            parentEditor.dispose();

        }
    }

    private final class InputKeyListener implements KeyListener {

        @Override
        public void keyTyped(KeyEvent e) {
            if (saved) {
                saved = false;
                changeSaveButton();
            }
        }

        @Override
        public void keyPressed(KeyEvent e) {
            if (saved) {
                saved = false;
                changeSaveButton();
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            if (saved) {
                saved = false;
                changeSaveButton();
            }
        }

        private void changeSaveButton() {
            saveButton.setForeground(Color.RED);
        }
    }

    MainFrame getMainFrame() {
        return frame;
    }

    boolean isSaved() {
        return saved;
    }
}
