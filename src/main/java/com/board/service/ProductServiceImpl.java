package com.board.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.board.domain.Criteria;
import com.board.domain.ImageVO;
import com.board.domain.ProductVO;
import com.board.domain.UserVO;
import com.board.exception.product.ProductExceptionMessage;
import com.board.exception.product.ProductNotFoundException;
import com.board.mapper.ImageMapper;
import com.board.mapper.ProductMapper;
import com.board.mapper.UserMapper;
import com.board.util.ImageFileUtils;
import com.board.util.LoginUserUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
	
	private final UserMapper userMapper;
	
	private final ProductMapper productMapper;
	
	private final ImageMapper imageMapper;
	
	/**
	 * 	파일 등록 작업에 productId가 사용되기 때문에 테이블 PK인 productId의 난수값 생성을 
	 * 	DB 내부가 아닌 비즈니스 로직에서 생성하도록 했다.
	 *	
	 */
	@Override
	@Transactional
	public void addNewProduct(String userId, ProductVO product, List<MultipartFile> productImage) {
		// 상품 등록
		UserVO userInfo = userMapper.getUserById(userId);
		String productId = UUID.randomUUID().toString().replace("-", "");
		ProductVO newProduct = ProductVO.builder()
				.prdtId(productId)
				.accountId(userInfo.getAccountId())
				.prdtName(product.getPrdtName())
				.prdtPrice(product.getPrdtPrice())
				.prdtCategory(product.getPrdtCategory())
				.prdtInfo(product.getPrdtInfo())
				.prdtCondition(product.getPrdtCondition())
				.prdtIsTradeable(product.getPrdtIsTradeable())
				.prdtIsDeliveryFree(product.getPrdtIsDeliveryFree())
				.prdtTradeLoc(product.getPrdtTradeLoc())
				.build();
		productMapper.addNewProduct(newProduct);
		
		if (productImage.isEmpty()) {
			throw new NoSuchElementException("업로드된 이미지가 없습니다.");
		}
		// 이미지 등록
		for (MultipartFile image : productImage) {
			String filePath = ImageFileUtils.getFilePath();
			String fileName = ImageFileUtils.getFileName(image);
			ImageFileUtils.saveImages(filePath, fileName, image);
			ImageVO newImage = ImageVO.builder()
					.prdtId(productId)
					.fileName(fileName)
					.filePath(filePath)
					.build();
			imageMapper.addImages(newImage);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public ProductVO getProductById(String prdtId) {
		ProductVO product = productMapper.getProductById(prdtId);
		if(product == null) {
			throw new ProductNotFoundException(ProductExceptionMessage.NOT_FOUND);
		}
		return product;
	}

	@Override
	@Transactional(readOnly = true)
	@Cacheable(value = "productListCache", keyGenerator = "keyGenerator")
	public List<ProductVO> getProductList(Criteria cri) {
		return productMapper.getListWithPaging(cri);
	}

	@Override
	@Transactional
	public void likeProuct(String prdtId) {
		String currentUserId = LoginUserUtils.getUserId();
		String accountId = userMapper.getAccountId(currentUserId);
		ProductVO addLikeProduct = ProductVO.builder()
				.prdtId(prdtId)
				.accountId(accountId)
				.build();
		productMapper.likeProuct(prdtId);
		productMapper.addLikeProduct(addLikeProduct);
	}

	@Override
	@Transactional
	public void unlikeProuct(String prdtId) {
		String currentUserId = LoginUserUtils.getUserId();
		String accountId = userMapper.getAccountId(currentUserId);
		ProductVO deleteLikeProduct = ProductVO.builder()
				.prdtId(prdtId)
				.accountId(accountId)
				.build();
		productMapper.unlikeProuct(prdtId);
		productMapper.deleteLikeProduct(deleteLikeProduct);
	}
	
	@Override
	public List<ProductVO> getLikeProduct() {
		String currentUserId = LoginUserUtils.getUserId();
		String accountId = userMapper.getAccountId(currentUserId);
		return productMapper.getLikeProduct(accountId);
	}

	@Override
	@Transactional(readOnly = true)
	public List<ProductVO> getProductListById() {
		String userId = LoginUserUtils.getUserId();
		String accountId = userMapper.getAccountId(userId);
		return productMapper.getProductListById(accountId);
	}

	@Override
	@Transactional
	public void deleteProduct(String prdtId) {
		String userId = LoginUserUtils.getUserId();
		String accountId = userMapper.getAccountId(userId);
		List<ImageVO> localImages = imageMapper.getImagesById(prdtId);
		for (int i = 0; i < localImages.size(); i++) {
			ImageFileUtils.deleteImages(localImages.get(i));
		}
		imageMapper.deleteImages(prdtId);
		productMapper.deleteProduct(accountId, prdtId);
	}

	@Override
	@Transactional
	public void updateProduct(ProductVO product, List<MultipartFile> productImage) {
		ProductVO updateProduct = ProductVO.builder()
				.prdtId(product.getPrdtId())
				.prdtName(product.getPrdtName())
				.prdtPrice(product.getPrdtPrice())
				.prdtCategory(product.getPrdtCategory())
				.prdtTradeLoc(product.getPrdtTradeLoc())
				.prdtCondition(product.getPrdtCondition())
				.prdtIsTradeable(product.getPrdtIsTradeable())
				.prdtIsDeliveryFree(product.getPrdtIsDeliveryFree())
				.prdtInfo(product.getPrdtInfo())
				.build();
		productMapper.updateProduct(updateProduct);
		
		String prdtId = product.getPrdtId();
		if (!productImage.isEmpty()) {
			List<ImageVO> localImages = imageMapper.getImagesById(prdtId);
			for (ImageVO image : localImages) {
				ImageFileUtils.deleteImagesPermanent(image.getFileName());
			}
			imageMapper.deleteImages(prdtId);
			
			// 업데이트 이미지 등록
			for (int i = 0; i < productImage.size(); i++) {
				String filePath = ImageFileUtils.getFilePath();
				String fileName = ImageFileUtils.getFileName(productImage.get(i));
				ImageFileUtils.saveImages(filePath, fileName, productImage.get(i));
				ImageVO newImage = ImageVO.builder()
						.prdtId(prdtId)
						.fileName(fileName)
						.filePath(filePath)
						.build();
				imageMapper.addImages(newImage);
			}
		}
	}

	@Override
	@Transactional(readOnly = true)
	public int getProductCount(Criteria cri) {
		return productMapper.getProductCount(cri);
	}

}
