package ui.panels;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.AbstractBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import dao.ParticipationDAO;
import dao.TontineDAO;
import models.Tontine;
import ui.MainFrame;

/**
 * Panneau de gestion des tontines avec design ultra-moderne
 * Design 3.0 - Interface Premium finale cohérente
 */
public class TontinesPanel extends JPanel {
    private MainFrame mainFrame;
    private TontineDAO tontineDAO;
    private ParticipationDAO participationDAO;
    
    private JTable tableTontines;
    private DefaultTableModel tableModel;
    private JLabel lblTitre;
    private JLabel lblStatTotal;
    private JLabel lblStatActives;
    private JLabel lblStatTerminees;
    
    // Palette de couleurs cohérente
    private static final Color PRIMARY_DARK = new Color(15, 23, 42);
    private static final Color PRIMARY_PURPLE = new Color(139, 92, 246);
    private static final Color PRIMARY_BLUE = new Color(59, 130, 246);
    private static final Color PRIMARY_EMERALD = new Color(16, 185, 129);
    private static final Color PRIMARY_RED = new Color(239, 68, 68);
    private static final Color PRIMARY_AMBER = new Color(251, 146, 60);
    private static final Color BACKGROUND = new Color(241, 245, 249);
    private static final Color CARD_BG = new Color(255, 255, 255);
    private static final Color TEXT_PRIMARY = new Color(15, 23, 42);
    private static final Color TEXT_SECONDARY = new Color(100, 116, 139);
    private static final Color BORDER_COLOR = new Color(226, 232, 240);
    
