package com.scalea.controllers;

import java.io.IOException;
import java.util.Optional;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.scalea.configurations.Messages;
import com.scalea.entities.Product;
import com.scalea.exceptions.GenericException;
import com.scalea.models.dto.SubProductDTO;
import com.scalea.services.ProductService;
import com.scalea.utils.Constants;
import com.scalea.utils.Utils;

@Controller
@RequestMapping("/products")
public class ProductController {

	private ProductService productService;
	private Logger log;
	private Messages messages;
	
	private static final int DEFAULT_PAGE = 1;
	private static final int DEFAULT_SIZE = 5;
	
	@Autowired
	public ProductController(ProductService productService, Messages messages) {
		this.productService = productService;
		this.log = LoggerFactory.getLogger(ProductController.class);
		this.messages = messages;
	}
	
	@PreAuthorize("hasAnyAuthority('" + Constants.VIEW_PRODUCTS_PRIVILEGE + "', '" + Constants.UPSERT_PRODUCTS_PRIVILEGE + "', '" + Constants.DELETE_PRODUCTS_PRIVILEGE + "')")
	@GetMapping
	public String allProducts(Model model, @RequestParam("page") Optional<Integer> page, @RequestParam("size") Optional<Integer> size) throws IOException {
		log.info("Method allProducts()");
		
		int currentPage = page.orElse(DEFAULT_PAGE);
        int pageSize = size.orElse(DEFAULT_SIZE);
        Page<Product> products = productService.findByFatherProductIsNullAndEnabledIsTrue(PageRequest.of(currentPage - 1, pageSize));
		
		model.addAttribute("products", products);
		model.addAttribute("pageNumbers", Utils.getPageNumbersList(products.getTotalPages()));
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
			storedFileName = productService.savePhotoToDisk(multipartFile, fileName, product.getName());
		}
		
		product.setImage(storedFileName);
		this.productService.save(product);
		
		redirectAttributes.addFlashAttribute("message", this.messages.get("messages.product.created"));
	    redirectAttributes.addFlashAttribute("alertClass", "alert-success");
		
