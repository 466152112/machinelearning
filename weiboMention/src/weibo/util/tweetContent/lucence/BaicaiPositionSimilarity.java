package weibo.util.tweetContent.lucence;  
    
import org.apache.lucene.index.FieldInvertState;  
import org.apache.lucene.search.Similarity;
    
public class BaicaiPositionSimilarity extends Similarity {  
    
	/** Implemented as
	   *  <code>state.getBoost()*lengthNorm(numTerms)</code>, where
	   *  <code>numTerms</code> is {@link FieldInvertState#getLength()} if {@link
	   *  #setDiscountOverlaps} is false, else it's {@link
	   *  FieldInvertState#getLength()} - {@link
	   *  FieldInvertState#getNumOverlap()}.
	   *
	   *  @lucene.experimental */
	  @Override
	  public float computeNorm(String field, FieldInvertState state) {
	    final int numTerms;
	    if (discountOverlaps)
	      numTerms = state.getLength() - state.getNumOverlap();
	    else
	      numTerms = state.getLength();
	    return state.getBoost() * ((float) (1.0 / Math.sqrt(numTerms)));
	  }
	  
	  /** Implemented as <code>1/sqrt(sumOfSquaredWeights)</code>. */
	  @Override
	  public float queryNorm(float sumOfSquaredWeights) {
	    return (float)(1.0 / Math.sqrt(sumOfSquaredWeights));
	  }

	  /** Implemented as <code>sqrt(freq)</code>. */
	  @Override
	  public float tf(float freq) {
	    return (float)Math.sqrt(freq);
	  }
	    
	  /** Implemented as <code>1 / (distance + 1)</code>. */
	  @Override
	  public float sloppyFreq(int distance) {
	    return 1.0f / (distance + 1);
	  }
	    
	  /** Implemented as <code>log(numDocs/(docFreq+1)) + 1</code>. */
	  @Override
	  public float idf(int docFreq, int numDocs) {
	    return (float)(Math.log(numDocs/(double)(docFreq+1)) + 1.0);
	  }
	    
	  /** Implemented as <code>overlap / maxOverlap</code>. */
	  @Override
	  public float coord(int overlap, int maxOverlap) {
	    return overlap / (float)maxOverlap;
	  }

	  // Default true
	  protected boolean discountOverlaps = true;

	  /** Determines whether overlap tokens (Tokens with
	   *  0 position increment) are ignored when computing
	   *  norm.  By default this is true, meaning overlap
	   *  tokens do not count when computing norms.
	   *
	   *  @lucene.experimental
	   *
	   *  @see #computeNorm
	   */
	  public void setDiscountOverlaps(boolean v) {
	    discountOverlaps = v;
	  }

	  /** @see #setDiscountOverlaps */
	  public boolean getDiscountOverlaps() {
	    return discountOverlaps;
	  }
}