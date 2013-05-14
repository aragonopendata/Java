package com.hiberus.main.manager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.ckan.Dataset;
import org.ckan.Extra;
import org.ckan.Group;
import org.ckan.Resource;
import org.ckan.Tag;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.hiberus.main.util.XMLSelect;

public class CreateDataSet {

	// Carga los ficheros RDF de una carpeta del equipo especificada en
	// config.properties.
	// Se puede especificar directamente la ruta
	// Devuelve todos los rdfs leidos
	public static List<Dataset> cargarDirectorio() throws Exception {

		Properties prop = new Properties();
		prop.load(new FileInputStream("config.properties"));
		String rutaFicheros = prop.getProperty("ruta_carpeta");

		List<Dataset> completo = new ArrayList<Dataset>();
		File directorio = new File(rutaFicheros);
		String[] archivos = directorio.list();
		for (int i = 0; i < archivos.length; i++) {
			completo.addAll(cargarFicheros(rutaFicheros + archivos[i]));
		}
		return completo;
	}

	// Carga de ficheros transformados
	public static List<Dataset> cargarFicheros(String ruta) throws Exception {
		BufferedReader rf = new BufferedReader(new InputStreamReader(
				new FileInputStream(ruta)));
		String s = "";
		String line;
		while ((line = rf.readLine()) != null) {
			s += line;
		}

		DocumentBuilderFactory factory = null;
		DocumentBuilder builder = null;
		Document doc = null;

		factory = DocumentBuilderFactory.newInstance();
		builder = factory.newDocumentBuilder();
		InputSource is = new InputSource(new StringReader(s));
		doc = builder.parse(is);

		return transformarDocumento(doc);

	}

