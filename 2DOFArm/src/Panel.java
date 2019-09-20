import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Panel extends JPanel implements Runnable, MouseListener, MouseMotionListener {
	private Thread thread;
	private boolean isRunning;
	private int width, height;
	private double[] mouseLoc;
	private Legs legs;
	private double[][] axes;
	private JLabel targetX, targetY, x1, y1, x2, y2, r, a1, a2, ra2;
	private JPanel labelPanel;
	private JTextField xField, yField;
	private ArrayList<JLabel> labels;


	public Panel(int width, int height) {
		thread = new Thread(this);
		isRunning = true;
		this.width = width;
		this.height = height;
		addMouseListener(this);
		addMouseMotionListener(this);
		mouseLoc = new double[2];
		legs = new Legs(width, height);
		axes = new double[2][4];
		createAxes();
		thread.start();
		setBackground(Color.black);
		initLabels();
	}
	/**
	 * intializes a panel for JLabels, initializes JLabels with respective values, 
	 * and adds them to the label Panel, then adds that panel to the JFrame
	 */
	public void initLabels() {
		labelPanel = new JPanel();
		labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.Y_AXIS));
		labelPanel.setBounds(width - 100, 100, 100, 100);
		labelPanel.setBackground(Color.black);

		labels = new ArrayList<JLabel>();
		targetX = new JLabel("targetX: ");
		targetY = new JLabel("targetY: ");
		x1 = new JLabel("x1: ");
		y1 = new JLabel("y1: ");
		x2 = new JLabel("x2: ");
		y2 = new JLabel("y2: ");
		r = new JLabel("r: ");
		a1 = new JLabel("a1: ");
		a2 = new JLabel("a2: ");
		ra2 = new JLabel("relative a2: ");

		xField = new JTextField(Double.toString(legs.getTargetX()));
		yField = new JTextField(Double.toString(legs.getTargetY()));

		labels.add(targetX);
		labels.add(targetY);
		labels.add(x1);
		labels.add(y1);
		labels.add(x2);
		labels.add(y2);
		labels.add(r);
		labels.add(a1);
		labels.add(a2);
		labels.add(ra2);

		for(JLabel label : labels) {
			label.setForeground(Color.white);
			labelPanel.add(label);
		}
		labelPanel.add(xField);
		labelPanel.add(yField);
		labelPanel.repaint();
		this.add(labelPanel);
		this.repaint();
	}

	/**
	 * removes all text after the semicolon for all JLabels
	 */
	private void resetLabels() {
		for(JLabel label : labels) {
			label.setText(label.getText().substring(0, label.getText().indexOf(":") + 1));
		}
	}

	/**
	 * resets JTextFields to be blank
	 */
	private void resetFields() {
		xField.setText("");
		yField.setText("");
	}

	/**
	 * gets diagnostics from Legs object and updates respective JLabel in panel, 
	 * then attempts to set target position of arm to the contents of two JTextFields
	 */
	public void updateLabels() {
		resetLabels();
		double[] diags = legs.getDiagnostics();
		for(int i = 0; i < labels.size(); i++) {
			labels.get(i).setText(labels.get(i).getText() + " " + diags[i]);
		}
		try {
			legs.setTargetX(Double.parseDouble(xField.getText()));
			legs.setTargetY(Double.parseDouble(yField.getText()));
		} catch (NumberFormatException e) {
			// TODO: handle exception
		}

	}

	/**
	 * creates points to be used to draw axis
	 */
	public void createAxes() {
		axes[0][0] = 0;
		axes[0][1] = height / 2;
		axes[0][2] = width;
		axes[0][3] = axes[0][1];
		axes[1][0] = width / 2;
		axes[1][1] = 0;
		axes[1][2] = width / 2;
		axes[1][3] = height;
	}

	/**
	 * paints all necessary components and updates labels
	 * @param g - Graphics object
	 */
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		drawRangeCircle(g);
		drawAxis(g);
		drawLegs(g);
		updateLabels();
	}

	/**
	 * Gets updated points for and draws both arm segments
	 * @param g - Graphics object
	 */
	public void drawLegs(Graphics g) {
		boolean isRed = true;

		for(double[] leg : legs.getDrawablePoints()) {
			if(isRed) {
				g.setColor(Color.red); 
			}
			else  {
				g.setColor(Color.green);
			}

			isRed = !isRed;
			g.drawLine((int)leg[0], (int)leg[1], (int)leg[2], (int)leg[3]);
		}
	}

	/**
	 * Draws X and Y axis
	 * @param g - Graphics object
	 */
	public void drawAxis(Graphics g) {
		g.setColor(Color.white);
		for(double[] axis : axes) {
			g.drawLine((int)axis[0], (int)axis[1],(int)axis[2], (int)axis[3]);
		}

	}

	/**
	 * Draws circle(s) representing the range of target points for the arm
	 * @param g - Graphics object
	 */
	public void drawRangeCircle(Graphics g) {
		double[] lengths = legs.getLengths();
		int maxRadius = (int)(lengths[0] + lengths[1]);
		int minRadius = (int)(lengths[0] - lengths[1]);
		if(minRadius < 0) {
			minRadius *= -1;
		}
		g.setColor(Color.white);
		g.drawOval(width / 2 - maxRadius, height / 2 - maxRadius, (int)maxRadius * 2, (int)maxRadius * 2);
		g.setColor(Color.white);
		g.drawOval(width / 2 - minRadius, height / 2 - minRadius, (int)minRadius * 2, (int)minRadius * 2);

	}

	@Override
	public void run() {
		int fps = 60; //number of update per second.
		double tickPerSecond = 1000000000/fps;
		double delta = 0;
		long now;
		long lastTime = System.nanoTime();

		while(isRunning){
			now = System.nanoTime();
			delta += (now - lastTime)/tickPerSecond;
			lastTime = now;
			if(delta >= 1){
				repaint();
				delta--;
			}
		}
	}

	

	/**
	 * stops the main loop in run()
	 */
	public void stop() {
		isRunning = false;
	}


	@Override
	public void mouseMoved(MouseEvent e) {
		setMouseLoc(e);
	}

	/**
	 * sets arm target position to location of cursor upon clicking
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON1) {
			setMouseLoc(e);
			double mouseX = mouseLoc[0] - (width / 2);
			double mouseY = -1 * (mouseLoc[1] - (height / 2));
			resetFields();
			//xField.setText(xField.getText() + " " + mouseX);
			//yField.setText(yField.getText() + " " + mouseY);
			legs.setTargetX(mouseX, xField);
			legs.setTargetY(mouseY, yField);

		} else if(e.getButton() == MouseEvent.BUTTON3) {
			resetFields();
		}


	}

	/**
	 * continuously sets arm target position to location of cursor while mouse button is held
	 */
	@Override
	public void mouseDragged(MouseEvent e) {
		setMouseLoc(e);
		double mouseX = mouseLoc[0] - (width / 2);
		double mouseY = -1 * (mouseLoc[1] - (height / 2));
		resetFields();
		xField.setText(xField.getText() + " " + mouseX);
		yField.setText(yField.getText() + " " + mouseY);
		legs.setTargetX(mouseX);
		legs.setTargetY(mouseY);

	}


	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	/**
	 * fills an array with the x,y coordinates of the cursor
	 * @param e - MouseEvent
	 */
	public void setMouseLoc(MouseEvent e) {
		mouseLoc[0] = e.getX();
		mouseLoc[1] = e.getY();
	}
}
