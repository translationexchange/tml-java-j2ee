package com.tr8n.j2ee.utils;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;

import com.tr8n.core.Tr8n;
import com.tr8n.core.Utils;

public class SecurityUtils {

    /**
    *
    * @param params
    * @param secret
    * @return
    */
   public static String signAndEncode(Map<String, Object> params, String secret) {
       try {
           Map<String, Object> encoded = new HashMap<String, Object>(params);
           encoded.put("algorithm", "HMAC-SHA256");
           encoded.put("ts", "" + (new Date()).getTime());
           String payload = Utils.buildJSON(encoded);
           payload = Base64.encodeBase64String(StringUtils.getBytesUtf8(payload));

           Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
           SecretKeySpec secretKey = new SecretKeySpec(StringUtils.getBytesUtf8(secret), "HmacSHA256");
           sha256_HMAC.init(secretKey);
           String sig = Base64.encodeBase64String(sha256_HMAC.doFinal(StringUtils.getBytesUtf8(payload)));

           String singedPayload = Base64.encodeBase64String(StringUtils.getBytesUtf8(sig + "." + payload));
           return URLEncoder.encode(singedPayload, "UTF-8");
       } catch (Exception ex) {
           Tr8n.getLogger().logException(ex);
           return null;
       }
   }


   /**
    *
    * @param payload
    * @param secret
    * @return
    */
   @SuppressWarnings("unchecked")
	public static Map<String, Object> decodeAndVerify(String payload, String secret) {
       try {
           String data = URLDecoder.decode(payload, "UTF-8");
           String signedRequest = StringUtils.newStringUtf8(Base64.decodeBase64(data));
           String parts[] = signedRequest.split("\\.");
           if (parts.length != 2) {
               Tr8n.getLogger().error("Invalid parameters");
               return null;
           }

           String jsonString = StringUtils.newStringUtf8(Base64.decodeBase64(parts[1]));
           Map<String, Object> params = (Map<String, Object>) Utils.parseJSON(jsonString);

           if (params.get("algorithm").equals("HMAC-SHA256")) {
               Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
               SecretKeySpec secretKey = new SecretKeySpec(StringUtils.getBytesUtf8(secret), "HmacSHA256");
               sha256_HMAC.init(secretKey);
               String expectedSig = Base64.encodeBase64String(sha256_HMAC.doFinal(StringUtils.getBytesUtf8(parts[1])));
               if (!expectedSig.trim().equals(parts[0].trim())) {
                   Tr8n.getLogger().error("Failed to verify signature");
                   return null;
               }
           } else {
               Tr8n.getLogger().error("Unsupported signature");
               return null;
           }

           params.remove("algorithm");
           params.remove("ts");
           return params;
       } catch (Exception ex) {
           Tr8n.getLogger().logException(ex);
           return null;
       }
   }

   
}
