package ui.panels;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import dao.ParticipationDAO;
import dao.MembreDAO;
import models.Tontine;
import models.Participation;
import models.Membre;
import javax.swing.JComboBox;
import javax.swing.JTextField;

/**
 * Dialogue pour gérer les participants d'une tontine
 */
public class ParticipantsDialog extends JDialog {
    private static final long serialVersionUID = 1L;
    
    private final Tontine tontine;
    private final ParticipationDAO participationDAO;
    private final MembreDAO membreDAO;
    
    private JTable tableParticipants;
    private DefaultTableModel tableModel;
    private JButton btnAjouter;
    private JButton btnSupprimer;
    private JButton btnFermer;
    
    public ParticipantsDialog(java.awt.Frame parent, Tontine tontine, ParticipationDAO participationDAO) {
        super(parent, "Gestion des participants - " + tontine.getNom(), true);
        this.tontine = tontine;
        this.participationDAO = participationDAO;
        this.membreDAO = new MembreDAO();
        
        initializeComponents();
        layoutComponents();
        loadParticipants();
        
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(parent);
    }
    
    private void initializeComponents() {
        // Table model
        String[] columns = {"ID", "Membre", "Date d'inscription", "Statut"};
        tableModel = new DefaultTableModel(columns, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tableParticipants = new JTable(tableModel);
        tableParticipants.getTableHeader().setReorderingAllowed(false);
        tableParticipants.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        
        // Buttons
        btnAjouter = new JButton("Ajouter participant");
        btnSupprimer = new JButton("Supprimer participant");
        btnFermer = new JButton("Fermer");
        
        // Button actions
        btnAjouter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ajouterParticipant();
            }
        });
        
        btnSupprimer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                supprimerParticipant();
            }
        });
        
        btnFermer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }
    
    private void layoutComponents() {
        setLayout(new BorderLayout());
        
        // Title
        JLabel titleLabel = new JLabel("Participants de la tontine: " + tontine.getNom());
        titleLabel.setFont(titleLabel.getFont().deriveFont(16f));
        titleLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(titleLabel, BorderLayout.NORTH);
        
        // Table
        JScrollPane scrollPane = new JScrollPane(tableParticipants);
        add(scrollPane, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(btnAjouter);
        buttonPanel.add(btnSupprimer);
        buttonPanel.add(btnFermer);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void loadParticipants() {
        try {
            tableModel.setRowCount(0);
            List<Participation> participations = participationDAO.findByTontine(tontine.getIdTontine());
            
            for (Participation participation : participations) {
                Object[] row = {
                    participation.getIdParticipation(),
                    participation.getNomMembre(),
                    participation.getDateInscription(),
                    participation.getStatut()
                };
                tableModel.addRow(row);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Erreur lors du chargement des participants: " + e.getMessage(),
                "Erreur", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void ajouterParticipant() {
        // Créer un dialogue pour sélectionner un membre
        JDialog dialogAjout = new JDialog(this, "Ajouter un participant", true);
        dialogAjout.setLayout(new BorderLayout());
        
        // Panel pour le formulaire
        JPanel panelForm = new JPanel(new java.awt.GridLayout(3, 2, 5, 5));
        
        // ComboBox pour sélectionner un membre
        JComboBox<Membre> comboMembres = new JComboBox<>();
        java.util.List<Membre> membres = membreDAO.findActifs();
        
        // Filtrer les membres qui ne participent pas déjà à cette tontine
        java.util.List<Membre> membresDisponibles = new java.util.ArrayList<>();
        java.util.List<Participation> participationsExistantes = participationDAO.findByTontine(tontine.getIdTontine());
        
        for (Membre membre : membres) {
            boolean dejaParticipant = false;
            for (Participation participation : participationsExistantes) {
                if (participation.getIdMembre() == membre.getIdMembre()) {
                    dejaParticipant = true;
                    break;
                }
            }
            if (!dejaParticipant) {
                membresDisponibles.add(membre);
            }
        }
        
        for (Membre membre : membresDisponibles) {
            comboMembres.addItem(membre);
        }
        
        // Si aucun membre n'est disponible, afficher un message
        if (membresDisponibles.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Aucun membre disponible pour cette tontine.\\nTous les membres actifs participent déjà à cette tontine.",
                "Information", 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Personnaliser l'affichage dans la ComboBox pour montrer le nom complet
        comboMembres.setRenderer(new javax.swing.ListCellRenderer<Membre>() {
            @Override
            public java.awt.Component getListCellRendererComponent(
                    javax.swing.JList<? extends Membre> list, Membre membre, int index,
                    boolean isSelected, boolean cellHasFocus) {
                JLabel label = new JLabel();
                if (membre != null) {
                    label.setText(membre.getNomComplet() + " (" + membre.getTelephone() + ")");
                }
                if (isSelected) {
                    label.setBackground(list.getSelectionBackground());
                    label.setForeground(list.getSelectionForeground());
                    label.setOpaque(true);
                }
                return label;
            }
        });
        
        // Champ pour le nombre de parts
        JTextField txtNombreParts = new JTextField("1");
        
        panelForm.add(new JLabel("Membre:"));
        panelForm.add(comboMembres);
        panelForm.add(new JLabel("Nombre de parts:"));
        panelForm.add(txtNombreParts);
        
        // Panel pour les boutons
        JPanel panelButtons = new JPanel(new FlowLayout());
        JButton btnConfirmer = new JButton("Ajouter");
        JButton btnAnnuler = new JButton("Annuler");
        
        panelButtons.add(btnConfirmer);
        panelButtons.add(btnAnnuler);
        
        dialogAjout.add(panelForm, BorderLayout.CENTER);
        dialogAjout.add(panelButtons, BorderLayout.SOUTH);
        
        // Action pour le bouton confirmer
        btnConfirmer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Membre membreSelectionne = (Membre) comboMembres.getSelectedItem();
                    if (membreSelectionne == null) {
                        JOptionPane.showMessageDialog(dialogAjout, 
                            "Veuillez sélectionner un membre",
                            "Erreur", 
                            JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    
                    int nombreParts = Integer.parseInt(txtNombreParts.getText());
                    if (nombreParts < 1) {
                        JOptionPane.showMessageDialog(dialogAjout, 
                            "Le nombre de parts doit être au moins 1",
                            "Erreur", 
                            JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    
                    // Créer la participation
                    Participation participation = new Participation();
                    participation.setIdMembre(membreSelectionne.getIdMembre());
                    participation.setIdTontine(tontine.getIdTontine());
                    participation.setNombreParts(nombreParts);
                    participation.setDateInscription(java.time.LocalDate.now());
                    participation.setStatut("active");
                    
                    // Ajouter la participation
                    if (participationDAO.create(participation)) {
                        JOptionPane.showMessageDialog(dialogAjout, 
                            "Participant ajouté avec succès",
                            "Succès", 
                            JOptionPane.INFORMATION_MESSAGE);
                        loadParticipants(); // Recharger la liste
                        dialogAjout.dispose();
                    } else {
                        JOptionPane.showMessageDialog(dialogAjout, 
                            "Erreur lors de l'ajout du participant",
                            "Erreur", 
                            JOptionPane.ERROR_MESSAGE);
                    }
                    
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialogAjout, 
                        "Veuillez entrer un nombre valide pour les parts",
                        "Erreur", 
                        JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialogAjout, 
                        "Erreur: " + ex.getMessage(),
                        "Erreur", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        // Action pour le bouton annuler
        btnAnnuler.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialogAjout.dispose();
            }
        });
        
        dialogAjout.setSize(300, 150);
        dialogAjout.setLocationRelativeTo(this);
        dialogAjout.setVisible(true);
    }
    
    private void supprimerParticipant() {
        int selectedRow = tableParticipants.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Veuillez sélectionner un participant à supprimer",
                "Avertissement", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Êtes-vous sûr de vouloir supprimer ce participant ?",
            "Confirmation",
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int participationId = (int) tableModel.getValueAt(selectedRow, 0);
                participationDAO.delete(participationId);
                loadParticipants();
                JOptionPane.showMessageDialog(this, 
                    "Participant supprimé avec succès",
                    "Succès", 
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Erreur lors de la suppression: " + e.getMessage(),
                    "Erreur", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
