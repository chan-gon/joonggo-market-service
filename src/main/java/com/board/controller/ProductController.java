package com.board.controller;

import java.util.List;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.board.domain.ImageVO;
import com.board.domain.ProductLikeVO;
import com.board.domain.ProductVO;
import com.board.domain.UserVO;
import com.board.service.ImageService;
import com.board.service.ProductLikeService;
import com.board.service.ProductService;
import com.board.service.UserService;
import com.board.util.ImageFileUtils;
import com.board.util.LoginUserUtils;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {
	
	private final UserService userService;
	
	private final ProductService productService;
	
	private final ImageService imageService;
	
	private final ProductLikeService productLikeService;
	
	/*
	 * 비즈니스 로직
	 */
	
	@PostMapping("/new")
	public void addNewProduct(@RequestPart List<MultipartFile> productImage,
			@RequestPart ProductVO product, @RequestPart UserVO user) {
		productService.addNewProduct(user.getUserId(), product, productImage);
	}
	
	@PostMapping("/like/{prdtId}")
	public void likeProduct(@PathVariable("prdtId") String prdtId) {
		productService.likeProuct(prdtId);
	}
	
	@PostMapping("/unlike/{prdtId}")
	public void unlikeProduct(@PathVariable("prdtId") String prdtId) {
		productService.unlikeProuct(prdtId);
	}
	
	@PostMapping("/delete/{prdtId}")
	public void deleteProduct(@PathVariable("prdtId") String prdtId) {
		productService.deleteProduct(prdtId);
	}
	
	@PostMapping("/update/{prdtId}")
	public void updateProduct(@PathVariable("prdtId") String prdtId, @RequestPart List<MultipartFile> productImage,
			@RequestPart ProductVO product) {
		productService.updateProduct(product, productImage);
	}
	
	/*
	 * 페이지 호출
	 */
	
	@GetMapping("/new")
	public ModelAndView newProduct(Model model) {
		try {
			String username = LoginUserUtils.getUserId();
			model.addAttribute("users", userService.getUserById(username));
			return new ModelAndView("product/newProduct");
		} catch (NullPointerException e) {
			throw new NullPointerException();
		}
	}
	
	@GetMapping("/{prdtId}")
	public ModelAndView getProduct(@PathVariable("prdtId") String prdtId, Model model) {
		ProductVO selectedProduct = productService.getProductById(prdtId);
		List<ImageVO> productImages = imageService.getImagesById(prdtId);
		ProductLikeVO productLike = productLikeService.isLikedOrNot(prdtId);
		model.addAttribute("productLike", productLike);
		model.addAttribute("images", productImages);
		model.addAttribute("product", selectedProduct);
		model.addAttribute("s3URL", ImageFileUtils.AWS_S3_URL);
		return new ModelAndView("product/productDetail");
	}
	
	@GetMapping("/shop")
	public ModelAndView myShop(Model model) {
		List<ProductVO> productList = productService.getProductListById();
		List<ProductVO> likeProductList = productService.getLikeProduct();
		model.addAttribute("products", productList);
		model.addAttribute("likeProductList", likeProductList);
		model.addAttribute("s3URL", ImageFileUtils.AWS_S3_URL);
		return new ModelAndView("product/shop");
	}
	
	@GetMapping("/update/{prdtId}")
	public ModelAndView productUpdate(@PathVariable("prdtId") String prdtId, Model model) {
		ProductVO product = productService.getProductById(prdtId);
		model.addAttribute("product", product);
		return new ModelAndView("product/update");
	}
}
