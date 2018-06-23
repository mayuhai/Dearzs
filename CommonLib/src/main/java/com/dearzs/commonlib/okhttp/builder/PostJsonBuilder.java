package com.dearzs.commonlib.okhttp.builder;

import com.dearzs.commonlib.okhttp.OkHttpUtils;
import com.dearzs.commonlib.okhttp.request.PostJsonRequest;
import com.dearzs.commonlib.okhttp.request.RequestCall;
import com.google.gson.Gson;

import java.util.LinkedHashMap;
import java.util.Map;

import okhttp3.MediaType;

/**
 * Created by zhy on 15/12/14.
 */
public class PostJsonBuilder extends OkHttpRequestBuilder
{
    private String content;
    private MediaType mediaType;


    public PostJsonBuilder content(Object content)
    {
        Gson gson = new Gson();
        this.content = gson.toJson(content);
        return this;
    }

    public PostJsonBuilder mediaType(MediaType mediaType)
    {
        this.mediaType = mediaType;
        return this;
    }


    @Override
    public RequestCall build()
    {
        return new PostJsonRequest(url, tag, params, headers, content, mediaType).build();
    }

    @Override
    public PostJsonBuilder url(String url)
    {
        this.url = url;
        return this;
    }

    @Override
    public PostJsonBuilder tag(Object tag)
    {
        this.tag = tag;
        return this;
    }

    @Override
    public PostJsonBuilder params(Map<String, String> params)
    {
        this.params = params;
        return this;
    }

    @Override
    public PostJsonBuilder addParams(String key, String val)
    {
        if (this.params == null)
        {
            params = new LinkedHashMap<>();
        }
        params.put(key, val);
        return this;
    }

    @Override
    public PostJsonBuilder headers(Map<String, String> headers)
    {
        this.headers = headers;
        return this;
    }

    @Override
    public PostJsonBuilder addHeader(String key, String val)
    {
        if (this.headers == null)
        {
            headers = new LinkedHashMap<>();
        }
        headers.put(key, val);
        return this;
    }
}
