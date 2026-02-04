package ui.panels;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.time.LocalDate;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.AbstractBorder;

import models.Membre;

public class MembreDialog extends JDialog {
    
    private static final long serialVersionUID = 1L;
    
    // Composants du formulaire
    private JTextField txtNom;
    private JTextField txtPrenom;
    private JTextField txtTelephone;
    private JTextField txtEmail;
    private JTextArea txtAdresse;
    private JComboBox<String> cmbStatut;
    
    // Boutons
    private JButton btnOK;
    private JButton btnCancel;
    
    // Données
    private Membre membre;
    private boolean confirmed = false;
    
    // Palette de couleurs cohérente
    private static final Color PRIMARY_PURPLE = new Color(139, 92, 246);
    private static final Color PRIMARY_BLUE = new Color(59, 130, 246);
    private static final Color TEXT_PRIMARY = new Color(15, 23, 42);
    private static final Color TEXT_SECONDARY = new Color(100, 116, 139);
    private static final Color BACKGROUND = new Color(248, 250, 252);
    private static final Color CARD_BG = new Color(255, 255, 255);
    private static final Color BORDER_COLOR = new Color(226, 232, 240);
    
    public MembreDialog(JFrame parent, String title, Membre membre) {
        super(parent, title, true);
        this.membre = membre;
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        
        if (membre != null) {
            populateFields();
        }
        
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(550, 700);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(BACKGROUND);
    }
    
