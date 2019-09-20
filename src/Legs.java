import javax.swing.JTextField;

public class Legs {
	private double[][] legs;
	private double[] leg1, leg2; //where the four doubles represent x1, y1, x2, y2 coords, respectively, for the leg
	private double len1, len2; //length of two arm segments
	private double x, y, r; //target x, target y, distance from origin to target;
	private double relativeA2;
	private int width, height;


	public Legs(int width, int height) {

		this.width = width;
		this.height = height;

		len1 = 150;
		len2 = 150;

		leg1 = new double[4];
		leg2 = new double[4];
		legs = new double[2][4];
		defaultPosition();
		legs[0] = leg1;
		legs[1] = leg2;

		x = 26;
		y = 13;
		r = Math.sqrt(x * x + y * y);

	}
	public void recalculateDist() {
		r = Math.sqrt(x * x + y * y);
	}

	/**
	 * sets both lines to point straight up
	 */
	public void defaultPosition() {
		leg1[0] = 0;
		leg1[1] = 0;
		leg1[2] = leg1[0];
		leg1[3] = leg1[1] + len1;
		leg2[0] = leg1[2];
		leg2[1] = leg1[3];
		leg2[2] = leg2[0];
		leg2[3] = leg2[1] + len2;
	}
	
	/**
	 * get points of the starting and ending points of both legs relative to origin of leg1
	 * @return points - double[ ][ ] [x1, y1, x2, y2] [x1, y1, x2, y2]
	 */
	public double[][] getPoints() {
		//updatePoints();
		return legs;
	}
	
	/**
	 * takes endpoints of arm segments and formats them into a String
	 * @param points - 2D double array containing endpoints of arm segments
	 * @return coords - String containting endpoitns of arm segments
	 */
	public String coordsToString(double[][] points) {
		String coords = "{ ";
		for(double[] line : points) {
			for(int i = 0; i < line.length; i++) {
				if(i % 2 == 0) {
					coords += "\n" + line[i] + ", ";
				} else {
					coords += line[i] + ", ";
				}
			}
		}
		coords += "}";
		return coords;
	}
	
	/**
	 * shifts arm segment endpoints to make them compatible with the JPanel (so everything is centered on the screen)
	 * @return drawableLegs - 2D double array containing shifted arm segment endpoints
	 */
	public double[][] getDrawablePoints() {
		updatePoints();
		double[][] drawableLegs = new double[legs.length][legs[0].length];
		for(int i = 0; i < drawableLegs.length; i ++) {
			drawableLegs[i] = legs[i].clone();
		}

		for(double[] leg : drawableLegs) { //changes y coords to negative to make the resulting line human friendly
			for(int i = 0; i < leg.length; i++) {
				if(i % 2 == 0) {
					leg[i] += width / 2;
				} else {
					leg[i] = height / 2 - leg[i];
				}
			}
		}
		return drawableLegs;

	}

	/**
	 * retrieves angles from getAngles() and uses them to update arm segment endpoints
	 */
	public void updatePoints( ) {
		double[] angles = getAngles();
		leg1[2] = len1 * Math.cos(angles[0]);
		leg1[3] = len1 * Math.sin(angles[0]);
		leg2[0] = leg1[2];
		leg2[1] = leg1[3];
		
		if(isInside()) {
			leg2[2] = leg2[0] - (len2 * Math.cos(angles[1]) );
			leg2[3] = leg2[1] - (len2 * Math.sin(angles[1]) );
		} else {
			leg2[2] = leg2[0] + (len2 * Math.cos(angles[1]) );
			leg2[3] = leg2[1] + (len2 * Math.sin(angles[1]) );		
		}
		//leg1[3] *= -1;
		//leg2[1] = leg1[3];
		//leg2[3] *= -1;
		//System.out.println(coordsToString(legs));
	}

	/**
	 * takes triangle side lengths and finds the angle opposite of side c
	 * @param a - double representing triangle side length
	 * @param b - double representing triangle side length
	 * @param c - double representing side length opposite desired angle
	 * @return C - double representing the angle obtained by using the Law of Cosines
	 */
	private double lawOfCosines(double a, double b, double c) {
		return Math.acos((a*a + b*b - c * c) / (2 * a * b));
	}
	
	/**
	 * 
	 * @param lenA - double represent side length opposite to desired angle
	 * @param lenB - double representing other side length
	 * @param angleB - double representing angle opposite of LenB
	 * @return a - double representing the angle obtained by using the Law of Sines
	 */
	private double lawOfSines(double lenA, double lenB, double angleB) {
		double z = (lenA * Math.sin(angleB)) / lenB;
		return Math.asin(z);
	}

