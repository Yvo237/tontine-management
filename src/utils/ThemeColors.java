package utils;

import java.awt.Color;

public class ThemeColors {
    
    // Couleurs primaires
    public static final Color PRIMARY_DARK = new Color(15, 23, 42);
    public static final Color PRIMARY_PURPLE = new Color(139, 92, 246);
    public static final Color PRIMARY_BLUE = new Color(59, 130, 246);
    public static final Color PRIMARY_EMERALD = new Color(16, 185, 129);
    public static final Color PRIMARY_RED = new Color(239, 68, 68);
    public static final Color PRIMARY_AMBER = new Color(251, 146, 60);
    
    // Couleurs de fond
    public static final Color BACKGROUND = new Color(241, 245, 249);
    public static final Color CARD_BG = new Color(255, 255, 255);
    public static final Color SIDEBAR_BG = new Color(30, 41, 59);
    public static final Color SIDEBAR_HOVER = new Color(51, 65, 85);
    public static final Color SIDEBAR_ACTIVE = new Color(139, 92, 246);
    
    // Couleurs de texte
    public static final Color TEXT_PRIMARY = new Color(15, 23, 42);
    public static final Color TEXT_SECONDARY = new Color(100, 116, 139);
    
    // Couleurs de bordure
    public static final Color BORDER_COLOR = new Color(226, 232, 240);
    
    // Couleurs de fond clair
    public static final Color BACKGROUND_LIGHT = new Color(248, 250, 252);
    
    // Constructeur privé pour empêcher l'instanciation
    private ThemeColors() {
        throw new AssertionError("Classe utilitaire - ne pas instancier");
    }
}
