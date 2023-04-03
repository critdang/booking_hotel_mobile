package com.example.myapplication.Utils;

import org.json.JSONException;

import java.util.List;

public interface VolleyCallback <E>{
    void onSuccess(E result) throws JSONException;
    void onSuccess(List<E> result) throws JSONException;
    void onError(String result);
}
