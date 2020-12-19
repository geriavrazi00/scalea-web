package com.scalea.controllers;

import java.io.IOException;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.scalea.configurations.Messages;
import com.scalea.entities.Product;
import com.scalea.repositories.ProductRepository;
import com.scalea.services.ConfigurationService;
import com.scalea.utils.Constants;
import com.scalea.utils.FileUploadUtil;

@Controller
@RequestMapping("/products")
public class ProductController {

	private ProductRepository productRepo;
	private ConfigurationService configService;
	private Logger log;
	private Messages messages;
	
	@Autowired
	public ProductController(ProductRepository productRepo, ConfigurationService configService, Messages messages) {
		this.productRepo = productRepo;
		this.configService = configService;
		this.log = LoggerFactory.getLogger(ProductController.class);
		this.messages = messages;
	}
	
	@PreAuthorize("hasAnyAuthority('" + Constants.VIEW_PRODUCTS_PRIVILEGE + "', '" + Constants.UPSERT_PRODUCTS_PRIVILEGE + "', '" + Constants.DELETE_PRODUCTS_PRIVILEGE + "')")
	@GetMapping
	public String allProducts(Model model) {
		log.info("Method allProducts()");
		Iterable<Product> products = productRepo.findByEnabled(true);
		
		model.addAttribute("products", products);
		return "private/products/productlist";
	}
	
	@PreAuthorize("hasAuthority('" + Constants.UPSERT_PRODUCTS_PRIVILEGE + "'")
	@GetMapping("/create")
	public String newProduct(Model model) {
		log.info("Method newProduct()");
		
		model.addAttribute("product", new Product());
		return "private/products/createproduct";
	}
	
	@PreAuthorize("hasAuthority('" + Constants.UPSERT_PRODUCTS_PRIVILEGE + "'")
	@PostMapping("/create")
	public String createProduct(@Valid Product product, Errors errors, Model model, RedirectAttributes redirectAttributes, 
			@RequestParam("img") MultipartFile multipartFile) throws IOException {
		log.info("Method createProduct()");
		
		if (errors.hasErrors()) {
			return "private/products/createproduct";
		}
		
		// While creating the image, we check if one was selected to upload. If so, we save it in the storage of the system. If not, we simply set the default image value and not write anything in the storage
		String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
		String storedFileName = Constants.PRODUCTS_DEFAULT_IMAGE;
		
		if (fileName != null && !fileName.isEmpty()) {
			String fileExtension = fileName.substring(fileName.lastIndexOf('.'));
			storedFileName = product.getName().toLowerCase() + fileExtension;
			
			String uploadDir = configService.findValueByName(Constants.IMAGE_PATH) + Constants.PRODUCTS_IMAGE_SYSTEM_PATH;
	        FileUploadUtil.saveFile(uploadDir, storedFileName, multipartFile);
		}
		
		product.setImage(storedFileName);
		this.productRepo.save(product);
		
		redirectAttributes.addFlashAttribute("message", this.messages.get("messages.product.created"));
	    redirectAttributes.addFlashAttribute("alertClass", "alert-success");
		
		return "redirect:/products";
	}
}
