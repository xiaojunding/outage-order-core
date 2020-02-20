package com.ameren.outage.order.core.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(MockitoJUnitRunner.class)
public class CallLogResponseTest {
	
	private static ObjectMapper mapper = new ObjectMapper();

	public <V> V readJsonFileIntoPojo(String pathname, Class<V> clazz) throws IOException{
		String json = new String(Files.readAllBytes(new File(pathname).toPath()));
		return mapper.readValue(json, clazz);
	}
	@Test
	public void testParsing() throws IOException {
		//prepare
		String json = new String(Files.readAllBytes(new File("src/test/resources/call_logs.json").toPath()));
		
		//act
		CallLogResponse response =  mapper.readValue(json, CallLogResponse.class);
	
		//verify
		assertNotNull(response);
		assertNotNull(response.getCallLogs());
		assertEquals(11, response.getCallLogs().size());
		assertEquals(200, response.getStatusCode());
		assertFalse(response.hasError());
		assertEquals("", response.getMessage());
	}

	@Test
	public void testParsing_noData() throws IOException {
		//prepare
		String json = new String(Files.readAllBytes(new File("src/test/resources/call_logs_no_data.json").toPath()));
		
		//act
		CallLogResponse response =  mapper.readValue(json, CallLogResponse.class);
	
		//verify
		assertNotNull(response);
		assertTrue(CollectionUtils.isEmpty(response.getCallLogs()));
		assertEquals(200, response.getStatusCode());
		assertFalse(response.hasError());
		assertEquals("No Data", response.getMessage());
	}
	
}
