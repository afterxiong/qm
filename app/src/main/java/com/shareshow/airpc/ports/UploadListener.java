package com.shareshow.airpc.ports;

/**
 * Created by TEST on 2017/8/11.
 */

public interface UploadListener {

    void success();

    void fail();

    void abort();
}
