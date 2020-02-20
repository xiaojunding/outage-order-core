package com.ameren.outage.order.core.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(MockitoJUnitRunner.class)
public class OperatingCenterResponseTest {

	private static ObjectMapper mapper = new ObjectMapper();

	@Test
	public void testParsing() throws IOException {
		// prepare
		String json = new String(Files.readAllBytes(new File("src/test/resources/operating_centers.json").toPath()));

		// act
		OperatingCenterResponse response = mapper.readValue(json, OperatingCenterResponse.class);

		// verify
		assertNotNull(response);
		assertNotNull(response.getOperatingCenters());
		assertEquals(3, response.getOperatingCenters().size());
//		assertEquals(200, response.getStatusCode());
		assertFalse(response.hasError());
		assertEquals("requested program voiceppo", response.getMessage());
	}

	@Test
	public void testParsing_noData() throws IOException {
		// prepare
		String json = new String(
				Files.readAllBytes(new File("src/test/resources/operating_centers_no_data.json").toPath()));

		// act
		OperatingCenterResponse response = mapper.readValue(json, OperatingCenterResponse.class);

		// verify
		assertNotNull(response);
		assertNull(response.getOperatingCenters());
		assertEquals(200, response.getStatusCode());
		assertFalse(response.hasError());
		assertEquals("No Data", response.getMessage());
	}

}
