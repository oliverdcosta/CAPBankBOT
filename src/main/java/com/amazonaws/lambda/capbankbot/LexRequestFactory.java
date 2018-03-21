package com.amazonaws.lambda.capbankbot;

import java.util.HashMap;
import java.util.Map;

import static org.apache.http.util.TextUtils.isEmpty;

public class LexRequestFactory {
    public static LexRequest createLexRequest(Map<String, Object> input) {
        Map<String,Object> botMap = (Map<String,Object>) input.get("bot");
        String botName = (String) botMap.get("name");
        LexRequest lexRequest = new LexRequest();
        lexRequest.setBotName(botName);
        Map<String,Object> currentIntent = (Map<String,Object>) input.get("currentIntent");
        System.out.println(botMap.keySet().toString());
        System.out.println(currentIntent.keySet().toString());
        for (String key : currentIntent.keySet()) {
            System.out.println(key + " -- " + currentIntent.get(key));
        }
        //Map<String,Object> slotDetails = (Map<String,Object>) input.get("slotDetails");
        //System.out.println(slotDetails.keySet().toString());
       //lexRequest.setIntentName(currentIntent.toString());
        lexRequest.setCreditcard((String) input.get("creditcard"));
        lexRequest.setAgreetoanswer((String)input.get("agreetoanswer"));
        lexRequest.setFeatureintent((String) input.get("featureintent"));
        lexRequest.setUserscore((String)input.get("userscore"));
        lexRequest.setUsercardintent((String)input.get("usercardintent"));
        lexRequest.setFilltypeintent((String)input.get("filltypeintent"));
        lexRequest.setThanks((String)input.get("thanks"));
        return lexRequest;
    }
}
