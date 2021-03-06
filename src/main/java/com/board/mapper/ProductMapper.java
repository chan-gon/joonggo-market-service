package com.board.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.board.domain.Criteria;
import com.board.domain.ProductVO;

public interface ProductMapper {

	ProductVO getProductById(String prdtId);

	List<ProductVO> getProductList();
	
	List<ProductVO> getProductListById(String accountId);

	void addNewProduct(ProductVO product);

	void deleteProduct(@Param("accountId") String accountId, @Param("prdtId") String prdtId);

	void likeProuct(String prdtId);
	
	void unlikeProuct(String prdtId);
	
	void addLikeProduct(ProductVO product);
	
	void deleteLikeProduct(ProductVO product);
	
	List<ProductVO> getProductId(String accountId);
	
	List<ProductVO> getLikeProduct(String accountId);
	
	void updateProduct(ProductVO product);
	
	List<ProductVO> getListWithPaging(Criteria cri);
	
	int getProductCount(Criteria cri);
	
	/*
	 * 회원 탈퇴 관련
	 */
	void deleteProductPermanent(String accountId);
	
	void deleteProductLikePermanent(String prdtId);
	
	/*
	 * JUnit 테스트 전용
	 * - ProductControllerTest.java에서 사용.
	 */
	ProductVO getProductIdByName(String prdtName);
}
