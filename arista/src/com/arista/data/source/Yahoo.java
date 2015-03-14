package com.arista.data.source;

import java.io.*;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.*;

public class Yahoo {
	
	private static String urlPrefix = "http://real-chart.finance.yahoo.com/table.csv?s=";

	public static void main(String[] args) throws Exception {
		String request = urlPrefix + "SPY&d=7&e=18&f=2014&g=d&a=0&b=29&c=1993&ignore=.csv";
		String line;
		BufferedReader br = getReader(request);
		while ((line = br.readLine()) != null) {
			System.out.println(line);
		}
		br.close();
	}

	public static String history(String ticker){
		String request = urlPrefix + ticker +"&d=7&e=18&f=2014&g=d&a=0&b=29&c=1993&ignore=.csv";
		String line = null;
		BufferedReader br = null;
		StringBuffer buffer = new StringBuffer();
		try {
			br = getReader(request);
			while ((line = br.readLine()) != null) {
				buffer.append(line+"\n");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{

		try {
			if (br != null)
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}}
		return buffer.toString();
	}
	public static BufferedReader getReader(String request) throws Exception {
		HttpClient client = new HttpClient();
		GetMethod method = new GetMethod(request);
		// Send GET request
		int statusCode = client.executeMethod(method);
		if (statusCode != HttpStatus.SC_OK) {
			System.err.println("Method failed: " + method.getStatusLine());
		}
		InputStream rstream = null;
		// Get the response body
		rstream = method.getResponseBodyAsStream();
		// Process the response from Yahoo! Web Services
		return new BufferedReader(new InputStreamReader(rstream));
	}
}
