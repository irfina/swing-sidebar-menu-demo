import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * The entire class is generated by AI (using Claude.ai) using my prompts. The AI didn't make perfect result at first attempt, so
 * this final code is the 13th iteration of iterative prompting.
 */
public class MDIApplication extends JFrame {
    private final int SIDEBAR_WIDTH = 200;
    private final int ANIMATION_DURATION = 150;
    private final int ANIMATION_STEPS = 15;

    private JSplitPane splitPane;
    private JPanel sidebarPanel;
    private JButton toggleButton;
    private JDesktopPane desktopPane;
    private Timer animationTimer;
    private boolean sidebarVisible = true;
    private int currentWidth;
    private boolean isDarkMode = false;

    private final Color DARK_BG = new Color(45, 45, 45);
    private final Color DARK_DESKTOP_BG = new Color(35, 35, 35);

    public MDIApplication() {
        setupLookAndFeel();
        setTitle("MDI Application");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1024, 768);
        setLocationRelativeTo(null);

        setupSidebarPanel();
        setupDesktopPane();
        setupSplitPane();
        setupAnimationTimer();
        createMenuBar();

        currentWidth = SIDEBAR_WIDTH + 20;
    }

    private void setupLookAndFeel() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupSidebarPanel() {
        sidebarPanel = new JPanel();
        sidebarPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));

        for (int i = 1; i <= 5; i++) {
            JButton btn = new JButton("Menu Item " + i);
            btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, btn.getPreferredSize().height));
            btn.setAlignmentX(Component.LEFT_ALIGNMENT);
            sidebarPanel.add(btn);
            sidebarPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        }

        toggleButton = new JButton("≡");
        toggleButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        toggleButton.setFocusPainted(false);
        toggleButton.setPreferredSize(new Dimension(20, toggleButton.getPreferredSize().height));
        toggleButton.setMargin(new Insets(0, 0, 0, 0));
        toggleButton.addActionListener(e -> toggleSidebar());
    }

    private void setupDesktopPane() {
        desktopPane = new JDesktopPane();
    }

    private void setupSplitPane() {
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(sidebarPanel, BorderLayout.CENTER);
        leftPanel.add(toggleButton, BorderLayout.EAST);

        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, desktopPane);
        splitPane.setDividerLocation(SIDEBAR_WIDTH + 20);
        splitPane.setDividerSize(1);
        splitPane.setEnabled(false);

        add(splitPane);
    }

    private void setupAnimationTimer() {
        animationTimer = new Timer(ANIMATION_DURATION / ANIMATION_STEPS, e -> {
            int targetWidth = sidebarVisible ? 20 : SIDEBAR_WIDTH + 20;
            int step = (targetWidth - currentWidth) / 5;
            
            if (Math.abs(targetWidth - currentWidth) <= 2) {
                currentWidth = targetWidth;
                animationTimer.stop();
                sidebarVisible = !sidebarVisible;
            } else {
                currentWidth = currentWidth + (sidebarVisible ? -10 : 10);
            }
            
            splitPane.setDividerLocation(currentWidth);
        });
        animationTimer.setRepeats(true);
    }

    private void toggleSidebar() {
        if (!animationTimer.isRunning()) {
            animationTimer.start();
        }
    }

    private void toggleTheme() {
        isDarkMode = !isDarkMode;
        Color bgColor = isDarkMode ? DARK_BG : UIManager.getColor("Panel.background");
        Color desktopBgColor = isDarkMode ? DARK_DESKTOP_BG : UIManager.getColor("Desktop.background");
        Color fgColor = isDarkMode ? Color.WHITE : UIManager.getColor("Label.foreground");

        sidebarPanel.setBackground(bgColor);
        for (Component c : sidebarPanel.getComponents()) {
            if (c instanceof JButton) {
                c.setBackground(bgColor);
                c.setForeground(fgColor);
            }
        }

        toggleButton.setBackground(bgColor);
        toggleButton.setForeground(fgColor);
        desktopPane.setBackground(desktopBgColor);

        for (JInternalFrame frame : desktopPane.getAllFrames()) {
            updateInternalFrameTheme(frame, bgColor, fgColor);
        }

        repaint();
    }

    private void updateInternalFrameTheme(JInternalFrame frame, Color bgColor, Color fgColor) {
        frame.getContentPane().setBackground(bgColor);
        for (Component c : frame.getContentPane().getComponents()) {
            if (c instanceof JTextArea) {
                c.setBackground(bgColor);
                c.setForeground(fgColor);
            }
            if (c instanceof JScrollPane) {
                c.setBackground(bgColor);
                ((JScrollPane) c).getViewport().setBackground(bgColor);
            }
        }
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        JMenu fileMenu = new JMenu("File");
        JMenuItem newWindow = new JMenuItem("New Window");
        newWindow.addActionListener(e -> createNewInternalFrame());
        fileMenu.add(newWindow);
        
        JMenu viewMenu = new JMenu("View");
        JMenuItem themeItem = new JMenuItem("Toggle Dark Mode");
        themeItem.addActionListener(e -> toggleTheme());
        viewMenu.add(themeItem);

        menuBar.add(fileMenu);
        menuBar.add(viewMenu);
        setJMenuBar(menuBar);
    }

    private void createNewInternalFrame() {
        JInternalFrame internalFrame = new JInternalFrame(
            "Document " + (desktopPane.getAllFrames().length + 1),
            true, true, true, true
        );
        
        internalFrame.setSize(400, 300);
        internalFrame.setLocation(30 * desktopPane.getAllFrames().length, 
                                30 * desktopPane.getAllFrames().length);
        
        JTextArea textArea = new JTextArea();
        if (isDarkMode) {
            textArea.setBackground(DARK_BG);
            textArea.setForeground(Color.WHITE);
        }
        JScrollPane scrollPane = new JScrollPane(textArea);
        if (isDarkMode) {
            scrollPane.setBackground(DARK_BG);
            scrollPane.getViewport().setBackground(DARK_BG);
        }
        internalFrame.add(scrollPane);
        
        internalFrame.setVisible(true);
        desktopPane.add(internalFrame);
        
        try {
            internalFrame.setSelected(true);
        } catch (java.beans.PropertyVetoException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Thread.startVirtualThread(() -> {
            SwingUtilities.invokeLater(() -> {
                new MDIApplication().setVisible(true);
            });
        });
    }
}
