package net.bookong.game.aoeviewer;

/**
 * @author jiangxu
 * 
 */
public class Daemon implements Runnable {
	private static Daemon instance = new Daemon();

	public static Daemon getInstance() {
		return instance;
	}

	public boolean isRunning = true;
	public boolean isStopping = false;

	public void run() {
		while (isRunning) {
			if (!isStopping)
				AoeCanvas.getInstance().repaint();
			try {
				Thread.sleep(70L);
			} catch (Exception e) {
				Thread.interrupted();
			}
		}
		System.out.println("exit daemon thread.");
	}
}
