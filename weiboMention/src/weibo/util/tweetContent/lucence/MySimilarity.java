package weibo.util.tweetContent.lucence;

import org.apache.lucene.index.FieldInvertState;
import org.apache.lucene.search.Similarity;

public class MySimilarity extends Similarity {

	  /** Implemented as
	   *  <code>state.getBoost()*lengthNorm(numTerms)</code>, where
	   *  <code>numTerms</code> is {@link FieldInvertState#getLength()} if {@link
	   *  #setDiscountOverlaps} is false, else it's {@link
	   *  FieldInvertState#getLength()} - {@link
	   *  FieldInvertState#getNumOverlap()}.
	   *
	   *  <p><b>WARNING</b>: This API is new and experimental, and may suddenly
	   *  change.</p> */
	  @Override
	  public float computeNorm(String field, FieldInvertState state) {
	    final int numTerms;
	    if (discountOverlaps)
	      numTerms = state.getLength() - state.getNumOverlap();
	    else
	      numTerms = state.getLength();
	    return (state.getBoost() * lengthNorm(field, numTerms));
	  }
	  
	  
	  /** Implemented as <code>1/sqrt(sumOfSquaredWeights)</code>. */
	  @Override
	  public float queryNorm(float sumOfSquaredWeights) {
//	    return (float)(1.0 / Math.sqrt(sumOfSquaredWeights));\
		return 1.0f;
	  }
	    
	  /** Implemented as <code>1 / (distance + 1)</code>. */
         //这里表示匹配的 term　与 term之间的距离因素,同样也不应该受影响
	  @Override
	  public float sloppyFreq(int distance) {
		return 1.0f;
	  }
	    
	  /** Implemented as <code>log(numDocs/(docFreq+1)) + 1</code>. */
          //这里表示匹配的docuemnt在全部document的影响因素,同理也不考滤
	  @Override
	  public float idf(int docFreq, int numDocs) {
		return 1.0f;
	  }
	    
	  /** Implemented as <code>overlap / maxOverlap</code>. */
          //这里表示每一个Document中所有匹配的关键字与当前关键字的匹配比例因素影响,同理也不考滤.
	  @Override
	  public float coord(int overlap, int maxOverlap) {
		return 1.0f;
	  }
	 
	  // Default false
	  protected boolean discountOverlaps;

	  /** Determines whether overlap tokens (Tokens with
	   *  0 position increment) are ignored when computing
	   *  norm.  By default this is false, meaning overlap
	   *  tokens are counted just like non-overlap tokens.
	   *
	   *  <p><b>WARNING</b>: This API is new and experimental, and may suddenly
	   *  change.</p>
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

	/* (non-Javadoc)
	 * @see org.apache.lucene.search.Similarity#tf(float)
	 */
	@Override
	public float tf(float freq) {
		// TODO Auto-generated method stub
		return 0;
	}
}
