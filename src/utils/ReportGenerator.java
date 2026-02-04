package utils;

import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;

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
    private BaseFont baseFontNormal;
    private BaseFont baseFontBold;
    
    private Document document;
    private PdfWriter writer;
    private PdfContentByte canvas;
    
    public ReportGenerator() {
        initializeFonts();
    }
    
    private void initializeFonts() {
        try {
            baseFontNormal = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.EMBEDDED);
            baseFontBold = BaseFont.createFont(BaseFont.HELVETICA_BOLD, BaseFont.WINANSI, BaseFont.EMBEDDED);
            
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
    
    public void generateReport(String filename, String reportType, Map<String, Object> data) {
        try {
            System.out.println("GÉNÉRATION RAPPORT PROFESSIONNEL - " + reportType);
            long startTime = System.currentTimeMillis();
            
            // Créer document
            document = new Document(PageSize.A4, 40, 40, 50, 50);
            writer = PdfWriter.getInstance(document, new FileOutputStream(filename));
            
            // Événements pour header/footer
            ModernHeaderFooter event = new ModernHeaderFooter(reportType);
            writer.setPageEvent(event);
            
            document.open();
            canvas = writer.getDirectContent();
            
            // === PAGE DE COUVERTURE MODERNE ===
            addProfessionalCoverPage(reportType, data);
            document.newPage();
            
            // === RÉSUMÉ EXÉCUTIF AVEC KPIs ===
            addExecutiveSummaryWithKPIs(data);
            document.newPage();
            
            // === CONTENU DÉTAILLÉ SELON TYPE ===
            addDetailedContentByType(reportType, data);
            
            // === GRAPHIQUES SI DISPONIBLES ===
            if (data.containsKey("chartData")) {
                document.newPage();
                addVisualizationsPage((Map<String, Double>) data.get("chartData"));
            }
            
            // === CONCLUSIONS ET RECOMMANDATIONS ===
            document.newPage();
            addProfessionalConclusions(reportType, data);
            
            document.close();
            
            long elapsed = System.currentTimeMillis() - startTime;
            System.out.println("Rapport généré en " + (elapsed/1000.0) + " secondes");
            
        } catch (Exception e) {
            System.err.println("ERREUR GÉNÉRATION: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void addProfessionalCoverPage(String reportType, Map<String, Object> data) throws DocumentException {
        // Fond gradient moderne
        canvas.saveState();
        canvas.setColorFill(PRIMARY_PURPLE);
        canvas.rectangle(0, 0, document.getPageSize().getWidth(), 200);
        canvas.fill();
        canvas.restoreState();
        
        // Icône moderne
        Paragraph icon = new Paragraph("📊", new Font(Font.FontFamily.HELVETICA, 64));
        icon.setAlignment(Element.ALIGN_CENTER);
        icon.setSpacingBefore(50);
        document.add(icon);
        
        // Titre principal
        Paragraph title = new Paragraph(reportType.toUpperCase(), 
            new Font(Font.FontFamily.HELVETICA, 32, Font.BOLD, BaseColor.WHITE));
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingBefore(20);
        document.add(title);
        
        // Sous-titre
        Paragraph subtitle = new Paragraph("Rapport Analytique Détaillé", 
            new Font(Font.FontFamily.HELVETICA, 16, Font.NORMAL, new BaseColor(255, 255, 255, 200)));
        subtitle.setAlignment(Element.ALIGN_CENTER);
        subtitle.setSpacingBefore(10);
        document.add(subtitle);
        
        // Date
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");
        Paragraph date = new Paragraph(LocalDate.now().format(formatter),
            new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, new BaseColor(255, 255, 255, 180)));
        date.setAlignment(Element.ALIGN_CENTER);
        date.setSpacingBefore(30);
        document.add(date);
        
        // Carte d'information détaillée
        PdfPTable infoCard = new PdfPTable(1);
        infoCard.setWidthPercentage(70);
        infoCard.setHorizontalAlignment(Element.ALIGN_CENTER);
        infoCard.setSpacingBefore(100);
        
        PdfPCell cell = new PdfPCell();
        cell.setBorder(Rectangle.BOX);
        cell.setBorderColor(PRIMARY_EMERALD);
        cell.setBorderWidth(2);
        cell.setBackgroundColor(new BaseColor(255, 255, 255, 250));
        cell.setPadding(30);
        
        Paragraph cardContent = new Paragraph();
        cardContent.setAlignment(Element.ALIGN_CENTER);
        
        // Informations clés
        cardContent.add(new Chunk("PÉRIODE\n", 
            new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, TEXT_SECONDARY)));
        cardContent.add(new Chunk(data.getOrDefault("period", "N/A").toString() + "\n\n", 
            new Font(Font.FontFamily.HELVETICA, 14, Font.NORMAL, TEXT_PRIMARY)));
        
        cardContent.add(new Chunk("TOTAL D'ÉLÉMENTS ANALYSÉS\n", 
            new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, TEXT_SECONDARY)));
        cardContent.add(new Chunk(data.getOrDefault("totalItems", "0").toString() + "\n\n", 
            new Font(Font.FontFamily.HELVETICA, 36, Font.BOLD, PRIMARY_PURPLE)));
        
        cardContent.add(new Chunk("Généré automatiquement • Confidentiel", 
            new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL, TEXT_SECONDARY)));
        
        cell.addElement(cardContent);
        infoCard.addCell(cell);
        document.add(infoCard);
    }
    
    private void addExecutiveSummaryWithKPIs(Map<String, Object> data) throws DocumentException {
        // Titre section
        addProfessionalSectionTitle("📈 Résumé Exécutif", PRIMARY_PURPLE);
        
        // Grille KPIs 3 colonnes
        PdfPTable kpiGrid = new PdfPTable(3);
        kpiGrid.setWidthPercentage(100);
        kpiGrid.setSpacingBefore(25);
        kpiGrid.setSpacingAfter(30);
        
        // KPI 1 - Revenus
        kpiGrid.addCell(createEnhancedKPICard(
            "💰", 
            "Revenus Totaux", 
            data.getOrDefault("totalRevenue", "0") + " FCFA",
            PRIMARY_EMERALD,
            "Depuis le début"
        ));
        
        // KPI 2 - Membres Actifs
        kpiGrid.addCell(createEnhancedKPICard(
            "👥", 
            "Membres Actifs", 
            data.getOrDefault("activeMembers", "0").toString(),
            PRIMARY_BLUE,
            "État actuel"
        ));
        
        // KPI 3 - Performance
        kpiGrid.addCell(createEnhancedKPICard(
            "📊", 
            "Taux de Performance", 
            data.getOrDefault("performance", "0") + "%",
            PRIMARY_AMBER,
            "Taux d'activité"
        ));
        
        document.add(kpiGrid);
        
        // Description détaillée
        addStyledParagraph(
            "Ce rapport présente une analyse exhaustive et détaillée des données de votre système de gestion. " +
            "Chaque section a été soigneusement élaborée pour vous fournir une vue complète et actionnable " +
            "de vos opérations, avec des insights précis basés sur les données réelles extraites de votre base de données.",
            TEXT_PRIMARY, 12
        );
    }
    
    private void addDetailedContentByType(String reportType, Map<String, Object> data) throws DocumentException {
        addProfessionalSectionTitle("📋 Analyse Détaillée - " + reportType, PRIMARY_BLUE);
        
        // Vérifier si on a des données de tableau
        if (data.containsKey("tableData")) {
            @SuppressWarnings("unchecked")
            List<String[]> tableData = (List<String[]>) data.get("tableData");
            
            if (tableData != null && tableData.size() > 1) {
                // Tableau professionnel avec données réelles
                PdfPTable table = createProfessionalTable(tableData);
                document.add(table);
                
                // Statistiques détaillées
                addDetailedStatistics(tableData, reportType);
            } else {
                addNoDataMessage();
            }
        } else {
            addNoDataMessage();
        }
        
        // Section Insights
        document.add(new Paragraph("\n"));
        addProfessionalSectionTitle("💡 Insights et Observations", PRIMARY_EMERALD);
        
        addEnhancedBulletPoint("📈", "Analyse des tendances", 
            "Les données montrent une évolution cohérente et continue sur la période analysée.", 
            PRIMARY_EMERALD);
        
        addEnhancedBulletPoint("🎯", "Points clés identifiés", 
            "Plusieurs indicateurs méritent une attention particulière pour optimiser les performances.", 
            PRIMARY_BLUE);
        
        addEnhancedBulletPoint("⚡", "Actions recommandées", 
            "Des opportunités d'amélioration ont été identifiées et sont détaillées dans la section conclusions.", 
            PRIMARY_AMBER);
    }
    
    private PdfPTable createProfessionalTable(List<String[]> data) throws DocumentException {
        if (data.isEmpty()) return new PdfPTable(1);
        
        String[] headers = data.get(0);
        PdfPTable table = new PdfPTable(headers.length);
        table.setWidthPercentage(100);
        table.setSpacingBefore(20);
        table.setSpacingAfter(20);
        
        // En-têtes avec style premium
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, 
                new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.WHITE)));
            cell.setBackgroundColor(PRIMARY_DARK);
            cell.setPadding(12);
            cell.setPaddingBottom(14);
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);
        }
        
        // Données avec alternance de couleurs
        boolean alternate = false;
        int rowCount = 0;
        for (int i = 1; i < data.size() && rowCount < 50; i++, rowCount++) {
            String[] row = data.get(i);
            for (int j = 0; j < row.length; j++) {
                String value = row[j] != null ? row[j] : "";
                
                PdfPCell cell = new PdfPCell(new Phrase(value, fontNormal));
                cell.setBackgroundColor(alternate ? BaseColor.WHITE : BACKGROUND_LIGHT);
                cell.setPadding(10);
                cell.setPaddingTop(12);
                cell.setPaddingBottom(12);
                cell.setBorder(Rectangle.NO_BORDER);
                cell.setBorderWidthBottom(0.5f);
                cell.setBorderColorBottom(BORDER_COLOR);
                
                // Alignement selon le contenu
                if (j == 0) {
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                } else if (value.matches(".*\\d+.*")) {
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                } else {
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                }
                
                // Coloration des statuts
                if (value.toLowerCase().contains("actif") || value.toLowerCase().contains("✓")) {
                    cell.addElement(new Phrase(value, new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD, PRIMARY_EMERALD)));
                } else if (value.toLowerCase().contains("en cours") || value.toLowerCase().contains("⏳")) {
                    cell.addElement(new Phrase(value, new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL, PRIMARY_BLUE)));
                } else if (value.toLowerCase().contains("terminé") || value.toLowerCase().contains("complété")) {
                    cell.addElement(new Phrase(value, new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL, TEXT_SECONDARY)));
                }
                
                table.addCell(cell);
            }
            alternate = !alternate;
        }
        
        if (rowCount >= 50) {
            addWarningMessage("Tableau limité aux 50 premières lignes pour optimiser les performances");
        }
        
        return table;
    }
    
    private void addDetailedStatistics(List<String[]> data, String reportType) throws DocumentException {
        document.add(new Paragraph("\n"));
        
        // Carte de statistiques
        PdfPTable statsCard = new PdfPTable(2);
        statsCard.setWidthPercentage(100);
        statsCard.setSpacingBefore(20);
        statsCard.setSpacingAfter(20);
        statsCard.setWidths(new float[]{1f, 1f});
        
        // Stat 1
        PdfPCell stat1 = createStatCell("📊 Total d'entrées", String.valueOf(data.size() - 1), PRIMARY_PURPLE);
        statsCard.addCell(stat1);
        
        // Stat 2
        PdfPCell stat2 = createStatCell("📅 Date d'analyse", LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), PRIMARY_BLUE);
        statsCard.addCell(stat2);
        
        document.add(statsCard);
    }
    
    private PdfPCell createStatCell(String label, String value, BaseColor color) {
        PdfPCell cell = new PdfPCell();
        cell.setBorder(Rectangle.BOX);
        cell.setBorderColor(BORDER_COLOR);
        cell.setBorderWidth(1);
        cell.setBackgroundColor(new BaseColor(color.getRed(), color.getGreen(), color.getBlue(), 10));
        cell.setPadding(15);
        
        Paragraph content = new Paragraph();
        content.add(new Chunk(label + "\n", new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD, TEXT_SECONDARY)));
        content.add(new Chunk(value, new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD, color)));
        
        cell.addElement(content);
        return cell;
    }
    
    private void addVisualizationsPage(Map<String, Double> chartData) throws DocumentException {
        addProfessionalSectionTitle("📊 Visualisations et Graphiques", PRIMARY_PURPLE);
        
        if (chartData.isEmpty()) {
            addNoDataMessage();
            return;
        }
        
 // Zone graphique - Visualisation des contributions de la tontine
PdfPTable chartContainer = new PdfPTable(1);
chartContainer.setWidthPercentage(95);
chartContainer.setSpacingBefore(30);
chartContainer.setHorizontalAlignment(Element.ALIGN_CENTER);

PdfPCell chartCell = new PdfPCell();
chartCell.setBorder(Rectangle.BOX);
chartCell.setBorderColor(BORDER_COLOR);
chartCell.setBorderWidth(2);
chartCell.setBackgroundColor(BaseColor.WHITE);
chartCell.setPadding(30);
chartCell.setFixedHeight(320);

document.add(chartContainer);

// Dessiner graphique des versements
drawEnhancedBarChart(canvas, chartData, 
    document.left() + 70, document.top() - 480, 450, 260);

// Légende des membres et contributions
addEnhancedChartLegend(chartData);
}

