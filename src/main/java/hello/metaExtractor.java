package hello;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openprovenance.prov.interop.InteropFramework;
import org.openprovenance.prov.interop.InteropFramework.ProvFormat;
import org.openprovenance.prov.model.Document;
import org.openprovenance.prov.model.Namespace;
import org.openprovenance.prov.dot.*;
import org.openprovenance.prov.model.ProvFactory;
import org.openprovenance.prov.template.Bindings;
import org.openprovenance.prov.template.BindingsJson;
import org.openprovenance.prov.template.Expand;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;



public class metaExtractor{
	
	public final ProvFactory pFactory;
	private static String bindingsString ="";
	private static String main_url ;
	private static boolean success;
	
	public metaExtractor(ProvFactory pFactory,String url, String bindingsString) {
		metaExtractor.setBindingsString(bindingsString);
		metaExtractor.main_url = url;
        this.pFactory = pFactory;
        metaExtractor.success = false;
    }
	public boolean extract(String url,metaExtractor me) throws Exception{
		Map<String,String> myMap = new HashMap<String,String>();
		Queue<String> linkqueue = new LinkedList<String>();
		Stack linkStack = new Stack();
		
		if(me.getPageMetaData(url, myMap,linkqueue,linkStack))
		{
			  if(me.BuildBindings(myMap,linkqueue,linkStack))
		        {
		        	Document doc;
		    		InteropFramework IF = new InteropFramework();
		    		Expand myExpand=new Expand(me.pFactory,false, true);
		            Document expanded;
		            doc = IF.readDocumentFromFile("template.provn");
		            Bindings bb=BindingsJson.fromBean(BindingsJson.importBean(new File("binding.json")),me.pFactory);
		            expanded = myExpand.expander(doc,bb);
		            IF.writeDocument("provenanceExpanded.provn", ProvFormat.PROVN, expanded); 
		           
		            outputSVG(url,IF,expanded);
		            me.success = true;
		            return me.success;
		        }
		        else{
		        	return me.success;
		        }
		}
		else
		{
			return false;
		}  
	}
	private String getPageTitle(String url) {
		 org.jsoup.nodes.Document doc = null;
		try {
			doc = Jsoup.connect(url).get();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String title = doc.title();
		return title;
		
	}
	private boolean getPageMetaData(String url,Map<String,String> myMap,Queue<String> linkqueue,Stack linkStack) throws Exception
	{
		try 
		{
		   org.jsoup.nodes.Document doc =  Jsoup.connect(url).get();
		   Elements meta = doc.getElementsByTag("meta");
		  // Elements links = doc.select("a[href]"); //gets all links
		   Elements links = doc.select("a[href~=^[^#]+$]"); //gets all external links
		   for(Element link : links)
		   {
			   String href = link.text();
			   if(href.contains(url) || href.contains(url.replace("http", "https")))	//self linking
			   {
				     linkStack.push(href);
			   }
			   else{
				   href.replaceAll("/http:\\/\\/(?:www\\.)?([a-z0-9\\-]+)(?:\\.[a-z\\.]+[\\/]?).*/i", ""); 	//external referencing
				   linkqueue.add(href); 
			   }
			   
		   }
		   for(Element metaTag : meta)
		   {
			  processTags(metaTag,myMap);  
		   }
		   
		   return true;
		} 
		catch (UnknownHostException e) {
		    System.err.println("Unknown host");
		    e.printStackTrace(); // I'd rather (re)throw it though.
		    return false;
		}
		
	}
	private void processTags(Element metaTag,Map<String,String> myMap)
	{
		String content = metaTag.attr("content");
		String name = metaTag.attr("name");
		String property = metaTag.attr("property");
		//System.out.println(metaTag.toString());		
		if(name!="" && property=="")		//dc or regular meta tags
		{
			parseTags(name,content,myMap);	
		}
		else if(name=="" && property!="")	//means og tags
		{
			parseOgTags(property,content,myMap);
		}	
	}
	private void parseTags(String name,String content,Map<String,String> myMap)
	{
		if(name.toLowerCase().equals("title") && content!="")
		{
			//toggle boolean for title
			myMap.put(name, content);
		}
		else if(name.toLowerCase().equals("description") && content!="")
		{
			//toggle boolean for description
			myMap.put(name, content);
		}
		else if(name.toLowerCase().equals("keywords") && content!="")
		{
			//toggle boolean for author
			myMap.put(name, content);
		}
		else if(name.toLowerCase().equals("author")&& content!="")
		{
			myMap.put(name, content);
		}
		else if(name.toLowerCase().equals("dc.publisher")&& content!="")
		{
			myMap.put(name, content);
		}
		else if(name.toLowerCase().equals("dc.creator")&& content!="")
		{
			myMap.put(name, content);
		}
		else if(name.toLowerCase().equals("dc.contributors")&& content!="")
		{
			myMap.put(name, content);
		}
		else if(name.toLowerCase().equals("dc.date")&& content!="")
		{
			myMap.put(name, content);
		}
		else if(name.toLowerCase().contains("contact")&& content!="")
		{
			myMap.put(name, content);
		}
		else{}	
	}
	private void parseOgTags(String property, String content,Map<String,String> myMap)
	{
		if(property.contains("og:title") && content!="")
		{
			//toggle boolean for title
			myMap.put(property, content);
		}
		else if(property.contains("og:description") && content!="")
		{
			//toggle boolean for description
			myMap.put(property, content);
		}
		else if(property.contains("article:author") && content!="")
		{
			//toggle boolean for author
			myMap.put(property, content);
		}
		else if(property.contains("published_time")&& content!="")
		{
			myMap.put(property, content);
		}
		else if(property.contains("site_name")&& content!="")
		{
			myMap.put(property, content);
		}
		else{}	
	}
	private boolean BuildBindings(Map<String,String> myMap,Queue<String> linq,Stack linkStack)
	{
		JSONObject bind = new JSONObject();	
		JSONObject var = new JSONObject();
		bind = createFixedParts(bind,var,myMap,linq);		
		for (Map.Entry<String, String> entry : myMap.entrySet()) 	//loop map to continue creating bindings
		{
		    String key = entry.getKey();
		    String value = entry.getValue();
		    
		    if(key.contains("og:title"))				//parse tags
		    {
		    	key = key.replace(":title", "Title");
		    }
		    else if(key.contains("og:site"))
		    {
		    	key = key.replace("og:", "");
		    }
		    else if(key.contains("og:description")){
		    	key = key.replace(":description", "Description");
		    }
		    else if(key.contains("article"))
		    {
		    	key = key.replace("article:", "");
		    }
		    else if(key.contains("dc."))
		    {
		    	key = key.replace("dc.", "");
		    }
		    
		    JSONArray key_array = new JSONArray();
		    JSONObject keyJson = new JSONObject();
		    
		    if(key.contains("publisher") || key.contains("url") || key.contains("website") || key.contains("author")||key.contains("creator"))
		    {
		    	
		    	keyJson.put("@id","meta:"+value);
		    }
		    else{
		    	keyJson.put("@value", "meta:"+value);
		    }
		    
		    key_array.add(keyJson);
		    var.put(key, key_array);
		    
		}		
		outputJSON(bind);
		return true;
	
	}

	private JSONObject createFixedParts(JSONObject bind,JSONObject var, Map<String,String> myMap,Queue<String> linq)
	{
		
		JSONObject context = new JSONObject();
		bind.put("var", var);
		bind.put("context", context);
		context.put("meta", main_url);
		
		
		
		JSONArray publishing_array = new JSONArray();	//fixed
		JSONObject publishing = new JSONObject();
		publishing.put("@id", "meta:publishing");
		publishing_array.add(publishing);
		var.put("publishing", publishing_array);
		
		JSONArray website_array = new JSONArray();		//fixed
		JSONObject website = new JSONObject();
		String title="";
		if(myMap.get("og:title") !=null)
		{
			
			title = myMap.get("og:title");
			String[] words = title.split("[^a-zA-Z]");
			title= words[0];
		}
		else if(myMap.get("title") != null)
		{
			title =  myMap.get("title");
			String[] words = title.split("[^a-zA-Z]");
			title= words[0];
		}
		else{
			
			title = main_url.replace("http://www.","").replace("www.","").replace(".co.uk","").replace(".com","").replace(".org","").replace(".net","");
		}
		
		website.put("@id", "meta:"+title);
		website_array.add(website);
		
		for (String link : linq) {
			JSONObject coll = new JSONObject();
			String arr[] = link.split(" ");
			link = arr[0];
			link = link.replaceAll("[^A-Za-z]+", "");
			
			coll.put("@id", "meta:"+link);
			website_array.add(coll);
	   }
				
		var.put("currentPage", website_array);
		
		
		JSONArray contributor_array = new JSONArray();	//fixed
		JSONObject contributor = new JSONObject();
		contributor.put("@id", "meta:Contributors");
		contributor_array.add(contributor);
		JSONObject contributorTwo = new JSONObject();
		contributorTwo.put("@id", "meta:ContributorsTwo");
		contributor_array.add(contributorTwo);
		var.put("contributor", contributor_array);
		
		
		
//		JSONArray link_array = new JSONArray();
//    	JSONObject linkOne = new JSONObject();
//    	linkOne.put("@id", "meta:"+linq.remove());
//    	link_array.add(linkOne);
//    	var.put("linkOne", link_array);
//        
//    	JSONArray linkTwo_array = new JSONArray();
//    	JSONObject linkTwo = new JSONObject();
//    	linkTwo.put("@id", "meta:"+linq.remove());
//    	linkTwo_array.add(linkTwo);
//    	var.put("linkTwo", linkTwo_array);
//
//    	JSONArray linkThree_array = new JSONArray();
//    	JSONObject linkThree = new JSONObject();
//    	linkThree.put("@id", "meta:"+linq.remove());
//    	linkThree_array.add(linkThree);
//    	var.put("linkThree", linkThree_array);
//
//    	JSONArray linkFour_array = new JSONArray();
//    	JSONObject linkFour = new JSONObject();
//    	linkFour.put("@id", "meta:"+linq.remove());
//    	linkFour_array.add(linkFour);
//    	var.put("linkFour", linkFour_array);
//    	
//    	JSONArray linkFive_array = new JSONArray();
//    	JSONObject linkFive = new JSONObject();
//    	linkFive.put("@id", "meta:"+linq.remove());
//    	linkFive_array.add(linkFive);
//    	var.put("linkFive", linkFive_array);
		
		
		JSONArray url_array = new JSONArray();					//fixed
		JSONObject url = new JSONObject();
		url.put("@id", "meta:"+main_url.replace("http://", ""));
		url_array.add(url);
		var.put("mainUrl", url_array);
		
		return bind;	
	}
	private void outputJSON(JSONObject bind){
		try (FileWriter file = new FileWriter("binding.json")) {

            file.write(bind.toJSONString().replace("\\", ""));
            file.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	private static String getBindingsString() {
		return bindingsString;
	}
	private static void setBindingsString(String bindingsString) {
		metaExtractor.bindingsString = bindingsString;
	}
	private void outputSVG(String url,InteropFramework IF, Document expanded)
	{
//		IF.writeDocument("provenanceExpanded.svg", ProvFormat.SVG, expanded);
		url = url.replaceAll("/^(?:https?:\\/\\/)?(?:[^@\n]+@)?(?:www\\.)?([^:\\/\\n]+)/im","");
		url = url.replace("http://","");
		url = url.replace(".co.uk","").replace(".com","").replace(".org","").replace(".net","").replace("www.", "");
		
		//siteName= siteName.replace("\\", "\\\\");
		String OUTPUT_FILE = "src/main/resources/static/graphs/"+url+".svg";
		OutputStream out = null;
		try {
			out = new FileOutputStream(OUTPUT_FILE);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		IF.writeDocument(out, ProvFormat.SVG, expanded);
	}
	
}

