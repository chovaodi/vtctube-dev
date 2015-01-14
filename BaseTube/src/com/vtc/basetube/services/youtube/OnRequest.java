package com.vtc.basetube.services.youtube;

public interface OnRequest<T> {
    public void onSuccess(T data);

    public void onError();
}