	// Lectura del RDF para obtener los campos del dataset
	private static List<Dataset> transformarDocumento(Document rdf)
			throws Exception {

		List<Dataset> listadoDatasets = new ArrayList<Dataset>();

		NodeList lstDs = rdf.getElementsByTagName("dcat:Dataset");
		for (int idx = 0; idx < lstDs.getLength(); idx++) {

			Node dataset = lstDs.item(idx);

			Dataset ds = new Dataset();
			// Nombre
			String nombre = XMLSelect.getString(dataset, "dct:identifier");
			ds.setName(nombre);

			// Descripcion
			String descripcion = XMLSelect
					.getString(dataset, "dct:description");
			ds.setNotes(descripcion);

			// Etiquetas
			int i = 1;
			Boolean quedanDatos = true;
			List<Tag> listadoTag = new ArrayList<Tag>();
			while (quedanDatos) {
				if (XMLSelect.getString(dataset, "dcat:keyword[" + i + "]") == null) {
					quedanDatos = false;
				} else {
					String clave = XMLSelect.getString(dataset, "dcat:keyword["
							+ i + "]");
					Tag tag = new Tag();
					tag.setDisplayName(clave);
					tag.setName(clave);
					listadoTag.add(tag);
					i++;
				}
			}
			ds.setTags(listadoTag);

			// Titulo
			String titulo = XMLSelect.getString(dataset, "dct:title");
			ds.setTitle(titulo);

			// Extras
			List<Extra> listadoExtra = new ArrayList<Extra>();

			// Fecha modificacion
			String fModificacion = XMLSelect.getString(dataset, "dct:modified");
			ds.setMetadata_modified(fModificacion);
			Extra extraModif = new Extra();
			extraModif.setKey("modifiedDate");
			extraModif.setValue("\"" + fModificacion + "\"");
			listadoExtra.add(extraModif);

			// Fecha creacion
			String fCreacion = XMLSelect.getString(dataset, "dct:issued");
			ds.setMetadata_created(fCreacion);

			Extra extraIssued = new Extra();
			extraIssued.setKey("issuedDate");
			extraIssued.setValue("\"" + fCreacion + "\"");
			listadoExtra.add(extraIssued);

			// Frecuencia
			String frecuencia = XMLSelect.getString(dataset,
					"dct:accrualPeriodicity//dct:Frequency//rdfs:label");
			if (frecuencia != null && frecuencia.length() > 0) {
				Extra extra = new Extra();
				extra.setKey("Frequency");
				extra.setValue("\"" + frecuencia + "\"");
				listadoExtra.add(extra);
			}

			// Cobertura espacial
			String spatial = XMLSelect.getString(dataset, "dct:spatial");
			if (spatial != null && spatial.length() > 0) {
				Extra extra = new Extra();
				extra.setKey("Spatial");
				extra.setValue("\"" + spatial + "\"");
				listadoExtra.add(extra);
			}

			// Rango de tiempo
			String temporal = XMLSelect.getString(dataset, "dct:temporal");
			if (temporal != null && temporal.length() > 0) {
				Extra extra = new Extra();
				extra.setKey("Temporal");
				extra.setValue("\"" + temporal + "\"");
				listadoExtra.add(extra);
			}

			// Idioma
			String language = XMLSelect.getString(dataset, "dct:language");
			if (language != null && language.length() > 0) {
				Extra extra = new Extra();
				extra.setKey("Language");
				extra.setValue("\"" + language + "\"");
				listadoExtra.add(extra);
			}

			// Referencias
			String references = XMLSelect.getString(dataset, "dct:references");
			if (references != null && references.length() > 0) {
				Extra extra = new Extra();
				extra.setKey("References");
				extra.setValue("\"" + references + "\"");
				listadoExtra.add(extra);
			}

			// Nivel de detalle
			String granularity = XMLSelect.getString(dataset,
					"dcat:granularity");
			if (granularity != null && granularity.length() > 0) {
				Extra extra = new Extra();
				extra.setKey("Granularity");
				extra.setValue("\"" + granularity + "\"");
				listadoExtra.add(extra);
			}

			// Calidad de los datos
			String dataQuality = XMLSelect.getString(dataset,
					"dcat:dataQuality");
			if (dataQuality != null && dataQuality.length() > 0) {
				Extra extra = new Extra();
				extra.setKey("Data Quality");
				extra.setValue("\"" + dataQuality + "\"");
				listadoExtra.add(extra);
			}

			// Diccionario de datos
			String dataDictionary = XMLSelect.getString(dataset,
					"dcat:dataDictionary");
			if (dataDictionary != null && dataDictionary.length() > 0) {
				Extra extra = new Extra();
				extra.setKey("Data Dictionary");
				extra.setValue("\"" + dataDictionary + "\"");
				listadoExtra.add(extra);
			}

			// Autor
			String publicador = XMLSelect.getString(dataset,
					"dct:publisher//foaf:Organization//dct:title");
			String webPublicador = XMLSelect.getString(dataset,
					"dct:publisher//foaf:Organization//foaf:homepage");

			List<Resource> listadoResources = new ArrayList<Resource>();
			List<Group> listadoGrupos = new ArrayList<Group>();
			NodeList listDataSets = dataset.getChildNodes();
			for (int index = 0; index < listDataSets.getLength(); index++) {
				// Licencia
				if (listDataSets.item(index).getNodeName()
						.equals("dct:license")) {
					if (listDataSets.item(index).getAttributes().getNamedItem(
							"rdf:resource") != null) {
						String ruta = listDataSets.item(index).getAttributes()
								.getNamedItem("rdf:resource").getNodeValue();
						ds.setLicense_url(ruta);
						if (ruta
								.equalsIgnoreCase("http://www.opendefinition.org/licenses/cc-by")) {
							ds.setLicense_id("cc-by");
						}
					}
					// Identificador
					if (listDataSets.item(index).getAttributes().getNamedItem(
							"dct:identifier") != null) {
						ds.setLicense_id(listDataSets.item(index)
								.getAttributes().getNamedItem("dct:identifier")
								.getNodeValue());
					}

					// Publicador
				} else if (listDataSets.item(index).getNodeName().equals(
						"dct:publisher")) {
					NodeList nodosPublisher = listDataSets.item(index)
							.getChildNodes();
					for (int i1 = 0; i1 < nodosPublisher.getLength(); i1++) {
						Node nodePublisher = nodosPublisher.item(i1);
						if (nodePublisher.getNodeName().equals(
								"foaf:Organization")) {
							NodeList nodesOrganization = nodePublisher
									.getChildNodes();
							for (int i2 = 0; i2 < nodesOrganization.getLength(); i2++) {
								Node nodeOrganization = nodesOrganization
										.item(i2);
								if (nodeOrganization.getNodeName().equals(
										"foaf:homepage")) {
									if (nodeOrganization.getAttributes()
											.getNamedItem("rdf:resource") != null)
										webPublicador = nodeOrganization
												.getAttributes().getNamedItem(
														"rdf:resource")
												.getNodeValue();
								}
							}
						}
					}
					// Distribucion
				} else if (listDataSets.item(index).getNodeName().equals(
						"dcat:distribution")) {

					NodeList listResources = listDataSets.item(index)
							.getChildNodes();

					for (int i2 = 0; i2 < listResources.getLength(); i2++) {
						Node resource = listResources.item(i2);

						if (resource.getNodeName().equals("dcat:Distribution")) {
							Resource resourceDs = new Resource();
							NodeList nodosDistribution = resource
									.getChildNodes();

							for (int indexAux = 0; indexAux < nodosDistribution
									.getLength(); indexAux++) {
								// URL de la distribucion
								if (nodosDistribution.item(indexAux)
										.getNodeName().equals("dcat:accessURL")) {
									if (nodosDistribution.item(indexAux)
											.getAttributes().getNamedItem(
													"rdf:resource") != null) {
										String ruta = nodosDistribution.item(
												indexAux).getAttributes()
												.getNamedItem("rdf:resource")
												.getNodeValue();
										resourceDs.setUrl(ruta);
									}
									// Tamaños
								} else if (nodosDistribution.item(indexAux)
										.getNodeName().equals("dcat:size")) {
									String bytes = XMLSelect.getString(
											nodosDistribution.item(indexAux),
											"rdf:Description//dcat:bytes");
									if (bytes.contains("Mb")) {
										bytes = bytes.split("Mb")[0].trim();
										float bytesfloat = Float
												.parseFloat(bytes);
										bytesfloat = bytesfloat * 1024 * 1024;

										DecimalFormat dec1 = new DecimalFormat(
												"####");
										bytes = String.valueOf(dec1
												.format(bytesfloat));
										resourceDs.setSize(new Integer(bytes));

									} else if (bytes.contains("kb")) {
										bytes = bytes.split("kb")[0].trim();
										float bytesfloat = Float
												.parseFloat(bytes);
										bytesfloat = bytesfloat * 1024;

										DecimalFormat dec1 = new DecimalFormat(
												"####");
										bytes = String.valueOf(dec1
												.format(bytesfloat));
										resourceDs.setSize(new Integer(bytes));
									} else {
										if (bytes != null && bytes.length() > 0) {
											bytes = bytes.replace(".", ",");
											bytes = bytes.split(",")[0];
											resourceDs.setSize(new Integer(
													bytes));
										}
									}
									// Formato
								} else if (nodosDistribution.item(indexAux)
										.getNodeName().equals("dct:format")) {
									String mimetype = XMLSelect.getString(
											nodosDistribution.item(indexAux),
											"dct:IMT//rdf:value");
									resourceDs.setMimetype(mimetype);
									String tipo = XMLSelect.getString(
											nodosDistribution.item(indexAux),
											"dct:IMT//rdfs:label");
									resourceDs.setFormat(tipo);
								} else if (nodosDistribution.item(indexAux)
										.getNodeName().equals("rdfs:label")) {
									if (nodosDistribution.item(indexAux)
											.getChildNodes() != null
											&& nodosDistribution.item(indexAux)
													.getChildNodes()
													.getLength() > 0) {
										resourceDs.setName(nodosDistribution
												.item(indexAux).getChildNodes()
												.item(0).getNodeValue());
									}
								} else if (nodosDistribution.item(indexAux)
										.getNodeName()
										.equals("dct:description")) {
									if (nodosDistribution.item(indexAux)
											.getChildNodes() != null
											&& nodosDistribution.item(indexAux)
													.getChildNodes()
													.getLength() > 0) {
										resourceDs
												.setDescription(nodosDistribution
														.item(indexAux)
														.getChildNodes()
														.item(0).getNodeValue());
									}
								}
							}
							listadoResources.add(resourceDs);
						}
					}

					// Tema (grupo)
				} else if (listDataSets.item(index).getNodeName().equals(
						"dcat:theme")) {

					Node grupo = listDataSets.item(index);

					String label = XMLSelect.getString(grupo,
							"rdf:Description//rdfs:label");
					String descGrupo = XMLSelect.getString(grupo,
							"rdf:Description//dct:description");
					String identifier = XMLSelect.getString(grupo,
							"rdf:Description//dct:identifier");

					Group group = new Group();
					group.setName(identifier);
					group.setTitle(label);
					group.setDescription(descGrupo);

					group.setType("group");

					group = ManagerCKAN.insertarGrupos(group);
					listadoGrupos.add(group);

				}
			}
			// Guardado de datos
			ds.setResources(listadoResources);
			ds.setGroups(listadoGrupos);

			ds.setAuthor(publicador);
			ds.setMaintainer(publicador);
			ds.setUrl(webPublicador);

			ds.setExtras(listadoExtra);

			// Incorporar dataset
			listadoDatasets.add(ds);
		}
		return listadoDatasets;
	}

}