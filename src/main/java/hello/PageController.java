package hello;

import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import hello.data.provScoreRepo;
import hello.domain.provScore;

@Controller
public class PageController {

	 @Autowired
	    private provScoreRepo repository;
	 

    
    @RequestMapping("/provrank/display")
    public String age(@RequestParam(value="age", required=false, defaultValue="5") String url, Model model) {
		
    	
		List<provScore> provScores = (List<provScore>) repository.findAll();
		Normalize(provScores);
		//provScores.sort(new ProvScoreComparator());
		model.addAttribute("provScores", provScores);
		
		return "display";
    }
    
    @RequestMapping(path="/provrank/displayEntityOrder")
	public @ResponseBody
	 List<Float> getAllEntityScores() {
		// This returns a JSON or XML with the users
		List<Float> ps = repository.findAllEntityScores();
		return ps;	
	}
    @RequestMapping(path="/provrank/displayGenerationOrder")
   	public @ResponseBody
   	 List<Float> getAllGenerationScores() {
   		// This returns a JSON or XML with the users
   		List<Float> ps = repository.findAllGenerationScores();
   		return ps;	
   	}
    @RequestMapping(path="/provrank/displayWeightOrder")
   	public @ResponseBody
   	 List<Float> getAllWeightScores() {
   		// This returns a JSON or XML with the users
   		List<Float> ps = repository.findAllWeightedScores();
   		return ps;	
   	}
    private void Normalize(List<provScore> ps)
    {
    	NormalizeEntityScore(ps);
    	NormalizeGenerationScore(ps);
    	NormalizeWeightedScore(ps);
    }
    private void NormalizeEntityScore(List<provScore> ps)
    {
    	List<Float> es = getAllEntityScores();
    	float maxEnt = es.get(0);
    	float minEnt = es.get(es.size()-1);
    	for (provScore psElement : ps) {
		   float newEntityScore = (psElement.getEntityScore() - minEnt) / (maxEnt - minEnt);
		   psElement.setEntityScore(newEntityScore);
		}
    }
    private void NormalizeGenerationScore(List<provScore> ps)
    {
    	List<Float> es = getAllGenerationScores();
    	float maxGen = es.get(0);
    	float minGen = es.get(es.size()-1);
    	for (provScore psElement : ps) {
		   float newGenerationScore = (psElement.getGenerationScore() - minGen) / (maxGen - minGen);
		   psElement.setGenerationScore(newGenerationScore);
		}
    }
    private void NormalizeWeightedScore(List<provScore> ps)
    {
    	List<Float> es = getAllWeightScores();
    	float maxWgt = es.get(0);
    	float minWgt = es.get(es.size()-1);
    	for (provScore psElement : ps) {
		   float newWeightedScore = (psElement.getWeightedScore() - minWgt) / (maxWgt - minWgt);
		   psElement.setWeightedScore(newWeightedScore);
		}
    }
}
