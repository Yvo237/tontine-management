package ui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GradientPaint;
import java.awt.RenderingHints;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import dao.CotisationDAO;
import dao.MembreDAO;
import models.Cotisation;
import models.Membre;
import models.Seance;

public class CotisationsDialog extends JDialog {
    private static final long serialVersionUID = 1L;
    
    private Seance seance;
    private CotisationDAO cotisationDAO;
    private MembreDAO membreDAO;
    private boolean saved = false;
    
    private JTable tableCotisations;
    private DefaultTableModel tableModel;
    private JLabel lblTitre;
    private JLabel lblTotal;
    private JButton btnAjouter;
    private JButton btnModifier;
    private JButton btnSupprimer;
    private JButton btnFermer;
    
    // Palette de couleurs cohérente
    private static final Color PRIMARY_DARK = new Color(15, 23, 42);
    private static final Color PRIMARY_BLUE = new Color(59, 130, 246);
    private static final Color PRIMARY_EMERALD = new Color(16, 185, 129);
    private static final Color PRIMARY_RED = new Color(239, 68, 68);
    private static final Color BACKGROUND = new Color(241, 245, 249);
    private static final Color CARD_BG = new Color(255, 255, 255);
    private static final Color TEXT_PRIMARY = new Color(15, 23, 42);
    private static final Color TEXT_SECONDARY = new Color(100, 116, 139);
    private static final Color BORDER_COLOR = new Color(226, 232, 240);
    
