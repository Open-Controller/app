package com.pjtsearch.opencontroller_lib;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.HashMap;
import java.util.Map;

public class OpenController {
    private static native String from_json(String json);

    private static native String to_json(String handle);

    private static native void execute_action(String handle, String device, String action);

    private static native String subscribe_dynamic_value(String handle, String device, String dynamic_value);

    private static native void unsubscribe_dynamic_value(String value_handle);

    static {
        System.loadLibrary("opencontroller_lib");
    }

    private static List<Listener> listeners = new ArrayList<Listener>();

    public static void event_cb(String handle, String device, String dynamic_value, String payload) {
        listeners.stream()
                .filter(l -> l.equals(new Listener(handle, device, dynamic_value)))
                .findAny()
                .ifPresent(l -> l.listeners.values().forEach(i -> i.accept(payload)));
    }

    private String handle;

    public OpenController(String json) {
        handle = from_json(json);
    }

    public String toJson() {
        return to_json(handle);
    }

    public void executeAction(String device, String action) {
        execute_action(handle, device, action);
    }

    public Runnable subscribeDynamicValue(String device, String dynamic_value, Consumer<String> cb) {
        Listener listener = listeners.stream()
                .filter(l -> l.equals(new Listener(handle, device, dynamic_value)))
                .findAny()
                .orElseGet(() -> {
                    Listener l = new Listener(handle, device, dynamic_value, subscribe_dynamic_value(handle, device, dynamic_value));
                    listeners.add(l);
                    return l;
                });
        UUID id = UUID.randomUUID();
        listener.listeners.put(id, cb);
        return () -> {
            if (listener.listeners.size() == 1) {
                unsubscribe_dynamic_value(listener.dynamic_value_handle);
                listeners = listeners.stream()
                        .filter(l -> !l.equals(new Listener(handle, device, dynamic_value)))
                        .collect(Collectors.toList());
            }
            else listener.listeners.remove(id);
        };
    }

    public static void main(String[] args) throws InterruptedException {
        // String handle = OpenController.from_json(
        //         "{ \"name\": \"Test house\", \"rooms\": [ { \"name\": \"Test room\", \"controllers\": [ { \"name\": \"test\", \"widgets\": [ { \"type\": \"Button\", \"action\": { \"device\": \"test\", \"action\": \"Test\" }, \"icon\": \"icon\", \"text\": \"text\" } ] } ] } ], \"devices\": [ { \"id\": \"test\", \"actions\": [ { \"type\": \"HttpAction\", \"url\": \"http://example.com\", \"id\": \"Test\", \"method\": \"GET\" }, { \"type\": \"TcpAction\", \"address\": \"localhost:2000\", \"id\": \"TCP\", \"command\": \"test\" } ], \"dynamic_values\": [ { \"id\": \"Test\", \"resources\": [ { \"type\": \"Date\" } ], \"script\": \"date + 2\" } ] } ] }");
        // System.out.println(handle);
        // System.out.println(OpenController.to_json(handle));
        // OpenController.execute_action(handle, "test", "Test");
        // OpenController.execute_action(handle, "test", "TCP");
        // String dynamicValueHandle = OpenController.subscribe_dynamic_value(handle, "test", "Test");
        // Thread.sleep(1000);
        // OpenController.unsubscribe_dynamic_value(dynamicValueHandle);
        // System.out.println("test");
        OpenController instance = new OpenController("{ \"name\": \"Test house\", \"rooms\": [ { \"name\": \"Test room\", \"controllers\": [ { \"name\": \"test\", \"widgets\": [ { \"type\": \"Button\", \"action\": { \"device\": \"test\", \"action\": \"Test\" }, \"icon\": \"icon\", \"text\": \"text\" } ] } ] } ], \"devices\": [ { \"id\": \"test\", \"actions\": [ { \"type\": \"HttpAction\", \"url\": \"http://example.com\", \"id\": \"Test\", \"method\": \"GET\" }, { \"type\": \"TcpAction\", \"address\": \"localhost:2000\", \"id\": \"TCP\", \"command\": \"test\" } ], \"dynamic_values\": [ { \"id\": \"Test\", \"resources\": [ { \"type\": \"Date\" } ], \"script\": \"date + 2\" } ] } ] }");
        instance.executeAction("test", "Test");
        instance.executeAction("test", "TCP");
        Runnable unsubscribe = instance.subscribeDynamicValue("test", "Test", p -> System.out.println(p));
        Thread.sleep(1000);
        Runnable unsubscribe2 = instance.subscribeDynamicValue("test", "Test", p -> System.out.println("2:"+p));
        Thread.sleep(1000);
        unsubscribe.run();
        Thread.sleep(1000);
        unsubscribe2.run();
        Runnable unsubscribe3 = instance.subscribeDynamicValue("test", "Test", p -> System.out.println("3:"+p));
        Thread.sleep(1000);
        unsubscribe3.run();
    }

    private static class Listener {
        public String handle;
        public String device;
        public String dynamic_value;
        public String dynamic_value_handle;
        public Map<UUID, Consumer<String>> listeners;

        public Listener(String handle, String device, String dynamic_value) {
            this.handle = handle;
            this.device = device;
            this.dynamic_value = dynamic_value;
            this.listeners = new HashMap<UUID, Consumer<String>>();
        }

        public Listener(String handle, String device, String dynamic_value, String dynamic_value_handle) {
            this.handle = handle;
            this.device = device;
            this.dynamic_value = dynamic_value;
            this.dynamic_value_handle = dynamic_value_handle;
            this.listeners = new HashMap<UUID, Consumer<String>>();
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) return true;
            if (!(o instanceof Listener)) return false;
            Listener c = (Listener) o;
            return c.handle.equals(handle) && c.device.equals(device) && c.dynamic_value.equals(dynamic_value);
        }
    }
}