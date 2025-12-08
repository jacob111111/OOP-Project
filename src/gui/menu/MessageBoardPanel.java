package gui.menu;

import java.awt.BorderLayout;
import javax.swing.*;
import javax.swing.text.*;

import gui.utils.UIPalette;

/**
 * Panel that displays game messages with styled text.
 * 
 * This panel manages a message board that shows INFO and ERROR messages
 * with appropriate color coding. Previous INFO messages are grayed out
 * when new messages arrive.
 */
public class MessageBoardPanel extends JPanel {
    private JTextPane messageBoard;
    private JScrollPane messageScrollPane;

    /**
     * Creates a new message board panel.
     */
    public MessageBoardPanel() {
        initializeComponents();
    }

    /**
     * Initializes the message board UI components.
     */
    private void initializeComponents() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(10, 0, 0, 0),
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(java.awt.Color.BLACK, 1),
                "Messages"
            )
        ));

        messageBoard = new JTextPane();
        messageBoard.setEditable(false);
        messageScrollPane = new JScrollPane(messageBoard);
        messageScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        add(messageScrollPane, BorderLayout.CENTER);
    }

    /**
     * Displays a message in the message board with format: [TYPE] Issue: Details
     * Messages appear at the bottom, user scrolls up to see older messages.
     * Previous INFO messages are grayed out when new messages are added.
     * 
     * @param message     The message to display (format: "Issue: Details")
     * @param messageType The type of message ("error" or "info")
     */
    public void displayMessage(String message, String messageType) {
        StyledDocument doc = messageBoard.getStyledDocument();
        
        // Gray out all previous INFO messages
        for (int i = 0; i < doc.getLength(); i++) {
            AttributeSet attrs = doc.getCharacterElement(i).getAttributes();
            java.awt.Color currentColor = StyleConstants.getForeground(attrs);
            
            if (currentColor != null && currentColor.equals(java.awt.Color.BLACK)) {
                SimpleAttributeSet grayStyle = new SimpleAttributeSet();
                StyleConstants.setForeground(grayStyle, java.awt.Color.GRAY);
                doc.setCharacterAttributes(i, 1, grayStyle, false);
            }
        }
        
        // Determine message type and color
        String typeTag;
        java.awt.Color textColor;
        
        switch (messageType.toLowerCase()) {
            case "error":
                typeTag = "[ERROR] ";
                textColor = java.awt.Color.RED;
                break;
            case "info":
            default:
                typeTag = "[INFO] ";
                textColor = java.awt.Color.BLACK;
                break;
        }
        
        // Create style for the message
        SimpleAttributeSet style = new SimpleAttributeSet();
        StyleConstants.setForeground(style, textColor);
        
        // Format: [TYPE] message\n
        String formattedMessage = typeTag + message + "\n";
        
        try {
            // Insert at the end (new messages at bottom)
            doc.insertString(doc.getLength(), formattedMessage, style);
            
            // Auto-scroll to bottom to show newest message
            messageBoard.setCaretPosition(doc.getLength());
        } catch (BadLocationException e) {
            System.err.println("Error inserting message: " + e.getMessage());
        }
    }

    /**
     * Clears all messages from the message board.
     */
    public void clearMessages() {
        messageBoard.setText("");
    }

    /**
     * Updates the visual styling using the current palette.
     * 
     * @param palette The UI palette to apply
     */
    public void updateStyle(UIPalette palette) {
        setBackground(palette.labelBackground);
        
        // Style message panel with themed border
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(10, 0, 0, 0),
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(palette.labelForeground, 1),
                "Messages",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                palette.font,
                palette.labelForeground
            )
        ));
        messageBoard.setFont(palette.font);
        // Note: Foreground color is set per-message (black for INFO, red for ERROR)
        messageBoard.setBackground(palette.labelBackground);
    }
}
