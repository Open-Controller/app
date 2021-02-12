package com.pjtsearch.opencontroller.extensions

import org.json.JSONArray
import org.json.JSONObject


fun JSONArray.toList(): List<JSONObject> =
        List<JSONObject>(length()) { i -> getJSONObject(i) }