package codemagic.generator.context.shared.parser;

import static codemagic.util.shared.common.SanitizerUtil.sanitize;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.Map;

import org.jdom2.Content;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.filter.ElementFilter;
import org.jdom2.input.SAXBuilder;
import org.jdom2.input.sax.XMLReaders;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.jdom2.util.IteratorIterable;

import com.floreysoft.jmte.Engine;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.common.base.Verify;

import grain.util.ClassLoadUtil;

public class JDomWrapper {
	private final Document doc;
	final Element root;
	
	public JDomWrapper(final String templateName, final Engine engine, final Map<String, Object> model, final Class<?> classLoader) {
		this( buildTransformedXml(templateName, engine, model, classLoader) );
	}

	public JDomWrapper(final String templateName, final Class<?> classLoader) {
		this( ClassLoadUtil.loadFile(classLoader, templateName) );
	}
	
	public JDomWrapper(final String template) {
		this(buildDoc(template));
	}

	public JDomWrapper(final Document doc) {
		this.doc = doc;
		this.root = this.doc.getRootElement();
	}

	public Element getFirstElement(final String searchedElement, final Namespace namespace) {
		final Element root = doc.getRootElement();
		final IteratorIterable<Element> formIt = root.getDescendants(new ElementFilter(searchedElement, namespace));
	    final Element foundedEl = formIt.next();
	    
	    Verify.verify( foundedEl != null, "The %s was not found", searchedElement);
	    
	    return foundedEl;
	}
	
	public Element findByAttribute(final String attrName, final String attrValue, final Namespace namespace) {

		Preconditions.checkArgument( !sanitize(attrName).trim().isEmpty(), "The searched attribute name cannot be empty" );
		Preconditions.checkArgument( !sanitize(attrValue).trim().isEmpty(), "The searched attribute values cannot be empty" );
		
		final Element root = doc.getRootElement();
		final IteratorIterable<Content> descendants = root.getDescendants();
		
		for (final Content content : descendants) {
		    if (content instanceof Element) {
		    	final Element el = (Element) content;
				final String attr = sanitize(el.getAttributeValue(attrName, namespace)).trim();
				if (attr.equals(attrValue)) {
					return el;
				}
		    }
		}
		
		throw new RuntimeException( String.format("None element found with the attribute %s=\"%s\"", attrName , attrValue));
	}
	
	public void writeToFile(final String fullFileName) {
		try {
			System.out.println(new File(fullFileName).getAbsolutePath());

			final OutputStream os = new FileOutputStream(fullFileName);
			final XMLOutputter xout = new XMLOutputter(Format.getPrettyFormat());
			xout.output(this.doc, os);
			
		} catch (final Throwable t) {
			throw Throwables.propagate(t);
		}
	}
	
	public Document getDoc() {
		return doc;
	}

	public Element getRoot() {
		return root;
	}

	  //~~~~~~~~~~~~~~~//
	 //  Utilities    //
	//~~~~~~~~~~~~~~~// 
	
	
	private static Document buildDoc(final String template) {
		final String _template = sanitize(template).trim();
		Verify.verify( !_template.isEmpty(), "The template cannot be null");
		
		try {
			final SAXBuilder sax = new SAXBuilder();
		    sax.setXMLReaderFactory(XMLReaders.NONVALIDATING);
			return sax.build(new StringReader(template));
		} catch (final Throwable t) {
			throw Throwables.propagate(t);
		}
	}
	
	private static String buildTransformedXml(final String templateName, final Engine engine, final Map<String, Object> model, final Class<?> classLoader) {
		final String xml = ClassLoadUtil.loadFile( classLoader,	templateName);
		return engine.transform(xml, model);
	}
}