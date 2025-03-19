public class FuzzyVariable {
	private String name;
	private float b1, t1, t2, b2; // bottom1, top1, top2, bottom2

	public FuzzyVariable() {
	}

	public FuzzyVariable(String name, float b1, float t1, float t2, float b2) {
		this.name = name;
		this.b1 = b1;
		this.t1 = t1;
		this.t2 = t2;
		this.b2 = b2;
	}

	public float fuzzify(float value) {
		if (value < b1 || value > b2) {
			return 0;
		}
		if (value >= t1 && value <= t2) {
			return 1;
		}
		if (value > b1 && value < t1) {
			return (value - b1) / (t1 - b1);
		}

    // value > t2 && value < b2
		return 1.0f - (value - t2) / (b2 - t2);
	}

	public String getName() {
		return name;
	}
}