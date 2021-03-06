package com.board.controller;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestPartFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;

import javax.servlet.ServletException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import com.board.domain.ProductVO;
import com.board.domain.UserVO;
import com.board.service.ProductService;
import com.google.gson.Gson;

/**
 * ????????? ????????? ????????? Spring Rest Docs ?????? ????????? ???????????? ?????? ???????????? ??????????????????.
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration({"file:src/main/webapp/WEB-INF/spring/root-context.xml", "file:src/main/webapp/WEB-INF/spring/appServlet/servlet-context.xml"})
@Transactional
public class ProductControllerTest {
	
	@Rule 
	public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation();
	
	@Autowired
	private ProductController productController;
	
	@Autowired
	private ProductService productService;
	
	@Autowired 
	private WebApplicationContext context;
	
	private MockMvc mockMvc;
	
	private RestDocumentationResultHandler document;
	
	private UserVO user;
	
	private ProductVO product;
	
	@Before
	public void setUp() throws ServletException {
		this.document = document("{class-name}/{method-name}", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()));
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
				.addFilters(new CharacterEncodingFilter("UTF-8", true))
				.apply(documentationConfiguration(this.restDocumentation))
				.alwaysDo(document).build();
	}
	
	@Before
	public void setUpMockMvc() throws Exception {
		this.mockMvc = MockMvcBuilders.standaloneSetup(productController).build();
	}
	
	@Before
	public void setUpUser() {
		user = UserVO.builder()
				.userId("changon")
				.userPwd("changon1234")
				.userName("?????????")
				.userEmail("changon@gmail.com")
				.userPhone("01012341234")
				.userAddr("?????? ????????? ??????")
				.build();
	}
	
	@Before
	public void setUpProduct() {
		product = ProductVO.builder()
				.prdtName("CD????????????")
				.prdtPrice("777")
				.prdtCategory("?????????")
				.prdtInfo("aiwa CD ???????????? ?????????.")
				.prdtCondition("?????????")
				.prdtIsTradeable("????????????")
				.prdtIsDeliveryFree("???????????????")
				.prdtLikeCnt(0)
				.prdtTradeLoc("?????? ??????")
				.build();
	}
	
	public void createUserOne() throws Exception {
		String jsonStr = new Gson().toJson(user);
		mockMvc.perform(RestDocumentationRequestBuilders.post("/users")
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonStr))
		.andExpect(status().isOk());
	}
	
	public void createUserTwo() throws Exception {
		user = UserVO.builder()
				.userId("bang")
				.userPwd("bang1234")
				.userName("?????????")
				.userEmail("bang@gmail.com")
				.userPhone("01002420404")
				.userAddr("?????? ?????? ???????????????")
				.build();
		String jsonStr = new Gson().toJson(user);
		mockMvc.perform(RestDocumentationRequestBuilders.post("/users")
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonStr))
		.andExpect(status().isOk());
	}
	
	@Test
	public void createProduct() throws Exception {
		// given
		createUserOne();
		
		user = UserVO.builder()
				.userId("changon")
				.build();
		
		String userJson = new Gson().toJson(user);
		String productJson = new Gson().toJson(product);
		MockMultipartFile imageFile = new MockMultipartFile("productImage", "CDPlayer.jpeg", "image/jpeg", "<<jpeg data>>".getBytes(StandardCharsets.UTF_8));
		MockMultipartFile userFile = new MockMultipartFile("user", "user", "application/json", userJson.getBytes(StandardCharsets.UTF_8));
		MockMultipartFile productFile = new MockMultipartFile("product", "product", "application/json", productJson.getBytes(StandardCharsets.UTF_8));
		
		// when
		mockMvc.perform(MockMvcRequestBuilders.multipart("/products/new")
				.file(imageFile)
				.file(userFile)
				.file(productFile)
				)
		.andDo(print())
		// then
		.andExpect(status().isOk())
		.andDo(document.document(
				requestParts(
						partWithName("productImage").description("?????? ?????? ?????????"),
						partWithName("user").description("?????? ??????"),
						partWithName("product").description("?????? ?????? ??????")
						),
				requestPartFields(
						"user", 
						fieldWithPath("userId").type(JsonFieldType.STRING).description("?????? ?????????")
						),
				requestPartFields(
						"product", 
						fieldWithPath("prdtName").type(JsonFieldType.STRING).description("?????? ??????"),
						fieldWithPath("prdtPrice").type(JsonFieldType.STRING).description("?????? ??????"),
						fieldWithPath("prdtCategory").type(JsonFieldType.STRING).description("?????? ????????????"),
						fieldWithPath("prdtInfo").type(JsonFieldType.STRING).description("?????? ??????"),
						fieldWithPath("prdtCondition").type(JsonFieldType.STRING).description("?????? ??????"),
						fieldWithPath("prdtIsTradeable").type(JsonFieldType.STRING).description("????????????"),
						fieldWithPath("prdtIsDeliveryFree").type(JsonFieldType.STRING).description("????????? ??????"),
						fieldWithPath("prdtLikeCnt").type(JsonFieldType.NUMBER).description("?????? ????????? ??????").optional(),
						fieldWithPath("prdtTradeLoc").type(JsonFieldType.STRING).description("?????? ??????")
						)));
	}
	
	@Test
	@WithMockUser(username = "bang", password = "bang1234", authorities = {"ROLE_USER"})
	public void likeProduct() throws Exception {
		// given
		// ??????1(changon) ?????? ??? ?????? ??????
		createProduct();
		// ??????2(bang) ??????
		createUserTwo();
		
		// ????????? ?????? ?????? ??????
		ProductVO insertProduct = productService.getProductIdByName(product.getPrdtName());
		
		// when
		mockMvc.perform(RestDocumentationRequestBuilders.post("/products/like/{prdtId}", insertProduct.getPrdtId()))
		// then
		.andExpect(status().isOk())
		.andDo(document.document(pathParameters(
				parameterWithName("prdtId").description("?????? ?????????")
				)));
	}
	
	@Test
	@WithMockUser(username = "bang", password = "bang1234", authorities = {"ROLE_USER"})
	public void unlikeProduct() throws Exception {
		// given
		// ?????????1 ?????? ??? ?????? ??????
		createProduct();
		// ?????????2 ??????
		createUserTwo();
		// ????????? ?????? ?????? ??????
		ProductVO insertProduct = productService.getProductIdByName(product.getPrdtName());
		
		// when
		// ?????? ????????? ????????? +1
		mockMvc.perform(post("/products/like/{prdtId}", insertProduct.getPrdtId()))
		.andExpect(status().isOk());
		
		// ?????? ????????? ????????? -1
		mockMvc.perform(RestDocumentationRequestBuilders.post("/products/unlike/{prdtId}", insertProduct.getPrdtId()))
		// then
		.andExpect(status().isOk())
		.andDo(document.document(pathParameters(
				parameterWithName("prdtId").description("?????? ?????????")
				)));
	}
	
	@Test
	@WithMockUser(username = "changon", password = "changon1234", authorities = {"ROLE_USER"})
	public void updateProduct() throws Exception {
		// given
		// ?????????1 ?????? ??? ?????? ??????
		createProduct();
		// ?????????2 ??????
		createUserTwo();
		
		user = UserVO.builder()
				.userId("changon")
				.build();
		
		// ????????? ?????? ?????? ??????
		ProductVO insertProduct = productService.getProductIdByName(product.getPrdtName());
		
		// ????????? ?????? ??????
		product = ProductVO.builder()
				.prdtId(insertProduct.getPrdtId())
				.prdtName("CD????????????")
				.prdtPrice("789")
				.prdtCategory("?????????")
				.prdtInfo("aiwa CD ???????????? ?????????.")
				.prdtCondition("?????????")
				.prdtIsTradeable("????????????")
				.prdtIsDeliveryFree("???????????????")
				.prdtLikeCnt(0)
				.prdtTradeLoc("?????? ??????")
				.build();
		
		
		String productJson = new Gson().toJson(product);
		MockMultipartFile imageFile = new MockMultipartFile("productImage", "CDPlayer.jpeg", "image/jpeg", "<<jpeg data>>".getBytes(StandardCharsets.UTF_8));
		MockMultipartFile productFile = new MockMultipartFile("product", "product", "application/json", productJson.getBytes(StandardCharsets.UTF_8));
		
		// when
		mockMvc.perform(MockMvcRequestBuilders.multipart("/products/update/{prdtId}", insertProduct.getPrdtId())
				.file(imageFile)
				.file(productFile)
				.characterEncoding("UTF-8")
				)
		// then
		.andExpect(status().isOk())
		.andDo(document.document(
				requestParts(
						partWithName("productImage").description("?????? ?????? ?????????"),
						partWithName("product").description("?????? ?????? ??????")
						),
				requestPartFields(
						"product", 
						fieldWithPath("prdtId").type(JsonFieldType.STRING).description("?????? ?????????"),
						fieldWithPath("prdtName").type(JsonFieldType.STRING).description("?????? ??????"),
						fieldWithPath("prdtPrice").type(JsonFieldType.STRING).description("?????? ??????"),
						fieldWithPath("prdtCategory").type(JsonFieldType.STRING).description("?????? ????????????"),
						fieldWithPath("prdtInfo").type(JsonFieldType.STRING).description("?????? ??????"),
						fieldWithPath("prdtCondition").type(JsonFieldType.STRING).description("?????? ??????"),
						fieldWithPath("prdtIsTradeable").type(JsonFieldType.STRING).description("????????????"),
						fieldWithPath("prdtIsDeliveryFree").type(JsonFieldType.STRING).description("????????? ??????"),
						fieldWithPath("prdtLikeCnt").type(JsonFieldType.NUMBER).description("?????? ????????? ??????").optional(),
						fieldWithPath("prdtTradeLoc").type(JsonFieldType.STRING).description("?????? ??????")
						)));
	}
	
	@Test
	@WithMockUser(username = "bang", password = "bang1234", authorities = {"ROLE_USER"})
	public void deleteProduct() throws Exception {
		// given
		// ?????????1 ?????? ??? ?????? ??????
		createProduct();
		// ?????????2 ??????
		createUserTwo();
		// ????????? ?????? ?????? ??????
		ProductVO insertProduct = productService.getProductIdByName(product.getPrdtName());
		
		// when
		mockMvc.perform(RestDocumentationRequestBuilders.post("/products/delete/{prdtId}", insertProduct.getPrdtId()))
		// then
		.andExpect(status().isOk())
		.andDo(document.document(pathParameters(
				parameterWithName("prdtId").description("?????? ?????????")
				)));
	}
}
