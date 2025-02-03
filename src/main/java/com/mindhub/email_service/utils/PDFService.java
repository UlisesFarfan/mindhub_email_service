package com.mindhub.email_service.utils;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;

@Service
public class PDFService {

    @Autowired
    private RestTemplate restTemplate;

    public byte[] generatePurchasePDF(Map<String, Object> orderDetails, String username, String userEmail, String token) throws Exception {
        Number orderIdNumber = (Number) orderDetails.get("id");
        Long orderId = orderIdNumber != null ? orderIdNumber.longValue() : null;
        String status = (String) orderDetails.get("status");
        List<Map<String, Object>> products = (List<Map<String, Object>>) orderDetails.get("products");

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        PdfWriter writer = new PdfWriter(byteArrayOutputStream);
        Document document = new Document(new com.itextpdf.kernel.pdf.PdfDocument(writer));

        Paragraph title = new Paragraph("Micro Services App - Detalles de Compra")
                .setFontSize(20)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20);
        document.add(title);

        document.add(new Paragraph("Orden ID: " + orderId)
                .setBold());
        document.add(new Paragraph("Usuario: " + username));
        document.add(new Paragraph("Email: " + userEmail));
        document.add(new Paragraph("Estado: " + status)
                .setMarginBottom(20));

        Table productTable = new Table(UnitValue.createPercentArray(new float[]{40, 20, 20, 20}))
                .useAllAvailableWidth();

        productTable.addHeaderCell(new Cell().add(new Paragraph("Producto").setBackgroundColor(ColorConstants.LIGHT_GRAY)));
        productTable.addHeaderCell(new Cell().add(new Paragraph("Descripción").setBackgroundColor(ColorConstants.LIGHT_GRAY)));
        productTable.addHeaderCell(new Cell().add(new Paragraph("Cantidad").setBackgroundColor(ColorConstants.LIGHT_GRAY)));
        productTable.addHeaderCell(new Cell().add(new Paragraph("Precio").setBackgroundColor(ColorConstants.LIGHT_GRAY)));

        double total = 0;

        for (Map<String, Object> product : products) {
            Long productId = ((Number) product.get("productId")).longValue();
            String productUrl = "http://localhost:8080/api/products/" + productId;
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + token);
            HttpEntity<Map<String, Object>> userdata = new HttpEntity<>(headers);
            ResponseEntity<Map> productResponse = restTemplate.exchange(productUrl, HttpMethod.GET, userdata, Map.class);
            Map productDetails = productResponse.getBody();

            assert productDetails != null;
            String productName = (String) productDetails.get("name");
            String description = (String) productDetails.get("description");
            Double price = (Double) productDetails.get("price");
            Integer quantity = ((Number) product.get("quantity")).intValue();

            productTable.addCell(new Cell().add(new Paragraph(productName)));
            productTable.addCell(new Cell().add(new Paragraph(description)));
            productTable.addCell(new Cell().add(new Paragraph(String.valueOf(quantity))));
            productTable.addCell(new Cell().add(new Paragraph("$" + price)));
            total += price * quantity;
        }

        document.add(productTable);

        // Total
        Paragraph totalParagraph = new Paragraph("Total: $" + total)
                .setBold()
                .setTextAlignment(TextAlignment.RIGHT)
                .setMarginTop(20);
        document.add(totalParagraph);

        document.close();

        return byteArrayOutputStream.toByteArray();
    }
}
