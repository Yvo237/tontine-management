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

import dao.ProjetFIACDAO;
import dao.ContributionFIACDAO;
import dao.TontineDAO;
import models.ProjetFIAC;
import models.Tontine;
import ui.MainFrame;
import utils.ThemeColors;
import utils.UIUtils;

public class ProjetsFIACPanel extends JPanel {
    private MainFrame mainFrame;
    private ProjetFIACDAO projetDAO;
    private ContributionFIACDAO contributionDAO;
    private TontineDAO tontineDAO;
    
    private JTable tableProjets;
    private DefaultTableModel tableModel;
    private JLabel lblTitre;
    private JLabel lblStatTotal;
    private JLabel lblStatEnCours;
    private JLabel lblStatTermines;
    private JComboBox<String> cmbFilterTontine;
    private JComboBox<String> cmbFilterStatut;
    
    public ProjetsFIACPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.projetDAO = new ProjetFIACDAO();
        this.contributionDAO = new ContributionFIACDAO();
        this.tontineDAO = new TontineDAO();
        initComponents();
        chargerProjets();
    }
    
    private void initComponents() {
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
        
        // Panneau tableau
        JPanel tablePanel = createTablePanel();
        bodyPanel.add(tablePanel, BorderLayout.CENTER);
        
        mainContainer.add(bodyPanel, BorderLayout.CENTER);
        
        // Panneau actions
        JPanel actionPanel = createActionPanel();
        mainContainer.add(actionPanel, BorderLayout.SOUTH);
        
        add(mainContainer, BorderLayout.CENTER);
    }
    
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
        
        JLabel iconLabel = new JLabel("🏗️");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        iconLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        lblTitre = new JLabel("Gestion des Projets FIAC");
        lblTitre.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitre.setForeground(ThemeColors.TEXT_PRIMARY);
        lblTitre.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel("Suivi des projets de développement communautaire");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(ThemeColors.TEXT_SECONDARY);
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        leftSection.add(iconLabel);
        leftSection.add(Box.createVerticalStrut(8));
        leftSection.add(lblTitre);
        leftSection.add(Box.createVerticalStrut(4));
        leftSection.add(subtitleLabel);
        
        // Section droite - Filtres
        JPanel rightSection = createFiltersPanel();
        
        innerPanel.add(leftSection, BorderLayout.WEST);
        innerPanel.add(rightSection, BorderLayout.EAST);
        
        header.add(innerPanel);
        return header;
    }
    
    private JPanel createFiltersPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(ThemeColors.CARD_BG);
        
        // Filtre par tontine
        JLabel tontineLabel = new JLabel("Tontine");
        tontineLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tontineLabel.setForeground(ThemeColors.TEXT_SECONDARY);
        tontineLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        cmbFilterTontine = new JComboBox<>();
        cmbFilterTontine.addItem("Toutes");
        chargerTontinesDansCombo();
        cmbFilterTontine.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbFilterTontine.setBackground(ThemeColors.CARD_BG);
        cmbFilterTontine.setPreferredSize(new Dimension(200, 40));
        cmbFilterTontine.setMaximumSize(new Dimension(200, 40));
        cmbFilterTontine.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeColors.BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        cmbFilterTontine.setAlignmentX(Component.LEFT_ALIGNMENT);
        cmbFilterTontine.addActionListener(e -> chargerProjets());
        
        // Filtre par statut
        JLabel statutLabel = new JLabel("Statut");
        statutLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statutLabel.setForeground(ThemeColors.TEXT_SECONDARY);
        statutLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        cmbFilterStatut = new JComboBox<>(new String[]{"Tous", "planifié", "en cours", "terminé", "annulé"});
        cmbFilterStatut.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbFilterStatut.setBackground(ThemeColors.CARD_BG);
        cmbFilterStatut.setPreferredSize(new Dimension(200, 40));
        cmbFilterStatut.setMaximumSize(new Dimension(200, 40));
        cmbFilterStatut.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeColors.BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        cmbFilterStatut.setAlignmentX(Component.LEFT_ALIGNMENT);
        cmbFilterStatut.addActionListener(e -> chargerProjets());
        
        panel.add(tontineLabel);
        panel.add(Box.createVerticalStrut(8));
        panel.add(cmbFilterTontine);
        panel.add(Box.createVerticalStrut(12));
        panel.add(statutLabel);
        panel.add(Box.createVerticalStrut(8));
        panel.add(cmbFilterStatut);
        
        return panel;
    }
    
    private void chargerTontinesDansCombo() {
        try {
            List<Tontine> tontines = tontineDAO.findAll();
            for (Tontine t : tontines) {
                cmbFilterTontine.addItem(t.getIdTontine() + " - " + t.getNom());
            }
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des tontines: " + e.getMessage());
        }
    }
    
    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 20, 0));
        panel.setBackground(ThemeColors.BACKGROUND);
        
        // Carte Total Projets
        JPanel cardTotal = createMiniStatCard("Total Projets", "0", "📊", ThemeColors.PRIMARY_PURPLE);
        lblStatTotal = (JLabel) ((JPanel)((JPanel)((JPanel)cardTotal.getComponent(0)).getComponent(1)).getComponent(0)).getComponent(0);
        
        // Carte En Cours
        JPanel cardEnCours = createMiniStatCard("En Cours", "0", "🔄", ThemeColors.PRIMARY_EMERALD);
        lblStatEnCours = (JLabel) ((JPanel)((JPanel)((JPanel)cardEnCours.getComponent(0)).getComponent(1)).getComponent(0)).getComponent(0);
        
        // Carte Terminés
        JPanel cardTermines = createMiniStatCard("Terminés", "0", "✅", ThemeColors.PRIMARY_BLUE);
        lblStatTermines = (JLabel) ((JPanel)((JPanel)((JPanel)cardTermines.getComponent(0)).getComponent(1)).getComponent(0)).getComponent(0);
        
        panel.add(cardTotal);
        panel.add(cardEnCours);
        panel.add(cardTermines);
        
        return panel;
    }
    
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
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(ThemeColors.CARD_BG);
        panel.setBorder(new UIUtils.RoundedBorder(16, ThemeColors.BORDER_COLOR, 1));
        
        JPanel innerPanel = new JPanel(new BorderLayout());
        innerPanel.setBackground(ThemeColors.CARD_BG);
        innerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Créer le tableau
        createModernTable();
        
        JScrollPane scrollPane = new JScrollPane(tableProjets);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(ThemeColors.CARD_BG);
        scrollPane.setBackground(ThemeColors.CARD_BG);
        
        innerPanel.add(scrollPane, BorderLayout.CENTER);
        panel.add(innerPanel);
        
        return panel;
    }
    
    private void createModernTable() {
        String[] columnNames = {"ID", "Nom du Projet", "Tontine", "Objectif", "Collecté", "Progression", "Statut", "Date Début"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tableProjets = new JTable(tableModel);
        tableProjets.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableProjets.setRowHeight(48);
        tableProjets.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tableProjets.setShowGrid(false);
        tableProjets.setIntercellSpacing(new Dimension(0, 0));
        tableProjets.setBackground(ThemeColors.CARD_BG);
        tableProjets.setSelectionBackground(new Color(237, 233, 254));
        tableProjets.setSelectionForeground(ThemeColors.TEXT_PRIMARY);
        
        // Style du header
        JTableHeader header = tableProjets.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBackground(new Color(248, 250, 252));
        header.setForeground(ThemeColors.TEXT_SECONDARY);
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 45));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, ThemeColors.BORDER_COLOR));
        
        // Renderer personnalisé
        tableProjets.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? ThemeColors.CARD_BG : new Color(248, 250, 252));
                }
                
                setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
                
                // Coloration de la progression
                if (column == 5 && value != null) {
                    String progression = value.toString().replace("%", "");
                    double prog = Double.parseDouble(progression);
                    if (prog >= 100) {
                        setForeground(ThemeColors.PRIMARY_EMERALD);
                    } else if (prog >= 50) {
                        setForeground(ThemeColors.PRIMARY_BLUE);
                    } else {
                        setForeground(ThemeColors.PRIMARY_AMBER);
                    }
                }
                // Coloration du statut
                else if (column == 6 && value != null) {
                    String statut = value.toString().toLowerCase();
                    switch (statut) {
                        case "en cours":
                            setForeground(ThemeColors.PRIMARY_EMERALD);
                            setText("🔄 En cours");
                            break;
                        case "terminé":
                            setForeground(ThemeColors.PRIMARY_BLUE);
                            setText("✅ Terminé");
                            break;
                        case "planifié":
                            setForeground(ThemeColors.PRIMARY_AMBER);
                            setText("📋 Planifié");
                            break;
                        case "annulé":
                            setForeground(ThemeColors.PRIMARY_RED);
                            setText("❌ Annulé");
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
        tableProjets.getColumnModel().getColumn(0).setPreferredWidth(50);
        tableProjets.getColumnModel().getColumn(1).setPreferredWidth(200);
        tableProjets.getColumnModel().getColumn(2).setPreferredWidth(150);
        tableProjets.getColumnModel().getColumn(3).setPreferredWidth(100);
        tableProjets.getColumnModel().getColumn(4).setPreferredWidth(100);
        tableProjets.getColumnModel().getColumn(5).setPreferredWidth(100);
        tableProjets.getColumnModel().getColumn(6).setPreferredWidth(120);
        tableProjets.getColumnModel().getColumn(7).setPreferredWidth(100);
    }
    
    private JPanel createActionPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        panel.setBackground(ThemeColors.BACKGROUND);
        
        JButton btnNouveau = createModernButton("➕ Nouveau Projet", ThemeColors.PRIMARY_PURPLE, new Color(237, 233, 254));
        JButton btnModifier = createModernButton("✏️ Modifier", ThemeColors.PRIMARY_BLUE, new Color(219, 234, 254));
        JButton btnContributions = createModernButton("💰 Contributions", ThemeColors.PRIMARY_EMERALD, new Color(209, 250, 229));
        JButton btnSupprimer = createModernButton("🗑️ Supprimer", ThemeColors.PRIMARY_RED, new Color(254, 226, 226));
        JButton btnRafraichir = createModernButton("🔄 Actualiser", ThemeColors.TEXT_SECONDARY, new Color(241, 245, 249));
        
        btnNouveau.addActionListener(e -> nouveauProjet());
        btnModifier.addActionListener(e -> modifierProjet());
        btnContributions.addActionListener(e -> gererContributions());
        btnSupprimer.addActionListener(e -> supprimerProjet());
        btnRafraichir.addActionListener(e -> chargerProjets());
        
        panel.add(btnNouveau);
        panel.add(btnModifier);
        panel.add(btnContributions);
        panel.add(btnSupprimer);
        panel.add(btnRafraichir);
        
        return panel;
    }
    
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
    
    private Color adjustBrightness(Color color, float factor) {
        int r = Math.min(255, (int)(color.getRed() * factor));
        int g = Math.min(255, (int)(color.getGreen() * factor));
        int b = Math.min(255, (int)(color.getBlue() * factor));
        return new Color(r, g, b);
    }
    
    private void chargerProjets() {
        try {
            tableModel.setRowCount(0);
            List<ProjetFIAC> projets = getFilteredProjets();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            
            for (ProjetFIAC p : projets) {
                Object[] row = {
                    p.getIdProjet(),
                    p.getNomProjet(),
                    p.getTontine() != null ? p.getTontine().getNom() : "N/A",
                    String.format("%.0f FCFA", p.getMontantObjectif()),
                    String.format("%.0f FCFA", p.getMontantCollecte()),
                    String.format("%.1f%%", p.getPourcentageAvancement()),
                    p.getStatut(),
                    p.getDateDebut() != null ? p.getDateDebut().format(formatter) : "N/A"
                };
                tableModel.addRow(row);
            }
            
            updateStatistics(projets);
        } catch (Exception e) {
            showErrorMessage("Erreur lors du chargement: " + e.getMessage());
        }
    }
    
    private List<ProjetFIAC> getFilteredProjets() {
        List<ProjetFIAC> projets = projetDAO.findAll();
        
        // Filtrer par tontine
        String tontineFilter = (String) cmbFilterTontine.getSelectedItem();
        if (tontineFilter != null && !tontineFilter.equals("Toutes")) {
            int idTontine = Integer.parseInt(tontineFilter.split(" - ")[0]);
            projets.removeIf(p -> p.getIdTontine() != idTontine);
        }
        
        // Filtrer par statut
        String statutFilter = (String) cmbFilterStatut.getSelectedItem();
        if (statutFilter != null && !statutFilter.equals("Tous")) {
            projets.removeIf(p -> !p.getStatut().equals(statutFilter));
        }
        
        return projets;
    }
    
    private void updateStatistics(List<ProjetFIAC> projets) {
        int total = projets.size();
        int enCours = 0;
        int termines = 0;
        
        for (ProjetFIAC p : projets) {
            String statut = p.getStatut();
            if ("en_cours".equals(statut)) enCours++;
            else if ("termine".equals(statut)) termines++;
        }
        
        if (lblStatTotal != null) lblStatTotal.setText(String.valueOf(total));
        if (lblStatEnCours != null) lblStatEnCours.setText(String.valueOf(enCours));
        if (lblStatTermines != null) lblStatTermines.setText(String.valueOf(termines));
    }
    
    private void nouveauProjet() {
        ProjetFIACDialog dialog = new ProjetFIACDialog(mainFrame, null);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            chargerProjets();
        }
    }
    
    private void modifierProjet() {
        int selectedRow = tableProjets.getSelectedRow();
        if (selectedRow == -1) {
            showWarningMessage("Veuillez sélectionner un projet");
            return;
        }
        
        try {
            int id = (int) tableModel.getValueAt(selectedRow, 0);
            ProjetFIAC projet = projetDAO.findById(id);
            if (projet != null) {
                ProjetFIACDialog dialog = new ProjetFIACDialog(mainFrame, projet);
                dialog.setVisible(true);
                if (dialog.isSaved()) {
                    chargerProjets();
                }
            } else {
                showErrorMessage("Projet introuvable");
            }
        } catch (Exception e) {
            showErrorMessage("Erreur lors de la modification: " + e.getMessage());
        }
    }
    
    private void supprimerProjet() {
        int selectedRow = tableProjets.getSelectedRow();
        if (selectedRow == -1) {
            showWarningMessage("Veuillez sélectionner un projet");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "⚠️ ATTENTION !\n\n" +
            "Êtes-vous sûr de vouloir supprimer ce projet ?\n\n" +
            "Cela supprimera également toutes les contributions associées.\n" +
            "Cette action est IRRÉVERSIBLE !",
            "Confirmation de suppression",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
            
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int id = (int) tableModel.getValueAt(selectedRow, 0);
                if (projetDAO.delete(id)) {
                    showSuccessMessage("Projet supprimé avec succès");
                    chargerProjets();
                } else {
                    showErrorMessage("Erreur lors de la suppression");
                }
            } catch (Exception e) {
                showErrorMessage("Erreur lors de la suppression: " + e.getMessage());
            }
        }
    }
    
    private void gererContributions() {
        int selectedRow = tableProjets.getSelectedRow();
        if (selectedRow == -1) {
            showWarningMessage("Veuillez sélectionner un projet");
            return;
        }
        
        try {
            int id = (int) tableModel.getValueAt(selectedRow, 0);
            ProjetFIAC projet = projetDAO.findById(id);
            
            if (projet != null) {
                ContributionsDialog dialog = new ContributionsDialog(mainFrame, projet);
                dialog.setVisible(true);
                if (dialog.isSaved()) {
                    chargerProjets();
                }
            } else {
                showErrorMessage("Projet introuvable");
            }
        } catch (Exception e) {
            showErrorMessage("Erreur: " + e.getMessage());
        }
    }
    
    private void showWarningMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Attention", JOptionPane.WARNING_MESSAGE);
    }
    
    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Erreur", JOptionPane.ERROR_MESSAGE);
    }
    
    private void showSuccessMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Succès", JOptionPane.INFORMATION_MESSAGE);
    }
    
    public void rafraichir() {
        chargerProjets();
    }
}
