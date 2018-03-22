package com.amazonaws.lambda.capbankbot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;

import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class AEMService {
    
    public void parseCreditcardOffers(List<HashMap<String, String>> creditcardOffers) {
    	if(creditcardOffers.isEmpty()) {
    		System.out.println("No Credit card information found.......");
    	}else {
    		for (Map<String, String> map : creditcardOffers) {
    		    for (Entry<String, String> entry : map.entrySet()) {
    		        String key = entry.getKey();
//    		        System.out.println("Curr key: "+ key);
    		        Object value = entry.getValue();
    		        System.out.println("Curr Value: "+ value);
    		    }
    		}
    	}
	}

    public List<String> getElementsListByKey(List<HashMap<String, String>> creditcardOffers, String elementKey) {
    	List<String> elements = new ArrayList<>();
    	if(creditcardOffers.isEmpty()) {
    		System.out.println("No Credit card information found.......");
    	}else {
    		for (Map<String, String> map : creditcardOffers) {
    		    for (Entry<String, String> entry : map.entrySet()) {
    		        String key = entry.getKey();
//    		        System.out.println("Curr key: "+ key);
    		        if(key.equalsIgnoreCase(elementKey)) {
    		        	Object value = entry.getValue();
    		        	System.out.println("Curr Value: "+ value);
    		        	elements.add((String) value);
    		        }
    		    }
    		}
    	}
    	return elements;
	}

    public String[] getElementsArrayByKey(List<HashMap<String, String>> creditcardOffers, String elementKey) {
    	List<String> elements = new ArrayList<>();
    	if(creditcardOffers.isEmpty()) {
    		System.out.println("No Credit card information found.......");
    	}else {
    		for (Map<String, String> map : creditcardOffers) {
    		    for (Entry<String, String> entry : map.entrySet()) {
    		        String key = entry.getKey();
//    		        System.out.println("Curr key: "+ key);
    		        if(key.equalsIgnoreCase(elementKey)) {
    		        	Object value = entry.getValue();
    		        	System.out.println("Curr Value: "+ value);
    		        	elements.add((String) value);
    		        }
    		    }
    		}
    	}
    	return elements.toArray(new String[elements.size()]);
//    	return (String[]) elements.toArray();
	}

	//public List<HashMap<String, String>> getCreditCardOffers()
	public JSONArray getCreditCardOffers(String featureIntent){
		System.out.println("Now getServiceresponse() service is being called.....");
    	JSONObject bodyObj = getServiceResponse("http://www.cap-bank.us/services/GetProducts?query=cards&category="+featureIntent);
    	
    	System.out.println("before parsing..........."+bodyObj);
    	
    	String responseBody = (String)bodyObj.get("body");		
		JSONObject responseJSON = new JSONObject(responseBody);
		
		JSONArray jArray = (JSONArray)responseJSON.get("creditcards");
		System.out.println("Service Response for getServiceresponse():: "+jArray);
		
		return jArray;
		//return getListOfCredirCards(jArray.toString());
    }

    public String getCardRegisterFormLink(String fName, String lName, String phoneNo, String emailId, String preference) {
    	// http://54.195.246.137/services/JsonResponceServlet?fName=karthik&sName=bajjuri&email=abc@gm.co&phone=12345678&preference=loan 
    	String paramString = "fName="+((fName == null) ? "" : fName) + "&sName="+((lName == null) ? "" : lName)+"&phone="+
    			((phoneNo == null) ? "" : phoneNo)+"&email="+((emailId == null) ? "" : emailId)+"&preference="+((preference == null) ? "" : preference);
    	String formServiceURL = "http://www.cap-bank.us/services/getApplyNow?" + paramString;
    	System.out.println("Now getCardregisterFormLink() service is being called.....:: "+formServiceURL);
    	JSONObject bodyObj = getServiceResponse(formServiceURL);
    	
    	String responseBody = (String)bodyObj.get("body");
    	System.out.println("before parsing..........."+bodyObj);
		
		return getFormLink(responseBody);
    }

	public List<HashMap<String, String>> getListOfCredirCards(String jsonResponse) {
    	Gson gson = new Gson();
		Type listType = new TypeToken<List<HashMap<String, String>>>(){}.getType();
		return gson.fromJson(jsonResponse, listType);
    }

	public String getFormLink(String responseBody) {
    	
   	 Map<String, String> jsonMap = new HashMap<String, String>();
        JSONObject jObject = new JSONObject(responseBody);
        Iterator<?> keys = jObject.keys();
        String formLink = "";
        if(keys.hasNext() ){
            String key = (String)keys.next();
            formLink = jObject.getString(key); 
            jsonMap.put(key, responseBody);

        }

        System.out.println("json : "+jObject);
        System.out.println("map : "+jsonMap);
        return formLink;
        
   	/*
   	JSONObject bObject = new JSONObject(responseMap);		
		JSONArray jArray = (JSONArray)bObject.get("creditcards");		
		StringBuffer sb = new StringBuffer();
		for(int i=0;i<jArray.length();i++) {
			String s = (String)jArray.getJSONObject(i).get("product_name");
			sb.append(s).append(",");
		}
		logger.log("CC cards::  "+sb.toString());
		*/
   }
    
    
    public Map<String, String> getReponseJsonToMap(String responseMap) {
    	
    	 Map<String, String> jsonMap = new HashMap<String, String>();
         JSONObject jObject = new JSONObject(responseMap);
         Iterator<?> keys = jObject.keys();

         while( keys.hasNext() ){
             String key = (String)keys.next();
             String value = jObject.getString(key); 
             jsonMap.put(key, value);

         }

         System.out.println("json : "+jObject);
         System.out.println("map : "+jsonMap);
         return jsonMap;
         
    	/*
    	JSONObject bObject = new JSONObject(responseMap);		
		JSONArray jArray = (JSONArray)bObject.get("creditcards");		
		StringBuffer sb = new StringBuffer();
		for(int i=0;i<jArray.length();i++) {
			String s = (String)jArray.getJSONObject(i).get("product_name");
			sb.append(s).append(",");
		}
		logger.log("CC cards::  "+sb.toString());
		*/
    }
    
    public JSONObject getServiceResponse(String serviceURL) {
    	System.out.println("Service is about to call..........");
    	BasicAuthRestTemplate restTemplate = new BasicAuthRestTemplate("summituser", "abcd");
    	ResponseEntity<String> jsonresult = restTemplate.getForEntity(serviceURL, String.class);
    	return new JSONObject(jsonresult);
    }
    
   

    public String getUserProfileInfo(String userInput) {
		System.out.println("You are here in AEM service called method.....");
		
		HttpClient client = HttpClientBuilder.create().build();		
		StringBuilder url = new StringBuilder();
		
		url.append("https://graph.facebook.com/v2.6/").append(userInput).append("?fields=first_name,last_name,profile_pic&access_token=EAAZAbP7jHJscBAMnegbrd6F66u6Y8u4OUEaxV4tmWT0XfnlHVqYkTWNDF7r4gOJAoEO6fZAcO97ynp3XoURZAWhWFvfqS68MOOirXt2lAyxgXEXlCqk1830xstBpaLjuQ4ZBIFwBIgWe8nKMdQYLTUNfl5vHviZCbZAXlfbIm0bAZDZD");
		System.out.println("RequestURI: " + url.toString());
		HttpGet req = new HttpGet(url.toString()); 
		
		HttpResponse response =  null;
		try {
			response = client.execute(req);
			System.out.println("Response: " + response.toString());
			
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	    int statusCode = response.getStatusLine().getStatusCode();
	    System.out.println("Get call response code: "+statusCode);
		
	    String line = "";
	    StringBuffer result = new StringBuffer();
	    BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		} catch (UnsupportedOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        try {
			while ((line = reader.readLine()) != null){ result.append(line); }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        System.out.println(result.toString());
        return result.toString();
	    
	}
    
	public String callAEMServicefor(String userInput, LambdaLogger logger) {
		System.out.println("You are here in AEM service called method.....");
		
		// AEM service restful HTTP get call
		CredentialsProvider credsProvider = new BasicCredentialsProvider();
		credsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials("summituser", "abcd"));
		HttpClient client = HttpClientBuilder.create().setDefaultCredentialsProvider(credsProvider).build();
//		HttpGet getRequest = new HttpGet("http://54.195.246.137/bin/trainingServlet?query=cards");
//		HttpGet getRequest = new HttpGet("http://54.195.246.137/bin/trainingServlet?query=accountDetails");
		HttpGet getRequest = new HttpGet("http://54.195.246.137/services/userInfoServletfirstname=<<>>&lastnmae=<<>>&email=<<>>&phoneno=<<>>&preference=<<>>");
		HttpResponse response =  null;
		try {
			response = client.execute(getRequest);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	    int statusCode = response.getStatusLine().getStatusCode();
	    System.out.println("Get call response code: "+statusCode);
		
	    String line = "";
	    StringBuffer result = new StringBuffer();
	    BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		} catch (UnsupportedOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        try {
			while ((line = reader.readLine()) != null){ result.append(line); }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        System.out.println(result.toString());
	    
	    return "return any response.....";
	    
	}
}