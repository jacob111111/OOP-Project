package gui.utils;

import java.awt.Color;
import java.awt.Font;

/**
 * Defines color palettes and styling themes for the chess game GUI.
 * 
 * This class encapsulates color schemes and fonts to provide consistent
 * theming throughout the application. It includes predefined themes and
 * allows for easy theme switching without modifying individual components.
 */
public class UIPalette {
    /** Background color for light chess board squares */
    public final Color boardCellLight;
    
    /** Background color for dark chess board squares */
    public final Color boardCellDark;

    /** Background color for label panels and menus */
    public final Color labelBackground;
    
    /** Foreground/text color for labels and text elements */
    public final Color labelForeground;

    /** Color used for borders and outlines */
    public final Color borderColor;
    
    /** Default font for text elements */
    public final Font font;

    /**
     * Creates a new UI palette with the specified colors and font.
     * 
     * @param boardLight Background color for light chess squares
     * @param boardDark Background color for dark chess squares
     * @param labelBackground Background color for UI panels
     * @param labelForeground Text color for labels and buttons
     * @param borderColor Color for borders and outlines
     * @param font Default font for text elements
     */
    public UIPalette(Color boardLight, Color boardDark, Color labelBackground, Color labelForeground, Color borderColor, Font font) {
        this.boardCellLight = boardLight;
        this.boardCellDark = boardDark;
        this.labelBackground = labelBackground;
        this.labelForeground = labelForeground;
        this.borderColor = borderColor;
        this.font = font;
    }

    /** Classic wood-style chess board theme with traditional colors */
    public static final UIPalette CLASSIC = new UIPalette(
        new Color(240, 217, 181), // light square
        new Color(181, 136, 99),  // dark square
        Color.LIGHT_GRAY,         // label background
        Color.BLACK,              // label foreground
        Color.DARK_GRAY,          // border
        new Font("Serif", Font.BOLD, 16)
    );

    /** Modern minimalist theme with neutral colors */
    public static final UIPalette MODERN = new UIPalette(
        new Color(220, 220, 220), // light square
        new Color(100, 100, 100), // dark square
        Color.WHITE,              // label background
        Color.BLUE,               // label foreground
        Color.GRAY,               // border
        new Font("SansSerif", Font.PLAIN, 16)
    );
}
