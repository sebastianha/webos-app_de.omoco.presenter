package de.omoco.presenterserver;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

public class ScreenHandler extends AbstractHandler {
	private Vector<String> allowedIds = new Vector<String>(); 

	public void handle(String target, Request request, HttpServletRequest httpServletRequest, HttpServletResponse response) throws IOException, ServletException {
		p("target = " + target);

		if ("/auth".equals(target)) {
			gotAuth(request, response);
		} else if ("/cmd".equals(target)) {
			gotCommand(request, response);
		} else {
			if ("/".equals(target)) {
				sendInfo(request, response);
			} else {
				sendError(request, response);
			}
		}
	}

	private void gotAuth(Request request, HttpServletResponse response) {
		p("gotAuth");

		try {
			FileInputStream fstream = new FileInputStream("ids.txt");
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String idLine;
			while ((idLine = br.readLine()) != null) {
				allowedIds.add(idLine);
			}
			in.close();
		} catch (Exception e) {
			//e.printStackTrace();
		}
		
		String id = request.getParameter("id");
		p("id = " + id);

		if(!allowedIds.contains(id)) {
			JOptionPane pane = new JOptionPane("Allow client with id \"" + id + "\" to connect?");
			String[] options = new String[] { "Allow", "Cancel" };
			pane.setOptions(options);
			
			JDialog dialog = pane.createDialog(new JFrame(), "Auth Request");
			dialog.setVisible(true);
			
			Object obj = pane.getValue();
			int result = -1;
			for (int k = 0; k < options.length; k++)
				if (options[k].equals(obj))
					result = k;
			
			p("user's choice: " + result);
			
			if(result == 0) {
				allowedIds.add(id);
				
				try {
					FileWriter fstream = new FileWriter("ids.txt");
					BufferedWriter out = new BufferedWriter(fstream);
					for (String tmpId : allowedIds) {
						out.write(tmpId + "\n");
					}
					out.close();
				} catch (Exception e) {
					//e.printStackTrace();
				}
				
				sendOkay(request, response);
			} else {
				sendError(request, response);	
			}
		} else {
			sendOkay(request, response);
		}

	}

	private void gotCommand(Request request, HttpServletResponse response) {
		try {
			p("gotCommand");
			
			String id = request.getParameter("id");
			p("id = " + id);
			
			String action = request.getParameter("action");
			p("action = " + action);

			if(allowedIds.contains(id)) {
				Robot robot = new Robot();
				
				switch(Integer.parseInt(action)) {
					case KeyEvent.VK_ENTER:
						robot.keyPress(KeyEvent.VK_ENTER); robot.keyRelease(KeyEvent.VK_ENTER); break;
					case KeyEvent.VK_ESCAPE:
						robot.keyPress(KeyEvent.VK_ESCAPE); robot.keyRelease(KeyEvent.VK_ESCAPE); break;
					case KeyEvent.VK_LEFT:
						robot.keyPress(KeyEvent.VK_LEFT); robot.keyRelease(KeyEvent.VK_LEFT); break;
					case KeyEvent.VK_RIGHT:
						robot.keyPress(KeyEvent.VK_RIGHT); robot.keyRelease(KeyEvent.VK_RIGHT); break;
					case KeyEvent.VK_UP:
						robot.keyPress(KeyEvent.VK_UP); robot.keyRelease(KeyEvent.VK_UP); break;
					case KeyEvent.VK_DOWN:
						robot.keyPress(KeyEvent.VK_DOWN); robot.keyRelease(KeyEvent.VK_DOWN); break;
					case KeyEvent.VK_HOME:
						robot.keyPress(KeyEvent.VK_HOME); robot.keyRelease(KeyEvent.VK_HOME); break;
					case KeyEvent.VK_END:
						robot.keyPress(KeyEvent.VK_END); robot.keyRelease(KeyEvent.VK_END); break;
					case KeyEvent.VK_PAGE_UP:
                		robot.keyPress(KeyEvent.VK_PAGE_UP); robot.keyRelease(KeyEvent.VK_PAGE_UP); break;
                	case KeyEvent.VK_PAGE_DOWN:
                		robot.keyPress(KeyEvent.VK_PAGE_DOWN); robot.keyRelease(KeyEvent.VK_PAGE_DOWN); break;
				}

				sendOkay(request, response);				
			} else {
				sendError(request, response);
			}
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}

	private void sendInfo(Request request, HttpServletResponse response) {
		response.setContentType("text/html;charset=utf-8");
		response.setStatus(HttpServletResponse.SC_OK);
		try {
			PrintWriter writer = response.getWriter();
			
			writer.print("<html><body>This is a presenter server</body></html>\n");
			
			writer.close();
		} catch (IOException e) {
			//e.printStackTrace();
		}
	}
	
	private void sendOkay(Request request, HttpServletResponse response) {
		response.setContentType("application/json;charset=utf-8");
		response.setStatus(HttpServletResponse.SC_OK);
		try {
			PrintWriter writer = response.getWriter();
			
			writer.print("{\"response\": \"ok\"}\n");
			
			writer.close();
		} catch (IOException e) {
			//e.printStackTrace();
		}
	}

	private void sendError(Request request, HttpServletResponse response) {
		response.setContentType("application/json;charset=utf-8");
		response.setStatus(HttpServletResponse.SC_OK);
		try {
			PrintWriter writer = response.getWriter();
			
			writer.print("{\"response\": \"error\"}\n");
			
			writer.close();
		} catch (IOException e) {
			//e.printStackTrace();
		}
	}

	private void p(String s) {
		System.out.println(s);
	}
}
