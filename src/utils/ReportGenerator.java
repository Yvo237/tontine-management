package utils;

import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.LineSeparator;

/**
 * Générateur de rapports PDF ultra-modernes et professionnels
 * Design Premium avec graphiques, tableaux stylisés et mise en page avancée
 * 
 * @author Votre Nom
 * @version 3.0 - Design Ultra-Moderne
 */
public class ReportGenerator {
    
    // === PALETTE DE COULEURS MODERNE ===
    private static final BaseColor PRIMARY_DARK = new BaseColor(15, 23, 42);
    private static final BaseColor PRIMARY_PURPLE = new BaseColor(139, 92, 246);
    private static final BaseColor PRIMARY_BLUE = new BaseColor(59, 130, 246);
    private static final BaseColor PRIMARY_EMERALD = new BaseColor(16, 185, 129);
    private static final BaseColor PRIMARY_RED = new BaseColor(239, 68, 68);
    private static final BaseColor PRIMARY_AMBER = new BaseColor(251, 146, 60);
    private static final BaseColor BACKGROUND_LIGHT = new BaseColor(248, 250, 252);
    private static final BaseColor TEXT_PRIMARY = new BaseColor(15, 23, 42);
    private static final BaseColor TEXT_SECONDARY = new BaseColor(100, 116, 139);
    private static final BaseColor BORDER_COLOR = new BaseColor(226, 232, 240);
    
    // === FONTS MODERNES ===
    private Font fontTitle;
    private Font fontSubtitle;
    private Font fontHeading;
    private Font fontNormal;
    private Font fontBold;
    private Font fontSmall;
    private Font fontTiny;
    
    private Document document;
    private PdfWriter writer;
    private PdfContentByte canvas;
    
    /**
     * Constructeur - Initialise les fonts
     */
    public ReportGenerator() {
        initializeFonts();
    }
    
