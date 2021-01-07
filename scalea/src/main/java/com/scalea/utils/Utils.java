package com.scalea.utils;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.Writer;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

public class Utils {
	
	public static String generateUUID() throws Exception {
		return UUID.randomUUID().toString();
	}
	
	public static String generateUniqueVacancyCodes() {
		return UUID.randomUUID().getLeastSignificantBits() + "";
	}
	
	public static String getBarCodeImage(String text, int width, int height) {
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
	
	public static String millisToString(long millis) {
		Duration timeLeft = Duration.ofMillis(millis);
		return String.format("%02d:%02d:%02d", timeLeft.toHours(), timeLeft.toMinutesPart(), timeLeft.toSecondsPart());
	}
	
	public static String albanianDateFormat(Date date) {
		DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");  
		return dateFormat.format(date);  
	}
	
	public static List<Integer> getPageNumbersList(int totalPages) {
		List<Integer> pageNumbers = new ArrayList<>();
		
		if (totalPages > 0) {
            pageNumbers = IntStream.rangeClosed(1, totalPages).boxed().collect(Collectors.toList());
        }
		
		return pageNumbers;
	}
}