private void drawEnhancedBarChart(PdfContentByte cb, Map<String, Double> data, 
        float x, float y, float width, float height) {
    try {
        if (data.isEmpty()) return;
        
        int numBars = data.size();
        float barWidth = Math.min(50, width / (numBars * 1.8f));
        float spacing = barWidth * 0.8f;
        float maxValue = (float) data.values().stream().mapToDouble(Double::doubleValue).max().orElse(100.0);
        
        BaseColor[] colors = {PRIMARY_PURPLE, PRIMARY_BLUE, PRIMARY_EMERALD, PRIMARY_AMBER, PRIMARY_RED};
        
        int i = 0;
        for (Map.Entry<String, Double> entry : data.entrySet()) {
            float barHeight = (float) ((entry.getValue() / maxValue) * height);
            float barX = x + i * (barWidth + spacing);
            float barY = y;
            
            // Barre avec ombre (représente la contribution)
            cb.saveState();
            cb.setColorFill(new BaseColor(0, 0, 0, 20));
            cb.rectangle(barX + 3, barY - 3, barWidth, barHeight);
            cb.fill();
            cb.restoreState();
            
            // Barre principale (montant versé par membre)
            cb.saveState();
            cb.setColorFill(colors[i % colors.length]);
            cb.rectangle(barX, barY, barWidth, barHeight);
            cb.fill();
            cb.restoreState();
            
            // Montant affiché au-dessus
            cb.beginText();
            cb.setFontAndSize(baseFontBold, 11);
            cb.setColorFill(colors[i % colors.length]);
            cb.showTextAligned(Element.ALIGN_CENTER, 
                String.format("%.0f", entry.getValue()), 
                barX + barWidth/2, barY + barHeight + 8, 0);
            cb.endText();
            
            // Nom du membre en bas
            cb.beginText();
            cb.setFontAndSize(baseFontNormal, 9);
            cb.setColorFill(TEXT_SECONDARY);
            String truncatedLabel = entry.getKey().length() > 10 ? 
                entry.getKey().substring(0, 10) + "..." : entry.getKey();
            cb.showTextAligned(Element.ALIGN_CENTER, truncatedLabel, 
                barX + barWidth/2, barY - 18, 0);
            cb.endText();
            
            i++;
        }
        
        // Axe X (ligne de base des contributions)
        cb.saveState();
        cb.setLineWidth(1);
        cb.setColorStroke(BORDER_COLOR);
        cb.moveTo(x - 10, y);
        cb.lineTo(x + width, y);
        cb.stroke();
        cb.restoreState();
        
    } catch (Exception e) {
        e.printStackTrace();
    }
}

