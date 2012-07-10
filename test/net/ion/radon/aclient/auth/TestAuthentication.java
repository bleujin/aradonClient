package net.ion.radon.aclient.auth;

import java.io.StringReader;
import java.util.Map;

import net.ion.framework.parse.html.HTag;
import net.ion.framework.util.Debug;
import net.ion.framework.util.MapUtil;
import net.ion.framework.util.StringUtil;
import net.ion.radon.aclient.AsyncCompletionHandler;
import net.ion.radon.aclient.NewClient;
import net.ion.radon.aclient.Realm;
import net.ion.radon.aclient.Response;
import net.ion.radon.aclient.TestBaseClient;
import net.ion.radon.aclient.Realm.AuthScheme;
import net.ion.radon.aclient.oauth.ConsumerKey;
import net.ion.radon.aclient.oauth.OAuthSignatureCalculator;
import net.ion.radon.aclient.oauth.RequestToken;

public class TestAuthentication extends TestBaseClient {

	public void testBasic() throws Exception {
		NewClient c = newClient();
		Response response = c.prepareGet(getSecureHelloUri()).execute().get();

		assertEquals(401, response.getStatusCode());

		Realm realm = new Realm.RealmBuilder().setPrincipal("bleujin").setPassword("redf").setUsePreemptiveAuth(true).setScheme(AuthScheme.BASIC).build();
		response = c.prepareGet(getSecureHelloUri()).setRealm(realm).execute().get();
		assertEquals(200, response.getStatusCode());
		assertEquals("hello", response.getTextBody());
	}

	public void xtestOAuth() throws Exception {
		String consumerKey = "gwQomTlxTCr4fGPiUXnrQ"; // bleujinSample
		String consumerSecret = "0E867RpLKylqNT3fo6aW3gCTQhsGNE0ypKOEJKBQU" ;
		
		ConsumerKey consumer = new ConsumerKey(consumerKey, consumerSecret) ;
		
		OAuthSignatureCalculator calc = new OAuthSignatureCalculator(consumer, new RequestToken("", "")) ;
		
		NewClient client = newClient() ;
		
		AuthData auth = client.preparePost("https://api.twitter.com/oauth/request_token")
			.setSignatureCalculator(calc)
			.addParameter("oauth_callback", "http://61.250.201.157:9000/twitter/oauth")
			.execute(new AsyncCompletionHandler<AuthData>(){
				@Override
				public AuthData onCompleted(Response response) throws Exception {
					String textBody = response.getTextBody() ;
					return AuthData.create(textBody);
				}
				
			}).get() ;
		
		
		OAuthSignatureCalculator newCalc = new OAuthSignatureCalculator(consumer, auth.toRequestToken());
		Response getResponse = client.prepareGet("https://api.twitter.com/oauth/authorize")
			.addQueryParameter("oauth_token", auth.getToken())
		    // .addParameter("oauth_verifier", verifier)
		    .setSignatureCalculator(newCalc)
		    .execute().get() ;
		
		String textBody = getResponse.getUTF8Body() ;
		
		HTag getTag = HTag.createGeneral(new StringReader(textBody), "html") ;
		String authenticityToken = getTag.findElementBy("input", "name", "authenticity_token").getAttributeValue("value") ;
		String oauthToken = auth.getToken() ;
		String twitterUserId = "bleuhero" ;
		String twitterUserPwd = "redftw" ;
		
		Response postResponse = client.preparePost("https://api.twitter.com/oauth/authorize")
			.addParameter("authenticity_token", authenticityToken)
			.addParameter("oauth_token", oauthToken)
			.addParameter("session[username_or_email]", twitterUserId)
			.addParameter("session[password]", twitterUserPwd)
		    .setSignatureCalculator(newCalc)
		    .execute().get() ;
		
		HTag postTag = HTag.createGeneral(new StringReader(postResponse.getUTF8Body()), "html") ;
		String targetHref = postTag.findElementBy("a", "class", "maintain-context").getAttributeValue("href") ;
		Debug.line(targetHref);
	}

}

class AuthData {

	private Map<String, String> datas ;
	public AuthData(Map<String, String> datas) {
		this.datas = datas ;
	}

	public String getToken(){
		return datas.get("oauth_token") ;
	}
	
	public String getTokenSecret(){
		return datas.get("oauth_token_secret") ;
	}

	public boolean isConfirmed(){
		return Boolean.valueOf(datas.get("oauth_callback_confirmed")) ;
	}
	
	public RequestToken toRequestToken(){
		return new RequestToken(getToken(), getTokenSecret()) ;
	}
	
	public static AuthData create(String textBody) {
		// oauth_token=fNjvPK4r2uNMxN7hZuHp1Vg6egAwdodO4KHUUdEIo&oauth_token_secret=05VuaXJgfAiuFuVWFmZOKRMjG9wjvpYTJJVmQYuHso&oauth_callback_confirmed=true
		String[] params = StringUtil.split(textBody, '&') ;
		Map<String, String> datas = MapUtil.newMap() ;
		for (String param : params) {
			String[] ps = StringUtil.split(param, '=') ;
			datas.put(ps[0], ps[1]) ;
		}
		return new AuthData(datas);
	}
	
}
