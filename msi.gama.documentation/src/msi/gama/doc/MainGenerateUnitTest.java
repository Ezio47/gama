/*********************************************************************************************
 * 
 *
 * 'MainGenerateUnitTest.java', in plugin 'msi.gama.documentation', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.doc;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.jdom2.JDOMException;
import org.tmatesoft.svn.core.SVNException;
import org.xml.sax.SAXException;

import msi.gama.doc.transform.XmlToTestGAML;
import msi.gama.doc.util.Constants;
import msi.gama.doc.util.PrepareEnv;
import msi.gama.doc.util.UnifyDoc;

public class MainGenerateUnitTest {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws JDOMException 
	 * @throws TransformerException 
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 * @throws SVNException 
	 */
	public static void main(String[] args) 
			throws IOException, JDOMException, ParserConfigurationException, SAXException, 
					TransformerException, SVNException {
		System.out.println("GENERATION OF THE TESTS FROM JAVA CODE");
		System.out.println("Please notice that the docGAMA.xml files should have been previously generated..");
		System.out.print("Preparation of the folders................");
		PrepareEnv.prepareDocumentation(Constants.ONLINE);
		System.out.println("DONE");
		System.out.print("Merge all the docGAMA.xml files................");		
		UnifyDoc.unify();
		System.out.println("DONE");
		System.out.print("Transform the docGAMA.xml file into test files................");		
		XmlToTestGAML.createAllTests();
		System.out.println("DONE");		
	}

}