	/**
	 * takes target x,y and arm lengths to calculate angles for both arm joints using trigonometry
	 * @return angles - double array containing angles for both arm joints
	 */
	public double[] getAngles() {
		double[] angles = new double[2];
		double d1 = lawOfCosines(len1, r, len2);
		double d2 = Math.atan2(y , x);
		double a1 = d1 + d2;

		double a2 = lawOfSines(r, len2, d1);
		//a2 = lawOfCosines(len1, len2, r);
		if(x < 0) {
			return getAngles2();
		}
		relativeA2 = a2;
		if(isInside()) {
			a2 = a1 + a2;
		} else {
			a2 = a1 - a2;
		}
		//System.out.println("a1 = " + Math.toDegrees(a1) + "   a2 = " + Math.toDegrees(a2));

		angles[0] = a1;
		angles[1] = a2;
		return angles;

	}
	/**
	 * finds the alternative set of angles for a given set position and arm lengths (i.e the angles for opposite elbow bend direction)
	 * @return angles - double array containing alternative angles for both arm joints
	 */
	public double[] getAngles2() {
		double[] angles = new double[2];
		double d1 = lawOfCosines(len1, r, len2);
		double d2 = Math.atan2(y , -x);
		double a1 = d1 + d2;

		double a2 = lawOfSines(r, len2, d1);
		//a2 = lawOfCosines(len1, len2, r);
		relativeA2 = a2;
		a2 = (Math.PI + a2);
		if(isInside()) {
			a2 = a1 + a2;
		} else {
			a2 = a1 - a2;
		}
		a2 *= -1;
		a1 = Math.PI - a1;
		//System.out.println("a1 = " + Math.toDegrees(a1) + "   a2 = " + Math.toDegrees(a2));

		angles[0] = a1;
		angles[1] = a2;
		return angles;
	}
	
	/**
	 * @return isInside - boolean representing whether the distance to the target position is less than a calculated threshold
	 */
	private boolean isInside() {
		double threshold = (len1 + len2) / Math.sqrt(2); //either side length of a triangle with length len1 + len2 at 45 degree angle
		if(r <= threshold) {
			return true;
		}
		return false;
	}
	
	/**
	 * takes double parameter and sets the target x position equal to it, then recalculates the distance to the target position
	 * @param x - double representing the desired target x position
	 */
	public void setTargetX(double x) {
		this.x = x;
		recalculateDist();
	}
	
	/**
	 * takes double parameter and sets JPanel JTextField to it, sets the target x position equal to it, and recalculates the distance to the target position
	 * @param x - double representing the desired target x position
	 * @param xField - JTextField used to set target x position
	 */
	public void setTargetX(double x, JTextField xField) {
		xField.setText(xField.getText() + " " + x);
		this.x = x;
		recalculateDist();
	}
	
	/**
	 * @return x - double target x position
	 */
	public double getTargetX() {
		return x;
	}
	
	/**
	 * takes double parameter and sets the target y position equal to it, then recalculates the distance to the target position
	 * @param y - double representing the desired target y position
	 */
	public void setTargetY(double y) {
		this.y = y;
		recalculateDist();
	}
	
	/**
	 * takes double parameter and sets JPanel JTextField to it, sets the target y position equal to it, and recalculates the distance to the target position
	 * @param y - double representing the desired target y position
	 * @param yField - JTextField used to set target y position
	 */
	public void setTargetY(double y, JTextField yField) {
		yField.setText(yField.getText() + " " + y);
		this.y = y;
		recalculateDist();
	}
	
	/**
	 * @return y - double target y position
	 */
	public double getTargetY() {
		return y;
	}
	/**
	 * @return lengths - double array containing the lengths of both arm segments
	 */
	public double[] getLengths() {
		double[] lengths = new double[2];
		lengths[0] = len1;
		lengths[1] = len2;
		return lengths;
	}

	/**
	 * returns double array containing (in order):
	 *  targetX, targetY, segment1 xEndpoint, segment1 yEndpoint, segment2 xEndpoint, segment2 yEndppint, angle1, angle2, angle2 relative to angle1
	 * @return diags - double array containing diagnostics
	 */
	public double[] getDiagnostics() {
		double[] diags = new double[10];
		diags[0] = x;
		diags[1] = y;
		diags[2] = leg1[2];
		diags[3] = leg1[3];
		diags[4] = leg2[2];
		diags[5] = leg2[3];
		diags[6] = r;
		diags[7] = Math.toDegrees(getAngles()[0]);
		diags[8] = Math.toDegrees(getAngles()[1]);
		diags[9] = Math.toDegrees(relativeA2);
		return diags;
	}
}
