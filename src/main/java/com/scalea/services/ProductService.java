package com.scalea.services;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.scalea.entities.Product;
import com.scalea.repositories.ProductRepository;
import com.scalea.utils.Constants;
import com.scalea.utils.FileUploadUtil;

@Service
@Transactional
public class ProductService {
	private ProductRepository productRepo;
	private ConfigurationService configService;

	@Autowired
	public ProductService(ProductRepository productRepo, ConfigurationService configService) {
		this.productRepo = productRepo;
		this.configService = configService;
	}
	
	public Iterable<Product> findByEnabled(boolean enabled) {
		return productRepo.findByEnabled(enabled);
	}
	
	public Page<Product> findByFatherProductIsNullAndEnabledIsTrue(Pageable pageable) throws Exception {
		Page<Product> products = productRepo.findByFatherProductIsNullAndEnabledIsTrueOrderByName(pageable);
		
		for (Product product: products) {
			product.setBase64Image(this.getBase64ImageString(product.getImage()));
		}
		
		return products;
	}
	
	public Iterable<Product> findByEnabledIsTrueAndWithSubProductsIsFalse() {
		return productRepo.findByEnabledIsTrueAndWithSubProductsIsFalseOrderByName();
	}
	
	public Product save(Product product) {
		return productRepo.save(product);
	}
	
	public Optional<Product> findById(Long id) {
		return productRepo.findById(id);
	}
	
	public Iterable<Product> findByFatherProductIsNullAndEnabledIsTrueOrderByName() {
		return productRepo.findByFatherProductIsNullAndEnabledIsTrueOrderByName();
	}
	
	public Optional<Product> findByIdAndFatherProductIsNullAndEnabledIsTrue(Long id) {
		return productRepo.findByIdAndFatherProductIsNullAndEnabledIsTrue(id);
	}
	
	public String savePhotoToDisk(MultipartFile multipartFile, String fileName, String desiredName) throws Exception {
		String fileExtension = fileName.substring(fileName.lastIndexOf('.'));
		String storedFileName = desiredName.toLowerCase() + "_" + System.currentTimeMillis() + fileExtension;
		
		String uploadDir = configService.findValueByName(Constants.IMAGE_PATH) + Constants.PRODUCTS_IMAGE_SYSTEM_PATH;
        FileUploadUtil.saveFile(uploadDir, storedFileName, multipartFile);
        return storedFileName;
	}
	
	public void deletePhotoFromDisk(String fileName) throws Exception {
		String fullFilePath = configService.findValueByName(Constants.IMAGE_PATH) + Constants.PRODUCTS_IMAGE_SYSTEM_PATH + fileName;
		File file = new File(fullFilePath); 
	    if (file.exists() && file.delete()) { 
	    	System.err.println("Deleted the file: " + file.getName());
	    } else {
	    	System.err.println("Failed to delete the file.");
	    }
	}
	
	public String getBase64ImageString(String imageName) throws Exception {
		String pathName = configService.findValueByName(Constants.IMAGE_PATH) + Constants.PRODUCTS_IMAGE_SYSTEM_PATH + imageName;
		try {
			File file = new File(pathName);
			byte[] fileContent = Files.readAllBytes(file.toPath());
	        return "data:image/png;base64, " + Base64.getEncoder().encodeToString(fileContent);
		} catch (IOException e) {
			System.err.println("File " + pathName + " not found!");
			
			pathName = configService.findValueByName(Constants.IMAGE_PATH) + Constants.PRODUCTS_IMAGE_SYSTEM_PATH + Constants.PRODUCTS_DEFAULT_IMAGE;
			File file = new File(pathName);
			byte[] fileContent = Files.readAllBytes(file.toPath());
	        return "data:image/png;base64, " + Base64.getEncoder().encodeToString(fileContent);
		}
	}
}
