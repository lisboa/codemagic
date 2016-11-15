package codemagic.widget.client;

import org.junit.Test;

/**
 * Prevents build error in eclipse, because it try to compile a non existent
 * folder src/test/java. The git cannot save empty folder, then, add this test
 * to force git to add this folder into repository.
 *
 */
public class DummyTest {

	@Test
	public void test() {
		/*Nothing to do, for now*/
	}

}
