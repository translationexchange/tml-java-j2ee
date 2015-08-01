/**
 * Copyright (c) 2015 Translation Exchange, Inc. All rights reserved.
 *
 *  _______                  _       _   _             ______          _
 * |__   __|                | |     | | (_)           |  ____|        | |
 *    | |_ __ __ _ _ __  ___| | __ _| |_ _  ___  _ __ | |__  __  _____| |__   __ _ _ __   __ _  ___
 *    | | '__/ _` | '_ \/ __| |/ _` | __| |/ _ \| '_ \|  __| \ \/ / __| '_ \ / _` | '_ \ / _` |/ _ \
 *    | | | | (_| | | | \__ \ | (_| | |_| | (_) | | | | |____ >  < (__| | | | (_| | | | | (_| |  __/
 *    |_|_|  \__,_|_| |_|___/_|\__,_|\__|_|\___/|_| |_|______/_/\_\___|_| |_|\__,_|_| |_|\__, |\___|
 *                                                                                        __/ |
 *                                                                                       |___/
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.translationexchange.j2ee.utils;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;

import com.translationexchange.core.Tml;
import com.translationexchange.core.Utils;

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
           Tml.getLogger().logException(ex);
           return null;
       }
   }

   /**
    * 
    * @param payload
    * @return
    */
   @SuppressWarnings("unchecked")
	public static Map<String, Object> decode(String payload) {
		 try {
			 if (payload == null) 
				 return new HashMap<String, Object>();
			 
	           String data = URLDecoder.decode(payload, "UTF-8");
	           String jsonString = StringUtils.newStringUtf8(Base64.decodeBase64(data));
	           Map<String, Object> params = (Map<String, Object>) Utils.parseJSON(jsonString);
	           return params;
		 } catch (Exception ex) {
	           Tml.getLogger().logException(ex);
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
               Tml.getLogger().error("Invalid parameters");
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
                   Tml.getLogger().error("Failed to verify signature");
                   return null;
               }
           } else {
               Tml.getLogger().error("Unsupported signature");
               return null;
           }

           params.remove("algorithm");
           params.remove("ts");
           return params;
       } catch (Exception ex) {
           Tml.getLogger().logException(ex);
           return null;
       }
   }

   
}
