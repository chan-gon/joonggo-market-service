package com.board.controller;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
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

import com.board.domain.MessageVO;
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
public class MessageControllerTest {
	
	@Rule 
	public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation();
	
	@Autowired 
	private WebApplicationContext context;
	
	@Autowired
	private MessageController messageController;
	
	@Autowired
	private ProductService productService;

	private MockMvc mockMvc;
	
	private RestDocumentationResultHandler document;
	
	private MessageVO message;
	
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
		this.mockMvc = MockMvcBuilders.standaloneSetup(messageController).build();
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
	
	@Test
	@WithMockUser(username = "bang", password = "bang1234", authorities = {"ROLE_USER"})
	public void sendMessage() throws Exception {
		// given
		// ??????1(changon) ?????? ??? ?????? ??????
		createProduct();
		// ??????2(bang) ??????
		createUserTwo();
		// ????????? ?????? ?????? ??????
		ProductVO insertProduct = productService.getProductIdByName(product.getPrdtName());
		
		message = MessageVO.builder()
				.prdtId(insertProduct.getPrdtId())
				.buyer("bang")
				.seller("changon")
				.content("???????????? ??????????????? ???????????? ???????????????.")
				.type("BUYER")
				.build();
		
		String jsonStr = new Gson().toJson(message);
		
		// when
		mockMvc.perform(RestDocumentationRequestBuilders.post("/messages")
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonStr))
		// then
		.andExpect(status().isOk())
		.andDo(document.document(requestFields(
				fieldWithPath("prdtId").type(JsonFieldType.STRING).description("???????????? ??????"),
				fieldWithPath("buyer").type(JsonFieldType.STRING).description("?????????"),
				fieldWithPath("seller").type(JsonFieldType.STRING).description("?????????"),
				fieldWithPath("content").type(JsonFieldType.STRING).description("????????? ??????"),
				fieldWithPath("type").type(JsonFieldType.STRING).description("?????????/????????? ??????")
				)));
	}
	
	@Test
	@WithMockUser(username = "changon", password = "changon1234", authorities = {"ROLE_USER"})
	public void sendResponse() throws Exception {
		// given
		// ??????1(changon) ?????? ??? ?????? ??????
		createProduct();
		// ??????2(bang) ??????
		createUserTwo();
		// ????????? ?????? ?????? ??????
		ProductVO insertProduct = productService.getProductIdByName(product.getPrdtName());
		
		message = MessageVO.builder()
				.prdtId(insertProduct.getPrdtId())
				.buyer("bang")
				.seller("changon")
				.content("???????????? ??????????????? ???????????? ????????? ????????? ????????????.")
				.type("SELLER")
				.build();
		
		String jsonStr = new Gson().toJson(message);
		
		// when
		mockMvc.perform(RestDocumentationRequestBuilders.post("/messages/response")
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonStr))
		.andDo(print())
		// then
		.andExpect(status().isOk())
		.andDo(document.document(requestFields(
				fieldWithPath("prdtId").type(JsonFieldType.STRING).description("???????????? ??????"),
				fieldWithPath("buyer").type(JsonFieldType.STRING).description("?????????"),
				fieldWithPath("seller").type(JsonFieldType.STRING).description("?????????"),
				fieldWithPath("content").type(JsonFieldType.STRING).description("????????? ??????"),
				fieldWithPath("type").type(JsonFieldType.STRING).description("?????????/????????? ??????")
				)));
	}

	public void createUserOne() throws Exception {
		// given
		UserVO user = UserVO.builder()
				.userId("changon")
				.userPwd("changon1234")
				.userName("?????????")
				.userEmail("changon@gmail.com")
				.userPhone("01012341234")
				.userAddr("?????? ????????? ??????")
				.build();
		String jsonStr = new Gson().toJson(user);
		
		// when
		mockMvc.perform(RestDocumentationRequestBuilders.post("/users")
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonStr))
		// then
		.andExpect(status().isOk());
	}
	
	public void createUserTwo() throws Exception {
		// given
		UserVO user = UserVO.builder()
				.userId("bang")
				.userPwd("bang1234")
				.userName("?????????")
				.userEmail("bang@gmail.com")
				.userPhone("01002420404")
				.userAddr("?????? ?????? ???????????????")
				.build();
		String jsonStr = new Gson().toJson(user);
		
		// when
		mockMvc.perform(RestDocumentationRequestBuilders.post("/users")
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonStr))
		// then
		.andExpect(status().isOk());
	}
	
	public void createProduct() throws Exception {
		// given
		createUserOne();
		
		UserVO user = UserVO.builder()
				.userId("changon")
				.userPwd("changon1234")
				.userName("?????????")
				.userEmail("changon@gmail.com")
				.userPhone("01012341234")
				.userAddr("?????? ????????? ??????")
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
		// then
		.andExpect(status().isOk());
	}
}