		return "redirect:/products";
	}
	
	@PreAuthorize("hasAuthority('" + Constants.UPSERT_PRODUCTS_PRIVILEGE + "'")
	@GetMapping("/edit/{id}")
	public String editProduct(@PathVariable("id") Long id, Model model) throws Exception {
		log.info("Method editProduct()");
		
		Optional<Product> product = productService.findById(id);
		if (!product.isPresent()) throw new GenericException(messages.get("messages.product.not.found"));
		if (product.get().getFatherProduct() != null) throw new GenericException(messages.get("messages.product.not.found"));
		
		String image = null;
		if (product.get().getImage() != null) image = productService.getBase64ImageString(product.get().getImage());
		
		model.addAttribute("imageSrc", image); 
		model.addAttribute("product", product.get());
		return "private/products/editproduct";
	}
	
	@PreAuthorize("hasAuthority('" + Constants.UPSERT_PRODUCTS_PRIVILEGE + "'")
	@PostMapping("/edit/{id}")
	public String updateProduct(@Valid Product product, Errors errors, Model model, RedirectAttributes redirectAttributes, 
			@RequestParam("img") MultipartFile multipartFile) throws GenericException, IOException {
		log.info("Method updateProduct()");
		
		if (errors.hasErrors()) {
			String image = null;
			if (product.getImage() != null) image = productService.getBase64ImageString(product.getImage());
			
			model.addAttribute("imageSrc", image); 
			return "private/products/editproduct";
		}
		
		Optional<Product> existingOptionalProduct = productService.findById(product.getId());
		if (!existingOptionalProduct.isPresent()) throw new GenericException(messages.get("messages.product.not.found"));
		Product existingProduct = existingOptionalProduct.get();
		
		/*
		 *  While creating the image, we check if one was selected to upload. If so, we save it in the storage of the system. If not, we simply set the 
		 *  default image value and not write anything in the storage. Also if the product has an image set and it's not the default one we should 
		 *  delete it from the storage so we don't stack the memory with unused images. Also, if no image has been selected from the user, we keep 
		 *  the old one.
		 */
		String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
		
		if (fileName != null && !fileName.isEmpty()) {
			if (existingProduct.getImage() != null && !existingProduct.getImage().equals(Constants.PRODUCTS_DEFAULT_IMAGE)) productService.deletePhotoFromDisk(existingProduct.getImage());
			String storedFileName = productService.savePhotoToDisk(multipartFile, fileName, product.getName());
			product.setImage(storedFileName);
		} else {
			product.setImage(existingProduct.getImage());
		}
		
		/*
		 * If the product has children products, but the "withSubProducts" option has been changes, we set the variable to false and the price to 0
		 * manually, and show a message to the user notifying this. We save the rest of the data.
		 */
		if (!product.isWithSubProducts() && existingProduct.getChildrenProducts() != null && existingProduct.getChildrenProducts().size() > 0) {
			redirectAttributes.addFlashAttribute("message", this.messages.get("messages.product.has.subproducts"));
		    redirectAttributes.addFlashAttribute("alertClass", "alert-warning");
		    
			product.setWithSubProducts(true);
			product.setPrice(0);
		} else {
			redirectAttributes.addFlashAttribute("message", this.messages.get("messages.product.updated"));
		    redirectAttributes.addFlashAttribute("alertClass", "alert-success");
		}
		
		productService.save(product);
	    return "redirect:/products";
	}
	
	@PreAuthorize("hasAuthority('" + Constants.DELETE_PRODUCTS_PRIVILEGE + "'")
	@PostMapping("/delete/{id}")
	public String deleteProduct(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) throws Exception {
		log.info("Method deleteProduct()");
		
		Optional<Product> optionalProduct = productService.findById(id);
		if (!optionalProduct.isPresent()) throw new GenericException(messages.get("messages.product.not.found"));
		Product product = optionalProduct.get();
		
		if (product.isWithSubProducts() && product.getChildrenProducts() != null && product.getChildrenProducts().size() > 0) {
			for (Product childProduct: product.getChildrenProducts()) {
				childProduct.setEnabled(false);
				productService.save(childProduct);
			}
		}
		
		product.setEnabled(false);
		productService.save(product);
		
		redirectAttributes.addFlashAttribute("message", this.messages.get("messages.product.deleted"));
	    redirectAttributes.addFlashAttribute("alertClass", "alert-success");
	    return "redirect:/products";
	}
	
	@PreAuthorize("hasAuthority('" + Constants.UPSERT_PRODUCTS_PRIVILEGE + "'")
	@GetMapping("/subproduct/create/{id}")
	public String newSubProduct(@PathVariable("id") Long id, Model model) throws GenericException {
		log.info("Method newSubProduct()");
		
		Optional<Product> fatherProduct = productService.findById(id);
		if (!fatherProduct.isPresent()) throw new GenericException(messages.get("messages.product.not.found"));
		if (!fatherProduct.get().isWithSubProducts()) throw new GenericException(messages.get("messages.product.not.with.subprdocuts"));
		SubProductDTO newProduct = new SubProductDTO();
		newProduct.setFatherProduct(fatherProduct.get());
		
		model.addAttribute("product", newProduct);
		return "private/products/subproducts/createsubproduct";
	}
	
	@PreAuthorize("hasAuthority('" + Constants.UPSERT_PRODUCTS_PRIVILEGE + "'")
	@PostMapping("/subproduct/create/{id}")
	public String createSubProduct(@Valid @ModelAttribute("product") SubProductDTO subProduct, Errors errors, Model model, RedirectAttributes redirectAttributes, 
			@RequestParam("img") MultipartFile multipartFile) throws IOException, GenericException {
		log.info("Method createSubProduct()");
		
		if (errors.hasErrors()) {
			return "private/products/subproducts/createsubproduct";
		}
		
		// While creating the image, we check if one was selected to upload. If so, we save it in the storage of the system. If not, we simply set the default image value and not write anything in the storage
		String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
		String storedFileName = Constants.PRODUCTS_DEFAULT_IMAGE;
		
		if (fileName != null && !fileName.isEmpty()) {
			storedFileName = productService.savePhotoToDisk(multipartFile, fileName, subProduct.getName());
		}
		
		subProduct.setImage(storedFileName);
		this.productService.save(subProduct.toNewProduct());
		
		redirectAttributes.addFlashAttribute("message", this.messages.get("messages.subproduct.created"));
	    redirectAttributes.addFlashAttribute("alertClass", "alert-success");
		
		return "redirect:/products";
	}
	
	@PreAuthorize("hasAuthority('" + Constants.UPSERT_PRODUCTS_PRIVILEGE + "'")
	@GetMapping("/subproduct/edit/{id}")
	public String editSubProduct(@PathVariable("id") Long id, Model model) throws Exception {
		log.info("Method editSubProduct()");
		
		Optional<Product> subProduct = productService.findById(id);
		if (!subProduct.isPresent()) throw new GenericException(messages.get("messages.product.not.found"));
		if (subProduct.get().getFatherProduct() == null) throw new GenericException(messages.get("messages.product.not.found"));
		
		String image = null;
		if (subProduct.get().getImage() != null) image = productService.getBase64ImageString(subProduct.get().getImage());
		
		SubProductDTO subProductDTO = new SubProductDTO();
		subProductDTO.toDTO(subProduct.get());
		
		model.addAttribute("imageSrc", image); 
		model.addAttribute("product", subProductDTO);
		return "private/products/subproducts/editsubproduct";
	}
	
	@PreAuthorize("hasAuthority('" + Constants.UPSERT_PRODUCTS_PRIVILEGE + "'")
	@PostMapping("/subproduct/edit/{id}")
	public String updateSubProduct(@Valid @ModelAttribute("product") SubProductDTO subProduct, Errors errors, Model model, RedirectAttributes redirectAttributes, 
			@RequestParam("img") MultipartFile multipartFile) throws GenericException, IOException {
		log.info("Method updateSubProduct()");
		
		if (errors.hasErrors()) {
			String image = null;
			if (subProduct.getImage() != null) image = productService.getBase64ImageString(subProduct.getImage());
			
			model.addAttribute("imageSrc", image); 
			return "private/products/subproducts/editsubproduct";
		}
		
		Optional<Product> existingOptionalProduct = productService.findById(subProduct.getId());
		if (!existingOptionalProduct.isPresent()) throw new GenericException(messages.get("messages.product.not.found"));
		if (existingOptionalProduct.get().getFatherProduct() == null) throw new GenericException(messages.get("messages.product.not.found"));
		Product existingProduct = existingOptionalProduct.get();
		
		/*
		 *  While creating the image, we check if one was selected to upload. If so, we save it in the storage of the system. If not, we simply set the 
		 *  default image value and not write anything in the storage. Also if the product has an image set and it's not the default one we should 
		 *  delete it from the storage so we don't stack the memory with unused images. Also, if no image has been selected from the user, we keep 
		 *  the old one.
		 */
		String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
		
		if (fileName != null && !fileName.isEmpty()) {
			if (existingProduct.getImage() != null && !existingProduct.getImage().equals(Constants.PRODUCTS_DEFAULT_IMAGE)) productService.deletePhotoFromDisk(existingProduct.getImage());
			String storedFileName = productService.savePhotoToDisk(multipartFile, fileName, subProduct.getName());
			subProduct.setImage(storedFileName);
		} else {
			subProduct.setImage(existingProduct.getImage());
		}
		
		productService.save(subProduct.toExistingProduct());
		
		redirectAttributes.addFlashAttribute("message", this.messages.get("messages.subproduct.updated"));
	    redirectAttributes.addFlashAttribute("alertClass", "alert-success");
	    return "redirect:/products";
	}
	
	@PreAuthorize("hasAuthority('" + Constants.DELETE_PRODUCTS_PRIVILEGE + "'")
	@PostMapping("/subproduct/delete/{id}")
	public String deleteSubProduct(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) throws Exception {
		log.info("Method deleteSubProduct()");
		
		Optional<Product> optionalSubProduct = productService.findById(id);
		if (!optionalSubProduct.isPresent()) throw new GenericException(messages.get("messages.product.not.found"));
		Product subProduct = optionalSubProduct.get();
		if (subProduct.getFatherProduct() == null) throw new GenericException(messages.get("messages.product.not.found"));
		
		subProduct.setEnabled(false);
		productService.save(subProduct);
		
		redirectAttributes.addFlashAttribute("message", this.messages.get("messages.subproduct.deleted"));
	    redirectAttributes.addFlashAttribute("alertClass", "alert-success");
	    return "redirect:/products";
	}
}
