/**
 * Matrix-Klasse die die notwendigen Daten der ausgewählten Matrix beinhaltet
 * 
 * errorDistribution: die Matrix selbst
 * start: Position des "X" von links in der ersten Zeile
 * errorSum: Die Summe der Zelleninhalte
 * threshold: Wert bei dem entschieden wird, ob Pixel schwarz oder weiß wird
 *
 */

public class Matrix {
	protected final int[][] errorDistribution;
	protected final int start;
	protected final int errorSum;
	protected final int threshold;

	protected Matrix(Matrix myMatrix){
		this.errorDistribution = myMatrix.errorDistribution;
		this.start = myMatrix.start;
		this.errorSum = myMatrix.errorSum;
		this.threshold = myMatrix.threshold;
	}
	
	protected Matrix(int[][] errorDistribution, int start, int errorSum, int threshold) {
		this.errorDistribution = errorDistribution;
		this.start = start;
		this.errorSum = errorSum;
		this.threshold = threshold;
	}

	protected Matrix(int[][] errorDistribution, int threshold) {
		this.errorDistribution = errorDistribution;
		this.threshold = threshold;

		int[] temp = getStartAndSum(errorDistribution);
		this.errorSum = temp[0];
		this.start = temp[1];
	}

	private static int[] getStartAndSum(int[][] errorDistribution) {
		int errorSum = 0;
		int start = -1;
		boolean started = false;
		
		for (int[] line : errorDistribution) {
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