    public TontinesPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.tontineDAO = new TontineDAO();
        this.participationDAO = new ParticipationDAO();
        initComponents();
        chargerTontines();
    }
    
    /**
     * Initialise tous les composants avec design ultra-moderne
     */
    private void initComponents() {
        setLayout(new BorderLayout(0, 0));
        setBackground(BACKGROUND);
        
        // Container principal
        JPanel mainContainer = new JPanel(new BorderLayout(0, 24));
        mainContainer.setBackground(BACKGROUND);
        mainContainer.setBorder(BorderFactory.createEmptyBorder(32, 32, 32, 32));
        
        // En-tête moderne
        JPanel headerPanel = createModernHeader();
        mainContainer.add(headerPanel, BorderLayout.NORTH);
        
        // Corps avec statistiques et tableau
        JPanel bodyPanel = new JPanel(new BorderLayout(0, 24));
        bodyPanel.setBackground(BACKGROUND);
        
        // Statistiques rapides
        JPanel statsPanel = createStatsPanel();
        bodyPanel.add(statsPanel, BorderLayout.NORTH);
        
        // Panneau tableau
        JPanel tablePanel = createTablePanel();
        bodyPanel.add(tablePanel, BorderLayout.CENTER);
        
        mainContainer.add(bodyPanel, BorderLayout.CENTER);
        
        // Panneau actions
        JPanel actionPanel = createActionPanel();
        mainContainer.add(actionPanel, BorderLayout.SOUTH);
        
        add(mainContainer, BorderLayout.CENTER);
    }
    
    /**
     * Crée l'en-tête moderne
     */
    private JPanel createModernHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(CARD_BG);
        header.setBorder(new RoundedBorder(20, BORDER_COLOR, 1));
        
        JPanel innerPanel = new JPanel(new BorderLayout());
        innerPanel.setBackground(CARD_BG);
        innerPanel.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));
        
        // Section gauche - Titre
        JPanel leftSection = new JPanel();
        leftSection.setLayout(new BoxLayout(leftSection, BoxLayout.Y_AXIS));
        leftSection.setBackground(CARD_BG);
        
        JLabel iconLabel = new JLabel("💰");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        iconLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        lblTitre = new JLabel("Gestion des Tontines");
        lblTitre.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitre.setForeground(TEXT_PRIMARY);
        lblTitre.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel("Organisation et suivi des épargnes collectives");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(TEXT_SECONDARY);
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        leftSection.add(iconLabel);
        leftSection.add(Box.createVerticalStrut(8));
        leftSection.add(lblTitre);
        leftSection.add(Box.createVerticalStrut(4));
        leftSection.add(subtitleLabel);
        
        innerPanel.add(leftSection, BorderLayout.WEST);
        header.add(innerPanel);
        return header;
    }
    
    /**
     * Crée le panneau de statistiques rapides
     */
    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 20, 0));
        panel.setBackground(BACKGROUND);
        
        // Carte Total Tontines
        JPanel cardTotal = createMiniStatCard("Total Tontines", "0", "📊", PRIMARY_PURPLE);
        lblStatTotal = (JLabel) ((JPanel)((JPanel)((JPanel)cardTotal.getComponent(0)).getComponent(1)).getComponent(0)).getComponent(0);
        
        // Carte Actives
        JPanel cardActives = createMiniStatCard("Actives", "0", "✅", PRIMARY_EMERALD);
        lblStatActives = (JLabel) ((JPanel)((JPanel)((JPanel)cardActives.getComponent(0)).getComponent(1)).getComponent(0)).getComponent(0);
        
        // Carte Terminées
        JPanel cardTerminees = createMiniStatCard("Terminées", "0", "🏁", PRIMARY_BLUE);
        lblStatTerminees = (JLabel) ((JPanel)((JPanel)((JPanel)cardTerminees.getComponent(0)).getComponent(1)).getComponent(0)).getComponent(0);
        
        panel.add(cardTotal);
        panel.add(cardActives);
        panel.add(cardTerminees);
        
        return panel;
    }
    
    /**
     * Crée une mini carte de statistique
     */
    private JPanel createMiniStatCard(String titre, String valeur, String icone, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_BG);
        card.setBorder(new RoundedBorder(12, BORDER_COLOR, 1));
        
        JPanel innerPanel = new JPanel(new BorderLayout());
        innerPanel.setBackground(CARD_BG);
        innerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(CARD_BG);
        
        JLabel iconLabel = new JLabel(icone);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        iconLabel.setForeground(color);
        
        JPanel valuePanel = new JPanel();
        valuePanel.setLayout(new BoxLayout(valuePanel, BoxLayout.Y_AXIS));
        valuePanel.setBackground(CARD_BG);
        
        JLabel valueLabel = new JLabel(valeur);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valueLabel.setForeground(TEXT_PRIMARY);
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel titleLabel = new JLabel(titre);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        titleLabel.setForeground(TEXT_SECONDARY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        valuePanel.add(valueLabel);
        valuePanel.add(Box.createVerticalStrut(4));
        valuePanel.add(titleLabel);
        
        contentPanel.add(iconLabel, BorderLayout.WEST);
        contentPanel.add(Box.createHorizontalStrut(12), BorderLayout.CENTER);
        
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(CARD_BG);
        rightPanel.add(valuePanel, BorderLayout.CENTER);
        
        innerPanel.add(contentPanel, BorderLayout.WEST);
        innerPanel.add(rightPanel, BorderLayout.CENTER);
        
        card.add(innerPanel);
        return card;
    }
    
    /**
     * Crée le panneau du tableau moderne
     */
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CARD_BG);
        panel.setBorder(new RoundedBorder(16, BORDER_COLOR, 1));
        
        JPanel innerPanel = new JPanel(new BorderLayout());
        innerPanel.setBackground(CARD_BG);
        innerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Créer le tableau
        createModernTable();
        
        JScrollPane scrollPane = new JScrollPane(tableTontines);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(CARD_BG);
        scrollPane.setBackground(CARD_BG);
        
        innerPanel.add(scrollPane, BorderLayout.CENTER);
        panel.add(innerPanel);
        
        return panel;
    }
    
    /**
     * Crée le tableau moderne avec style premium
     */
    private void createModernTable() {
        String[] columnNames = {"ID", "Nom", "Type", "Date Début", "Tours", "Tour Actuel", "Statut"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tableTontines = new JTable(tableModel);
        tableTontines.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableTontines.setRowHeight(48);
        tableTontines.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tableTontines.setShowGrid(false);
        tableTontines.setIntercellSpacing(new Dimension(0, 0));
        tableTontines.setBackground(CARD_BG);
        tableTontines.setSelectionBackground(new Color(237, 233, 254));
        tableTontines.setSelectionForeground(TEXT_PRIMARY);
        
        // Style du header
        JTableHeader header = tableTontines.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBackground(new Color(248, 250, 252));
        header.setForeground(TEXT_SECONDARY);
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 45));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR));
        
        // Renderer personnalisé
        tableTontines.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? CARD_BG : new Color(248, 250, 252));
                }
                
                setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
                
                // Coloration du statut
                if (column == 6 && value != null) {
                    String statut = value.toString().toLowerCase();
                    switch (statut) {
                        case "active":
                            setForeground(PRIMARY_EMERALD);
                            setText("✅ Active");
                            break;
                        case "terminée":
                            setForeground(PRIMARY_BLUE);
                            setText("🏁 Terminée");
                            break;
                        case "suspendue":
                            setForeground(PRIMARY_AMBER);
                            setText("⏸️ Suspendue");
                            break;
                        default:
                            setForeground(TEXT_PRIMARY);
                    }
                } else if (!isSelected) {
                    setForeground(TEXT_PRIMARY);
                }
                
                return c;
            }
        });
        
        // Largeurs des colonnes
        tableTontines.getColumnModel().getColumn(0).setPreferredWidth(50);
        tableTontines.getColumnModel().getColumn(1).setPreferredWidth(200);
        tableTontines.getColumnModel().getColumn(2).setPreferredWidth(120);
        tableTontines.getColumnModel().getColumn(3).setPreferredWidth(110);
        tableTontines.getColumnModel().getColumn(4).setPreferredWidth(80);
        tableTontines.getColumnModel().getColumn(5).setPreferredWidth(100);
        tableTontines.getColumnModel().getColumn(6).setPreferredWidth(120);
    }
    
    /**
     * Crée le panneau d'actions avec boutons modernes
     */
    private JPanel createActionPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        panel.setBackground(BACKGROUND);
        
        JButton btnNouvelle = createModernButton("➕ Nouvelle Tontine", PRIMARY_PURPLE, new Color(237, 233, 254));
        JButton btnModifier = createModernButton("✏️ Modifier", PRIMARY_BLUE, new Color(219, 234, 254));
        JButton btnParticipants = createModernButton("👥 Participants", PRIMARY_EMERALD, new Color(209, 250, 229));
        JButton btnSupprimer = createModernButton("🗑️ Supprimer", PRIMARY_RED, new Color(254, 226, 226));
        JButton btnRafraichir = createModernButton("🔄 Actualiser", TEXT_SECONDARY, new Color(241, 245, 249));
        
        btnNouvelle.addActionListener(e -> nouvelleTontine());
        btnModifier.addActionListener(e -> modifierTontine());
        btnParticipants.addActionListener(e -> gererParticipants());
        btnSupprimer.addActionListener(e -> supprimerTontine());
        btnRafraichir.addActionListener(e -> chargerTontines());
        
        panel.add(btnNouvelle);
        panel.add(btnModifier);
        panel.add(btnParticipants);
        panel.add(btnSupprimer);
        panel.add(btnRafraichir);
        
        return panel;
    }
    
    /**
     * Crée un bouton moderne avec effet hover
     */
    private JButton createModernButton(String text, Color textColor, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setForeground(textColor);
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(170, 44));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(adjustBrightness(bgColor, 0.95f));
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }
    
    /**
     * Ajuste la luminosité d'une couleur
     */
    private Color adjustBrightness(Color color, float factor) {
        int r = Math.min(255, (int)(color.getRed() * factor));
        int g = Math.min(255, (int)(color.getGreen() * factor));
        int b = Math.min(255, (int)(color.getBlue() * factor));
        return new Color(r, g, b);
    }
    
    /**
     * Charge les tontines
     */
    private void chargerTontines() {
        try {
            tableModel.setRowCount(0);
            List<Tontine> tontines = tontineDAO.findAll();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            
            for (Tontine t : tontines) {
                Object[] row = {
                    t.getIdTontine(),
                    t.getNom(),
                    t.getTypeTontine() != null ? t.getTypeTontine().getNom() : "Non défini",
                    t.getDateDebut() != null ? t.getDateDebut().format(formatter) : "Non définie",
                    t.getNombreTours(),
                    t.getTourActuel(),
                    t.getStatut()
                };
                tableModel.addRow(row);
            }
            
            updateStatistics(tontines);
        } catch (Exception e) {
            showErrorMessage("Erreur lors du chargement: " + e.getMessage());
        }
    }
    
    /**
     * Met à jour les statistiques
     */
    private void updateStatistics(List<Tontine> tontines) {
        int total = tontines.size();
        int actives = 0;
        int terminees = 0;
        
        for (Tontine t : tontines) {
            String statut = t.getStatut();
            if ("active".equals(statut)) actives++;
            else if ("terminée".equals(statut)) terminees++;
        }
        
        if (lblStatTotal != null) lblStatTotal.setText(String.valueOf(total));
        if (lblStatActives != null) lblStatActives.setText(String.valueOf(actives));
        if (lblStatTerminees != null) lblStatTerminees.setText(String.valueOf(terminees));
    }
    
    /**
     * Ouvre le dialogue pour créer une nouvelle tontine
     */
    private void nouvelleTontine() {
        TontineDialog dialog = new TontineDialog(mainFrame, null);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            chargerTontines();
        }
    }
    
    /**
     * Ouvre le dialogue pour modifier une tontine
     */
    private void modifierTontine() {
        int selectedRow = tableTontines.getSelectedRow();
        if (selectedRow == -1) {
            showWarningMessage("Veuillez sélectionner une tontine");
            return;
        }
        
        try {
            int id = (int) tableModel.getValueAt(selectedRow, 0);
            Tontine tontine = tontineDAO.findById(id);
            if (tontine != null) {
                TontineDialog dialog = new TontineDialog(mainFrame, tontine);
                dialog.setVisible(true);
                if (dialog.isSaved()) {
                    chargerTontines();
                }
            } else {
                showErrorMessage("Tontine introuvable");
            }
        } catch (Exception e) {
            showErrorMessage("Erreur lors de la modification: " + e.getMessage());
        }
    }
    
    /**
     * Supprime la tontine sélectionnée
     */
    private void supprimerTontine() {
        int selectedRow = tableTontines.getSelectedRow();
        if (selectedRow == -1) {
            showWarningMessage("Veuillez sélectionner une tontine");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "⚠️ ATTENTION !\n\n" +
            "Êtes-vous sûr de vouloir supprimer cette tontine ?\n\n" +
            "Cela supprimera également toutes les participations associées.\n" +
            "Cette action est IRRÉVERSIBLE !",
            "Confirmation de suppression",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
            
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int id = (int) tableModel.getValueAt(selectedRow, 0);
                if (tontineDAO.delete(id)) {
                    showSuccessMessage("Tontine supprimée avec succès");
                    chargerTontines();
                } else {
                    showErrorMessage("Erreur lors de la suppression");
                }
            } catch (Exception e) {
                showErrorMessage("Erreur lors de la suppression: " + e.getMessage());
            }
        }
    }
    
    /**
     * Ouvre le dialogue pour gérer les participants
     */
    private void gererParticipants() {
        int selectedRow = tableTontines.getSelectedRow();
        if (selectedRow == -1) {
            showWarningMessage("Veuillez sélectionner une tontine");
            return;
        }
        
        try {
            int id = (int) tableModel.getValueAt(selectedRow, 0);
            Tontine tontine = tontineDAO.findById(id);
            
            if (tontine != null) {
                ParticipantsDialog dialog = new ParticipantsDialog(mainFrame, tontine, participationDAO);
                dialog.setVisible(true);
            } else {
                showErrorMessage("Tontine introuvable");
            }
        } catch (Exception e) {
            showErrorMessage("Erreur: " + e.getMessage());
        }
    }
    
    /**
     * Messages
     */
    private void showWarningMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Attention", JOptionPane.WARNING_MESSAGE);
    }
    
    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Erreur", JOptionPane.ERROR_MESSAGE);
    }
    
    private void showSuccessMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Succès", JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Rafraîchit le panneau
     */
    public void rafraichir() {
        chargerTontines();
    }
    
    /**
     * Classe pour créer des bordures arrondies
     */
    private static class RoundedBorder extends AbstractBorder {
        private int radius;
        private Color borderColor;
        private int thickness;
        
        public RoundedBorder(int radius, Color borderColor, int thickness) {
            this.radius = radius;
            this.borderColor = borderColor;
            this.thickness = thickness;
        }
        
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            if (thickness > 0) {
                g2d.setColor(borderColor);
                g2d.setStroke(new BasicStroke(thickness));
                g2d.draw(new RoundRectangle2D.Double(
                    x + thickness/2.0, 
                    y + thickness/2.0, 
                    width - thickness, 
                    height - thickness, 
                    radius, 
                    radius
                ));
            }
            
            g2d.dispose();
        }
        
        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(thickness, thickness, thickness, thickness);
        }
    }
}

