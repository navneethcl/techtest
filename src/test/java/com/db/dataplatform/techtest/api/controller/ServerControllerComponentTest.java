package com.db.dataplatform.techtest.api.controller;

import com.db.dataplatform.techtest.TestDataHelper;
import com.db.dataplatform.techtest.server.api.controller.ServerController;
import com.db.dataplatform.techtest.server.api.model.DataEnvelope;
import com.db.dataplatform.techtest.server.exception.HadoopClientException;
import com.db.dataplatform.techtest.server.component.Server;
import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.util.UriTemplate;

import java.io.IOException;
import java.net.URI;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@RunWith(MockitoJUnitRunner.class)
public class ServerControllerComponentTest {

	public static final String URI_PUSHDATA = "http://localhost:8090/dataserver/pushdata";
	public static final UriTemplate URI_GETDATA = new UriTemplate("http://localhost:8090/dataserver/data/{blockType}");
	public static final UriTemplate URI_PATCHDATA = new UriTemplate("http://localhost:8090/dataserver/update/{name}/{newBlockType}");
	public static final String expectedEnvelope = "[{\"dataHeader\":{\"name\":\"Test\",\"blockType\":\"BLOCKTYPEA\",\"checksum\":\"cecfd3953783df706878aaec2c22aa70\"},\"dataBody\":{\"dataBody\":\"AKCp5fU4WNWKBVvhXsbNhqk33tawri9iJUkA5o4A6YqpwvAoYjajVw8xdEw6r9796h1wEp29D\"}}]";

	@Mock
	private Server serverMock;

	private DataEnvelope testDataEnvelope;
	private ObjectMapper objectMapper;
	private MockMvc mockMvc;
	private ServerController serverController;

	@Before
	public void setUp() throws HadoopClientException, NoSuchAlgorithmException, IOException {
		serverController = new ServerController(serverMock);
		mockMvc = standaloneSetup(serverController).build();
		objectMapper = Jackson2ObjectMapperBuilder
				.json()
				.build();

		testDataEnvelope = TestDataHelper.createTestDataEnvelopeApiObject();
		when(serverMock.saveDataEnvelope(any(DataEnvelope.class))).thenReturn(true);
	}


	@Test
	public void testPushDataPostCallWorksAsExpected() throws Exception {
		String testDataEnvelopeJson = objectMapper.writeValueAsString(testDataEnvelope);

		MvcResult mvcResult = mockMvc.perform(post(URI_PUSHDATA)
				.content(testDataEnvelopeJson)
				.contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isOk())
				.andReturn();

		boolean checksumPass = Boolean.parseBoolean(mvcResult.getResponse().getContentAsString());
		assertThat(checksumPass).isTrue();
		verify(serverMock,times(1)).pushDataToHadoopDataLake(any(DataEnvelope.class));
	}

	@Test
	public void testGetDataGetCallWorksAsExpected() throws Exception {
		final List<DataEnvelope> dataEnvelopeList = new ArrayList<>();
		dataEnvelopeList.add(TestDataHelper.createTestDataEnvelopeApiObject());
		when(serverMock.getDataEnvelopeByBlockType(any(BlockTypeEnum.class))).thenReturn(dataEnvelopeList);
		final Map<String,String> uriVariables=new HashMap<>();
		uriVariables.put("blockType", BlockTypeEnum.BLOCKTYPEA.name());
		final URI uri = URI_GETDATA.expand(uriVariables);

		MvcResult mvcResult = mockMvc.perform(get(uri)
				.contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isOk())
				.andReturn();
	 assertThat(mvcResult.getResponse().getContentAsString()).isEqualTo(expectedEnvelope);
	}


	@Test
	public void testUpdateDataPatchCallWorksAsExpected() throws Exception {

		when(serverMock.updateBlockTypeByName(any(String.class),any(String.class))).thenReturn(true);
		final List<DataEnvelope> dataEnvelopeList = new ArrayList<>();
		dataEnvelopeList.add(TestDataHelper.createTestDataEnvelopeApiObject());
		final Map<String,String> uriVariables=new HashMap<>();
		uriVariables.put("newBlockType", BlockTypeEnum.BLOCKTYPEA.name());
		uriVariables.put("name", "blockName");
		final URI uri = URI_PATCHDATA.expand(uriVariables);

		MvcResult mvcResult = mockMvc.perform(patch(uri)
						.contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isOk())
				.andReturn();
		boolean isDataBlockUpdated = Boolean.parseBoolean(mvcResult.getResponse().getContentAsString());
		assertThat(isDataBlockUpdated).isTrue();
	}
}
