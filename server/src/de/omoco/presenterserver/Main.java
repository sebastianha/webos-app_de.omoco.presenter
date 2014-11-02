package de.omoco.presenterserver;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.eclipse.jetty.server.Server;

public class Main {
	public static String SERVICE_NAME = "_omocops._tcp.local.";
	public static JmDNS jmdns;
	private static int port;

	public static void main(String... args) throws Exception {
		
		p(Integer.toString(KeyEvent.VK_ENTER));
		p(Integer.toString(KeyEvent.VK_ESCAPE));
		p(Integer.toString(KeyEvent.VK_LEFT));
		p(Integer.toString(KeyEvent.VK_RIGHT));
		p(Integer.toString(KeyEvent.VK_UP));
		p(Integer.toString(KeyEvent.VK_DOWN));
		p(Integer.toString(KeyEvent.VK_PAGE_UP));
		p(Integer.toString(KeyEvent.VK_PAGE_DOWN));
		p(Integer.toString(KeyEvent.VK_HOME));
		p(Integer.toString(KeyEvent.VK_END));
		

		port = 26666;
		Server server = setupWebserver();

		setupZeroConf();

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				setupGUI();
			}
		});

		server.join();
	}

	private static Server setupWebserver() throws Exception {
		Server server = new Server(port);
		server.setHandler(new ScreenHandler());
		server.start();
		return server;
	}

	private static void setupZeroConf() throws IOException {
		jmdns = JmDNS.create();
		p("jmdns host = " + jmdns.getHostName());
		p("jmdns using port: " + port);
		p("jmdns interface = " + jmdns.getInterface());

		ServiceInfo service = ServiceInfo.create(SERVICE_NAME, "omocops", port, "omoco presenter server");
		System.out.println("creating: " + service);
		jmdns.registerService(service);
	}

	private static void setupGUI() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(0, 1));
		panel.add(new JLabel("Presenter Server"));

		JButton quit = new JButton("Quit");
		quit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jmdns.unregisterAllServices();
				jmdns.close();
				System.exit(0);
			}
		});
		quit.setSize(new Dimension(150, 20));
		panel.add(quit);

		JFrame frame = new JFrame();
		frame.add(panel);
		frame.setSize(150, 100);
		frame.setVisible(true);
		frame.addWindowListener(new WindowListener() {
			public void windowOpened(WindowEvent e) {}
			public void windowIconified(WindowEvent e) {}
			public void windowDeiconified(WindowEvent e) {}
			public void windowDeactivated(WindowEvent e) {	}
			public void windowClosing(WindowEvent e) {
				jmdns.unregisterAllServices();
				jmdns.close();
				System.exit(0);
			}
			public void windowClosed(WindowEvent e) {}
			public void windowActivated(WindowEvent e) {}
		});
	}

	private static void p(String s) {
		System.out.println(s);
	}
}
