package codemagic.util.client.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import org.junit.Before;
import org.junit.Test;

import codemagic.util.client.common.ResponseUtil;

import com.google.gwt.http.client.RequestException;

public class ResponseUtilTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testExtracJsonFromFailure_therIsJson() {
		final String prepend = "HTTP ERROR: 500 Internal Server Error\n";
		final String originalError = "{\"timestamp\":1441125549974,\"status\":500,\"error\":\"Internal Server Error\",\"exception\":\"db.assist.shared.common.to.exception.DBAssistException\",\"message\":\"There is already a project with the name 'teste2'\",\"path\":\"/projects\"}";
		final String requestExceptionMessage = prepend + originalError;

		final String actual = ResponseUtil.extracJsonFromFailure( new RequestException(requestExceptionMessage));
		System.out.println(actual);
		
		assertThat(actual, is(equalTo(originalError)) );
	}
	
	@Test
	public void testExtracJsonFromFailure_null_message() {
		final String requestExceptionMessage = null;

		final String actual = ResponseUtil.extracJsonFromFailure( new RequestException(requestExceptionMessage));
		System.out.println(actual);
		
		assertThat(actual, is(equalTo("")) );
	}

	@Test
	public void testExtracJsonFromFailure_NoPrepend() {
		final String originalError = "{\"timestamp\":1441125549974,\"status\":500,\"error\":\"Internal Server Error\",\"exception\":\"db.assist.shared.common.to.exception.DBAssistException\",\"message\":\"There is already a project with the name 'teste2'\",\"path\":\"/projects\"}";

		final String actual = ResponseUtil.extracJsonFromFailure( new RequestException(originalError));
		System.out.println(actual);
		
		assertThat(actual, is(equalTo(originalError)) );
	}
	
	@Test
	public void testExtracJsonFromFailure_thereIsOnlyLeftBracket() {
		final String prepend = "HTTP ERROR: 500 Internal Server Error\n";
		final String originalError = "{\"timestamp\":1441125549974,\"status\":500,\"error\":\"Internal Server Error\",\"exception\":\"db.assist.shared.common.to.exception.DBAssistException\",\"message\":\"There is already a project with the name 'teste2'\",\"path\":\"/projects\"";
		final String requestExceptionMessage = prepend + originalError;

		final String actual = ResponseUtil.extracJsonFromFailure( new RequestException(requestExceptionMessage));
		assertThat(actual, is(equalTo("")) );
	}
	
	@Test
	public void testExtracJsonFromFailure_thereIsOnlyRightBracket() {
		final String prepend = "HTTP ERROR: 500 Internal Server Error\n";
		final String originalError = "\"timestamp\":1441125549974,\"status\":500,\"error\":\"Internal Server Error\",\"exception\":\"db.assist.shared.common.to.exception.DBAssistException\",\"message\":\"There is already a project with the name 'teste2'\",\"path\":\"/projects\"";
		final String requestExceptionMessage = prepend + originalError;

		final String actual = ResponseUtil.extracJsonFromFailure( new RequestException(requestExceptionMessage));
		assertThat(actual, is(equalTo("")) );
	}
}
