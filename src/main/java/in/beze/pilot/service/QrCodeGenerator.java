package in.beze.pilot.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import in.beze.pilot.repository.SprintRepository;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class QrCodeGenerator {

    private static final Logger LOG = LoggerFactory.getLogger(QrCodeGenerator.class);

    public QrCodeGenerator() {}

    public byte[] generateQRCode(String id, int height, int width) throws WriterException, IOException {
        QRCodeWriter writer = new QRCodeWriter();

        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

        BitMatrix bitMatrix = writer.encode(id, BarcodeFormat.QR_CODE, width, height, hints);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", baos);

        return baos.toByteArray();
    }

    public byte[] generateBarCode(String id, int height, int width) throws WriterException, IOException {
        // Use MultiFormatWriter for generating barcodes
        MultiFormatWriter writer = new MultiFormatWriter();

        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

        // Use BarcodeFormat.CODE_128 for barcode generation
        BitMatrix bitMatrix = writer.encode(id, BarcodeFormat.CODE_128, width, height, hints);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", baos);

        return baos.toByteArray();
    }
}
