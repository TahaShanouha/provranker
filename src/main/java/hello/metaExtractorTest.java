package hello;

import static org.junit.Assert.*;

import org.junit.Test;
import org.openprovenance.prov.interop.InteropFramework;

public class metaExtractorTest {

	@Test
	public void testMetaExtractor() {
		metaExtractor me = new metaExtractor(InteropFramework.newXMLProvFactory(), "http://www.example.org", "");
	}

	@Test
	public void testExtract() {
		metaExtractor me = new metaExtractor(InteropFramework.newXMLProvFactory(), "http://www.example.org", "");
		try {
			me.extract("http://www.example.org", me);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
