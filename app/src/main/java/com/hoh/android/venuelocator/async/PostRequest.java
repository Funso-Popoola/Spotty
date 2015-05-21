package com.hoh.android.venuelocator.async;

import org.apache.http.NameValuePair;

import java.net.URL;
import java.util.List;

/**
 * Created by funso on 3/4/15.
 */
public class PostRequest {

    private URL url;
    private List<NameValuePair> data;

    public PostRequest(URL url){
        this.url = url;
    }

    public PostRequest(URL url, List<NameValuePair> data){
        this(url);
        this.data = data;
    }

    public void addData(NameValuePair data){
        this.data.add(data);
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public List<NameValuePair> getData() {
        return data;
    }

    public void setData(List<NameValuePair> data) {
        this.data = data;
    }
}