private void addEnhancedChartLegend(Map<String, Double> data) throws DocumentException {
    PdfPTable legend = new PdfPTable(Math.min(4, data.size()));
    legend.setWidthPercentage(90);
    legend.setHorizontalAlignment(Element.ALIGN_CENTER);
    legend.setSpacingBefore(25);
    
    BaseColor[] colors = {PRIMARY_PURPLE, PRIMARY_BLUE, PRIMARY_EMERALD, PRIMARY_AMBER, PRIMARY_RED};
    int i = 0;
    
    for (Map.Entry<String, Double> entry : data.entrySet()) {
        PdfPCell cell = new PdfPCell();
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(10);
        
        Paragraph p = new Paragraph();
        p.setAlignment(Element.ALIGN_LEFT);
        p.add(new Chunk("■ ", new Font(Font.FontFamily.HELVETICA, 16, Font.NORMAL, colors[i % colors.length])));
        p.add(new Chunk("Membre : " + entry.getKey() + "\n", new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD, TEXT_PRIMARY)));
        p.add(new Chunk("Contribution : " + String.format("%.0f", entry.getValue()), new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL, TEXT_SECONDARY)));
        
        cell.addElement(p);
        legend.addCell(cell);
        i++;
    }
    
    document.add(legend);
}

    
    private void addProfessionalConclusions(String reportType, Map<String, Object> data) throws DocumentException {
        addProfessionalSectionTitle("🎯 Conclusions et Recommandations", PRIMARY_EMERALD);
        
       addStyledParagraph(
            "L'analyse des contributions et des cycles de versement au sein de la tontine met en évidence " +
            "une dynamique collective solide et des opportunités de renforcement de la solidarité. " +
            "Les résultats obtenus offrent une vision claire du fonctionnement actuel et permettent " +
            "d'identifier les axes prioritaires pour améliorer la gestion et la transparence.",
            TEXT_PRIMARY, 11
        );

        document.add(new Paragraph("\n"));
        
        // Recommandations stratégiques
        Paragraph recTitle = new Paragraph("Recommandations Stratégiques", 
            new Font(Font.FontFamily.HELVETICA, 15, Font.BOLD, PRIMARY_BLUE));
        recTitle.setSpacingBefore(15);
        recTitle.setSpacingAfter(15);
        document.add(recTitle);
        
        addEnhancedBulletPoint("1", "Consolidation", 
            "Maintenir et renforcer les initiatives actuelles qui ont démontré leur efficacité.", 
            PRIMARY_BLUE);
        
        addEnhancedBulletPoint("2", "Optimisation", 
            "Améliorer les processus identifiés comme perfectibles pour maximiser les performances.", 
            PRIMARY_EMERALD);
        
        addEnhancedBulletPoint("3", "Innovation", 
            "Explorer de nouvelles opportunités basées sur les insights découverts dans cette analyse.", 
            PRIMARY_PURPLE);
        
        addEnhancedBulletPoint("4", "Suivi", 
            "Mettre en place des indicateurs de suivi pour mesurer l'impact des actions entreprises.", 
            PRIMARY_AMBER);
        
        // Call-to-action final
        PdfPTable ctaBox = new PdfPTable(1);
        ctaBox.setWidthPercentage(95);
        ctaBox.setHorizontalAlignment(Element.ALIGN_CENTER);
        ctaBox.setSpacingBefore(40);
        
        PdfPCell ctaCell = new PdfPCell();
        ctaCell.setBorder(Rectangle.BOX);
        ctaCell.setBorderColor(PRIMARY_BLUE);
        ctaCell.setBorderWidth(2);
        ctaCell.setBackgroundColor(new BaseColor(PRIMARY_BLUE.getRed(), 
            PRIMARY_BLUE.getGreen(), PRIMARY_BLUE.getBlue(), 15));
        ctaCell.setPadding(25);
        
        Paragraph ctaText = new Paragraph();
        ctaText.setAlignment(Element.ALIGN_CENTER);
        ctaText.add(new Chunk("📞 Besoin d'Assistance?\n\n", 
            new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, PRIMARY_BLUE)));
        ctaText.add(new Chunk(
            "Pour toute question, analyse complémentaire ou support technique, " +
            "n'hésitez pas à contacter notre équipe d'analyse de données.",
            new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL, TEXT_PRIMARY)
        ));
        
        ctaCell.addElement(ctaText);
        ctaBox.addCell(ctaCell);
        document.add(ctaBox);
    }
    
    // === MÉTHODES UTILITAIRES AMÉLIORÉES ===
    
    private void addProfessionalSectionTitle(String title, BaseColor color) throws DocumentException {
        Paragraph p = new Paragraph(title, 
            new Font(Font.FontFamily.HELVETICA, 19, Font.BOLD, color));
        p.setSpacingBefore(30);
        p.setSpacingAfter(18);
        document.add(p);
        
        LineSeparator line = new LineSeparator();
        line.setLineColor(color);
        line.setLineWidth(3);
        document.add(new Chunk(line));
        document.add(new Paragraph(" ")); // Espacement
    }
    
    private void addStyledParagraph(String text, BaseColor color, int size) throws DocumentException {
        Paragraph p = new Paragraph(text, 
            new Font(Font.FontFamily.HELVETICA, size, Font.NORMAL, color));
        p.setAlignment(Element.ALIGN_JUSTIFIED);
        p.setSpacingBefore(12);
        p.setSpacingAfter(12);
        p.setLeading(size * 1.5f);
        document.add(p);
    }
    
    private PdfPCell createEnhancedKPICard(String icon, String label, String value, BaseColor color, String subtext) {
        PdfPCell cell = new PdfPCell();
        cell.setBorder(Rectangle.BOX);
        cell.setBorderColor(color);
        cell.setBorderWidth(2);
        cell.setBackgroundColor(new BaseColor(color.getRed(), color.getGreen(), color.getBlue(), 8));
        cell.setPadding(20);
        cell.setPaddingBottom(25);
        
        Paragraph content = new Paragraph();
        content.setAlignment(Element.ALIGN_CENTER);
        content.add(new Chunk(icon + "\n", new Font(Font.FontFamily.HELVETICA, 38)));
        content.add(new Chunk(label + "\n", new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, TEXT_SECONDARY)));
        content.add(new Chunk(value + "\n", new Font(Font.FontFamily.HELVETICA, 26, Font.BOLD, color)));
        content.add(new Chunk(subtext, new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL, TEXT_SECONDARY)));
        
        cell.addElement(content);
        return cell;
    }
    
    private void addEnhancedBulletPoint(String bullet, String title, String text, BaseColor color) throws DocumentException {
        Paragraph p = new Paragraph();
        p.setSpacingBefore(10);
        p.setIndentationLeft(25);
        
        p.add(new Chunk(bullet + ". ", new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, color)));
        p.add(new Chunk(title + ": ", new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD, TEXT_PRIMARY)));
        p.add(new Chunk(text, fontNormal));
        
        document.add(p);
    }
    
    private void addNoDataMessage() throws DocumentException {
        Paragraph noData = new Paragraph("Aucune donnée disponible pour cette section", 
            new Font(Font.FontFamily.HELVETICA, 12, Font.ITALIC, TEXT_SECONDARY));
        noData.setAlignment(Element.ALIGN_CENTER);
        noData.setSpacingBefore(30);
        noData.setSpacingAfter(30);
        document.add(noData);
    }
    
    private void addWarningMessage(String message) throws DocumentException {
        Paragraph warning = new Paragraph(message, 
            new Font(Font.FontFamily.HELVETICA, 9, Font.ITALIC, PRIMARY_AMBER));
        warning.setSpacingBefore(10);
        document.add(warning);
    }
    
    class ModernHeaderFooter extends PdfPageEventHelper {
        private String reportType;
        
        public ModernHeaderFooter(String reportType) {
            this.reportType = reportType;
        }
        
        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            PdfContentByte cb = writer.getDirectContent();
            
            try {
                // Header (sauf page 1)
                if (writer.getPageNumber() > 1) {
                    cb.saveState();
                    cb.setLineWidth(0.5f);
                    cb.setColorStroke(BORDER_COLOR);
                    cb.moveTo(document.left(), document.top() + 10);
                    cb.lineTo(document.right(), document.top() + 10);
                    cb.stroke();
                    
                    cb.setColorFill(TEXT_SECONDARY);
                    cb.beginText();
                    cb.setFontAndSize(BaseFont.createFont(), 9);
                    cb.showTextAligned(Element.ALIGN_LEFT, reportType, document.left(), document.top() + 20, 0);
                    cb.showTextAligned(Element.ALIGN_RIGHT, 
                        LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), 
                        document.right(), document.top() + 20, 0);
                    cb.endText();
                    cb.restoreState();
                }
                
                // Footer
                cb.saveState();
                cb.setLineWidth(0.5f);
                cb.setColorStroke(BORDER_COLOR);
                cb.moveTo(document.left(), document.bottom() - 10);
                cb.lineTo(document.right(), document.bottom() - 10);
                cb.stroke();
                
                cb.setColorFill(TEXT_SECONDARY);
                cb.beginText();
                cb.setFontAndSize(BaseFont.createFont(), 8);
                cb.showTextAligned(Element.ALIGN_LEFT, "Confidentiel", document.left(), document.bottom() - 20, 0);
                cb.showTextAligned(Element.ALIGN_CENTER, "Page " + writer.getPageNumber(), 
                    document.getPageSize().getWidth() / 2, document.bottom() - 20, 0);
                cb.showTextAligned(Element.ALIGN_RIGHT, "Généré automatiquement", document.right(), document.bottom() - 20, 0);
                cb.endText();
                cb.restoreState();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}