package hello;

import static org.junit.Assert.*;

import org.junit.Test;
import org.openprovenance.prov.interop.InteropFramework;

public class ProcessProvTest {

	@Test
	public void test() {
		ProcessProv process = new ProcessProv(InteropFramework.newXMLProvFactory(),"provenanceExpanded.provn","http://www.example.com");
	}

}
