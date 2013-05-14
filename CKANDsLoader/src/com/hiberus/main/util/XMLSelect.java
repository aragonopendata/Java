package com.hiberus.main.util;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class XMLSelect {

	private static final Log log = LogFactory.getLog(XMLSelect.class);

	public static String getString(Object object, String xPath) {

		String dev = null;

		try {
			JXPathContext context = JXPathContext.newContext(object);
			Object value = context.getValue(xPath);

			if (value!=null) {
				dev = value.toString();
			}
			else {
				dev = null;
			}
		}
		catch (Exception e) {
			log.debug("La expresion "+xPath+" no devuelve ningun resultado");
			
			dev = null;
		}
		log.debug("xPath="+xPath+" -> ["+dev+"]");
		return dev;
	}

	/*	Parsea un valor "Integer" del XML de PADIA */
	public static int getInteger(Object object, String xPath) throws Exception {
		String cadena = getString(object,xPath);

		if (cadena==null || "".equals(cadena)) {
			return (int) 0;
		}
		else {
			return Integer.parseInt(cadena);
		}
	}

}
