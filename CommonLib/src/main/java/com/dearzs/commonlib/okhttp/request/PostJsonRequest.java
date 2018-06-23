package com.dearzs.commonlib.okhttp.request;

import com.dearzs.commonlib.okhttp.utils.Exceptions;

import java.util.Map;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by zhy on 15/12/14.
 */
public class PostJsonRequest extends OkHttpRequest
{
    private static MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json;charset=utf-8");

    private String content;
    private MediaType mediaType;

    public PostJsonRequest(String url, Object tag, Map<String, String> params, Map<String, String> headers, String content, MediaType mediaType)
    {
        super(url, tag, params, headers);
        this.content = content;
        this.mediaType = mediaType;

        if (this.content == null)
        {
            Exceptions.illegalArgument("the content can not be null !");
        }
        if (this.mediaType == null)
        {
            this.mediaType = MEDIA_TYPE_JSON;
        }

    }

    @Override
    protected RequestBody buildRequestBody()
    {
        return RequestBody.create(mediaType, content);
    }

    @Override
    protected Request buildRequest(Request.Builder builder, RequestBody requestBody)
    {
        return builder.post(requestBody).build();
    }

    @Override
    public String toString()
    {
        return super.toString() + ", requestBody{content=" + content + "} ";
    }

}
