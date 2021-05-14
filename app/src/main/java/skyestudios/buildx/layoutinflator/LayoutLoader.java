/*
 * Copyright 2014 Yahoo Inc.
 *
 * Licensed under the terms of the Apache License, Version 2.
 * Please see LICENSE.txt in the project root for terms.
 */
package skyestudios.buildx.layoutinflator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * <code>LayoutLoader</code> helps you load compiled layouts from any source.
 *
 * <p>
 *     The layout can be loaded from either an {@link InputStream} or raw bytes.
 * </p>
 *
 * <p>
 *     A typical application using {@link LayoutLoader} will have code similar to that given below:
 * </p>
 *
 * <pre>
 * class MyApplication extends Application {
 *     private LayoutLoader layoutLoader;
 *     private LayoutCache layoutCache;
 *
 *     protected void onCreate() {
 *         layoutLoader = new LayoutLoader().initialize();
 *         layoutCache = new LayoutCache();
 *     }
 *
 *     public LayoutLoader getLayoutLoader() {
 *         return layoutLoader;
 *     }
 *
 *     public LayoutCache getLayoutCache() {
 *         return layoutCache;
 *     }
 * }
 *
 * class LayoutCache {
 *     private LruCache&lt;String, byte[]&gt; cache = new LruCache&lt;&gt;();
 *     private Map&lt;String, Boolean&gt; fetchedURLs = new HashMap&lt;&gt;();
 *
 *     public void load(String url) {
 *         //load from network
 *         //validate signature
 *         //add to cache
 *         if(data != null) {
 *             cache.put(ur, data);
 *         }
 *         fetchedURLs.put(url, data != null);
 *     }
 *
 *     public boolean shouldFetch(String url) {
 *         return !fetchedURLs.containsKey(url);
 *     }
 *
 *     public boolean hasLayout(String url) {
 *         Boolean isDataValid = fetchedURLs.get(url);
 *         if(isDataValid == null) {
 *             return false;
 *         }
 *         return isDataValid;
 *     }
 *
 *     public byte[] get(String url) {
 *         return cache.get(url);
 *     }
 * }
 *
 * class SomeActivity extends Activity {
 *     protected void onCreate(Bundle savedInstanceState) {
 *         MyApplication app = (MyApplication) getApplicationContext();
 *         LayoutCache cache = app.getLayoutCache();
 *         String url = getFromSomeConfig(this); //load app config, a/b test config etc
 *
 *         View root = null;
 *         if(cache.shouldFetch(url)) {
 *             cache.load(url);
 *         } else if(cache.hasLayout(url) {
 *             root = app.getLayoutLoader().load(cache.get(url), this, null, false);
 *         }
 *
 *         //If the layout was loaded dynamically, great!
 *         // else, use the bundled layout.
 *         if(root != null) {
 *             setContentView(root);
 *         } else {
 *             setContentView(R.layout.activity_some);
 *         }
 *     }
 * }
 *
 * </pre>
 *
 * @author Gaurav Vaish
 * @since v1.0
 * @see #load(InputStream, Context, ViewGroup, boolean)
 * @see #load(byte[], Context, ViewGroup, boolean)
 */
public class LayoutLoader {

    public static final String TAG = "LayoutLoader";
    private Constructor<?> xmlBlockCtor;
    private Method newParserMethod;
    private boolean ready;

    /**
     * Initializes the loader.
     *
     * <p>
     *     This method must be called before {@link #load(byte[], Context, ViewGroup, boolean)}
     *     or {@link #load(InputStream, Context, ViewGroup, boolean)} can be used.
     * </p>
     *
     * <p>
     *     The method is reentrant (can be called multiple times) and thread-safe.
     * </p>
     *
     * <p>
     *     This method has been provided for lazy initialization.
     * </p>
     *
     * @return {@link LayoutLoader} instance for chaining.
     */
    public LayoutLoader initialize() {
        if(!ready) {
            synchronized(this) {
                if(!ready) {
                    initializeImpl();
                    ready = true;
                }
            }
        }
        return this;
    }

    /**
     * Clears up the internal state of the loader.
     *
     * <p>
     *     After the cleanup, the loader must be {@link #initialize()}d again before reuse.
     * </p>
     *
     * <p>
     *     The method is reentrant (can be called multiple times) and thread-safe.
     * </p>
     *
     * @return {@link LayoutLoader} instance for chaining.
     */
    public LayoutLoader cleanup() {
        if(ready) {
            synchronized(this) {
                if(ready) {
                    xmlBlockCtor = null;
                    newParserMethod = null;
                    ready = false;
                }
            }
        }
        return this;
    }

    protected void initializeImpl() {
        try {
            Class<?> cls = Class.forName("android.content.res.XmlBlock");
            xmlBlockCtor = cls.getDeclaredConstructor(byte[].class);
            xmlBlockCtor.setAccessible(true);

            newParserMethod = cls.getDeclaredMethod("newParser");
            newParserMethod.setAccessible(true);
        } catch(RuntimeException e) {
            Log.e(TAG, "Failed initializing loader", e);
        } catch(Exception e) {
            Log.e(TAG, "Failed initializing loader", e);
        }
    }

    /**
     * Loads the template from the given {@link InputStream}.
     *
     * <p>
     *     It returns the {@link View} if it can be inflated, <code>null</code> otherwise.
     * </p>
     *
     * <p>
     *     It internally uses {@link LayoutInflater#inflate(int, ViewGroup, boolean)}.
     *     All semantics of the corresponding method apply.
     * </p>
     *
     * @param input {@link InputStream} from where the contents of the compiled layout can be read
     * @param context {@link Context} to load the resources from
     * @param root Optional view to be the parent of the generated hierarchy (if
     *        <em>attachToRoot</em> is true), or else simply an object that
     *        provides a set of LayoutParams values for root of the returned
     *        hierarchy (if <em>attachToRoot</em> is false.)
     * @param attachToRoot Whether the inflated hierarchy should be attached to
     *        the root parameter? If <code>false</code>, root is only used to create the
     *        correct subclass of LayoutParams for the root view in the XML.
     * @return The root View of the inflated hierarchy. If root was supplied and
     *         attachToRoot is true, this is root; otherwise it is the root of
     *         the inflated XML file.
     * @see #load(byte[], Context, ViewGroup, boolean) 
     */
    public View load(InputStream input, Context context, ViewGroup root, boolean attachToRoot) {
        View rv = null;

        if(ready && xmlBlockCtor != null && newParserMethod != null) {
            byte[] data = readAll(input);
            rv = load(data, context, root, attachToRoot);
        }

        return rv;
    }

    /**
     * Loads the template from the given {@link InputStream}.
     *
     * <p>
     *     It returns the {@link View} if it can be inflated, <code>null</code> otherwise.
     * </p>
     *
     * <p>
     *     It internally uses {@link LayoutInflater#inflate(int, ViewGroup, boolean)}.
     *     All semantics of the corresponding method apply.
     * </p>
     *
     * @param data Raw data of the compiled layout.
     * @param context {@link Context} to load the resources from
     * @param root Optional view to be the parent of the generated hierarchy (if
     *        <em>attachToRoot</em> is true), or else simply an object that
     *        provides a set of LayoutParams values for root of the returned
     *        hierarchy (if <em>attachToRoot</em> is false.)
     * @param attachToRoot Whether the inflated hierarchy should be attached to
     *        the root parameter? If <code>false</code>, root is only used to create the
     *        correct subclass of LayoutParams for the root view in the XML.
     * @return The root View of the inflated hierarchy. If root was supplied and
     *         attachToRoot is true, this is root; otherwise it is the root of
     *         the inflated XML file.
     * @see #load(InputStream, Context, ViewGroup, boolean) 
     */
    public View load(byte[] data, Context context, ViewGroup root, boolean attachToRoot) {
        View rv = null;
        if(ready && xmlBlockCtor != null && newParserMethod != null && data != null) {
            try {
                Object xmlBlock = xmlBlockCtor.newInstance(data);
                XmlResourceParser parser = (XmlResourceParser) newParserMethod.invoke(xmlBlock);

                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                rv = inflater.inflate(parser, root, attachToRoot);
            } catch(RuntimeException e) {
                Log.e(TAG, "Failed loading layout", e);
            } catch(Exception e) {
                Log.e(TAG, "Failed loading layout", e);
            }
        }
        return rv;
    }

    private byte[] readAll(InputStream input) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int read;
        byte[] rv = null;

        try {
            read = input.read(buffer, 0, buffer.length);
            while(read >= 0) {
                baos.write(buffer, 0, read);
                read = input.read(buffer, 0, buffer.length);
            }
            rv = baos.toByteArray();
        } catch(IOException e) {
            Log.e(TAG, "Failed reading layout content", e);
        }

        return rv;
    }
}