package com.board.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.text.ParseException;

import javax.servlet.ServletException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import com.board.domain.UserVO;
import com.google.gson.Gson;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration({"file:src/main/webapp/WEB-INF/spring/root-context.xml", "file:src/main/webapp/WEB-INF/spring/appServlet/servlet-context.xml"})
@Transactional
public class UserControllerTest {

	@Rule 
	public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation();

	@Autowired
	private UserController userController;
	
	@Autowired 
	private WebApplicationContext context;
	
	private MockMvc mockMvc;
	
	private RestDocumentationResultHandler document;
	
	private UserVO user;
	
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
		this.mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
	}
	
	@Before
	public void setUpUser() throws ParseException {
		user = UserVO.builder()
				.accountId("accountId")
				.userId("changon")
				.userPwd("changon1234")
				.userName("?????????")
				.userEmail("changon@gmail.com")
				.userPhone("01012341234")
				.userAddr("?????? ????????? ??????")
				.build();
	}
	
	@Test
	public void Successfully_create_new_user() throws Exception {
		// given
		String jsonStr = new Gson().toJson(user);
		// when
		mockMvc.perform(RestDocumentationRequestBuilders.post("/users")
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonStr))
		// then
		.andExpect(status().isOk())
		.andDo(document.document(requestFields(
				fieldWithPath("accountId").type(JsonFieldType.STRING).description("?????? ?????? ??????"),
				fieldWithPath("userId").type(JsonFieldType.STRING).description("?????????"),
				fieldWithPath("userPwd").type(JsonFieldType.STRING).description("????????????"),
				fieldWithPath("userName").type(JsonFieldType.STRING).description("??????"),
				fieldWithPath("userEmail").type(JsonFieldType.STRING).description("?????????"),
				fieldWithPath("userPhone").type(JsonFieldType.STRING).description("?????????"),
				fieldWithPath("userAddr").type(JsonFieldType.STRING).description("??????")
				)));
	}
	
	@Test
	public void Can_use_this_email() throws Exception {
		// when
		mockMvc.perform(RestDocumentationRequestBuilders.get("/users/signup/email").param("userEmail", user.getUserEmail()))
		// then
		.andExpect(status().isOk())
		.andDo(document.document(
				requestParameters(parameterWithName("userEmail").description("????????? ?????????"))));
	}
	
	@Test
	public void Can_use_this_id() throws Exception {
		// when
		mockMvc.perform(RestDocumentationRequestBuilders.get("/users/signup/id").param("userId", user.getUserId()))
		// then
		.andExpect(status().isOk())
		.andDo(document.document(requestParameters(parameterWithName("userId").description("????????? ?????????"))));
	}
	
	@Test
	public void Able_to_retrieve_id() throws Exception {
		// given
		Successfully_create_new_user();
		
		user = UserVO.builder()
				.userName("?????????")
				.userPhone("01012341234")
				.build();
		
		String jsonStr = new Gson().toJson(user);
		
		// when
		MvcResult result = mockMvc.perform(RestDocumentationRequestBuilders.post("/users/help/id")
				.contentType(MediaType.APPLICATION_JSON).content(jsonStr))
		// then
		.andExpect(status().isOk())
		.andDo(print())
		.andDo(document.document(
				requestFields(
					fieldWithPath("userName").type(JsonFieldType.STRING).description("??????"),
					fieldWithPath("userPhone").type(JsonFieldType.STRING).description("?????????")
					)))
		.andReturn();
		
		String response = result.getResponse().getContentAsString();
		assertThat(response, is("???????????? ???????????????: changon"));
	}
	
	@Test
	public void Successfully_change_user_password() throws Exception {
		// given
		Successfully_create_new_user();
		
		user = UserVO.builder()
				.userId(user.getUserId())
				.userPwd("newPwd")
				.build();
		
		String jsonStr = new Gson().toJson(user);
		
		// when
		mockMvc.perform(RestDocumentationRequestBuilders.post("/users/help/pwd")
				.contentType(MediaType.APPLICATION_JSON).content(jsonStr))
		// then
		.andExpect(status().isOk())
		.andDo(document.document(requestFields(
				fieldWithPath("userId").type(JsonFieldType.STRING).description("?????????"),
				fieldWithPath("userPwd").type(JsonFieldType.STRING).description("????????? ????????????")
				)));
	}
	
	@Test
	public void Successfully_change_user_profile() throws Exception {
		// given
		Successfully_create_new_user();
		
		user = UserVO.builder()
				.userId("changon")
				.userEmail("changon@gmail.com")
				.userPhone("01012341234")
				.userAddr("?????? ????????? ??????")
				.build();
		
		String jsonStr = new Gson().toJson(user);
		
		// when
		mockMvc.perform(RestDocumentationRequestBuilders.post("/users/profile")
				.contentType(MediaType.APPLICATION_JSON).content(jsonStr))
		// then
		.andExpect(status().isOk())
		.andDo(print())
		.andDo(document.document(requestFields(
				fieldWithPath("userId").type(JsonFieldType.STRING).description("?????????"),
				fieldWithPath("userEmail").type(JsonFieldType.STRING).description("?????????"),
				fieldWithPath("userPhone").type(JsonFieldType.STRING).description("?????????"),
				fieldWithPath("userAddr").type(JsonFieldType.STRING).description("??????")
				)));
	}
	
	@Test
	public void Permanently_delete_user() throws Exception {
		// given
		Successfully_create_new_user();
		
		user = UserVO.builder()
				.userId("changon")
				.userEmail("changon@gmail.com")
				.build();
		
		String jsonStr = new Gson().toJson(user);
		
		// when
		mockMvc.perform(RestDocumentationRequestBuilders.delete("/users")
				.contentType(MediaType.APPLICATION_JSON).content(jsonStr))
		// then
		.andExpect(status().isOk())
		.andDo(document.document(requestFields(
				fieldWithPath("userId").type(JsonFieldType.STRING).description("?????????"),
				fieldWithPath("userEmail").type(JsonFieldType.STRING).description("?????????")
				)));
	}
	
}
