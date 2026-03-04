package com.example.offlineaccounting.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import com.example.offlineaccounting.data.Invoice
import com.example.offlineaccounting.data.InvoiceItem
import com.example.offlineaccounting.data.StoreProfile
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object InvoiceExporter {

    fun exportPdf(context: Context, profile: StoreProfile, invoice: Invoice, items: List<InvoiceItem>): String {
        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = document.startPage(pageInfo)
        drawInvoice(page.canvas, profile, invoice, items)
        document.finishPage(page)

        val file = File(context.getExternalFilesDir(null), "invoice_${invoice.id}.pdf")
        FileOutputStream(file).use { document.writeTo(it) }
        document.close()
        return file.absolutePath
    }

    fun exportImage(context: Context, profile: StoreProfile, invoice: Invoice, items: List<InvoiceItem>): String {
        val bitmap = Bitmap.createBitmap(1080, 1600, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.WHITE)
        drawInvoice(canvas, profile, invoice, items)

        val file = File(context.getExternalFilesDir(null), "invoice_${invoice.id}.png")
        FileOutputStream(file).use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
        return file.absolutePath
    }

    private fun drawInvoice(canvas: Canvas, profile: StoreProfile, invoice: Invoice, items: List<InvoiceItem>) {
        val titlePaint = Paint().apply {
            color = profile.primaryColor.toInt()
            textSize = 44f
            isFakeBoldText = true
        }
        val bodyPaint = Paint().apply {
            color = Color.DKGRAY
            textSize = 28f
        }

        var y = 80f
        canvas.drawText(profile.storeName, 40f, y, titlePaint)
        y += 45
        canvas.drawText(profile.invoiceTitle, 40f, y, titlePaint)
        y += 45

        val date = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date(invoice.createdAt))
        canvas.drawText("Date: $date", 40f, y, bodyPaint)
        y += 40
        canvas.drawText("Phone: ${profile.phone}", 40f, y, bodyPaint)
        y += 60

        canvas.drawText("Item", 40f, y, bodyPaint)
        canvas.drawText("Qty", 500f, y, bodyPaint)
        canvas.drawText("Price", 640f, y, bodyPaint)
        canvas.drawText("Total", 820f, y, bodyPaint)
        y += 25
        canvas.drawLine(40f, y, 1040f, y, bodyPaint)
        y += 35

        items.forEach {
            canvas.drawText(it.productName, 40f, y, bodyPaint)
            canvas.drawText(it.quantity.toString(), 500f, y, bodyPaint)
            canvas.drawText(it.unitPrice.toString(), 640f, y, bodyPaint)
            canvas.drawText(it.lineTotal.toString(), 820f, y, bodyPaint)
            y += 35
        }

        y += 30
        canvas.drawLine(40f, y, 1040f, y, bodyPaint)
        y += 45
        canvas.drawText("Final Total: ${invoice.finalTotal}", 40f, y, titlePaint)
        y += 50
        if (invoice.notes.isNotBlank()) {
            canvas.drawText("Notes: ${invoice.notes}", 40f, y, bodyPaint)
        }
    }
}
