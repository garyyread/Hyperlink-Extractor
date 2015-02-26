/*
 * Node.java
 * Gary Read 662193
 * Swansea University
 */

import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.net.URL;
import java.net.MalformedURLException;
import java.util.Scanner;
import java.util.ArrayList;

public class Node {
	private String urlOfNode;
	private boolean partial;
	private ArrayList<String> nodeLeaves;
	private PrintWriter oLog;
	
	public static void main(String[] args) {
		//Error case handleing
		usage();
		
		return;
	}
	
	//Console Usage Message
	public static void usage() {
		System.out.println();
		System.out.println("HyperLinkExtractor");
		System.out.println("\tUSAGE\t"+ "java HyperLinkExtractor [-OPTION] [URL_PATH]");
		System.out.println("");
		System.out.println("\tOPTIONS");
		System.out.println("\t\t"+ "-cp" + "\tCapture Partial URLs");
		System.out.println("\t\t"+ "" + "\tLeave blank to Omit Partial URLs");
		System.out.println();
		System.out.println("\tURL_PATH\t" + "http://example.com ");
		System.out.println();
	}
	
	//Constructor
	public Node(String urlOfNode, boolean partial) {
		this.urlOfNode = urlOfNode;
		this.partial = partial;
		
		//Set up a PrintWriter for logging
		setPrintWriter();
		
		//Logging
		if (oLog != null) { 
			oLog.println();
			oLog.println("NODE CREATED @\t" + getNodeURL()); 
			oLog.println("PARTIAL URLS CAPTURED? = " + partial);
			oLog.println();
		}
		
		findNodeLeaves(urlOfNode);
	}
	
	//Return Node URL [String]
	public String getNodeURL() {
		return urlOfNode;
	}
	
	//Return Nodes Links [ArrayList<String>]
	public ArrayList<String> getNodeLeaves() {
		return nodeLeaves;
	}
	
	//Set capturing of partial URLS
	public void capturePartialUrl(boolean partial) {
		if (partial == true) {
			this.partial = true;
		} else {
			this.partial = false;
		}
	}
	
	//Method to crawl Node URL for its links
	private void findNodeLeaves(String url) {
		nodeLeaves = null;
		
		Scanner in = getURLScanner(url);

		
		if (in == null) {
			nodeLeaves = null;
			return;
		} else {
			//Logging
			if (oLog != null) { oLog.println("\tRETRIEVING HYPERLINKS @\t" + getNodeURL()); }
		
			nodeLeaves = new ArrayList<String>();
			in.useDelimiter("<a href=\"");
			if (in.hasNext()) { in.next(); }
		}
		
		while (in.hasNext()) {
			Scanner end = new Scanner(" " + in.next());
			end.useDelimiter("\""); //i.e www.csvision.com/index.php{"}<\a>
			
			String link = null;
			
			if (end.hasNext()) {
				link = end.next();
				
				if (link != null) {
					//Remove leading and trailing whitespace
					link = link.trim();
					
					if (link.length() > 3) {
						//Add all String begining with http://
						if (link.substring(0,4).equalsIgnoreCase("http")) {
							//Add link
							nodeLeaves.add(link);
							
							//Logging
							if (oLog != null) { oLog.println("\t\t" + link); }
						} 
						else if (link.substring(0,4).equalsIgnoreCase("www.")) {
							//Add all String begining with www.
							nodeLeaves.add(link);
							
							//Logging
							if (oLog != null) { oLog.println("\t\t" + link); }
						}
						else if (partial && link.length() > 0) {
							/* Add all other urls begining with '/' appending Node URL to begining
							*					/uk/superman.html
							*	www.csvision.com/uk/superman.html */
							if (link.charAt(0) == '/') {
								//Corrects for appending back slashes "../part_1//part_2.html"
								if (url.charAt(url.length() - 1) == '/') {
									String newLink = url + link.substring(1,link.length());
									
									//Add link
									nodeLeaves.add(newLink);
									
									//Logging
									if (oLog != null) { oLog.println("\t\t" + newLink); }
								} else {
									String newLink = url + link;
									
									//Add link
									nodeLeaves.add(newLink);
									
									//Logging
									if (oLog != null) { oLog.println("\t\t" + newLink); }
								}
							}
						}
					}
				}
			}
			end.close();
		}
			
		in.close();
		
		//Logging
		if (oLog != null) { 
			oLog.println();
			oLog.println("COMPLETED CRAWL"); 
			oLog.println("URLS CAPTURED = " + getNodeLeaves().size());
			oLog.println();
			oLog.println("=====================================================================");
			oLog.close();
		}
	}
	
	//Initalize PrintWriter for logging
	private void setPrintWriter() {
		oLog = null;
		
		//Creating PrintWriter for logging
		try {
			//Create FileWriter captured in a PrintWriter for append and automatic flushing
			oLog = new PrintWriter(new FileWriter("log.txt", true), true);
		}
		catch (IOException e) {
			System.out.println();
			System.out.println("IO Exception");
			System.out.println("\t" + "Logs will not be captured.");
			System.out.println();
		}
	}

	//Return scanner, pre-initilised with URL stream [Scanner(URL.openStream())]
	private Scanner getURLScanner(String url) {
		Scanner	in = null;
		
		try {
			URL locator = new URL(url);
			in = new Scanner(locator.openStream());
		}
		catch (MalformedURLException e) {
			if (oLog != null) { oLog.println("ERROR: Node.getURLScanner()\t" + "MALFORMED URL @\t" + getNodeURL()); }
			return in;
		}
		catch (FileNotFoundException e) {
			if (oLog != null) { oLog.println("ERROR: Node.getURLScanner()\t" + "INVALID URL @\t" + getNodeURL()); }
			return in;
		}
		catch (IOException e) {
			if (oLog != null) { oLog.println("ERROR: Node.getURLScanner()\t" + "IO EXCEPTION @\t" + getNodeURL()); }
			return in;
		}
		
		return in;
	}
}