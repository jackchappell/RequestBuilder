RequestBuilder
==============
A builder class made for preforming simple GET/POST requests to a webpage.
==============
How to use
==============
RequestBuilder request = new RequestBuilder()
						.setURL("http://darkcop.co.uk/BungeeStats/test.php")
						.addUrlParameter("name", "Bob")
						.addUrlParameter("mood", "Happy")
						.setRequestType(RequestType.GET)
						.setAsync(true)
						.setFinishRunnable(new RequestRunnable()
						{
							public void run(RequestInfo info)
							{
								System.out.println("Response: " + info.getResponse());
							}
						})
						.doRequest();
==========
