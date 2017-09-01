package hello;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;

import org.openprovenance.prov.interop.InteropFramework;
import org.openprovenance.prov.interop.InteropFramework.ProvFormat;
import org.openprovenance.prov.model.Activity;
import org.openprovenance.prov.model.Document;
import org.openprovenance.prov.model.Entity;
import org.openprovenance.prov.model.IndexedDocument;
import org.openprovenance.prov.model.Other;
import org.openprovenance.prov.model.ProvFactory;
import org.openprovenance.prov.model.QualifiedName;
import org.openprovenance.prov.model.QualifiedNameUtils;
import org.openprovenance.prov.xml.WasGeneratedBy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;

import hello.data.provScoreRepo;
import hello.domain.provScore;


public class ProcessProv {
	public final ProvFactory pFactory;
	String filename;
	float[] finalScore;
	String sitename;
	@Autowired
 	provScoreRepo repo;
	public ProcessProv(ProvFactory pFactory,String filename,String url){
		this.pFactory = pFactory;
		this.filename = filename;
		this.sitename = url;
	}
	private String getSiteName(){
		return this.sitename;
	}
	public float[] process(ProcessProv processor)
	{
		InteropFramework IF = new InteropFramework();
		Document doc;
		doc = IF.readDocumentFromFile(processor.filename, ProvFormat.PROVN);
		IndexedDocument indexedDocument = new IndexedDocument(processor.pFactory, doc);
		

		int entityPoints = getEntityScore(indexedDocument,processor.sitename);
		
		int generationPoints = getGenerationScore(indexedDocument);
		
		this.finalScore =  weightedSum(entityPoints,generationPoints);
		if(finalScore!=null)
		{
			return finalScore;
		}
		else{
			return null;
		}
	}
	private float[] weightedSum(int entityPoints,int generationPoints)
	{
		float[] weightedSum = new float[3];
		weightedSum[0]= (float) (entityPoints);
		weightedSum[1] = (float)(generationPoints);
		weightedSum[2] = (float)(((entityPoints)+(generationPoints))/2);
		return weightedSum;
	}
	private static int getActivityScore(IndexedDocument doc)
	{
		QualifiedName q = (QualifiedName) (doc.getActivityMap().keySet().toArray()[0]);
		Activity publishing = (Activity) doc.getActivityMap().get(q);
		List other = publishing.getOther();
		return other.size();
		
		
	}
	private static int getEntityScore(IndexedDocument doc,String sitename)//,ProcessProv processor)
	{
		HashMap hm = doc.getEntityMap();
		int size = hm.size();
		sitename = sitename.replace("http://", "");
		
		
		
		
		Object[] obj =  doc.getEntityMap().keySet().toArray();
		int index =0;
		for(int i=0;i<obj.length;i++)
		{

			if(obj[i].toString().equals("\'meta:{http://"+sitename+"}"+sitename+"\'"))
			{
				index = i;
			}
		}
		QualifiedName q = (QualifiedName) (doc.getEntityMap().keySet().toArray()[index]);


		
		
		Entity url = (Entity) doc.getEntityMap().get(q);
		List<Other> other = url.getOther();
//		for (Other o : other) {
//		    System.out.println(o.getValue());
//		}
		return other.size();	
	}
	private static int getGenerationScore(IndexedDocument doc)
	{
		Collection<org.openprovenance.prov.model.WasGeneratedBy>  generations = doc.getWasGeneratedBy();
		int extraPoints = 0;
		for ( org.openprovenance.prov.model.WasGeneratedBy wgb : generations) {
			if(!wgb.getOther().isEmpty())
			{
				extraPoints+= wgb.getOther().size();
			}
		}
		Collection other = generations;
		return extraPoints+other.size();
		
	}
	private int getDerivatons(IndexedDocument doc)
	{
		Collection derivations = doc.getWasDerivedFrom();
		int count = 0;
		for (Object obj : derivations) {
			count++;
		      
		   }
		return count;
	}
	private int getAssociations(IndexedDocument doc)
	{
		Collection associations = doc.getWasAssociatedWith();
		int count = 0;
		for (Object obj : associations) {
			count++;
		      
		   }
		return count;
	}
	private int getGenerations(IndexedDocument doc)
	{
		Collection  generations = doc.getWasGeneratedBy();
		int count = 0;
		for (Object obj : generations) {
			count++;
		      
		   }
		return count;
	}

} 