/**
 * Dialogue pour créer/modifier une tontine - Design moderne
 */
class TontineDialog extends JDialog {
    private Tontine tontine;
    private boolean saved = false;
    private TontineDAO tontineDAO;
    
    private JTextField txtNom, txtNombreTours;
    private JComboBox<String> cmbType;
    private JComboBox<String> cmbStatut;
    private com.toedter.calendar.JDateChooser dateDebut;
    
    private static final Color PRIMARY_PURPLE = new Color(139, 92, 246);
    private static final Color TEXT_PRIMARY = new Color(15, 23, 42);
    private static final Color TEXT_SECONDARY = new Color(100, 116, 139);
    private static final Color BACKGROUND = new Color(248, 250, 252);
    private static final Color BORDER_COLOR = new Color(226, 232, 240);
    
    public TontineDialog(Frame parent, Tontine tontine) {
        super(parent, tontine == null ? "Nouvelle Tontine" : "Modifier Tontine", true);
        this.tontine = tontine;
        this.tontineDAO = new TontineDAO();
        initComponents();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(0, 0));
        setSize(600, 600);
        setLocationRelativeTo(getParent());
        getContentPane().setBackground(BACKGROUND);
        
        // Titre du dialogue
        JPanel titlePanel = createTitlePanel();
        add(titlePanel, BorderLayout.NORTH);
        
