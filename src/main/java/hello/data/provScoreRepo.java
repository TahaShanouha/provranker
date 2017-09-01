package hello.data;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import hello.domain.provScore;



public interface provScoreRepo extends CrudRepository<provScore, Long> {
	public static final String FIND_ENTITY_SCORES = "SELECT EntityScore FROM provscore ORDER BY EntityScore DESC";
	
    List<provScore> findBySiteName(String url);
    
    @Query("select EntityScore from provScore ORDER BY EntityScore DESC")
   public List<Float> findAllEntityScores();

    @Query("select GenerationScore from provScore ORDER BY GenerationScore DESC")
    public List<Float> findAllGenerationScores();
    
    @Query("select WeightedScore from provScore ORDER BY WeightedScore DESC")
    public List<Float> findAllWeightedScores();
  
}