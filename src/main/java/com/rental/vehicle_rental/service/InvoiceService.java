package com.rental.vehicle_rental.service;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.rental.vehicle_rental.model.Booking;
import com.rental.vehicle_rental.repository.BookingRepository;
import org.springframework.stereotype.Service;
import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
public class InvoiceService {

    private final BookingRepository bookingRepository;

    public InvoiceService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    public byte[] generateInvoice(Long bookingId) {
        Optional<Booking> opt = bookingRepository.findById(bookingId);
        if (opt.isEmpty()) return null;
        Booking booking = opt.get();

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4, 50, 50, 50, 50);
            PdfWriter.getInstance(document, out);
            document.open();

            Color darkBlue  = new Color(26, 26, 46);
            Color red       = new Color(233, 69, 96);
            Color lightGray = new Color(245, 245, 245);

            Font titleFont    = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 24, darkBlue);
            Font subtitleFont = FontFactory.getFont(FontFactory.HELVETICA, 11, Color.GRAY);
            Font headerFont   = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, Color.WHITE);
            Font cellFont     = FontFactory.getFont(FontFactory.HELVETICA, 11, darkBlue);
            Font boldFont     = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, darkBlue);
            Font totalFont    = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, red);

            // ── Header ───────────────────────────────────────────────────────
            Paragraph title = new Paragraph("VEHICLE RENTAL SYSTEM", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            Paragraph sub = new Paragraph("Official Rental Invoice", subtitleFont);
            sub.setAlignment(Element.ALIGN_CENTER);
            sub.setSpacingAfter(20);
            document.add(sub);

            // Red divider line
            PdfPTable divider = new PdfPTable(1);
            divider.setWidthPercentage(100);
            PdfPCell dividerCell = new PdfPCell();
            dividerCell.setBackgroundColor(red);
            dividerCell.setFixedHeight(3);
            dividerCell.setBorder(Rectangle.NO_BORDER);
            divider.addCell(dividerCell);
            divider.setSpacingAfter(20);
            document.add(divider);

            // ── Invoice Info ─────────────────────────────────────────────────
            PdfPTable infoTable = new PdfPTable(2);
            infoTable.setWidthPercentage(100);
            infoTable.setSpacingAfter(20);

            addInfoRow(infoTable, "Invoice Number",
                    "INV-" + String.format("%05d", booking.getId()), cellFont, boldFont);
            addInfoRow(infoTable, "Invoice Date",
                    LocalDate.now().toString(), cellFont, boldFont);
            addInfoRow(infoTable, "Borrower Email",
                    booking.getUser().getEmail(), cellFont, boldFont);
            addInfoRow(infoTable, "Booking ID",
                    "#" + booking.getId(), cellFont, boldFont);
            document.add(infoTable);

            // ── Vehicle Details Table ────────────────────────────────────────
            Paragraph vehicleHeader = new Paragraph("VEHICLE DETAILS", boldFont);
            vehicleHeader.setSpacingAfter(8);
            document.add(vehicleHeader);

            PdfPTable vehicleTable = new PdfPTable(2);
            vehicleTable.setWidthPercentage(100);
            vehicleTable.setSpacingAfter(20);

            addTableRow(vehicleTable, "Vehicle Name",
                    booking.getVehicle().getName(),
                    headerFont, cellFont, darkBlue, lightGray, false);
            addTableRow(vehicleTable, "Vehicle Type",
                    booking.getVehicle().getType().name(),
                    headerFont, cellFont, darkBlue, lightGray, true);
            addTableRow(vehicleTable, "Number Plate",
                    booking.getVehicle().getNumberPlate(),
                    headerFont, cellFont, darkBlue, lightGray, false);
            addTableRow(vehicleTable, "Rental Price",
                    "Rs." + booking.getVehicle().getRentalPricePerDay() + "/day",
                    headerFont, cellFont, darkBlue, lightGray, true);
            document.add(vehicleTable);

            // ── Rental Period ────────────────────────────────────────────────
            Paragraph periodHeader = new Paragraph("RENTAL PERIOD", boldFont);
            periodHeader.setSpacingAfter(8);
            document.add(periodHeader);

            PdfPTable periodTable = new PdfPTable(2);
            periodTable.setWidthPercentage(100);
            periodTable.setSpacingAfter(20);

            long days = ChronoUnit.DAYS.between(booking.getRentDate(), booking.getReturnDate());
            if (days < 1) days = 1;

            addTableRow(periodTable, "Rent Date",
                    booking.getRentDate().toString(),
                    headerFont, cellFont, darkBlue, lightGray, false);
            addTableRow(periodTable, "Return Date",
                    booking.getReturnDate().toString(),
                    headerFont, cellFont, darkBlue, lightGray, true);
            addTableRow(periodTable, "Total Days",
                    days + " day(s)",
                    headerFont, cellFont, darkBlue, lightGray, false);
            document.add(periodTable);

            // ── Billing Breakdown ────────────────────────────────────────────
            Paragraph billHeader = new Paragraph("BILLING BREAKDOWN", boldFont);
            billHeader.setSpacingAfter(8);
            document.add(billHeader);

            double baseRent = days * booking.getVehicle().getRentalPricePerDay();
            double total    = booking.getTotalRent();
            double extras   = total - baseRent;

            PdfPTable billTable = new PdfPTable(2);
            billTable.setWidthPercentage(100);
            billTable.setSpacingAfter(10);

            addTableRow(billTable, "Base Rent",
                    "Rs." + String.format("%.2f", baseRent),
                    headerFont, cellFont, darkBlue, lightGray, false);
            addTableRow(billTable, "Extra Charges (KM / Damage)",
                    "Rs." + String.format("%.2f", Math.max(0, extras)),
                    headerFont, cellFont, darkBlue, lightGray, true);
            document.add(billTable);

            // Total amount highlighted
            PdfPTable totalTable = new PdfPTable(2);
            totalTable.setWidthPercentage(100);
            totalTable.setSpacingAfter(30);

            PdfPCell totalLabel = new PdfPCell(new Phrase("TOTAL AMOUNT", totalFont));
            totalLabel.setBackgroundColor(new Color(255, 243, 205));
            totalLabel.setPadding(12);
            totalLabel.setBorder(Rectangle.NO_BORDER);

            PdfPCell totalValue = new PdfPCell(
                    new Phrase("Rs." + String.format("%.2f", total), totalFont));
            totalValue.setBackgroundColor(new Color(255, 243, 205));
            totalValue.setPadding(12);
            totalValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
            totalValue.setBorder(Rectangle.NO_BORDER);

            totalTable.addCell(totalLabel);
            totalTable.addCell(totalValue);
            document.add(totalTable);

            // ── Status ───────────────────────────────────────────────────────
            Paragraph status = new Paragraph(
                    booking.getReturned()
                            ? "STATUS: PAYMENT COMPLETED"
                            : "STATUS: ACTIVE RENTAL",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13,
                            booking.getReturned() ? new Color(40, 167, 69) : red));
            status.setAlignment(Element.ALIGN_CENTER);
            status.setSpacingAfter(20);
            document.add(status);

            // Footer divider
            PdfPTable footerDiv = new PdfPTable(1);
            footerDiv.setWidthPercentage(100);
            PdfPCell fc = new PdfPCell();
            fc.setBackgroundColor(Color.LIGHT_GRAY);
            fc.setFixedHeight(1);
            fc.setBorder(Rectangle.NO_BORDER);
            footerDiv.addCell(fc);
            document.add(footerDiv);

            Paragraph footer = new Paragraph(
                    "Thank you for choosing Vehicle Rental System | " +
                    "Generated on " + LocalDate.now(),
                    FontFactory.getFont(FontFactory.HELVETICA, 10, Color.GRAY));
            footer.setAlignment(Element.ALIGN_CENTER);
            footer.setSpacingBefore(10);
            document.add(footer);

            document.close();
            return out.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void addInfoRow(PdfPTable table, String label,
                            String value, Font labelFont, Font valueFont) {
        PdfPCell lc = new PdfPCell(new Phrase(label, labelFont));
        lc.setBorder(Rectangle.NO_BORDER);
        lc.setPaddingBottom(6);

        PdfPCell vc = new PdfPCell(new Phrase(value, valueFont));
        vc.setBorder(Rectangle.NO_BORDER);
        vc.setPaddingBottom(6);
        vc.setHorizontalAlignment(Element.ALIGN_RIGHT);

        table.addCell(lc);
        table.addCell(vc);
    }

    private void addTableRow(PdfPTable table, String label, String value,
                             Font headerFont, Font cellFont,
                             Color headerBg, Color altBg, boolean alternate) {
        PdfPCell lc = new PdfPCell(new Phrase(label, headerFont));
        lc.setBackgroundColor(headerBg);
        lc.setPadding(10);
        lc.setBorder(Rectangle.NO_BORDER);

        PdfPCell vc = new PdfPCell(new Phrase(value, cellFont));
        vc.setBackgroundColor(alternate ? altBg : Color.WHITE);
        vc.setPadding(10);
        vc.setBorder(Rectangle.NO_BORDER);

        table.addCell(lc);
        table.addCell(vc);
    }
}
