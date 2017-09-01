package hello.controller;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.openprovenance.prov.interop.InteropFramework;
import org.openprovenance.prov.interop.InteropFramework.ProvFormat;
import org.openprovenance.prov.interop.*;
import org.openprovenance.prov.model.Document;
import org.openprovenance.prov.template.Bindings;
import org.openprovenance.prov.template.BindingsJson;
import org.openprovenance.prov.template.Expand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import hello.Greeting;
import hello.ProcessProv;
import hello.metaExtractor;
import hello.data.provScoreRepo;
import hello.domain.provScore;

@RestController
public class ServicesController {

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();
    private final String provenanceExpanded = "provenanceExpanded.provn";
    
//    @Autowired
//    private provScoreRepo psRepo;
//    @Autowired
//    JdbcTemplate jdbcTemplate;
    @Autowired
    private provScoreRepo repository;
    
    @RequestMapping("/greeting")
    public Greeting greeting(@RequestParam(value="name", defaultValue="World") String name) {
        return new Greeting(counter.incrementAndGet(),
                            String.format(template, name));
    }
    
    @RequestMapping("/provrank/extract")
    public String extract(@RequestParam(value="url", defaultValue="http://www.dailymail.co.uk") String url) throws Exception {
    	String domain = "";
    	domain = url.replaceAll("/^(?:https?:\\/\\/)?(?:[^@\n]+@)?(?:www\\.)?([^:\\/\\n]+)/im","");
    	domain = domain.replace("http://","");
    	domain = domain.replace(".co.uk","").replace(".com","").replace(".org","").replace(".net","").replace("www.", "");
        metaExtractor me = new metaExtractor(InteropFramework.newXMLProvFactory(), url, "");
        if(me.extract(url, me))
        {
        	float[] scores = process(provenanceExpanded,url);
        	if(scores!=null)
        	{
        		boolean exists= checkUrlExists(url);
        		if(exists!=true)
        		{
        			addNewUser(url,scores[0],scores[1],scores[2], domain);
            		return "Provenance Score of "+url+" has been stored in the db. To view call the Display service.";
        		}
        		return "url already exists";
        	}
        	return "An score wasnt generated properly";
        }
		return "An error has occured";
    }
    
    @RequestMapping("/provrank/process")
    private float[] process(@RequestParam(value="filename",defaultValue="provenanceExpanded.provn") String filename,String url){
    	ProcessProv processor = new ProcessProv(InteropFramework.newXMLProvFactory(),filename,url);
		return processor.process(processor);
    	
    }
	@RequestMapping(path="/provrank/displayAll")
	public @ResponseBody Iterable<provScore> getAllScores() {
		// This returns a JSON or XML with the users
		Iterable<provScore> ps = repository.findAll();
		System.out.println(ps);
		return ps;
		
		
	}
	
	@RequestMapping(path="/provrank/demo")
	public @ResponseBody String demo() {
		try {
			extract("http://www.dailymail.co.uk");
			extract("http://www.theguardian.com");
			extract("http://www.telegraph.co.uk");
			extract("http://www.mirror.co.uk");
			extract("http://www.express.co.uk");
			extract("http://www.metro.co.uk");
			extract("http://www.channel4.com");
			extract("http://www.huffingtonpost.co.uk");
			extract("http://www.standard.co.uk");
			extract("http://www.irishtimes.com");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			
			e.printStackTrace();
			return "demo failed";
		}
		return "demo successful";
		
		
	}
	
	@RequestMapping(path="/provrank/add") // Map ONLY GET Requests
	public @ResponseBody String addNewUser (@RequestParam String siteName
			, @RequestParam float EntityScore,@RequestParam float GenerationScore,float WeightedScore,String domain) {
		// @ResponseBody means the returned String is the response, not a view name
		// @RequestParam means it is a parameter from the GET or POST request
		
		provScore ps = new provScore(siteName,EntityScore,GenerationScore,WeightedScore,domain);
		repository.save(ps);
		return "Saved\n";


	}
    private boolean checkUrlExists(String url)
    {
    	Iterable<provScore> returned = repository.findAll();
    	 for (provScore item : returned) {
    	        if(item.getSiteName().equals(url))
    	        {
    	        	return true;
    	        }
    	    }
    	 return false;

    }
    
//    @RequestMapping("/provrank/display")
//    private List<provScore> display()
//    {
//    	
//    	List<provScore> scores = this.jdbcTemplate.query(
//    	        "select siteName, score from provScores",
//    	        new RowMapper<provScore>() {
//    	            public provScore mapRow(ResultSet rs, int rowNum) throws SQLException {
//    	            	provScore ps = new provScore();
//    	                ps.setName(rs.getString("siteName"));
//    	                ps.setScore(rs.getFloat("score"));
//    	                return ps;
//    	            }
//    	        });
//    		return scores;
//    }
    
}
