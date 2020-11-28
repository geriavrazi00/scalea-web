package com.scalea.controllers;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.Hashtable;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.Writer;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;
import com.google.zxing.oned.EAN13Writer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.scalea.utils.Constants;

@Controller
@RequestMapping("/barcode")
public class BarCodeController {
	
	@PreAuthorize("hasAuthority('" + Constants.VIEW_BARCODE_PRIVILEGE + "')")
	@GetMapping
	public String barCodeIndex(Model model) throws Exception {
		UUID uuid = UUID.randomUUID();
		String imageAsBase64 = this.getBarCodeImage(uuid.toString(), 300, 150);
		
		UUID uuid2 = UUID.randomUUID();
		String imageAsBase64_2 = this.getBarCodeImage(uuid2.toString(), 300, 150);
		
		model.addAttribute("code1", uuid);
		model.addAttribute("image1", imageAsBase64);
		model.addAttribute("code2", uuid2);
		model.addAttribute("image2", imageAsBase64_2);
		return "private/barcode";
	}
	
	public String generateEAN13BarcodeImage() throws Exception {
		String text = "geriavraziaa";
		
	    EAN13Writer barcodeWriter = new EAN13Writer();
	    BitMatrix bitMatrix = barcodeWriter.encode(text, BarcodeFormat.EAN_13, 300, 150);
	    
	    BufferedImage img = MatrixToImageWriter.toBufferedImage(bitMatrix);
	    
	    ByteArrayOutputStream output = new ByteArrayOutputStream();
	    ImageIO.write(img, "png", output);
	    return Base64.getEncoder().encodeToString(output.toByteArray());
	}
	
	
	public String getBarCodeImage(String text, int width, int height) {
		try {
			Hashtable<EncodeHintType, ErrorCorrectionLevel> hintMap = new Hashtable<>();
			hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
			Writer writer = new Code128Writer();
			BitMatrix bitMatrix = writer.encode(text, BarcodeFormat.CODE_128, width, height);
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			MatrixToImageWriter.writeToStream(bitMatrix, "png", byteArrayOutputStream);
			return Base64.getEncoder().encodeToString( byteArrayOutputStream.toByteArray());
//			return byteArrayOutputStream.toByteArray();
		} catch (Exception e) {
			return null;
		}
	}
}
