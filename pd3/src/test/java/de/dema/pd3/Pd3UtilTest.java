package de.dema.pd3;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class Pd3UtilTest {

	@Test
	public void testInjectHtmlTags() {
		String[][] expectations = {
				{"www.test.de", "<a href=\"http://www.test.de\" target=\"_blank\">http://www.test.de</a>"},	
				{"Hallo www.test.de.", "Hallo <a href=\"http://www.test.de\" target=\"_blank\">http://www.test.de</a>."},	
				{"https://docs.spring.io/spring/docs/current/spring-framework-reference/html/integration-testing.html", "<a href=\"https://docs.spring.io/spring/docs/current/spring-framework-reference/html/integration-testing.html\" target=\"_blank\">https://docs.spring.io/spring/docs/current/spring-framework-reference/html/integration-testing.html</a>"},	
				{".https://docs.spring.io/spring/docs/current/spring-framework-reference/html/integration-testing.html abcd", ".<a href=\"https://docs.spring.io/spring/docs/current/spring-framework-reference/html/integration-testing.html\" target=\"_blank\">https://docs.spring.io/spring/docs/current/spring-framework-reference/html/integration-testing.html</a> abcd"},	
				{"Schau mal hier: http://www.dict.cc/?s=notification\n", "Schau mal hier: <a href=\"http://www.dict.cc/?s=notification\" target=\"_blank\">http://www.dict.cc/?s=notification</a><br/>"}	
		};
		
		for (String[] expectation : expectations) {
			assertThat(Pd3Util.injectHtmlTags(expectation[0])).isEqualTo(expectation[1]);
		}
	}
	
}
