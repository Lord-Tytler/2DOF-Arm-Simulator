import javax.swing.JFrame;

public class Main {
	public static void main(String[] args) {
		int width = 800;
		int height = 800;
		Panel panel = new Panel(width, height);
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(panel);
		frame.setSize(width, height);
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);  //sets game to launch in full screen, because jframe is strange and cuts off
		frame.setVisible(true);
	}
}
