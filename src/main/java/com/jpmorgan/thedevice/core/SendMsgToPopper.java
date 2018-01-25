package com.jpmorgan.thedevice.core;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

/**
 * Created by S.King on 15/02/2017.
 */
public class SendMsgToPopper {

    public enum Action {
        POP,
        RESET
    }

    /**
     * The <code>Logger</code> to be used.
     */
    private static Logger log = LogManager.getLogger(StateMachine.class);

    public static void Send(Action status) {
        String urlRoot = "http://192.168.1.250/";
        CloseableHttpClient httpclient = HttpClients.createDefault();

        try {
            String url;

            switch (status) {
                case POP:
                    url = "/balloon/pop";
                    break;

                case RESET:
                default:
                    url = "/balloon/reset";
                    break;
            }

            HttpPost httpPost = new HttpPost(urlRoot + url);

            log.debug("Executing request " + httpPost.getRequestLine());

            // Create a custom response handler
            ResponseHandler<String> responseHandler = new ResponseHandler<String>() {

                @Override
                public String handleResponse(
                        final HttpResponse response) throws ClientProtocolException, IOException {
                    int status = response.getStatusLine().getStatusCode();
                    if (status >= 200 && status < 300) {
                        HttpEntity entity = response.getEntity();
                        return entity != null ? EntityUtils.toString(entity) : null;
                    } else {
                        throw new ClientProtocolException("Unexpected response status: " + status);
                    }
                }
            };

            String responseBody = httpclient.execute(httpPost, responseHandler);
            log.debug("----------------------------------------");
            log.debug(responseBody);
        } catch (Exception ex)
        {
            log.error("Exception : " + ex.getMessage(), ex);
        } finally {

            try {
                httpclient.close();
            } catch (IOException e) {
                log.error("Exception when closing httpClient : " + e.getMessage(), e);
            }
        }
    }

}
