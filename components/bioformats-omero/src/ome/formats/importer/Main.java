/*
 * ome.formats.testclient.TestClient
 *
 *------------------------------------------------------------------------------
 *
 *  Copyright (C) 2005 Open Microscopy Environment
 *      Massachusetts Institute of Technology,
 *      National Institutes of Health,
 *      University of Dundee
 *
 *------------------------------------------------------------------------------
 */

package ome.formats.importer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import ome.formats.importer.util.Actions;
import ome.formats.importer.util.GuiCommonElements;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Brian W. Loranger
 */

public class Main extends JFrame implements ActionListener, WindowListener, IObserver
{
    private static final long   serialVersionUID = 1228000122345370913L;

    public static String        versionText = "Beta 2.3";
    
    /** The data of the last release date. */
    public static String        releaseDate      
         = "2007-06-06 15:18:13 +0100 (Wed, 04 Oct 2007)";

    /** The repository revision. */
    public static String        revision  = "$LastChangedRevision$";

    /** The data of the last repository revision. */
    public static String        revisionDate     
         = "$LastChangedDate$";

    
    
    /** Logger for this class. */
    @SuppressWarnings("unused")
    private static Log          log     = LogFactory.getLog(Main.class);

    public static Point splashLocation = Splasher.location;

    // -- Constants --

    public final static String TITLE            = "OMERO.importer";
    public final static String splash           = "gfx/importer_splash.png";
    private final static boolean useSplashScreenAbout   = false;
     
    private final static int width = 980;
    private final static int height = 580;

    public static final String ICON = "gfx/icon.png";
    
    public LoginHandler         loginHandler;
    
    public FileQueueHandler    fileQueueHandler;
    
    public static HistoryDB db = null;

    public StatusBar            statusBar;

    private JMenu               fileMenu;

    private JMenu               helpMenu;
  
    private JMenuItem           login;
    
    public Boolean              loggedIn;

    private JTextPane           outputTextPane;

    private JTextPane           debugTextPane;
    
    private JTextPane           historyTextPane;

    @SuppressWarnings("unused")
    private String              username;

    @SuppressWarnings("unused")
    private String              password;

    @SuppressWarnings("unused")
    private String              server;

    @SuppressWarnings("unused")
    private String              port;

