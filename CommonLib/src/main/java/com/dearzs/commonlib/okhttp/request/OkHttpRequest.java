package com.dearzs.commonlib.okhttp.request;

import com.dearzs.commonlib.okhttp.callback.Callback;
import com.dearzs.commonlib.okhttp.utils.Exceptions;

import java.util.Map;

import okhttp3.Headers;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by zhy on 15/11/6.
 */
public abstract class OkHttpRequest
{

    protected String url;
    protected Object tag;
    protected Map<String, String> params;
    protected Map<String, String> headers;


    protected Request.Builder builder = new Request.Builder();

    protected OkHttpRequest(String url, Object tag,
                            Map<String, String> params, Map<String, String> headers)
    {
        this.url = url;
        this.tag = tag;
        this.params = params;
        this.headers = headers;

        if (url == null)
        {
            Exceptions.illegalArgument("url can not be null.");
        }
    }


    protected abstract RequestBody buildRequestBody();

    protected RequestBody wrapRequestBody(RequestBody requestBody, final Callback callback)
    {
        return requestBody;
    }

    protected abstract Request buildRequest(Request.Builder builder, RequestBody requestBody);

    public com.dearzs.commonlib.okhttp.request.RequestCall build()
    {
        return new com.dearzs.commonlib.okhttp.request.RequestCall(this);
    }


    public Request generateRequest(Callback callback)
    {
        RequestBody requestBody = wrapRequestBody(buildRequestBody(), callback);
        prepareBuilder();
        return buildRequest(builder, requestBody);
    }


    private void prepareBuilder()
    {
        builder.url(url).tag(tag);
        appendHeaders();
    }


    protected void appendHeaders()
    {
        Headers.Builder headerBuilder = new Headers.Builder();
        if (headers == null || headers.isEmpty()) return;

        for (String key : headers.keySet())
        {
            headerBuilder.add(key, headers.get(key));
        }
        builder.headers(headerBuilder.build());
    }

    @Override
    public String toString()
    {
        return "OkHttpRequest{" +
                "url='" + url + '\'' +
                ", tag=" + tag +
                ", params=" + params +
                ", headers=" + headers +
                '}';
    }
}
