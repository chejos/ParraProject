
public class matrix {
	protected final int[][] errorDistribution;
	protected final int start;
	protected final int errorSum;
	protected final int threshold;

	protected matrix(matrix myMatrix){
		this.errorDistribution = myMatrix.errorDistribution;
		this.start = myMatrix.start;
		this.errorSum = myMatrix.errorSum;
		this.threshold = myMatrix.threshold;
	}
	
	protected matrix(int[][] errorDistribution, int start, int errorSum, int threshold) {
		this.errorDistribution = errorDistribution;
		this.start = start;
		this.errorSum = errorSum;
		this.threshold = threshold;
	}

	protected matrix(int[][] errorDistribution, int threshold) {
		this.errorDistribution = errorDistribution;
		this.threshold = threshold;

		int[] temp = getStartAndSum(errorDistribution);
		this.errorSum = temp[0];
		this.start = temp[1];
	}

	private static int[] getStartAndSum(int[][] errorDistribution) {
		int errorSum = -1;
		int start = -1;

		for (int[] line : errorDistribution) {
			boolean started = false;
			for (int ed : line) {
				errorSum += ed;
				if (started == false) {
					if (ed == 0) {
						start++;
					} else {
						started = true;
					}
				}
			}
		}
		return new int[] {errorSum, start};
	}
}