    /**
     * Main entry class for the application
     */
    public Main()
    {
        super(TITLE);
        
        GuiCommonElements gui = new GuiCommonElements();
        
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        JPanel pane = new JPanel();
        pane.setLayout(new BorderLayout());
        setContentPane(pane);
        setPreferredSize(new Dimension(width, height)); // default size
        pack();
        setLocationRelativeTo(null);
        addWindowListener(this);
        
        setTitle(TITLE);
        setIconImage(gui.getImageIcon(Main.ICON).getImage());

        // menu bar
        JMenuBar menubar = new JMenuBar();
        setJMenuBar(menubar);
        fileMenu = new JMenu("File");
        menubar.add(fileMenu);
        login = new JMenuItem("Login to the server...");
        login.setActionCommand("login");
        login.addActionListener(this);        
        fileMenu.add(login);
        JMenuItem fileQuit = new JMenuItem("Quit");
        fileQuit.setActionCommand("quit");
        fileQuit.addActionListener(this);
        fileMenu.add(fileQuit);
        helpMenu = new JMenu("Help");
        menubar.add(helpMenu);
        JMenuItem helpComment = new JMenuItem("Send a Comment...");
        helpComment.setActionCommand("comment");
        helpComment.addActionListener(this);
        JMenuItem helpAbout = new JMenuItem("About the Importer...");
        helpAbout.setActionCommand("about");
        helpAbout.addActionListener(this);
        helpMenu.add(helpComment);
        helpMenu.add(helpAbout);


        // tabbed panes
        JTabbedPane tPane = new JTabbedPane();
        tPane.setOpaque(false); // content panes must be opaque

        // file chooser pane
        JPanel filePanel = new JPanel(new BorderLayout());

        // The file chooser sub-pane
        fileQueueHandler = new FileQueueHandler(this);
        //splitPane.setResizeWeight(0.5);

        filePanel.add(fileQueueHandler, BorderLayout.CENTER);
        tPane.addTab("File Viewer", null, filePanel,
        "Add and delete images here to the import queue.");
        tPane.setMnemonicAt(0, KeyEvent.VK_1);

        // history pane
        JPanel historyPanel = new JPanel();
        historyPanel.setOpaque(false);
        historyPanel.setLayout(new BorderLayout());
        
        tPane.addTab("Import History", null, historyPanel,
                "Import history is displayed here.");
        tPane.setMnemonicAt(0, KeyEvent.VK_4);

       
        // output text pane
        JPanel outputPanel = new JPanel();
        outputPanel.setLayout(new BorderLayout());
        outputTextPane = new JTextPane();
        outputTextPane.setEditable(false);

        JScrollPane outputScrollPane = new JScrollPane();
        outputScrollPane.getViewport().add(outputTextPane);
        
        outputScrollPane.getVerticalScrollBar().addAdjustmentListener(
                new AdjustmentListener()
        {
            public void adjustmentValueChanged(AdjustmentEvent e)
            {
                outputTextPane.setCaretPosition(outputTextPane.getDocument().
                        getLength());
            }
        }
        );

        outputPanel.add(outputScrollPane, BorderLayout.CENTER);

        tPane.addTab("Output Text", null, outputPanel,
                "Standard output text goes here.");
        tPane.setMnemonicAt(0, KeyEvent.VK_2);


        // debug pane
        JPanel debugPanel = new JPanel();
        debugPanel.setLayout(new BorderLayout());
        debugTextPane = new JTextPane();
        debugTextPane.setEditable(false);

        JScrollPane debugScrollPane = new JScrollPane();
        debugScrollPane.getViewport().add(debugTextPane);

        debugScrollPane.getVerticalScrollBar().addAdjustmentListener(
                new AdjustmentListener()
        {
            public void adjustmentValueChanged(AdjustmentEvent e)
            {
                debugTextPane.setCaretPosition(debugTextPane.getDocument().
                        getLength());
            }
        }
        );

        debugPanel.add(debugScrollPane, BorderLayout.CENTER);

        tPane.addTab("Debug Text", null, debugPanel,
                "Debug messages are displayed here.");
        tPane.setMnemonicAt(0, KeyEvent.VK_3);

        tPane.setSelectedIndex(0);
        
        // Add the tabbed pane to this panel.
        add(tPane);
        
        statusBar = new StatusBar();
        statusBar.setStatusIcon("gfx/server_disconn16.png",
                "Server disconnected.");
        statusBar.setProgress(false, 0, "");
        this.getContentPane().add(statusBar, BorderLayout.SOUTH);

        this.setVisible(false);

        LogAppender.getInstance().setTextArea(debugTextPane);
        appendToOutputLn("> Starting the importer (revision "
                + getPrintableKeyword(revision) + ").");
        appendToOutputLn("> Build date: " + getPrintableKeyword(revisionDate));
        appendToOutputLn("> Release date: " + releaseDate);
        
        loginHandler = new LoginHandler(this, false, false);
        HistoryHandler historyHandler = new HistoryHandler(this);
        historyPanel.add(historyHandler, BorderLayout.CENTER);       

    }