    public CotisationsDialog(java.awt.Frame parent, Seance seance, CotisationDAO cotisationDAO) {
        super(parent, "Gestion des Cotisations - " + seance.getDateSeance(), true);
        this.seance = seance;
        this.cotisationDAO = cotisationDAO;
        this.membreDAO = new MembreDAO();
        
        initComponents();
        loadCotisations();
        setupDialog();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        
        // Panel principal avec design moderne
        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Fond dégradé subtil
                GradientPaint gradient = new GradientPaint(0, 0, BACKGROUND, 0, getHeight(), CARD_BG);
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                g2d.dispose();
            }
        };
        
        // Header
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Panel table
        JPanel tablePanel = createTablePanel();
        mainPanel.add(tablePanel, BorderLayout.CENTER);
        
        // Panel boutons
        JPanel buttonPanel = createButtonPanel();
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        
        lblTitre = new JLabel("Cotisations de la séance du " + seance.getDateSeance());
        lblTitre.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitre.setForeground(TEXT_PRIMARY);
        
        lblTotal = new JLabel("Total: 0 FCFA");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTotal.setForeground(PRIMARY_EMERALD);
        lblTotal.setHorizontalAlignment(JLabel.RIGHT);
        
        headerPanel.add(lblTitre, BorderLayout.WEST);
        headerPanel.add(lblTotal, BorderLayout.EAST);
        
        return headerPanel;
    }
    
    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setOpaque(false);
        tablePanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        // Création du tableau
        String[] columns = {"ID", "Membre", "Montant", "Date", "Statut"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tableCotisations = new JTable(tableModel);
        tableCotisations.setRowHeight(35);
        tableCotisations.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tableCotisations.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tableCotisations.setShowGrid(false);
        tableCotisations.setIntercellSpacing(new Dimension(0, 0));
        
        // Style du header
        tableCotisations.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tableCotisations.getTableHeader().setBackground(PRIMARY_DARK);
        tableCotisations.getTableHeader().setForeground(Color.WHITE);
        tableCotisations.getTableHeader().setPreferredSize(new Dimension(0, 40));
        
        // Renderer personnalisé
        tableCotisations.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (c instanceof JLabel) {
                    JLabel label = (JLabel) c;
                    label.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                    label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                    
                    if (isSelected) {
                        label.setBackground(PRIMARY_BLUE);
                        label.setForeground(Color.WHITE);
                        label.setOpaque(true);
                    } else {
                        label.setOpaque(false);
                        label.setForeground(TEXT_PRIMARY);
                    }
                }
                
                return c;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(tableCotisations);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setOpaque(false);
        
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        return tablePanel;
    }
    
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        
        btnAjouter = createModernButton("Ajouter", PRIMARY_EMERALD);
        btnModifier = createModernButton("Modifier", PRIMARY_BLUE);
        btnSupprimer = createModernButton("Supprimer", PRIMARY_RED);
        btnFermer = createModernButton("Fermer", TEXT_SECONDARY);
        
        btnAjouter.addActionListener(e -> ajouterCotisation());
        btnModifier.addActionListener(e -> modifierCotisation());
        btnSupprimer.addActionListener(e -> supprimerCotisation());
        btnFermer.addActionListener(e -> dispose());
        
        buttonPanel.add(btnAjouter);
        buttonPanel.add(btnModifier);
        buttonPanel.add(btnSupprimer);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(btnFermer);
        
        return buttonPanel;
    }
    
    private JButton createModernButton(String text, Color bgColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Fond arrondi
                g2d.setColor(getModel().isPressed() ? bgColor.darker() : 
                             getModel().isRollover() ? bgColor.brighter() : bgColor);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                
                // Texte
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 12));
                g2d.getFontMetrics();
                int x = (getWidth() - g2d.getFontMetrics().stringWidth(getText())) / 2;
                int y = (getHeight() + g2d.getFontMetrics().getAscent()) / 2;
                g2d.drawString(getText(), x, y);
                
                g2d.dispose();
            }
            
            @Override
            public void setContentAreaFilled(boolean b) {
                // Désactiver le remplissage par défaut
            }
            
            @Override
            public void setBorderPainted(boolean b) {
                // Désactiver la bordure par défaut
            }
        };
        
        button.setPreferredSize(new Dimension(100, 35));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setFocusPainted(false);
        
        return button;
    }
    
    private void loadCotisations() {
        try {
            List<Cotisation> cotisations = cotisationDAO.findBySeance(seance.getIdSeance());
            tableModel.setRowCount(0);
            
            double total = 0;
            for (Cotisation cotisation : cotisations) {
                Object[] row = {
                    cotisation.getIdCotisation(),
                    cotisation.getNomMembre(),
                    cotisation.getMontant() + " FCFA",
                    cotisation.getDatePaiement(),
                    "Payée" // Statut par défaut
                };
                tableModel.addRow(row);
                total += cotisation.getMontant().doubleValue();
            }
            
            lblTotal.setText("Total: " + String.format("%.0f", total) + " FCFA");
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des cotisations: " + e.getMessage(), 
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void ajouterCotisation() {
        try {
            // Demander le montant
            String montantStr = JOptionPane.showInputDialog(this, 
                "Montant de la cotisation (FCFA):", 
                "Ajouter une cotisation", 
                JOptionPane.QUESTION_MESSAGE);
            
            if (montantStr == null || montantStr.trim().isEmpty()) {
                return;
            }
            
            double montant = Double.parseDouble(montantStr.trim());
            if (montant <= 0) {
                JOptionPane.showMessageDialog(this, 
                    "Le montant doit être supérieur à 0", 
                    "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Demander le membre
            List<Membre> membres = membreDAO.findAll();
            if (membres.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Aucun membre disponible", 
                    "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            String[] nomsMembres = membres.stream()
                .map(Membre::getNomComplet)
                .toArray(String[]::new);
            
            String nomMembre = (String) JOptionPane.showInputDialog(this,
                "Sélectionnez un membre:",
                "Sélection du membre",
                JOptionPane.QUESTION_MESSAGE,
                null,
                nomsMembres,
                nomsMembres[0]);
            
            if (nomMembre == null) {
                return;
            }
            
            // Trouver le membre sélectionné
            Membre membreSelectionne = membres.stream()
                .filter(m -> m.getNomComplet().equals(nomMembre))
                .findFirst()
                .orElse(null);
            
            if (membreSelectionne != null) {
                // Créer la cotisation
                Cotisation cotisation = new Cotisation();
                cotisation.setIdSeance(seance.getIdSeance());
                cotisation.setIdMembre(membreSelectionne.getIdMembre());
                cotisation.setMontant(java.math.BigDecimal.valueOf(montant));
                cotisation.setDatePaiement(java.time.LocalDate.now());
                
                if (cotisationDAO.create(cotisation)) {
                    loadCotisations();
                    JOptionPane.showMessageDialog(this, 
                        "Cotisation ajoutée avec succès", 
                        "Succès", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Erreur lors de l'ajout de la cotisation", 
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "Montant invalide", 
                "Erreur", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Erreur: " + e.getMessage(), 
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void modifierCotisation() {
        int selectedRow = tableCotisations.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner une cotisation à modifier", 
                "Attention", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            int id = (int) tableModel.getValueAt(selectedRow, 0);
            Cotisation cotisation = cotisationDAO.findById(id);
            
            if (cotisation != null) {
                // Demander le nouveau montant
                String nouveauMontantStr = JOptionPane.showInputDialog(this, 
                    "Nouveau montant (FCFA):", 
                    cotisation.getMontant().toString());
                
                if (nouveauMontantStr != null && !nouveauMontantStr.trim().isEmpty()) {
                    double nouveauMontant = Double.parseDouble(nouveauMontantStr.trim());
                    if (nouveauMontant > 0) {
                        cotisation.setMontant(java.math.BigDecimal.valueOf(nouveauMontant));
                        
                        if (cotisationDAO.update(cotisation)) {
                            loadCotisations();
                            JOptionPane.showMessageDialog(this, 
                                "Cotisation modifiée avec succès", 
                                "Succès", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(this, 
                                "Erreur lors de la modification", 
                                "Erreur", JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, 
                            "Le montant doit être supérieur à 0", 
                            "Erreur", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Cotisation introuvable", 
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "Montant invalide", 
                "Erreur", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Erreur: " + e.getMessage(), 
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void supprimerCotisation() {
        int selectedRow = tableCotisations.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner une cotisation à supprimer", 
                "Attention", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Êtes-vous sûr de vouloir supprimer cette cotisation ?", 
            "Confirmation", JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int id = (int) tableModel.getValueAt(selectedRow, 0);
                cotisationDAO.delete(id);
                loadCotisations();
                JOptionPane.showMessageDialog(this, "Cotisation supprimée avec succès", 
                    "Succès", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erreur lors de la suppression: " + e.getMessage(), 
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void setupDialog() {
        setSize(800, 600);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(true);
    }
    
    public boolean isSaved() {
        return saved;
    }
}