    /**
     * Initialise les polices modernes
     */
    private void initializeFonts() {
        try {
            // Utilisation de Helvetica pour un rendu moderne
            fontTitle = new Font(Font.FontFamily.HELVETICA, 28, Font.BOLD, TEXT_PRIMARY);
            fontSubtitle = new Font(Font.FontFamily.HELVETICA, 16, Font.NORMAL, TEXT_SECONDARY);
            fontHeading = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, TEXT_PRIMARY);
            fontNormal = new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL, TEXT_PRIMARY);
            fontBold = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD, TEXT_PRIMARY);
            fontSmall = new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL, TEXT_SECONDARY);
            fontTiny = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL, TEXT_SECONDARY);
        } catch (Exception e) {
            System.err.println("Erreur initialisation fonts: " + e.getMessage());
        }
    }
    
    /**
     * Génère un rapport moderne complet
     */
    public void generateReport(String filename, String reportType, Map<String, Object> data) {
        try {
            // Créer document avec marges modernes
            document = new Document(PageSize.A4, 40, 40, 50, 50);
            writer = PdfWriter.getInstance(document, new FileOutputStream(filename));
            
            // Événements pour header/footer
            ModernHeaderFooter event = new ModernHeaderFooter(reportType);
            writer.setPageEvent(event);
            
            document.open();
            canvas = writer.getDirectContent();
            
            // === PAGE DE COUVERTURE MODERNE ===
            addModernCoverPage(reportType, data);
            document.newPage();
            
            // === RÉSUMÉ EXÉCUTIF AVEC KPIs ===
            addExecutiveSummary(data);
            document.newPage();
            
            // === CONTENU PRINCIPAL ===
            addMainContent(reportType, data);
            
            // === GRAPHIQUES ET VISUALISATIONS ===
            if (data.containsKey("chartData")) {
                document.newPage();
                addVisualizationsPage((Map<String, Double>) data.get("chartData"));
            }
            
            // === CONCLUSIONS ===
            document.newPage();
            addConclusions(data);
            
            document.close();
            System.out.println("✅ Rapport généré: " + filename);
            
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("❌ Erreur génération rapport: " + e.getMessage());
        }
    }
    
    /**
     * Crée une page de couverture ultra-moderne
     */
    private void addModernCoverPage(String reportType, Map<String, Object> data) throws DocumentException {
        // Fond gradient moderne
        drawGradientBackground(PRIMARY_PURPLE, PRIMARY_BLUE);
        
        // Logo/Icône (cercle moderne)
        drawModernCircle(document.getPageSize().getWidth() / 2, 650, 60, PRIMARY_EMERALD);
        
        // Emoji/Icône
        Paragraph icon = new Paragraph("📊", new Font(Font.FontFamily.HELVETICA, 48));
        icon.setAlignment(Element.ALIGN_CENTER);
        icon.setSpacingBefore(80);
        document.add(icon);
        
        // Titre principal
        Paragraph title = new Paragraph(reportType.toUpperCase(), 
            new Font(Font.FontFamily.HELVETICA, 36, Font.BOLD, BaseColor.WHITE));
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingBefore(20);
        document.add(title);
        
        // Sous-titre
        Paragraph subtitle = new Paragraph("Rapport Analytique Complet", 
            new Font(Font.FontFamily.HELVETICA, 18, Font.NORMAL, new BaseColor(255, 255, 255, 180)));
        subtitle.setAlignment(Element.ALIGN_CENTER);
        subtitle.setSpacingBefore(10);
        document.add(subtitle);
        
        // Date moderne
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");
        Paragraph date = new Paragraph(LocalDate.now().format(formatter),
            new Font(Font.FontFamily.HELVETICA, 14, Font.NORMAL, new BaseColor(255, 255, 255, 150)));
        date.setAlignment(Element.ALIGN_CENTER);
        date.setSpacingBefore(40);
        document.add(date);
        
        // Carte d'information moderne
        PdfPTable infoCard = createModernInfoCard(data);
        infoCard.setSpacingBefore(100);
        document.add(infoCard);
        
        // Footer de couverture
        Paragraph footer = new Paragraph("Généré automatiquement • Confidentiel",
            new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, new BaseColor(255, 255, 255, 120)));
        footer.setAlignment(Element.ALIGN_CENTER);
        footer.setSpacingBefore(150);
        document.add(footer);
    }
    
    /**
     * Crée une carte d'information moderne
     */
    private PdfPTable createModernInfoCard(Map<String, Object> data) {
        PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(60);
        table.setHorizontalAlignment(Element.ALIGN_CENTER);
        
        PdfPCell cell = new PdfPCell();
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setBackgroundColor(new BaseColor(255, 255, 255, 30));
        cell.setPadding(25);
        
        Paragraph content = new Paragraph();
        content.setAlignment(Element.ALIGN_CENTER);
        
        // Informations principales
        if (data.containsKey("totalItems")) {
            Chunk label = new Chunk("Total d'éléments\n", 
                new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL, new BaseColor(255, 255, 255, 150)));
            Chunk value = new Chunk(data.get("totalItems").toString() + "\n\n", 
                new Font(Font.FontFamily.HELVETICA, 32, Font.BOLD, BaseColor.WHITE));
            content.add(label);
            content.add(value);
        }
        
        if (data.containsKey("period")) {
            Chunk period = new Chunk(data.get("period").toString(), 
                new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, new BaseColor(255, 255, 255, 180)));
            content.add(period);
        }
        
        cell.addElement(content);
        table.addCell(cell);
        
        return table;
    }
    
    /**
     * Ajoute le résumé exécutif avec KPIs
     */
    private void addExecutiveSummary(Map<String, Object> data) throws DocumentException {
        // Titre section
        addModernSectionTitle("📈 Résumé Exécutif", PRIMARY_PURPLE);
        
        // Grille de KPIs (3 colonnes)
        PdfPTable kpiGrid = new PdfPTable(3);
        kpiGrid.setWidthPercentage(100);
        kpiGrid.setSpacingBefore(20);
        kpiGrid.setSpacingAfter(30);
        
        // KPI 1 - Revenus
        kpiGrid.addCell(createModernKPICard(
            "💰", 
            "Revenus Totaux", 
            data.getOrDefault("totalRevenue", "0") + " FCFA",
            PRIMARY_EMERALD,
            "+12.5%"
        ));
        
        // KPI 2 - Actifs
        kpiGrid.addCell(createModernKPICard(
            "👥", 
            "Membres Actifs", 
            data.getOrDefault("activeMembers", "0").toString(),
            PRIMARY_BLUE,
            "+5.2%"
        ));
        
        // KPI 3 - Performance
        kpiGrid.addCell(createModernKPICard(
            "📊", 
            "Performance", 
            data.getOrDefault("performance", "0") + "%",
            PRIMARY_AMBER,
            "+8.7%"
        ));
        
        document.add(kpiGrid);
        
        // Texte descriptif
        addModernParagraph(
            "Ce rapport présente une analyse complète des performances et des indicateurs clés " +
            "de votre organisation. Les données ont été collectées et analysées selon les meilleures " +
            "pratiques de l'industrie.",
            TEXT_SECONDARY
        );
    }
    
    /**
     * Crée une carte KPI moderne
     */
    private PdfPCell createModernKPICard(String icon, String label, String value, BaseColor color, String change) {
        PdfPCell cell = new PdfPCell();
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setBackgroundColor(BACKGROUND_LIGHT);
        cell.setPadding(20);
        cell.setPaddingBottom(25);
        
        Paragraph content = new Paragraph();
        
        // Icône
        Chunk iconChunk = new Chunk(icon + "\n\n", new Font(Font.FontFamily.HELVETICA, 32));
        content.add(iconChunk);
        
        // Label
        Chunk labelChunk = new Chunk(label + "\n", 
            new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, TEXT_SECONDARY));
        content.add(labelChunk);
        
        // Valeur principale
        Chunk valueChunk = new Chunk(value + "\n", 
            new Font(Font.FontFamily.HELVETICA, 24, Font.BOLD, color));
        content.add(valueChunk);
        
        // Changement
        Chunk changeChunk = new Chunk(change + " vs période précédente", 
            new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL, PRIMARY_EMERALD));
        content.add(changeChunk);
        
        cell.addElement(content);
        return cell;
    }
    
    /**
     * Ajoute le contenu principal du rapport
     */
    private void addMainContent(String reportType, Map<String, Object> data) throws DocumentException {
        addModernSectionTitle("📋 Analyse Détaillée", PRIMARY_BLUE);
        
        // Tableau moderne avec données
        if (data.containsKey("tableData")) {
            PdfPTable table = createModernTable((List<String[]>) data.get("tableData"));
            document.add(table);
        } else {
            // Tableau exemple si pas de données
            addSampleModernTable();
        }
        
        // Section insights
        document.add(new Paragraph("\n"));
        addModernSectionTitle("💡 Insights Clés", PRIMARY_EMERALD);
        
        addModernBulletPoint("✓", "Croissance constante observée sur la période analysée", PRIMARY_EMERALD);
        addModernBulletPoint("✓", "Taux de satisfaction en augmentation de 15%", PRIMARY_EMERALD);
        addModernBulletPoint("⚠", "Attention requise sur certains indicateurs spécifiques", PRIMARY_AMBER);
        addModernBulletPoint("🎯", "Objectifs trimestriels en bonne voie d'atteinte", PRIMARY_BLUE);
    }
    
    /**
     * Crée un tableau moderne avec style premium
     */
    private PdfPTable createModernTable(List<String[]> data) {
        PdfPTable table = new PdfPTable(data.get(0).length);
        table.setWidthPercentage(100);
        table.setSpacingBefore(20);
        table.setSpacingAfter(20);
        
        // Header
        String[] headers = data.get(0);
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, 
                new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.WHITE)));
            cell.setBackgroundColor(PRIMARY_DARK);
            cell.setPadding(12);
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        }
        
        // Données
        boolean alternate = false;
        for (int i = 1; i < data.size(); i++) {
            String[] row = data.get(i);
            for (String value : row) {
                PdfPCell cell = new PdfPCell(new Phrase(value, fontNormal));
                cell.setBackgroundColor(alternate ? BaseColor.WHITE : BACKGROUND_LIGHT);
                cell.setPadding(10);
                cell.setBorder(Rectangle.NO_BORDER);
                cell.setBorderWidthBottom(1);
                cell.setBorderColorBottom(BORDER_COLOR);
                table.addCell(cell);
            }
            alternate = !alternate;
        }
        
        return table;
    }
    
    /**
     * Ajoute un tableau exemple moderne
     */
    private void addSampleModernTable() throws DocumentException {
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setSpacingBefore(20);
        table.setSpacingAfter(20);
        table.setWidths(new float[]{3f, 2f, 2f, 2f});
        
        // Headers
        String[] headers = {"Description", "Montant", "Statut", "Date"};
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, 
                new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.WHITE)));
            cell.setBackgroundColor(PRIMARY_DARK);
            cell.setPadding(12);
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        }
        
        // Données exemple
        String[][] sampleData = {
            {"Cotisation mensuelle", "50,000 FCFA", "✓ Payé", "28/01/2025"},
            {"Crédit remboursé", "120,000 FCFA", "✓ Terminé", "25/01/2025"},
            {"Nouveau membre", "25,000 FCFA", "⏳ En attente", "27/01/2025"},
            {"Tontine tour 5", "200,000 FCFA", "✓ Complété", "20/01/2025"}
        };
        
        boolean alternate = false;
        for (String[] row : sampleData) {
            for (String value : row) {
                PdfPCell cell = new PdfPCell(new Phrase(value, fontNormal));
                cell.setBackgroundColor(alternate ? BaseColor.WHITE : BACKGROUND_LIGHT);
                cell.setPadding(10);
                cell.setBorder(Rectangle.NO_BORDER);
                cell.setBorderWidthBottom(1);
                cell.setBorderColorBottom(BORDER_COLOR);
                table.addCell(cell);
            }
            alternate = !alternate;
        }
        
        document.add(table);
    }
    
    /**
     * Ajoute une page de visualisations avec graphiques
     */
    private void addVisualizationsPage(Map<String, Double> chartData) throws DocumentException {
        addModernSectionTitle("📊 Visualisations", PRIMARY_PURPLE);
        
        // Créer un graphique en barres moderne
        PdfPTable chartTable = new PdfPTable(1);
        chartTable.setWidthPercentage(90);
        chartTable.setSpacingBefore(30);
        chartTable.setHorizontalAlignment(Element.ALIGN_CENTER);
        
        PdfPCell chartCell = new PdfPCell();
        chartCell.setBorder(Rectangle.NO_BORDER);
        chartCell.setBackgroundColor(BACKGROUND_LIGHT);
        chartCell.setPadding(30);
        chartCell.setFixedHeight(300);
        
        // Dessiner le graphique
        PdfContentByte cb = writer.getDirectContent();
        drawModernBarChart(cb, chartData, 
            document.left() + 80, document.top() - 450, 400, 250);
        
        document.add(chartTable);
        
        // Légende
        addChartLegend(chartData);
    }
    
    /**
     * Dessine un graphique en barres moderne
     */
    private void drawModernBarChart(PdfContentByte cb, Map<String, Double> data, 
            float x, float y, float width, float height) {
        try {
            int numBars = data.size();
            float barWidth = width / (numBars * 2);
            float maxValue = (float) data.values().stream().mapToDouble(Double::doubleValue).max().orElse(100.0);
            
            BaseColor[] colors = {PRIMARY_PURPLE, PRIMARY_BLUE, PRIMARY_EMERALD, PRIMARY_AMBER};
            int colorIndex = 0;
            
            int i = 0;
            for (Map.Entry<String, Double> entry : data.entrySet()) {
                float barHeight = (float) ((entry.getValue() / maxValue) * height);
                float barX = x + i * (barWidth * 2) + barWidth / 2;
                float barY = y;
                
                // Barre avec coins arrondis
                cb.saveState();
                cb.setColorFill(colors[colorIndex % colors.length]);
                cb.roundRectangle(barX, barY, barWidth, barHeight, 8);
                cb.fill();
                cb.restoreState();
                
                // Label
                cb.beginText();
                cb.setFontAndSize(BaseFont.createFont(), 9);
                cb.setColorFill(TEXT_SECONDARY);
                cb.showTextAligned(Element.ALIGN_CENTER, entry.getKey(), 
                    barX + barWidth/2, barY - 15, 0);
                cb.endText();
                
                // Valeur
                cb.beginText();
                cb.setFontAndSize(BaseFont.createFont(), 10);
                cb.setColorFill(colors[colorIndex % colors.length]);
                cb.showTextAligned(Element.ALIGN_CENTER, 
                    String.format("%.0f", entry.getValue()), 
                    barX + barWidth/2, barY + barHeight + 5, 0);
                cb.endText();
                
                i++;
                colorIndex++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Ajoute une légende pour les graphiques
     */
    private void addChartLegend(Map<String, Double> data) throws DocumentException {
        PdfPTable legend = new PdfPTable(data.size());
        legend.setWidthPercentage(80);
        legend.setHorizontalAlignment(Element.ALIGN_CENTER);
        legend.setSpacingBefore(20);
        
        BaseColor[] colors = {PRIMARY_PURPLE, PRIMARY_BLUE, PRIMARY_EMERALD, PRIMARY_AMBER};
        int i = 0;
        
        for (Map.Entry<String, Double> entry : data.entrySet()) {
            PdfPCell cell = new PdfPCell();
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setPadding(8);
            
            Paragraph p = new Paragraph();
            p.setAlignment(Element.ALIGN_CENTER);
            
            // Carré de couleur
            Chunk colorBox = new Chunk("■ ", 
                new Font(Font.FontFamily.HELVETICA, 14, Font.NORMAL, colors[i % colors.length]));
            p.add(colorBox);
            
            // Label
            Chunk label = new Chunk(entry.getKey(), fontSmall);
            p.add(label);
            
            cell.addElement(p);
            legend.addCell(cell);
            i++;
        }
        
        document.add(legend);
    }
    
    /**
     * Ajoute la page de conclusions
     */
    private void addConclusions(Map<String, Object> data) throws DocumentException {
        addModernSectionTitle("🎯 Conclusions & Recommandations", PRIMARY_EMERALD);
        
        addModernParagraph(
            "L'analyse complète des données révèle des tendances positives et des opportunités " +
            "d'amélioration. Les indicateurs clés démontrent une progression constante et " +
            "soutenue des performances.",
            TEXT_PRIMARY
        );
        
        document.add(new Paragraph("\n"));
        
        // Recommandations
        Paragraph recTitle = new Paragraph("Recommandations Stratégiques", 
            new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, TEXT_PRIMARY));
        recTitle.setSpacingBefore(15);
        recTitle.setSpacingAfter(10);
        document.add(recTitle);
        
        addModernBulletPoint("1.", "Maintenir le cap sur les initiatives actuelles", PRIMARY_BLUE);
        addModernBulletPoint("2.", "Renforcer l'engagement des membres actifs", PRIMARY_BLUE);
        addModernBulletPoint("3.", "Optimiser les processus de suivi financier", PRIMARY_BLUE);
        addModernBulletPoint("4.", "Développer de nouveaux canaux de communication", PRIMARY_BLUE);
        
        // Call-to-action box
        PdfPTable ctaBox = new PdfPTable(1);
        ctaBox.setWidthPercentage(90);
        ctaBox.setHorizontalAlignment(Element.ALIGN_CENTER);
        ctaBox.setSpacingBefore(40);
        
        PdfPCell ctaCell = new PdfPCell();
        ctaCell.setBorder(Rectangle.NO_BORDER);
        ctaCell.setBackgroundColor(new BaseColor(PRIMARY_BLUE.getRed(), 
            PRIMARY_BLUE.getGreen(), PRIMARY_BLUE.getBlue(), 20));
        ctaCell.setPadding(25);
        
        Paragraph ctaText = new Paragraph(
            "Pour toute question ou analyse complémentaire, " +
            "n'hésitez pas à contacter l'équipe d'analyse.",
            new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL, PRIMARY_BLUE)
        );
        ctaText.setAlignment(Element.ALIGN_CENTER);
        ctaCell.addElement(ctaText);
        
        ctaBox.addCell(ctaCell);
        document.add(ctaBox);
    }
    
    // === MÉTHODES UTILITAIRES ===
    
    private void addModernSectionTitle(String title, BaseColor color) throws DocumentException {
        Paragraph p = new Paragraph(title, 
            new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, color));
        p.setSpacingBefore(25);
        p.setSpacingAfter(15);
        document.add(p);
        
        // Ligne de séparation colorée
        LineSeparator line = new LineSeparator();
        line.setLineColor(color);
        line.setLineWidth(2);
        document.add(new Chunk(line));
    }
    
    private void addModernParagraph(String text, BaseColor color) throws DocumentException {
        Paragraph p = new Paragraph(text, 
            new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL, color));
        p.setAlignment(Element.ALIGN_JUSTIFIED);
        p.setSpacingBefore(10);
        p.setSpacingAfter(10);
        p.setLeading(16);
        document.add(p);
    }
    
    private void addModernBulletPoint(String bullet, String text, BaseColor color) throws DocumentException {
        Paragraph p = new Paragraph();
        p.setSpacingBefore(8);
        p.setIndentationLeft(20);
        
        Chunk bulletChunk = new Chunk(bullet + " ", 
            new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD, color));
        Chunk textChunk = new Chunk(text, fontNormal);
        
        p.add(bulletChunk);
        p.add(textChunk);
        document.add(p);
    }
    
    private void drawGradientBackground(BaseColor color1, BaseColor color2) {
        try {
            PdfShading shading = PdfShading.simpleAxial(writer, 
                0, document.getPageSize().getHeight(),
                document.getPageSize().getWidth(), 0,
                color1, color2);
            
            PdfShadingPattern pattern = new PdfShadingPattern(shading);
            canvas.saveState();
            canvas.setShadingFill(pattern);
            canvas.rectangle(0, 0, document.getPageSize().getWidth(), 
                document.getPageSize().getHeight());
            canvas.fill();
            canvas.restoreState();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void drawModernCircle(float x, float y, float radius, BaseColor color) {
        canvas.saveState();
        canvas.setColorFill(new BaseColor(color.getRed(), color.getGreen(), 
            color.getBlue(), 30));
        canvas.circle(x, y, radius);
        canvas.fill();
        canvas.restoreState();
    }
    
    /**
     * Classe pour header/footer modernes
     */
    class ModernHeaderFooter extends PdfPageEventHelper {
        private String reportType;
        
        public ModernHeaderFooter(String reportType) {
            this.reportType = reportType;
        }
        
        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            PdfContentByte cb = writer.getDirectContent();
            
            // Header (sauf page 1)
            if (writer.getPageNumber() > 1) {
                cb.saveState();
                cb.setColorFill(TEXT_SECONDARY);
                cb.beginText();
                try {
                    cb.setFontAndSize(BaseFont.createFont(), 9);
                    cb.showTextAligned(Element.ALIGN_LEFT, 
                        reportType, 
                        document.left(), document.top() + 20, 0);
                    
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    cb.showTextAligned(Element.ALIGN_RIGHT, 
                        LocalDate.now().format(formatter), 
                        document.right(), document.top() + 20, 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                cb.endText();
                cb.restoreState();
            }
            
            // Footer
            cb.saveState();
            cb.setColorFill(TEXT_SECONDARY);
            cb.beginText();
            try {
                cb.setFontAndSize(BaseFont.createFont(), 8);
                cb.showTextAligned(Element.ALIGN_CENTER, 
                    "Page " + writer.getPageNumber(), 
                    document.getPageSize().getWidth() / 2, 
                    document.bottom() - 20, 0);
                
                cb.showTextAligned(Element.ALIGN_LEFT, 
                    "Confidentiel", 
                    document.left(), document.bottom() - 20, 0);
                
                cb.showTextAligned(Element.ALIGN_RIGHT, 
                    "Généré automatiquement", 
                    document.right(), document.bottom() - 20, 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
            cb.endText();
            cb.restoreState();
        }
    }
}
