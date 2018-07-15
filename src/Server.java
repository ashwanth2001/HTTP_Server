import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

	private static final int PORT = 8080;

	public static void main(String[] args) {
		try {
			ServerSocket server = new ServerSocket(PORT);
			System.out.println("MiniServer active " + PORT);
			while (true) {
				new ThreadedSocket(server.accept());
			}
		} catch (Exception e) {
		}
	}
}

class ThreadedSocket extends Thread {

	private Socket insocket;

	ThreadedSocket(Socket insocket) {
		this.insocket = insocket;
		this.start();
	}

	@Override
	public void run() {
		try {
			InputStream is = insocket.getInputStream();
			PrintWriter out = new PrintWriter(insocket.getOutputStream());
			BufferedReader in = new BufferedReader(new InputStreamReader(is));
			String line;
			line = in.readLine();
			String request_method = line;
			System.out.println(line);
			line = "";
			int postDataI = -1;
			while ((line = in.readLine()) != null && (line.length() != 0)) {
				// System.out.println("\n\n\n\n\n" + line + "\n\n\n\n\n");
				if (line.indexOf("Content-Length:") > -1) {
					postDataI = new Integer(line.substring(line.indexOf("Content-Length:") + 16, line.length()))
							.intValue();
				}
			}
			String postData = "";

			if (postDataI > 0) {
				char[] charArray = new char[postDataI];
				in.read(charArray, 0, postDataI);
				postData = new String(charArray);
			}
			
			String shorten = shortenString(request_method);
			if(shorten.length()==0) {
				out.println("HTTP/1.0 200 OK");
				out.println("Content-Type: text/html; charset=utf-8");
				out.println("Server: MINISERVER");
				out.println("");
				out.println("<h1>Welcome to the Web Site</h1>");
				out.println("Github Username: <input></input><br>");
				out.println("<button>submit</button>");
				out.println("<input type=\"submit\" value=\"Go to my link location\" \n" + 
						"					    onclick=\"window.location='http://www.google.com/';\" />");
			}
			else {
				out.println("HTTP/1.0 200 OK");
				out.println("Content-Type: text/html; charset=utf-8");
				out.println("Server: MINISERVER");
				out.println("");
				out.println(readFile(shorten));
			}
			out.close();
			insocket.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String readFile(String filename) {
		String s = "";
		try {
			BufferedReader br = new BufferedReader(new FileReader(filename));
			String line = br.readLine();

			while (line != null) {
				s += line;
				line = br.readLine();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return s;

	}
	
	public String shortenString(String s) {
		int i = 0;
		while(s.charAt(i)!= '/') {
			s = s.substring(1);
		}
		s = s.substring(1);
		i = s.indexOf("html");
		if(i<0) {
			i = -4;
		}
		s = s.substring(0, i+4);
		System.out.println(s);
		return s;
		
	}
}