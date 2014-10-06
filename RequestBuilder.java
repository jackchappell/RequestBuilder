import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/*
 * Class made by Jack Chappell
 * Version: 2.0
 */

/*
 * Example:
 * 
 *	RequestBuilder request = new RequestBuilder()
 *						    .setURL("http://darkcop.co.uk/BungeeStats/test.php")
 *							.addUrlParameter("name", "Bob")
 *							.addUrlParameter("mood", "Happy")
 *							.setRequestType(RequestType.GET)
 *							.setAsync(true)
 *							.setFinishRunnable(new RequestRunnable() 
 *							{
 *								public void run(RequestInfo info) 
 *								{
 *									System.out.println("Response: " + info.getResponse());							
 *								}
 *							})
 *							.doRequest();
 *
 * Output:
 * 	Response: Hello there Bob you are Happy
 */

public class RequestBuilder 
{	
	public enum RequestType 
	{ 
		POST, GET;
	}
	
	private StringBuilder   urlParameters   = new StringBuilder(),
				response        = new StringBuilder();
	
	private RequestType     requestType     = RequestType.GET; 
	
	private RequestRunnable finishRunnable  = null;  
	
	private boolean         useCaches       = false, 
				doInput         = true, 
				doOutput        = true,
				async           = false;
	
	private String  	contentLanguage = "en-UK", 
				contentType     = "application/x-www-form-urlencoded",
				userAgent       = "Mozilla/5.0",
			        url             = "";
	
	private int     	responseCode    = 0;
	
	public RequestBuilder setURL(String url)
	{
		this.url = url;
		return this;
	}
	
	public RequestBuilder setRequestType(RequestType requestType)
	{
		this.requestType = requestType;
		return this;
	}
	
	public RequestBuilder setFinishRunnable(RequestRunnable finishRunnable)
	{
		this.finishRunnable = finishRunnable;
		return this;
	}
	
	public RequestBuilder setContentLanguage(String contentLanguage)
	{
		this.contentLanguage = contentLanguage;
		return this;
	}
	
	public RequestBuilder setContentType(String contentType)
	{
		this.contentType = contentType;
		return this;
	}
	
	public RequestBuilder setAsync(boolean async)
	{
		this.async = async;
		return this;
	}
	
	public RequestBuilder useCaches(boolean useCaches)
	{
		this.useCaches = useCaches;
		return this;
	}
	
	public RequestBuilder doInput(boolean doInput)
	{
		this.doInput = doInput;
		return this;
	}
	
	public RequestBuilder doOutput(boolean doOutput)
	{
		this.doOutput = doOutput;
		return this;
	}
	
	public RequestBuilder addUrlParameter(String key, String value)
	{
		try 
		{
			urlParameters.append("&" + key + "=" + URLEncoder.encode(value, "UTF-8"));
		} catch (UnsupportedEncodingException e) { }
		return this;
	}
	
	public RequestBuilder setUserAgent(String userAgent)
	{
		this.userAgent = userAgent;
		return this;
	}
	
	public String getResponse()
	{
		return this.response.toString();
	}
	
	public int getResponseCode()
	{
		return responseCode;
	}
	
	public RequestBuilder doRequest()
	{
		Thread thread = new Thread()
		{
			public void run()
			{
				HttpURLConnection con = null;
				URL newUrl            = null;
				try
				{
					String urlParams = urlParameters.toString().replaceFirst("&", "");
					
					newUrl = new URL(requestType.equals(RequestType.POST) ? url : url + "?" + urlParams);
					con    = (HttpURLConnection) newUrl.openConnection();
					
					con.setRequestMethod(requestType.toString());
					con.setDoInput(doInput);
					con.setDoOutput(doOutput);
					con.setUseCaches(useCaches);
		
					con.setRequestProperty("Content-Type", contentType);
					con.setRequestProperty("Content-Language", contentLanguage);
					con.setRequestProperty("Content-Length", urlParams.getBytes().length + "");
					con.setRequestProperty("User-Agent", userAgent);
					
					if(requestType.equals(RequestType.POST))
					{
					    DataOutputStream wr = new DataOutputStream(con.getOutputStream());
					    wr.writeBytes(urlParams);
					    wr.flush();
					    wr.close();
					}
					
				    responseCode = con.getResponseCode();
				    
				    BufferedReader rd = new BufferedReader(new InputStreamReader(con.getInputStream()));
				    String line;
				    
				    while((line = rd.readLine()) != null) 
				    	response.append(line).append('\n');
			
				    if(response.length() != 0)
						response.replace(response.length() - 1, response.length(), "");
				    
				    rd.close();
				}
				catch(Exception e) { }
				finally
				{
					if(con != null) con.disconnect();
					if(finishRunnable != null) finishRunnable.run(new RequestInfo(response.toString(), responseCode));
				}
			}
		};
		
		if(async)
			thread.start();
		else
			thread.run();
		
		return this;
	}
}

interface RequestRunnable
{
	public void run(RequestInfo info);
}

class RequestInfo
{
	private int responseCode;
	private String response;
	
	public RequestInfo(String response, int responseCode)
	{
		this.response     = response;
		this.responseCode = responseCode;
	}
	
	public String getResponse()
	{
		return response;
	}
	
	public int getResponseCode()
	{
		return responseCode;
	}
}
