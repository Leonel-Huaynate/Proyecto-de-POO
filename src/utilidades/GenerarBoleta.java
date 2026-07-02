
package utilidades;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import modelo.Venta;
import modelo.DetalleVenta;
import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;

public class GenerarBoleta {
    
    public static void generar(Venta venta) {
        try {
            // ── PASO 1: Crear carpeta "boletas" si no existe ──
            File carpeta = new File("boletas");
            if (!carpeta.exists()) {
                carpeta.mkdir();
            }

            // Nombre del archivo: boletas/boleta_15.pdf
            String ruta = "boletas/boleta_" + venta.getId() + ".pdf";

            // ── PASO 2: Crear el documento PDF tamaño A5 ──
            Document documento = new Document(PageSize.A5);
            PdfWriter.getInstance(documento,
                    new FileOutputStream(ruta));
            documento.open();

            // ── PASO 3: Definir las fuentes que vamos a usar ──
            Font fontTitulo = new Font(Font.FontFamily.HELVETICA,
                    16, Font.BOLD);
            Font fontSubtitulo = new Font(Font.FontFamily.HELVETICA,
                    11, Font.BOLD);
            Font fontNormal = new Font(Font.FontFamily.HELVETICA,
                    10, Font.NORMAL);

            // ── PASO 4: Encabezado del minimarket ──
            Paragraph titulo = new Paragraph(
                    "MINIMARKET \"LA FAVORITA\"", fontTitulo);
            titulo.setAlignment(Element.ALIGN_CENTER);
            documento.add(titulo);

            Paragraph direccion = new Paragraph(
                    "Comas, Lima - Perú", fontNormal);
            direccion.setAlignment(Element.ALIGN_CENTER);
            documento.add(direccion);

            documento.add(new Paragraph(" ")); // espacio en blanco

            Paragraph boletaTitulo = new Paragraph(
                    "BOLETA DE VENTA", fontSubtitulo);
            boletaTitulo.setAlignment(Element.ALIGN_CENTER);
            documento.add(boletaTitulo);

            documento.add(new Paragraph(" "));

            // ── PASO 5: Datos de la venta ──
            SimpleDateFormat formatoFecha =
                    new SimpleDateFormat("dd/MM/yyyy HH:mm");

            documento.add(new Paragraph(
                    "Nº Venta: " + venta.getId(), fontNormal));
            documento.add(new Paragraph(
                    "Fecha: " + formatoFecha.format(venta.getFecha()),
                    fontNormal));
            documento.add(new Paragraph(
                    "Cajero: " + venta.getNombreCajero(), fontNormal));

            // Cliente — solo si está registrado
            if (venta.tieneCliente()) {
                documento.add(new Paragraph(
                        "Cliente: " + venta.getCliente().nombreCompleto(),
                        fontNormal));
                documento.add(new Paragraph(
                        "DNI: " + venta.getCliente().getDni(),
                        fontNormal));
            } else {
                documento.add(new Paragraph(
                        "Cliente: Sin registrar", fontNormal));
            }

            documento.add(new Paragraph(" "));

            // ── PASO 6: Tabla de productos ──
            // 4 columnas: Producto, Cant., P.Unit, Subtotal
            PdfPTable tabla = new PdfPTable(4);
            tabla.setWidthPercentage(100); // ocupa todo el ancho

            // Anchos relativos de cada columna
            tabla.setWidths(new float[]{4f, 1f, 1.5f, 1.5f});

            // Encabezados de la tabla (fondo gris)
            agregarCelda(tabla, "Producto", fontSubtitulo, true);
            agregarCelda(tabla, "Cant.", fontSubtitulo, true);
            agregarCelda(tabla, "P.Unit", fontSubtitulo, true);
            agregarCelda(tabla, "Subtotal", fontSubtitulo, true);

            // Una fila por cada producto del carrito
            for (DetalleVenta d : venta.getDetalles()) {
                agregarCelda(tabla, d.getNombreProducto(),
                        fontNormal, false);
                agregarCelda(tabla, String.valueOf(d.getCantidad()),
                        fontNormal, false);
                agregarCelda(tabla,
                        String.format("S/ %.2f", d.getPrecioUnitario()),
                        fontNormal, false);
                agregarCelda(tabla,
                        String.format("S/ %.2f", d.getSubtotal()),
                        fontNormal, false);
            }

            documento.add(tabla);
            documento.add(new Paragraph(" "));

            // ── PASO 7: Total y forma de pago ──
            Paragraph total = new Paragraph(
                    "TOTAL: " + venta.getTotalFormateado(),
                    fontTitulo);
            total.setAlignment(Element.ALIGN_RIGHT);
            documento.add(total);

            documento.add(new Paragraph(
                    "Forma de pago: " + venta.getTipoPago(),
                    fontNormal));

            documento.add(new Paragraph(" "));

            // ── PASO 8: Mensaje final ──
            Paragraph gracias = new Paragraph(
                    "¡Gracias por su compra!", fontNormal);
            gracias.setAlignment(Element.ALIGN_CENTER);
            documento.add(gracias);

            // ── PASO 9: Cerrar el documento (guarda el PDF) ──
            documento.close();

            // ── PASO 10: Abrir el PDF automáticamente ──
            File archivoPdf = new File(ruta);
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(archivoPdf);
            }

        } catch (Exception e) {
            Mensajes.error("Error al generar la boleta: "
                          + e.getMessage());
        }
    }

    // ── Método auxiliar para crear cada celda de la tabla ──
    private static void agregarCelda(PdfPTable tabla, String texto,
                                       Font font, boolean esEncabezado) {
        PdfPCell celda = new PdfPCell(new Phrase(texto, font));
        celda.setPadding(5);

        if (esEncabezado) {
            celda.setBackgroundColor(BaseColor.LIGHT_GRAY);
            celda.setHorizontalAlignment(Element.ALIGN_CENTER);
        }
        tabla.addCell(celda);
    }
}
    

