package com.rental.vehicle_rental.service;

import com.rental.vehicle_rental.model.Booking;
import com.rental.vehicle_rental.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.base.url}")
    private String baseUrl;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    public void sendWelcomeEmail(User user) {
        String subject = "Welcome to Vehicle Rental System!";
        String body = buildWelcomeEmail(user);
        sendHtmlEmail(user.getEmail(), subject, body);
    }

    @Async
    public void sendRentConfirmationEmail(Booking booking) {
        String subject = "Booking Confirmed — " + booking.getVehicle().getName();
        String body = buildRentEmail(booking);
        sendHtmlEmail(booking.getUser().getEmail(), subject, body);
    }

    @Async
    public void sendReturnConfirmationEmail(Booking booking, double totalCharge) {
        String subject = "Vehicle Returned — Invoice #" + booking.getId();
        String body = buildReturnEmail(booking, totalCharge);
        sendHtmlEmail(booking.getUser().getEmail(), subject, body);
    }

    @Async
    public void sendBlockedEmail(User user) {
        String subject = "Account Status Update — Vehicle Rental";
        String body = buildBlockedEmail(user);
        sendHtmlEmail(user.getEmail(), subject, body);
    }

    private void sendHtmlEmail(String to, String subject, String htmlBody) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            mailSender.send(message);
            System.out.println("Email sent to: " + to);
        } catch (Exception e) {
            System.out.println("Email failed: " + e.getMessage());
        }
    }

    // ── Email Templates ───────────────────────────────────────────────────────

    private String buildWelcomeEmail(User user) {
        return emailWrapper(
            "Welcome to Vehicle Rental! 🚗",
            "Hi " + (user.getFullName() != null ? user.getFullName() : user.getEmail()) + ",",
            "<p style='color:#555;line-height:1.8'>Your account has been created successfully.</p>" +
            "<table style='width:100%;border-collapse:collapse;margin:16px 0'>" +
            "  <tr style='background:#f8f9fa'>" +
            "    <td style='padding:10px;font-weight:700'>Email</td>" +
            "    <td style='padding:10px'>" + user.getEmail() + "</td>" +
            "  </tr>" +
            "  <tr>" +
            "    <td style='padding:10px;font-weight:700'>Security Deposit</td>" +
            "    <td style='padding:10px;color:#28a745;font-weight:700'>Rs.30,000</td>" +
            "  </tr>" +
            "  <tr style='background:#f8f9fa'>" +
            "    <td style='padding:10px;font-weight:700'>Role</td>" +
            "    <td style='padding:10px'>BORROWER</td>" +
            "  </tr>" +
            "</table>" +
            "<p style='color:#555'>You can now browse and rent Cars and Bikes.</p>" +
            "<a href='" + baseUrl + "/index.html' " +
            "   style='display:inline-block;background:#e94560;color:white;" +
            "          padding:12px 28px;border-radius:8px;text-decoration:none;" +
            "          font-weight:700;margin-top:8px'>Login Now</a>"
        );
    }

    private String buildRentEmail(Booking booking) {
        return emailWrapper(
            "Booking Confirmed! ✅",
            "Hi " + booking.getUser().getEmail() + ",",
            "<p style='color:#555;line-height:1.8'>Your vehicle has been booked successfully.</p>" +
            "<table style='width:100%;border-collapse:collapse;margin:16px 0'>" +
            "  <tr style='background:#1a1a2e;color:white'>" +
            "    <td style='padding:12px;font-weight:700' colspan='2'>BOOKING DETAILS</td>" +
            "  </tr>" +
            "  <tr><td style='padding:10px;background:#f8f9fa;font-weight:700'>Booking ID</td>" +
            "      <td style='padding:10px'>#" + booking.getId() + "</td></tr>" +
            "  <tr><td style='padding:10px;font-weight:700'>Vehicle</td>" +
            "      <td style='padding:10px'>" + booking.getVehicle().getName() + "</td></tr>" +
            "  <tr><td style='padding:10px;background:#f8f9fa;font-weight:700'>Type</td>" +
            "      <td style='padding:10px'>" + booking.getVehicle().getType() + "</td></tr>" +
            "  <tr><td style='padding:10px;font-weight:700'>Number Plate</td>" +
            "      <td style='padding:10px'>" + booking.getVehicle().getNumberPlate() + "</td></tr>" +
            "  <tr><td style='padding:10px;background:#f8f9fa;font-weight:700'>Rent Date</td>" +
            "      <td style='padding:10px'>" + booking.getRentDate() + "</td></tr>" +
            "  <tr><td style='padding:10px;font-weight:700'>Price/Day</td>" +
            "      <td style='padding:10px;color:#e94560;font-weight:700'>" +
            "         Rs." + booking.getVehicle().getRentalPricePerDay() + "</td></tr>" +
            "</table>" +
            "<div style='background:#fff3cd;border-radius:8px;padding:14px;margin-top:8px'>" +
            "  <strong>⚠️ Reminder:</strong> Vehicle must be returned by end of today." +
            "  You can extend up to 2 times." +
            "</div>" +
            "<a href='" + baseUrl + "/my-bookings.html' " +
            "   style='display:inline-block;background:#e94560;color:white;" +
            "          padding:12px 28px;border-radius:8px;text-decoration:none;" +
            "          font-weight:700;margin-top:16px'>View My Bookings</a>"
        );
    }

    private String buildReturnEmail(Booking booking, double totalCharge) {
        return emailWrapper(
            "Vehicle Returned Successfully 🎉",
            "Hi " + booking.getUser().getEmail() + ",",
            "<p style='color:#555;line-height:1.8'>Your vehicle has been returned. Here is your summary:</p>" +
            "<table style='width:100%;border-collapse:collapse;margin:16px 0'>" +
            "  <tr style='background:#1a1a2e;color:white'>" +
            "    <td style='padding:12px;font-weight:700' colspan='2'>RETURN SUMMARY</td>" +
            "  </tr>" +
            "  <tr><td style='padding:10px;background:#f8f9fa;font-weight:700'>Booking ID</td>" +
            "      <td style='padding:10px'>#" + booking.getId() + "</td></tr>" +
            "  <tr><td style='padding:10px;font-weight:700'>Vehicle</td>" +
            "      <td style='padding:10px'>" + booking.getVehicle().getName() + "</td></tr>" +
            "  <tr><td style='padding:10px;background:#f8f9fa;font-weight:700'>Rented On</td>" +
            "      <td style='padding:10px'>" + booking.getRentDate() + "</td></tr>" +
            "  <tr><td style='padding:10px;font-weight:700'>Returned On</td>" +
            "      <td style='padding:10px'>" + booking.getReturnDate() + "</td></tr>" +
            "  <tr style='background:#fff3cd'>" +
            "    <td style='padding:12px;font-weight:700'>Total Charged</td>" +
            "    <td style='padding:12px;font-weight:700;color:#e94560;font-size:16px'>" +
            "       Rs." + String.format("%.2f", totalCharge) + "</td></tr>" +
            "</table>" +
            "<p style='color:#555'>Your PDF invoice is available in the My Bookings section.</p>" +
            "<a href='" + baseUrl + "/my-bookings.html' " +
            "   style='display:inline-block;background:#28a745;color:white;" +
            "          padding:12px 28px;border-radius:8px;text-decoration:none;" +
            "          font-weight:700;margin-top:8px'>Download Invoice</a>"
        );
    }

    private String buildBlockedEmail(User user) {
        return emailWrapper(
            "Account Status Update",
            "Hi " + user.getEmail() + ",",
            "<p style='color:#555;line-height:1.8'>" +
            "Your account has been temporarily blocked by the administrator." +
            "</p>" +
            "<div style='background:#f8d7da;border-radius:8px;padding:14px;margin-top:8px'>" +
            "  <strong>Your account is currently blocked.</strong><br>" +
            "  Please contact support if you believe this is an error." +
            "</div>"
        );
    }

    private String emailWrapper(String title, String greeting, String content) {
        return "<!DOCTYPE html><html><body style='margin:0;padding:0;" +
               "font-family:Segoe UI,sans-serif;background:#f0f2f5'>" +
               "<div style='max-width:600px;margin:32px auto;background:white;" +
               "           border-radius:16px;overflow:hidden;" +
               "           box-shadow:0 4px 16px rgba(0,0,0,0.1)'>" +

               "  <div style='background:#1a1a2e;padding:28px 32px;text-align:center'>" +
               "    <h1 style='color:#e94560;margin:0;font-size:22px'>Vehicle Rental System</h1>" +
               "    <p style='color:#aaa;margin:6px 0 0;font-size:13px'>Your trusted rental partner</p>" +
               "  </div>" +

               "  <div style='padding:32px'>" +
               "    <h2 style='color:#1a1a2e;margin:0 0 8px'>" + title + "</h2>" +
               "    <p style='color:#666;margin:0 0 20px'>" + greeting + "</p>" +
               content +
               "  </div>" +

               "  <div style='background:#f8f9fa;padding:20px 32px;text-align:center'>" +
               "    <p style='color:#999;font-size:12px;margin:0'>" +
               "      Vehicle Rental System | Do not reply to this email<br>" +
               "      <a href='" + baseUrl + "' style='color:#e94560'>Visit our website</a>" +
               "    </p>" +
               "  </div>" +
               "</div>" +
               "</body></html>";
    }
}
