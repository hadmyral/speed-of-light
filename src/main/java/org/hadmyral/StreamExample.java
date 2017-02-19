package org.hadmyral;

import com.google.common.collect.Lists;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class StreamExample {

    public static void run(String consumerKey, String consumerSecret, String token, String secret) throws InterruptedException, ParseException {
        BlockingQueue<String> queue = new LinkedBlockingQueue<String>(10000);
        StatusesFilterEndpoint endpoint = new StatusesFilterEndpoint();
        endpoint.trackTerms(Lists.newArrayList("renzi"));

        Authentication auth = new OAuth1(consumerKey, consumerSecret, token, secret);

        Client client = new ClientBuilder()
                .hosts(Constants.STREAM_HOST)
                .endpoint(endpoint)
                .authentication(auth)
                .processor(new StringDelimitedProcessor(queue))
                .build();

        client.connect();

        JSONParser parser = new JSONParser();

        for (int msgRead = 0; msgRead < 1000; msgRead++) {
            String msg = queue.take();
            JSONObject json = (JSONObject) parser.parse(msg);
            System.out.println("" + msgRead + ": " + json.get("text") + "\n\n");
        }

        client.stop();

    }

    public static void main(String[] args) {
        try {
            try {

                StreamExample.run(args[0], args[1], args[2], args[3]);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } catch (InterruptedException e) {
            System.out.println(e);
        }
    }

}
