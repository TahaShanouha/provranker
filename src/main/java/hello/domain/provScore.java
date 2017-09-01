package hello.domain;



import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "provScore")
public class provScore {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private String siteName;
    private float EntityScore;
    private float GenerationScore;
    private float WeightedScore;
    private String domain;

    public provScore() {}

    public provScore(String siteName, float EntityScore,float GenerationScore,float WeightedScore,String domain) {
        this.siteName = siteName;
        this.domain = domain;
        this.EntityScore = EntityScore;
        this.GenerationScore = GenerationScore;
        this.WeightedScore = WeightedScore;
    }

    @Override
    public String toString() {
        return String.format(
                "Customer[id=%d, siteName='%s', EntityScore='%f']",
                id, siteName, EntityScore);
    }

	public void setEntityScore(float f) {
		// TODO Auto-generated method stub
		this.EntityScore= f;
	}
	public void setGenerationScore(float f) {
		// TODO Auto-generated method stub
		this.GenerationScore= f;
	}

	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String domain) {
		this.domain = domain;
	}
	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.siteName = siteName;
	}

	public float getGenerationScore() {
		return GenerationScore;
	}
	public float getEntityScore() {
		return EntityScore;
	}
	public float getWeightedScore() {
		return WeightedScore;
	}
	public void setWeightedScore(float f) {
		// TODO Auto-generated method stub
		this.WeightedScore= f;
	}
	

}