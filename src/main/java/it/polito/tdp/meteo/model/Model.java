package it.polito.tdp.meteo.model;

import java.util.ArrayList;
import java.util.List;

import it.polito.tdp.meteo.DAO.MeteoDAO;

public class Model {
	
	private MeteoDAO meteoDAO; 
	List <List<Rilevamento>> sequenzaCorretta=new ArrayList <List<Rilevamento>>(); 
	int consecutivi; 
	List <Rilevamento> tuttiRilevamenti; 
	List <Rilevamento> rilevamentiMese=new ArrayList<Rilevamento>();
	List <Rilevamento> rilevamentiGiorno=new ArrayList<Rilevamento>();
	List <Rilevamento> parz= new ArrayList<Rilevamento>(); 
	int costo; 
	int costoMinimo=1000000; 
	int o=0; 
	int indice; 
	
	private final static int COST = 100;
	private final static int NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN = 3;
	private final static int NUMERO_GIORNI_CITTA_MAX = 6;
	private final static int NUMERO_GIORNI_TOTALI = 15;

	public Model() {
		this.meteoDAO= new MeteoDAO(); 

	}
	
	public List<Rilevamento> getAllRilevamenti(){
		return this.meteoDAO.getAllRilevamenti(); 
	}

	// of course you can change the String output with what you think works best
	public String getUmiditaMedia(int mese) {
		int contatoreTo=0; 
		int contatoreMi=0; 
		int contatoreGe=0;
		
		int umiditaTo=0; 
		int umiditaMi=0; 
		int umiditaGe=0; 
		
		tuttiRilevamenti=new ArrayList <Rilevamento>(); 
		tuttiRilevamenti=getAllRilevamenti(); 
		
		rilevamentiMese=new ArrayList <Rilevamento>(); 
		for (int a=0; a<tuttiRilevamenti.size(); a++) {
			if (tuttiRilevamenti.get(a).getData().getMonthValue()==mese) {
				Rilevamento rilTemp=tuttiRilevamenti.get(a); 
			
				rilevamentiMese.add(rilTemp); 
			}
		}
		for (int a=0; a<rilevamentiMese.size(); a++) {
			if(rilevamentiMese.get(a).getLocalita().compareTo("Torino")==0)
			{
			contatoreTo++; 
			umiditaTo=umiditaTo+rilevamentiMese.get(a).getUmidita(); 
			}
			
			if(rilevamentiMese.get(a).getLocalita().compareTo("Milano")==0)
			{
			contatoreMi++; 
			umiditaMi=umiditaMi+rilevamentiMese.get(a).getUmidita(); 
			}
			
			if(rilevamentiMese.get(a).getLocalita().compareTo("Genova")==0)
			{
			contatoreGe++; 
			umiditaGe=umiditaGe+rilevamentiMese.get(a).getUmidita(); 
			}
		}
		
		
		return "UmiditaMediaTO= "+ umiditaTo/contatoreTo+ " UmiditaMediaMI= "+ umiditaMi/contatoreMi+ " UmiditaMediaGE= "+ umiditaGe/contatoreGe;
	}
	
	// of course you can change the String output with what you think works best
	public String trovaSequenza(int mese) {
		costoMinimo=1000000; 
		o=0; 
		parz= new ArrayList<Rilevamento>(); 
		sequenzaCorretta=new ArrayList <List<Rilevamento>>();
		tuttiRilevamenti=new ArrayList <Rilevamento>();
		tuttiRilevamenti=getAllRilevamenti(); 
		
		rilevamentiMese=new ArrayList <Rilevamento>();
		for (int a=0; a<tuttiRilevamenti.size(); a++) {
			if (tuttiRilevamenti.get(a).getData().getMonthValue()==mese) {
				Rilevamento rilTemp=tuttiRilevamenti.get(a); 
			
				rilevamentiMese.add(rilTemp); 
			}
		}
		trovaSequenzaRicorsiva(parz,1,mese); 
		
		//controllare quale sequenza ha costo minimo 
		for (List<Rilevamento> list: sequenzaCorretta) {
			costo=list.get(0).getUmidita(); 
			for (int s=1; s<list.size(); s++) {
				if(list.get(s).getLocalita().compareTo(list.get(s-1).getLocalita())!=0)
					costo=costo+100; 
				costo=costo+list.get(s).getUmidita(); 
			}
		if (costo<costoMinimo) {
			costoMinimo=costo; 
			indice=o; 	
		}
		o++; 
		}
		
		
		return sequenzaCorretta.get(indice).toString();
	}
	
	public void trovaSequenzaRicorsiva(List<Rilevamento>parziale, int livello, int mese) {
		//controllato che non ci siano più di 6 giorni 
		int contatoreT=0; 
		int contatoreM=0; 
		int contatoreG=0; 
		for(Rilevamento r: parziale) {
			if(r.getLocalita().compareTo("Torino")==0)
				contatoreT++; 
			if(r.getLocalita().compareTo("Milano")==0)
				contatoreM++; 
			if(r.getLocalita().compareTo("Genova")==0)
				contatoreG++; 
		}
		
		if(contatoreT>6) return; 
		if(contatoreM>6) return; 
		if(contatoreG>6) return; 
		
		//controllare che i tre giorni siano consecutivi
		consecutivi=1; 
		for (int i=1; i<parziale.size(); i++) {
			if(parziale.get(i).getLocalita().compareTo(parziale.get(i-1).getLocalita())==0)
				consecutivi++; 
			else if (parziale.get(i).getLocalita().compareTo(parziale.get(i-1).getLocalita())!=0 && consecutivi<3)
				return; 
			else if(parziale.get(i).getLocalita().compareTo(parziale.get(i-1).getLocalita())!=0 && consecutivi>=3)
				consecutivi=1; 
		}
		
		if(livello==16) {
			sequenzaCorretta.add(new ArrayList<Rilevamento>(parziale)); 
		}
		//caso intermedio
		rilevamentiGiorno=new ArrayList <Rilevamento>(); 
		rilevamentiGiorno=getRilevamentiGiorno(livello, rilevamentiMese); 
		
		for (Rilevamento c: rilevamentiGiorno) {
			parziale.add(c); 
			trovaSequenzaRicorsiva(parziale, livello+1, mese); 
			parziale.remove(parziale.size()-1); 
			
		}
	}
	
	public List<Rilevamento> getRilevamentiGiorno(int livello, List <Rilevamento> rilevamentiMese){
		for (int b=0; b<rilevamentiMese.size(); b++) {
			if (rilevamentiMese.get(b).getData().getDayOfMonth()==livello)
				rilevamentiGiorno.add(rilevamentiMese.get(b)); 
		}
		return rilevamentiGiorno; 
	}
	

}