    /**
     * @param s This method appends data to the output window.
     */
    public void appendToOutput(String s)
    {
        try
        {
            StyledDocument doc = (StyledDocument) outputTextPane.getDocument();
            Style style = doc.addStyle("StyleName", null);
            StyleConstants.setForeground(style, Color.black);
            StyleConstants.setFontFamily(style, "SansSerif");
            StyleConstants.setFontSize(style, 12);
            StyleConstants.setBold(style, false);

            // set to blank before update, this will speed up inserts by 3
            //StyledDocument blank = new DefaultStyledDocument();
            //outputTextPane.setDocument(blank);
            doc.insertString(doc.getLength(), s, style);
            
            //trim the document size so it doesn't grow to big
            int maxChars = 100000;
            if (doc.getLength() > maxChars)
                doc.remove(0, doc.getLength() - maxChars);
            
            //outputTextPane.setDocument(doc);
        } catch (BadLocationException e) {}
    }

    /**
     * @param s Append to the output window and add a line return
     */
    public void appendToOutputLn(String s)
    {
        appendToOutput(s + "\n");
    }

    /**
     * @param s This method appends data to the output window.
     */
    public void appendToDebug(String s)
    {
        try
        {          
            StyledDocument doc = (StyledDocument) debugTextPane.getDocument();
            
            Style style = doc.addStyle("StyleName", null);
            StyleConstants.setForeground(style, Color.black);
            StyleConstants.setFontFamily(style, "SansSerif");
            StyleConstants.setFontSize(style, 12);
            StyleConstants.setBold(style, false);

            // set to blank before update, this will speed up inserts by 3
            //StyledDocument blank = new DefaultStyledDocument();
            //debugTextPane.setDocument(blank);
            doc.insertString(doc.getLength(), s, style);
            
            //trim the document size so it doesn't grow to big
            int maxChars = 100000;
            if (doc.getLength() > maxChars)
                doc.remove(0, doc.getLength() - maxChars);
            
            //debugTextPane.setDocument(doc);
        } catch (BadLocationException e) {}
    }

    /**
     * @param s Append to the output window and add a line return
     */
    public void appendToDebugLn(String s)
    {
        appendToDebug(s + "\n");
    }
    
    public void actionPerformed(ActionEvent e)
    {
        String cmd = e.getActionCommand();

        if ("login".equals(cmd))
        {
            if (loggedIn == true)
            {
                setImportEnabled(false);
                loggedIn = false;
                appendToOutputLn("> Logged out.");
                statusBar.setStatusIcon("gfx/server_disconn16.png", "Logged out.");
                loginHandler = null;
            } else 
            {                
                loginHandler = new LoginHandler(this, true, true);
                db = HistoryDB.getHistoryDB();
            }
        } else if ("quit".equals(cmd)) {
            if (quitConfirmed(this) == true)
            {
                System.exit(0);
            }
        }
        else if ("about".equals(cmd))
        {
            // HACK - JOptionPane prevents shutdown on dispose
            setDefaultCloseOperation(EXIT_ON_CLOSE);
            About.show(this.getContentPane(), useSplashScreenAbout);
        }
        else if ("comment".equals(cmd))
        {
            new CommentMessenger(this, "Comment Dialog", true);
        }
    }

    /**
     * @param keyword
     * @return This function strips out the unwanted sections of the keywords
     *         used for the version number and build time variables, leaving
     *         only the stuff we want.
     */
    public static String getPrintableKeyword(String keyword)
    {
        int begin = keyword.indexOf(" ") + 1;
        int end = keyword.lastIndexOf(" ");
        return keyword.substring(begin, end);
    }