    private void initializeComponents() {
        txtNom = createStyledTextField();
        txtPrenom = createStyledTextField();
        txtTelephone = createStyledTextField();
        txtEmail = createStyledTextField();
        
        txtAdresse = new JTextArea(4, 20);
        txtAdresse.setLineWrap(true);
        txtAdresse.setWrapStyleWord(true);
        txtAdresse.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtAdresse.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        
        cmbStatut = createStyledComboBox();
        cmbStatut.addItem("actif");
        cmbStatut.addItem("suspendu");
        cmbStatut.addItem("inactif");
        cmbStatut.setSelectedIndex(0);
        
        btnOK = createStyledButton("💾 Enregistrer", new Color(34, 197, 94), Color.WHITE);   // Vert avec texte blanc
        btnCancel = createStyledButton("✕ Annuler", TEXT_SECONDARY, Color.WHITE);  // Gris clair

    }
    
    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setPreferredSize(new Dimension(0, 42));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        
        // Effet focus
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(PRIMARY_PURPLE, 2),
                    BorderFactory.createEmptyBorder(7, 11, 7, 11)
                ));
            }
            public void focusLost(java.awt.event.FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER_COLOR, 1),
                    BorderFactory.createEmptyBorder(8, 12, 8, 12)
                ));
            }
        });
        
        return field;
    }
    
    private JComboBox<String> createStyledComboBox() {
        JComboBox<String> combo = new JComboBox<>();
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        combo.setBackground(CARD_BG);
        combo.setPreferredSize(new Dimension(0, 42));
        combo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        return combo;
    }
    
    private JButton createStyledButton(String text, Color bgColor, Color fgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(160, 44));
        button.setOpaque(true);
        
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
    
    private void setupLayout() {
        setLayout(new BorderLayout(0, 0));
        
        // Panneau titre
        JPanel titlePanel = createTitlePanel();
        add(titlePanel, BorderLayout.NORTH);
        
        // Panneau formulaire
        JPanel formPanel = createFormPanel();
        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBackground(BACKGROUND);
        add(scrollPane, BorderLayout.CENTER);
        
        // Panneau boutons
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createTitlePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CARD_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR),
            BorderFactory.createEmptyBorder(24, 28, 24, 28)
        ));
        
        // Icône
        JLabel iconLabel = new JLabel("👤");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        
        // Titre
        JLabel titleLabel = new JLabel(membre == null ? "Nouveau Membre" : "Modifier Membre");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 0));
        
        // Sous-titre
        JLabel subtitleLabel = new JLabel("Remplissez les informations du membre");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitleLabel.setForeground(TEXT_SECONDARY);
        subtitleLabel.setBorder(BorderFactory.createEmptyBorder(4, 12, 0, 0));
        
        // Panel gauche avec icône et textes
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(CARD_BG);
        leftPanel.add(iconLabel, BorderLayout.WEST);
        
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(CARD_BG);
        textPanel.add(titleLabel);
        textPanel.add(subtitleLabel);
        leftPanel.add(textPanel, BorderLayout.CENTER);
        
        panel.add(leftPanel, BorderLayout.WEST);
        
        return panel;
    }
    
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(28, 28, 28, 28));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1.0;
        gbc.gridx = 0;
        
        // Carte d'informations personnelles
        JPanel infoCard = createInfoCard();
        gbc.gridy = 0;
        panel.add(infoCard, gbc);
        
        // Carte de contact
        JPanel contactCard = createContactCard();
        gbc.gridy = 1;
        gbc.insets = new Insets(20, 0, 10, 0);
        panel.add(contactCard, gbc);
        
        // Carte de statut
        JPanel statusCard = createStatusCard();
        gbc.gridy = 2;
        gbc.insets = new Insets(20, 0, 10, 0);
        panel.add(statusCard, gbc);
        
        return panel;
    }
    
    private JPanel createInfoCard() {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(12, BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 0, 8, 0);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1.0;
        gbc.gridx = 0;
        
        // Titre de section
        JLabel sectionTitle = new JLabel("📋 Informations Personnelles");
        sectionTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        sectionTitle.setForeground(TEXT_PRIMARY);
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 16, 0);
        card.add(sectionTitle, gbc);
        
        // Nom
        gbc.gridy = 1;
        gbc.insets = new Insets(8, 0, 8, 0);
        card.add(createFieldLabel("Nom *", "Nom de famille du membre"), gbc);
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 16, 0);
        card.add(txtNom, gbc);
        
        // Prénom
        gbc.gridy = 3;
        gbc.insets = new Insets(8, 0, 8, 0);
        card.add(createFieldLabel("Prénom *", "Prénom(s) du membre"), gbc);
        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, 0, 0);
        card.add(txtPrenom, gbc);
        
        return card;
    }
    
    private JPanel createContactCard() {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(12, BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 0, 8, 0);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1.0;
        gbc.gridx = 0;
        
        // Titre de section
        JLabel sectionTitle = new JLabel("📞 Coordonnées");
        sectionTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        sectionTitle.setForeground(TEXT_PRIMARY);
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 16, 0);
        card.add(sectionTitle, gbc);
        
        // Téléphone
        gbc.gridy = 1;
        gbc.insets = new Insets(8, 0, 8, 0);
        card.add(createFieldLabel("Téléphone *", "Numéro de téléphone du membre"), gbc);
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 16, 0);
        card.add(txtTelephone, gbc);
        
        // Email
        gbc.gridy = 3;
        gbc.insets = new Insets(8, 0, 8, 0);
        card.add(createFieldLabel("Email", "Adresse email du membre"), gbc);
        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, 16, 0);
        card.add(txtEmail, gbc);
        
        // Adresse
        gbc.gridy = 5;
        gbc.insets = new Insets(8, 0, 8, 0);
        card.add(createFieldLabel("Adresse", "Adresse complète du membre"), gbc);
        gbc.gridy = 6;
        gbc.insets = new Insets(0, 0, 0, 0);
        JScrollPane scrollPane = new JScrollPane(txtAdresse);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setPreferredSize(new Dimension(0, 100));
        card.add(scrollPane, gbc);
        
        return card;
    }
    
    private JPanel createStatusCard() {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(12, BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 0, 8, 0);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1.0;
        gbc.gridx = 0;
        
        // Titre de section
        JLabel sectionTitle = new JLabel("⚙️ Configuration");
        sectionTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        sectionTitle.setForeground(TEXT_PRIMARY);
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 16, 0);
        card.add(sectionTitle, gbc);
        
        // Statut
        gbc.gridy = 1;
        gbc.insets = new Insets(8, 0, 8, 0);
        card.add(createFieldLabel("Statut", "Statut du membre dans le système"), gbc);
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 0, 0);
        card.add(cmbStatut, gbc);
        
        return card;
    }
    
    private JPanel createFieldLabel(String label, String description) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(CARD_BG);
        
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
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        panel.setBackground(CARD_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        panel.add(btnOK);
        panel.add(btnCancel);
        
        return panel;
    }
    
    private void setupEventHandlers() {
        btnOK.addActionListener(e -> {
            if (validateFields()) {
                saveMembre();
                confirmed = true;
                dispose();
            }
        });
        
        btnCancel.addActionListener(e -> {
            confirmed = false;
            dispose();
        });
        
        // Validation en temps réel
        txtNom.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent e) {
                validateFieldRealtime(txtNom);
            }
        });
        
        txtPrenom.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent e) {
                validateFieldRealtime(txtPrenom);
            }
        });
        
        txtTelephone.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent e) {
                validateFieldRealtime(txtTelephone);
            }
        });
    }
    
    private void validateFieldRealtime(JTextField field) {
        if (field.getText().trim().isEmpty()) {
            field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(239, 68, 68), 2),
                BorderFactory.createEmptyBorder(7, 11, 7, 11)
            ));
        } else {
            field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(16, 185, 129), 2),
                BorderFactory.createEmptyBorder(7, 11, 7, 11)
            ));
        }
    }
    
    private void populateFields() {
        if (membre != null) {
            txtNom.setText(membre.getNom());
            txtPrenom.setText(membre.getPrenom());
            txtTelephone.setText(membre.getTelephone());
            txtEmail.setText(membre.getEmail());
            txtAdresse.setText(membre.getAdresse());
            cmbStatut.setSelectedItem(membre.getStatut());
        }
    }
    
    private boolean validateFields() {
        if (txtNom.getText().trim().isEmpty()) {
            showModernError("Le nom est obligatoire!", "Veuillez saisir le nom du membre.");
            txtNom.requestFocus();
            return false;
        }
        
        if (txtPrenom.getText().trim().isEmpty()) {
            showModernError("Le prénom est obligatoire!", "Veuillez saisir le prénom du membre.");
            txtPrenom.requestFocus();
            return false;
        }
        
        if (txtTelephone.getText().trim().isEmpty()) {
            showModernError("Le téléphone est obligatoire!", "Veuillez saisir le numéro de téléphone.");
            txtTelephone.requestFocus();
            return false;
        }
        
        // Validation format email si renseigné
        String email = txtEmail.getText().trim();
        if (!email.isEmpty() && !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            showModernError("Email invalide!", "Veuillez saisir une adresse email valide.");
            txtEmail.requestFocus();
            return false;
        }
        
        return true;
    }
    
    private void showModernError(String title, String message) {
        JOptionPane optionPane = new JOptionPane(
            message,
            JOptionPane.ERROR_MESSAGE,
            JOptionPane.DEFAULT_OPTION,
            null,
            new Object[]{"OK"},
            "OK"
        );
        
        JDialog dialog = optionPane.createDialog(this, title);
        dialog.setVisible(true);
    }
    
    private void saveMembre() {
        if (membre == null) {
            membre = new Membre();
        }
        
        membre.setNom(txtNom.getText().trim());
        membre.setPrenom(txtPrenom.getText().trim());
        membre.setTelephone(txtTelephone.getText().trim());
        membre.setEmail(txtEmail.getText().trim());
        membre.setAdresse(txtAdresse.getText().trim());
        membre.setStatut((String) cmbStatut.getSelectedItem());
        
        // Si c'est un nouveau membre, définir la date d'adhésion
        if (membre.getDateAdhesion() == null) {
            membre.setDateAdhesion(LocalDate.now());
        }
    }
    
    public Membre getMembre() {
        return membre;
    }
    
    public boolean isConfirmed() {
        return confirmed;
    }
    
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