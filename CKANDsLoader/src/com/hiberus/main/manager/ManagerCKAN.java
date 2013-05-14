package com.hiberus.main.manager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.ckan.CKANException;
import org.ckan.Client;
import org.ckan.Connection;
import org.ckan.Dataset;
import org.ckan.Group;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


public class ManagerCKAN {
	
	//Datos de configuración para acceso a CKAN
	private static String ApiKey = "xxxxx-xxx-xxxx-xxxx-xxxxxxxxxxx"; 	//API KEY para acceder
	private static String hostName = "http://xx.xx.xxx.xxx"; 			//Host de CKAN
	private static int portName = 0;									//Puerto
	
	private static Client c = new Client( new Connection(hostName, portName),
            ApiKey);
	

	//Crea los datasets
	public static void insertarDataSets(List<Dataset> listadoDataSets) throws Exception{

		for( Dataset ds : listadoDataSets) {
			
			Gson gson = new Gson();
			String data = gson.toJson( ds);
			 
			JsonParser parser = new JsonParser();
			JsonObject obj = parser.parse(data).getAsJsonObject();
			 
			Dataset resultAux = null;
			try {
				resultAux =  c.getDataset(ds.getName());
			} catch (Exception e) {
				System.out.println("Dataset no encontrado");
			}
			
			if(resultAux != null ) {
				ds.setId(resultAux.getId()); 
				ds.setState("active");
				c.updateDataset(ds);
			} else {
				Dataset result = c.createDataset(ds); 
			}
			
		}
		 
	}
	
	public static Group comprobarExistenciaGrupo(Group grupo) throws Exception {
		return c.getGroup(grupo.getName());
	}
	
	
	public static Group insertarGrupos(Group grupo) throws Exception{
		
		Group groupAux =  null;
		try {
			groupAux = c.getGroup(grupo.getName());
		} catch (Exception e) {
			//Grupo no encontrado
		}
	
		if(groupAux == null)
			groupAux = c.createGroup(grupo);
	
		return groupAux;
	}
	
	protected static String Post(String host, int port, String api, String path, String data)
    throws CKANException {
	    URL url = null;
	
	    try {
	        url = new URL( host + ":" + port + path);
	    } catch ( MalformedURLException mue ) {
	        System.err.println(mue);
	        return null;
	    }
	
	    String body = "";
	    
    	HttpClient httpclient = new DefaultHttpClient();
	    try {
	        HttpPost postRequest = new HttpPost(url.toString());
	        postRequest.setHeader( "X-CKAN-API-Key", api);
	        
		    StringEntity input = new StringEntity(data, null, "UTF-8");
		    input.setContentType("application/json");
		    postRequest.setEntity(input);
		    
	        HttpResponse response = httpclient.execute(postRequest);
	        
	        int statusCode = response.getStatusLine().getStatusCode();
	
	        BufferedReader br = new BufferedReader(
	                    new InputStreamReader((response.getEntity().getContent())));
	
	        String line = "";
		    while ((line = br.readLine()) != null) {
	            body += line;
		    }
	    } catch( IOException ioe ) {
	        System.out.println( ioe );
	    } finally {
	        httpclient.getConnectionManager().shutdown();
	    }

	    return body;
	}
	
    protected static <T> T LoadClass( Class<T> cls, String data ) {
        Gson gson = new Gson();
        return gson.fromJson(data, cls);
    }

	 
			 
}
