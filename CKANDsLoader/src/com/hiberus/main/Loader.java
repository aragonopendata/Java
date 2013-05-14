package com.hiberus.main;

import java.util.List;

import org.ckan.Dataset;

import com.hiberus.main.manager.CreateDataSet;
import com.hiberus.main.manager.ManagerCKAN;
 

public class Loader {
 
			
	public static void main(String[] args)   {
		try {
			
 			List<Dataset> listadoDataSets = CreateDataSet.cargarDirectorio();
 			
 			ManagerCKAN.insertarDataSets(listadoDataSets);

		} catch (Exception e) {
			e.printStackTrace();
		}
			 
	}			 
}
