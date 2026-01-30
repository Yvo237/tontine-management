package ui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import dao.MembreDAO;
import models.Membre;
import ui.MainFrame;
import utils.ThemeColors;
import utils.UIUtils;

/**
 * Panneau de gestion des membres avec design ultra-moderne
 * Design 3.0 - Interface Premium cohérente
 * Projet INF2212 - Université de Yaoundé I
 */
public class MembresPanel extends JPanel {
    
    private static final long serialVersionUID = 1L;
    
    // Référence à la fenêtre principale
    private MainFrame mainFrame;
    
    // DAO
    private MembreDAO membreDAO;
    
    // Composants
    private JTable tblMembres;
    private DefaultTableModel tableModel;
    private JTextField txtRecherche;
    private JButton btnNouveau;
    private JButton btnModifier;
    private JButton btnSupprimer;
    private JButton btnRafraichir;
    private JLabel lblCount;
    private JLabel lblStatActifs;
    private JLabel lblStatSuspendus;
    private JLabel lblStatInactifs;
    
    // Données
    private List<Membre> membresList;
    
    
    public MembresPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.membreDAO = new MembreDAO();
        initializeComponents();
        setupEventHandlers();
        loadMembres();
        SwingUtilities.invokeLater(() -> updateMemberCount());
    }
    
    /**
     * Initialise tous les composants avec design ultra-moderne
     */
    private void initializeComponents() {
        setLayout(new BorderLayout(0, 0));
        setBackground(ThemeColors.BACKGROUND);
        
        // Container principal
        JPanel mainContainer = new JPanel(new BorderLayout(0, 24));
        mainContainer.setBackground(ThemeColors.BACKGROUND);
        mainContainer.setBorder(BorderFactory.createEmptyBorder(32, 32, 32, 32));
        
        // En-tête moderne
        JPanel headerPanel = createModernHeader();
        mainContainer.add(headerPanel, BorderLayout.NORTH);
        
        // Corps avec statistiques et tableau
        JPanel bodyPanel = new JPanel(new BorderLayout(0, 24));
        bodyPanel.setBackground(ThemeColors.BACKGROUND);
        
        // Statistiques rapides
        JPanel statsPanel = createStatsPanel();
        bodyPanel.add(statsPanel, BorderLayout.NORTH);
        
        // Panneau recherche et tableau
        JPanel contentPanel = createContentPanel();
        bodyPanel.add(contentPanel, BorderLayout.CENTER);
        
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
        header.setBackground(ThemeColors.CARD_BG);
        header.setBorder(new UIUtils.RoundedBorder(20, ThemeColors.BORDER_COLOR, 1));
        
        JPanel innerPanel = new JPanel(new BorderLayout());
        innerPanel.setBackground(ThemeColors.CARD_BG);
        innerPanel.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));
        
        // Section gauche - Titre
        JPanel leftSection = new JPanel();
        leftSection.setLayout(new BoxLayout(leftSection, BoxLayout.Y_AXIS));
        leftSection.setBackground(ThemeColors.CARD_BG);
        
        JLabel iconLabel = new JLabel("👥");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        iconLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel titleLabel = new JLabel("Gestion des Membres");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(ThemeColors.TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel("Gérer les adhérents et leurs informations");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(ThemeColors.TEXT_SECONDARY);
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        leftSection.add(iconLabel);
        leftSection.add(Box.createVerticalStrut(8));
        leftSection.add(titleLabel);
        leftSection.add(Box.createVerticalStrut(4));
        leftSection.add(subtitleLabel);
        
        // Section droite - Compteur
        JPanel rightSection = new JPanel();
        rightSection.setLayout(new BoxLayout(rightSection, BoxLayout.Y_AXIS));
        rightSection.setBackground(ThemeColors.CARD_BG);
        
        lblCount = new JLabel("0");
        lblCount.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblCount.setForeground(ThemeColors.PRIMARY_PURPLE);
        lblCount.setAlignmentX(Component.RIGHT_ALIGNMENT);
        
        JLabel countLabel = new JLabel("Total Membres");
        countLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        countLabel.setForeground(ThemeColors.TEXT_SECONDARY);
        countLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        
        rightSection.add(lblCount);
        rightSection.add(Box.createVerticalStrut(4));
        rightSection.add(countLabel);
        
        innerPanel.add(leftSection, BorderLayout.WEST);
        innerPanel.add(rightSection, BorderLayout.EAST);
        
        header.add(innerPanel);
        return header;
    }
    
    /**
     * Crée le panneau de statistiques rapides
     */
    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 20, 0));
        panel.setBackground(ThemeColors.BACKGROUND);
        
        // Carte Membres Actifs
        JPanel cardActifs = createMiniStatCard("Membres Actifs", "0", "✅", ThemeColors.PRIMARY_EMERALD);
        lblStatActifs = (JLabel) ((JPanel)((JPanel)((JPanel)cardActifs.getComponent(0)).getComponent(1)).getComponent(0)).getComponent(0);
        
        // Carte Membres Suspendus
        JPanel cardSuspendus = createMiniStatCard("Suspendus", "0", "⏸️", ThemeColors.PRIMARY_AMBER);
        lblStatSuspendus = (JLabel) ((JPanel)((JPanel)((JPanel)cardSuspendus.getComponent(0)).getComponent(1)).getComponent(0)).getComponent(0);
        
        // Carte Membres Inactifs
        JPanel cardInactifs = createMiniStatCard("Inactifs", "0", "🔴", ThemeColors.PRIMARY_RED);
        lblStatInactifs = (JLabel) ((JPanel)((JPanel)((JPanel)cardInactifs.getComponent(0)).getComponent(1)).getComponent(0)).getComponent(0);
        
        panel.add(cardActifs);
        panel.add(cardSuspendus);
        panel.add(cardInactifs);
        
        return panel;
    }
    
    /**
     * Crée une mini carte de statistique
     */
    private JPanel createMiniStatCard(String titre, String valeur, String icone, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(ThemeColors.CARD_BG);
        card.setBorder(new UIUtils.RoundedBorder(12, ThemeColors.BORDER_COLOR, 1));
        
        JPanel innerPanel = new JPanel(new BorderLayout());
        innerPanel.setBackground(ThemeColors.CARD_BG);
        innerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(ThemeColors.CARD_BG);
        
        JLabel iconLabel = new JLabel(icone);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        iconLabel.setForeground(color);
        
        JPanel valuePanel = new JPanel();
        valuePanel.setLayout(new BoxLayout(valuePanel, BoxLayout.Y_AXIS));
        valuePanel.setBackground(ThemeColors.CARD_BG);
        
        JLabel valueLabel = new JLabel(valeur);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valueLabel.setForeground(ThemeColors.TEXT_PRIMARY);
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel titleLabel = new JLabel(titre);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        titleLabel.setForeground(ThemeColors.TEXT_SECONDARY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        valuePanel.add(valueLabel);
        valuePanel.add(Box.createVerticalStrut(4));
        valuePanel.add(titleLabel);
        
        contentPanel.add(iconLabel, BorderLayout.WEST);
        contentPanel.add(Box.createHorizontalStrut(12), BorderLayout.CENTER);
        
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(ThemeColors.CARD_BG);
        rightPanel.add(valuePanel, BorderLayout.CENTER);
        
        innerPanel.add(contentPanel, BorderLayout.WEST);
        innerPanel.add(rightPanel, BorderLayout.CENTER);
        
        card.add(innerPanel);
        return card;
    }
    
    /**
     * Crée le panneau de contenu avec recherche et tableau
     */
    private JPanel createContentPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(ThemeColors.CARD_BG);
        panel.setBorder(new UIUtils.RoundedBorder(16, ThemeColors.BORDER_COLOR, 1));
        
        JPanel innerPanel = new JPanel(new BorderLayout(0, 20));
        innerPanel.setBackground(ThemeColors.CARD_BG);
        innerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Panneau de recherche moderne
        JPanel searchPanel = createSearchPanel();
        innerPanel.add(searchPanel, BorderLayout.NORTH);
        
        // Tableau moderne
        createModernTable();
        JScrollPane scrollPane = new JScrollPane(tblMembres);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(ThemeColors.CARD_BG);
        scrollPane.setBackground(ThemeColors.CARD_BG);
        
        innerPanel.add(scrollPane, BorderLayout.CENTER);
        panel.add(innerPanel);
        
        return panel;
    }
    
    /**
     * Crée le panneau de recherche moderne
     */
    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new BorderLayout(12, 0));
        panel.setBackground(ThemeColors.CARD_BG);
        
        JLabel searchIcon = new JLabel("🔍");
        searchIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        
        txtRecherche = new JTextField();
        txtRecherche.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtRecherche.setPreferredSize(new Dimension(0, 42));
        txtRecherche.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeColors.BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        
        // Placeholder
        txtRecherche.setForeground(ThemeColors.TEXT_SECONDARY);
        txtRecherche.setText("Rechercher un membre par nom, prénom, téléphone...");
        
        txtRecherche.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                if (txtRecherche.getText().equals("Rechercher un membre par nom, prénom, téléphone...")) {
                    txtRecherche.setText("");
                    txtRecherche.setForeground(ThemeColors.TEXT_PRIMARY);
                }
                txtRecherche.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(ThemeColors.PRIMARY_PURPLE, 2),
                    BorderFactory.createEmptyBorder(7, 11, 7, 11)
                ));
            }
            public void focusLost(java.awt.event.FocusEvent e) {
                if (txtRecherche.getText().isEmpty()) {
                    txtRecherche.setForeground(ThemeColors.TEXT_SECONDARY);
                    txtRecherche.setText("Rechercher un membre par nom, prénom, téléphone...");
                }
                txtRecherche.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(ThemeColors.BORDER_COLOR, 1),
                    BorderFactory.createEmptyBorder(8, 12, 8, 12)
                ));
            }
        });
        
        panel.add(searchIcon, BorderLayout.WEST);
        panel.add(txtRecherche, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Crée le tableau moderne
     */
    private void createModernTable() {
        String[] columns = {"ID", "Nom", "Prénom", "Téléphone", "Email", "Statut"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tblMembres = new JTable(tableModel);
        tblMembres.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblMembres.setRowHeight(48);
        tblMembres.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tblMembres.setShowGrid(false);
        tblMembres.setIntercellSpacing(new Dimension(0, 0));
        tblMembres.setBackground(ThemeColors.CARD_BG);
        tblMembres.setSelectionBackground(new Color(237, 233, 254));
        tblMembres.setSelectionForeground(ThemeColors.TEXT_PRIMARY);
        
        // Style du header
        JTableHeader header = tblMembres.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBackground(new Color(248, 250, 252));
        header.setForeground(ThemeColors.TEXT_SECONDARY);
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 45));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, ThemeColors.BORDER_COLOR));
        
        // Renderer personnalisé
        tblMembres.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? ThemeColors.CARD_BG : new Color(248, 250, 252));
                }
                
                setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
                
                // Coloration du statut
                if (column == 5 && value != null) {
                    String statut = value.toString();
                    switch (statut.toLowerCase()) {
                        case "actif":
                            setForeground(ThemeColors.PRIMARY_EMERALD);
                            setText("✓ Actif");
                            break;
                        case "suspendu":
                            setForeground(ThemeColors.PRIMARY_AMBER);
                            setText("⏸ Suspendu");
                            break;
                        case "inactif":
                            setForeground(ThemeColors.PRIMARY_RED);
                            setText("○ Inactif");
                            break;
                        default:
                            setForeground(ThemeColors.TEXT_PRIMARY);
                    }
                } else if (!isSelected) {
                    setForeground(ThemeColors.TEXT_PRIMARY);
                }
                
                return c;
            }
        });
        
        // Largeurs des colonnes
        tblMembres.getColumnModel().getColumn(0).setPreferredWidth(60);
        tblMembres.getColumnModel().getColumn(1).setPreferredWidth(150);
        tblMembres.getColumnModel().getColumn(2).setPreferredWidth(150);
        tblMembres.getColumnModel().getColumn(3).setPreferredWidth(130);
        tblMembres.getColumnModel().getColumn(4).setPreferredWidth(200);
        tblMembres.getColumnModel().getColumn(5).setPreferredWidth(110);
    }
    
    /**
     * Crée le panneau d'actions
     */
    private JPanel createActionPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        panel.setBackground(ThemeColors.BACKGROUND);
        
        btnNouveau = createModernButton("➕ Nouveau Membre", ThemeColors.PRIMARY_PURPLE, new Color(237, 233, 254));
        btnModifier = createModernButton("✏️ Modifier", ThemeColors.PRIMARY_BLUE, new Color(219, 234, 254));
        btnSupprimer = createModernButton("🗑️ Supprimer", ThemeColors.PRIMARY_RED, new Color(254, 226, 226));
        btnRafraichir = createModernButton("🔄 Actualiser", ThemeColors.TEXT_SECONDARY, new Color(241, 245, 249));
        
        btnModifier.setEnabled(false);
        btnSupprimer.setEnabled(false);
        
        panel.add(btnNouveau);
        panel.add(btnModifier);
        panel.add(btnSupprimer);
        panel.add(btnRafraichir);
        
        return panel;
    }
    
    /**
     * Crée un bouton moderne
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
        
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (button.isEnabled()) {
                    button.setBackground(adjustBrightness(bgColor, 0.95f));
                }
            }
            public void mouseExited(MouseEvent e) {
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
     * Configure les gestionnaires d'événements
     */
    private void setupEventHandlers() {
        // Recherche
        txtRecherche.addActionListener(e -> {
            if (!txtRecherche.getText().equals("Rechercher un membre par nom, prénom, téléphone...")) {
                rechercherMembres();
            }
        });
        
        // Recherche en temps réel
        txtRecherche.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent e) {
                if (!txtRecherche.getText().equals("Rechercher un membre par nom, prénom, téléphone...")) {
                    rechercherMembres();
                }
            }
        });
        
        // Boutons
        btnNouveau.addActionListener(e -> ajouterMembre());
        btnModifier.addActionListener(e -> modifierMembre());
        btnSupprimer.addActionListener(e -> supprimerMembre());
        btnRafraichir.addActionListener(e -> loadMembres());
        
        // Sélection dans le tableau
        tblMembres.getSelectionModel().addListSelectionListener(e -> {
            boolean selection = !tblMembres.getSelectionModel().isSelectionEmpty();
            btnModifier.setEnabled(selection);
            btnSupprimer.setEnabled(selection);
        });
        
        // Double-clic pour modifier
        tblMembres.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && tblMembres.getSelectedRow() != -1) {
                    modifierMembre();
                }
            }
        });
    }
    
    /**
     * Charge tous les membres
     */
    private void loadMembres() {
        try {
            membresList = membreDAO.findAll();
            updateTable();
            updateMemberCount();
            updateStatistics();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Erreur lors du chargement des membres: " + e.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Met à jour le tableau
     */
    private void updateTable() {
        tableModel.setRowCount(0);
        
        for (Membre membre : membresList) {
            Object[] row = {
                membre.getIdMembre(),
                membre.getNom(),
                membre.getPrenom(),
                membre.getTelephone(),
                membre.getEmail() != null ? membre.getEmail() : "",
                membre.getStatut()
            };
            tableModel.addRow(row);
        }
    }
    
    /**
     * Recherche des membres
     */
    private void rechercherMembres() {
        String critere = txtRecherche.getText().trim();
        if (critere.isEmpty() || critere.equals("Rechercher un membre par nom, prénom, téléphone...")) {
            loadMembres();
            return;
        }
        
        try {
            membresList = membreDAO.search(critere);
            updateTable();
            updateMemberCount();
            updateStatistics();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Erreur lors de la recherche: " + e.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Ajoute un nouveau membre
     */
    private void ajouterMembre() {
        MembreDialog dialog = new MembreDialog(
            (JFrame) SwingUtilities.getWindowAncestor(this), 
            "Nouveau Membre", 
            null
        );
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            Membre nouveauMembre = dialog.getMembre();
            if (nouveauMembre != null && membreDAO.create(nouveauMembre)) {
                loadMembres();
                // Rafraîchir le panneau d'accueil
                mainFrame.getAccueilPanel().rafraichir();
                JOptionPane.showMessageDialog(this,
                    "Membre ajouté avec succès!",
                    "Succès",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
    
    /**
     * Modifie le membre sélectionné
     */
    private void modifierMembre() {
        int selectedRow = tblMembres.getSelectedRow();
        if (selectedRow == -1) return;
        
        int modelRow = tblMembres.convertRowIndexToModel(selectedRow);
        if (modelRow < 0 || modelRow >= membresList.size()) return;
        
        Membre membre = membresList.get(modelRow);
        MembreDialog dialog = new MembreDialog(
            (JFrame) SwingUtilities.getWindowAncestor(this), 
            "Modifier Membre", 
            membre
        );
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            Membre membreModifie = dialog.getMembre();
            if (membreModifie != null && membreDAO.update(membreModifie)) {
                loadMembres();
                // Rafraîchir le panneau d'accueil
                mainFrame.getAccueilPanel().rafraichir();
                JOptionPane.showMessageDialog(this,
                    "Membre modifié avec succès!",
                    "Succès",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
    
    /**
     * Supprime le membre sélectionné
     */
    private void supprimerMembre() {
        int selectedRow = tblMembres.getSelectedRow();
        if (selectedRow == -1) return;
        
        int modelRow = tblMembres.convertRowIndexToModel(selectedRow);
        if (modelRow < 0 || modelRow >= membresList.size()) return;
        
        Membre membre = membresList.get(modelRow);
        
        int option = JOptionPane.showConfirmDialog(
            this,
            "Voulez-vous vraiment supprimer le membre:\n" + membre.getNomComplet() + " ?",
            "Confirmation de Suppression",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (option == JOptionPane.YES_OPTION) {
            if (membreDAO.delete(membre.getIdMembre())) {
                loadMembres();
                // Rafraîchir le panneau d'accueil
                mainFrame.getAccueilPanel().rafraichir();
                JOptionPane.showMessageDialog(this,
                    "Membre supprimé avec succès!",
                    "Succès",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
    
    /**
     * Met à jour le compteur de membres
     */
    private void updateMemberCount() {
        if (lblCount != null && membresList != null) {
            lblCount.setText(String.valueOf(membresList.size()));
        }
    }
    
    /**
     * Met à jour les statistiques
     */
    private void updateStatistics() {
        if (membresList == null) return;
        
        int actifs = 0, suspendus = 0, inactifs = 0;
        
        for (Membre m : membresList) {
            String statut = m.getStatut().toLowerCase();
            switch (statut) {
                case "actif": actifs++; break;
                case "suspendu": suspendus++; break;
                case "inactif": inactifs++; break;
            }
        }
        
        if (lblStatActifs != null) {
            lblStatActifs.setText(String.valueOf(actifs));
        }
        if (lblStatSuspendus != null) {
            lblStatSuspendus.setText(String.valueOf(suspendus));
        }
        if (lblStatInactifs != null) {
            lblStatInactifs.setText(String.valueOf(inactifs));
        }
    }
    
    /**
     * Donne le focus au champ de recherche
     */
    public void focusRecherche() {
        txtRecherche.requestFocus();
        if (!txtRecherche.getText().equals("Rechercher un membre par nom, prénom, téléphone...")) {
            txtRecherche.selectAll();
        }
    }
    
    /**
     * Rafraîchit le panneau
     */
    public void rafraichir() {
        loadMembres();
    }
    
}