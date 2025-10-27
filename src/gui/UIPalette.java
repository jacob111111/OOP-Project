package gui;

import java.awt.Color;
import java.awt.Font;

public class UIPalette {
    public final Color boardCellLight;
    public final Color boardCellDark;

    public final Color labelBackground;
    public final Color labelForeground;

    public final Color borderColor;

    public final Font font;

    public UIPalette(Color boardLight, Color boardDark, Color labelBackground, Color labelForeground, Color borderColor, Font font) {
        this.boardCellLight = boardLight;
        this.boardCellDark = boardDark;
        this.labelBackground = labelBackground;
        this.labelForeground = labelForeground;
        this.borderColor = borderColor;
        this.font = font;
    }

    public static final UIPalette CLASSIC = new UIPalette(
        new Color(240, 217, 181), // light square
        new Color(181, 136, 99),  // dark square
        Color.LIGHT_GRAY,         // label background
        Color.BLACK,              // label foreground
        Color.DARK_GRAY,          // border
        new Font("Serif", Font.BOLD, 16)
    );

    public static final UIPalette MODERN = new UIPalette(
        new Color(220, 220, 220), // light square
        new Color(100, 100, 100), // dark square
        Color.WHITE,              // label background
        Color.BLUE,               // label foreground
        Color.GRAY,               // border
        new Font("SansSerif", Font.PLAIN, 16)
    );
}