    /** Toggles wait cursor. */
    public void waitCursor(boolean wait)
    {
        setCursor(wait ? Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR) : null);
    }

    /**
     * @param toggle boolean toggle for the import menu
     */
    public void setImportEnabled(boolean toggle)
    {
        if (toggle == true) login.setText("Logout of the server...");
        else login.setText("Login to the server...");
    }

    /**
     * only allow the exit menu option
     */
    public void onlyAllowExit()
    {
        fileMenu.setEnabled(true);
        helpMenu.setEnabled(true);
    }

    /**
     * @param toggle Enable all menu options
     */
    public void enableMenus(boolean toggle)
    {
        fileMenu.setEnabled(toggle);
        helpMenu.setEnabled(toggle);
    }

    // Getters and Setters

    public void setPassword(String password)
    {
        this.password = password;
    }

    public void setPort(String port)
    {
        this.port = port;
    }

    public void setServer(String server)
    {
        this.server = server;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    private boolean quitConfirmed(JFrame frame) {
        String s1 = "Quit";
        String s2 = "Don't Quit";
        Object[] options = {s1, s2};
        int n = JOptionPane.showOptionDialog(frame,
                "Do you really want to quit?\n" +
                "Doing so will cancel any running imports.",
                "Quit Confirmation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                s1);
        if (n == JOptionPane.YES_OPTION) {
            return true;
        } else {
            return false;
        }
    }

    public void windowClosing(WindowEvent e)  
    {
        if (quitConfirmed(this) == true)
        {
            System.exit(0);
        }
    }

    public void windowActivated(WindowEvent e)  {}
    public void windowClosed(WindowEvent e)  {}
    public void windowDeactivated(WindowEvent e)  {}
    public void windowDeiconified(WindowEvent e)  {}
    public void windowIconified(WindowEvent e)  {}
    public void windowOpened(WindowEvent e) {}

    /**
     * @param args Start up the application, display the main window and the
     *            login dialog.
     */
    public static void main(String[] args)
    {  

        String laf = UIManager.getSystemLookAndFeelClassName() ;

        //laf = "ch.randelshofer.quaqua.QuaquaLookAndFeel";
        //laf = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
        //laf = "com.sun.java.swing.plaf.gtk.GTKLookAndFeel";
        //laf = "com.sun.java.swing.plaf.motif.MotifLookAndFeel";
        //laf = "javax.swing.plaf.metal.MetalLookAndFeel";

        if (laf.equals("apple.laf.AquaLookAndFeel"))
        {
            System.setProperty("Quaqua.design", "panther");
            
            try {
                UIManager.setLookAndFeel(
                    "ch.randelshofer.quaqua.QuaquaLookAndFeel"
                );
           } catch (Exception e) { System.err.println(laf + " not supported.");}
        } else {
            try {
                UIManager.setLookAndFeel(laf);
            } catch (Exception e) 
           { System.err.println(laf + " not supported."); }
        }
        new Main();
    }

    public static Point getSplashLocation()
    {
        return splashLocation;
    }

    public void update(IObservable importLibrary, Object message, Object[] args)
    {
        if (message == Actions.LOADING_IMAGE)
        {
            appendToOutput("> [" + args[1] + "] Loading image \"" + args[0] + "\"...\n");
            statusBar.setStatusIcon("gfx/import_icon_16.png", "Prepping file \"" + args[0] + "\"");
        }
        if (message == Actions.LOADED_IMAGE)
        {
            appendToOutput(" Succesfully loaded.\n");
            statusBar.setProgress(true, 0, "Importing file " + args[2] + " of " + args[3]);
            statusBar.setProgressValue((Integer)args[2] - 1);
            appendToOutput("> [" + args[1] + "] Importing metadata for " + "image \"" + args[0] + "\"... ");
            statusBar.setStatusIcon("gfx/import_icon_16.png", "Analyzing the metadata for file \"" + args[0] + "\"");
        }
        
        if (message == Actions.DATASET_STORED)
        {
            appendToOutputLn("Successfully stored to dataset \"" + args[4] + "\" with id \"" + args[5] + "\".");
            appendToOutputLn("> [" + args[1] + "] Importing pixel data for " + "image \"" + args[0] + "\"... ");
            statusBar.setStatusIcon("gfx/import_icon_16.png", "Importing the plane data for file \"" + args[0] + "\"");
            appendToOutput("> Importing plane: ");
        }
        
        if (message == Actions.DATA_STORED)
        {
            appendToOutputLn("> Successfully stored with pixels id \"" + args[5] + "\".");
            appendToOutputLn("> [" + args[1] + "] Image imported successfully!");
        }
    }
}