package com.scalea.utils;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.imageio.ImageIO;

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
	
	public static String getBarCodeImage(String text, int width, int height, String label) {
		try {
			Hashtable<EncodeHintType, ErrorCorrectionLevel> hintMap = new Hashtable<>();
			hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
			Writer writer = new Code128Writer();
			BitMatrix bitMatrix = writer.encode(text, BarcodeFormat.CODE_128, width, height);
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			MatrixToImageWriter.writeToStream(bitMatrix, "png", byteArrayOutputStream);
			byte[] barcodeByteArray = byteArrayOutputStream.toByteArray();
			
			if (label != null && label.length() > 0) barcodeByteArray = addLabelToBarcode(byteArrayOutputStream, height, label);
			
			return Base64.getEncoder().encodeToString(barcodeByteArray);
		} catch (Exception e) {
			return null;
		}
	}
	
	private static byte[] addLabelToBarcode(ByteArrayOutputStream byteArrayOutputStream, int height, String label) throws IOException { 
		int totalTextLineToAdd = 1;
		byte[] data = byteArrayOutputStream.toByteArray();
		
        InputStream in = new ByteArrayInputStream(data);
        BufferedImage image = ImageIO.read(in);

        BufferedImage outputImage = new BufferedImage(image.getWidth(), image.getHeight() + 25 * totalTextLineToAdd, BufferedImage.TYPE_INT_ARGB);
        Graphics g = outputImage.getGraphics();
        g.setColor(Color.WHITE);
        
        g.fillRect(0, 0, outputImage.getWidth(), outputImage.getHeight());
        g.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
        g.setFont(new Font("Arial Black", Font.PLAIN, 14));
        Color textColor = Color.BLACK;
        g.setColor(textColor);
        FontMetrics fm = g.getFontMetrics();
        int startingYposition = height + 15;
        
        // Drawing the label on the image
        g.drawString(label, (outputImage.getWidth() / 2)   - (fm.stringWidth(label) / 2), startingYposition);
        startingYposition += 20;
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(outputImage, "png", baos);
        baos.flush();
        data = baos.toByteArray();
        baos.close();
        
        return data;
	}
	
	public static String millisToString(long millis) {
		Duration timeLeft = Duration.ofMillis(millis);
		return String.format("%02d:%02d:%02d", timeLeft.toHours(), timeLeft.toMinutesPart(), timeLeft.toSecondsPart());
	}
	
	public static String albanianDateFormat(Date date) {
		DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
		return dateFormat.format(date);
	}
	
	public static String albanianFullDateFormat(Date date) {
		DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
		return dateFormat.format(date);
	}
	
	public static Date inputDateStringToDate(String dateString) throws ParseException {
		return (dateString != null && !dateString.isBlank()) ? new SimpleDateFormat("yyyy-MM-dd").parse(dateString) : null;  
	}
	
	public static String dateToDateString(Date date) {
		return date != null ? new SimpleDateFormat("yyyy-MM-dd").format(date) : null;
	}
	
	public static List<Integer> getPageNumbersList(int totalPages) {
		List<Integer> pageNumbers = new ArrayList<>();
		
		if (totalPages > 0) {
            pageNumbers = IntStream.rangeClosed(1, totalPages).boxed().collect(Collectors.toList());
        }
		
		return pageNumbers;
	}
	
	public static Date addOrRemoveDaysToDate(Date date, int days) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.DATE, days);  	// number of days to add or remove
		return c.getTime();  		// dt is now the new date
	}
	
	/*
	 * Months start from 0 (January) to 11 (December)
	 */
	public static int getMonthFromDate(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.MONTH) + 1;
	}
	
	public static int getYearFromDate(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.YEAR);
	}
	
	public static double twoDecimalPlacesDouble(Double number) {
		if (number == null) number = 0.0;
		
		DecimalFormat df = new DecimalFormat("#.##");
		String formattedNumber = df.format(number);
		return Double.valueOf(formattedNumber);
	}
	
	public static String thousandSeparatorDouble(double number) {
		NumberFormat formatter = NumberFormat.getInstance(new Locale("en_US"));
		return formatter.format(number);
	}
	
	public static String getTimeFromDate(Date date) {
		DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");  
        return dateFormat.format(date);  
	}
}