        // Formulaire
        JPanel formPanel = createFormPanel();
        add(formPanel, BorderLayout.CENTER);
        
        // Boutons
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
        
        if (tontine != null) {
            populateFields();
        }
    }
    
    private JPanel createTitlePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR),
            BorderFactory.createEmptyBorder(24, 28, 24, 28)
        ));
        
        JLabel iconLabel = new JLabel("💰");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        
        JLabel titleLabel = new JLabel(tontine == null ? "Nouvelle Tontine" : "Modifier Tontine");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 0));
        
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftPanel.setBackground(Color.WHITE);
        leftPanel.add(iconLabel);
        leftPanel.add(titleLabel);
        
        panel.add(leftPanel, BorderLayout.WEST);
        return panel;
    }
    
    private JPanel createFormPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(28, 28, 28, 28));
        
        // Créer les champs
        txtNom = createStyledTextField();
        cmbType = createStyledComboBox();
        cmbType.addItem("1 - Tontine de Présence");
        cmbType.addItem("2 - Tontine Épargne");
        cmbType.addItem("3 - Tontine Solidarité");
        
        dateDebut = createStyledDateChooser();
        txtNombreTours = createStyledTextField();
        
        cmbStatut = createStyledComboBox();
        cmbStatut.addItem("active");
        cmbStatut.addItem("terminée");
        cmbStatut.addItem("suspendue");
        
        // Ajouter les champs avec des espacements - utilisation de JLabel simples
        JLabel lblNom = new JLabel("Nom de la tontine *");
        lblNom.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblNom.setForeground(TEXT_PRIMARY);
        lblNom.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lblNom);
        panel.add(Box.createVerticalStrut(4));
        panel.add(txtNom);
        panel.add(Box.createVerticalStrut(16));
        
        JLabel lblType = new JLabel("Type *");
        lblType.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblType.setForeground(TEXT_PRIMARY);
        lblType.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lblType);
        panel.add(Box.createVerticalStrut(4));
        panel.add(cmbType);
        panel.add(Box.createVerticalStrut(16));
        
        JLabel lblDate = new JLabel("Date de début *");
        lblDate.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblDate.setForeground(TEXT_PRIMARY);
        lblDate.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lblDate);
        panel.add(Box.createVerticalStrut(4));
        panel.add(dateDebut);
        panel.add(Box.createVerticalStrut(16));
        
        JLabel lblTours = new JLabel("Nombre de tours *");
        lblTours.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTours.setForeground(TEXT_PRIMARY);
        lblTours.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lblTours);
        panel.add(Box.createVerticalStrut(4));
        panel.add(txtNombreTours);
        panel.add(Box.createVerticalStrut(16));
        
        JLabel lblStatut = new JLabel("Statut");
        lblStatut.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblStatut.setForeground(TEXT_PRIMARY);
        lblStatut.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lblStatut);
        panel.add(Box.createVerticalStrut(4));
        panel.add(cmbStatut);
        
        return panel;
    }
    
    private void addFormField(JPanel panel, GridBagConstraints gbc, int row, 
                              String label, String description, JComponent field) {
        gbc.gridy = row * 2;
        gbc.insets = new Insets(8, 0, 8, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(createFieldLabel(label, description), gbc);
        
        gbc.gridy = row * 2 + 1;
        gbc.insets = new Insets(0, 0, 16, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        panel.add(field, gbc);
    }
    
    private JPanel createFieldLabel(String label, String description) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BACKGROUND);
        
        JLabel mainLabel = new JLabel(label);
        mainLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        mainLabel.setForeground(TEXT_PRIMARY);
        mainLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        descLabel.setForeground(TEXT_SECONDARY);
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        panel.add(mainLabel);
        panel.add(Box.createVerticalStrut(2));
        panel.add(descLabel);
        
        return panel;
    }
    
    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setPreferredSize(new Dimension(300, 42));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        return field;
    }
    
    private JComboBox<String> createStyledComboBox() {
        JComboBox<String> combo = new JComboBox<>();
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        combo.setBackground(Color.WHITE);
        combo.setPreferredSize(new Dimension(300, 42));
        combo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        return combo;
    }
    
    private com.toedter.calendar.JDateChooser createStyledDateChooser() {
        com.toedter.calendar.JDateChooser chooser = new com.toedter.calendar.JDateChooser();
        chooser.setDateFormatString("dd/MM/yyyy");
        chooser.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        chooser.setPreferredSize(new Dimension(300, 42));
        chooser.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        return chooser;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        JButton btnSave = createDialogButton("💾 Enregistrer", PRIMARY_PURPLE, Color.WHITE);
        JButton btnCancel = createDialogButton("✕ Annuler", new Color(148, 163, 184), Color.WHITE);
        
        btnSave.addActionListener(e -> enregistrer());
        btnCancel.addActionListener(e -> dispose());
        
        panel.add(btnSave);
        panel.add(btnCancel);
        
        return panel;
    }
    
    private JButton createDialogButton(String text, Color bgColor, Color fgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(160, 44));
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(bgColor.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }
    
    private void populateFields() {
        if (tontine != null) {
            txtNom.setText(tontine.getNom());
            txtNombreTours.setText(String.valueOf(tontine.getNombreTours()));
            
            if (tontine.getDateDebut() != null) {
                dateDebut.setDate(java.util.Date.from(tontine.getDateDebut()
                    .atStartOfDay(java.time.ZoneId.systemDefault()).toInstant()));
            }
            
            if (tontine.getStatut() != null) {
                cmbStatut.setSelectedItem(tontine.getStatut());
            }
        }
    }
    
    private void enregistrer() {
        if (!validateForm()) return;
        
        try {
            if (tontine == null) {
                tontine = new Tontine();
            }
            
            tontine.setNom(txtNom.getText().trim());
            // Extraire l'ID du type à partir du format "ID - Nom"
            String typeSelection = (String) cmbType.getSelectedItem();
            int idType = Integer.parseInt(typeSelection.split(" - ")[0]);
            tontine.setIdType(idType);
            tontine.setDateDebut(dateDebut.getDate().toInstant()
                .atZone(java.time.ZoneId.systemDefault()).toLocalDate());
            tontine.setNombreTours(Integer.parseInt(txtNombreTours.getText().trim()));
            tontine.setTourActuel(1);
            tontine.setStatut((String) cmbStatut.getSelectedItem());
            
            boolean success = tontine.getIdTontine() == 0 ? 
                tontineDAO.create(tontine) : tontineDAO.update(tontine);
                
            if (success) {
                saved = true;
                JOptionPane.showMessageDialog(this, 
                    "Tontine enregistrée avec succès", 
                    "Succès", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Erreur lors de l'enregistrement", 
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace(); // Pour voir l'erreur dans la console
            JOptionPane.showMessageDialog(this, 
                "Erreur: " + e.getMessage() + "\nType: " + e.getClass().getSimpleName(), 
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private boolean validateForm() {
        if (txtNom.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Le nom est obligatoire", 
                "Validation", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        if (txtNombreTours.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Le nombre de tours est obligatoire", 
                "Validation", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        if (dateDebut.getDate() == null) {
            JOptionPane.showMessageDialog(this, "La date de début est obligatoire", 
                "Validation", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        if (cmbType.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Le type de tontine est obligatoire", 
                "Validation", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        return true;
    }
    
    public boolean isSaved() {
        return saved;
    }
}