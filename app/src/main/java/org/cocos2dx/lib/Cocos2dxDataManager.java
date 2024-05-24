package org.cocos2dx.lib;

import com.oppo.oiface.engine.OifaceGameEngineManager;

/* loaded from: classes2.dex */
public class Cocos2dxDataManager {
    public static void setOptimise(String thing, float value) {
        String jsonStr = "{\"" + thing + "\":" + String.valueOf(value) + "}";
        OifaceGameEngineManager.getInstance().updateGameEngineInfo(jsonStr);
    }

    public static void setProcessID(int pid) {
        setOptimise("render_pid", pid);
    }

    public static void setFrameSize(int width, int height) {
        setOptimise("buffer_size", width * height);
    }

    public static void onSceneLoaderBegin() {
        setOptimise("load_scene", 1.0f);
    }

    public static void onSceneLoaderEnd() {
        setOptimise("load_scene", 0.0f);
    }

    public static void onShaderLoaderBegin() {
        setOptimise("shader_compile", 1.0f);
    }

    public static void onShaderLoaderEnd() {
        setOptimise("shader_compile", 0.0f);
    }
}
