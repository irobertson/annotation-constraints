package org.annotationconstraints.processor;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

class SpecialClassLoader extends ClassLoader {
    private Map<String,MemoryByteCode> m = new HashMap<String, MemoryByteCode>();

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        MemoryByteCode mbc = m.get(name);
        if (mbc==null){
            return super.findClass(name);
        }
        return defineClass(name, mbc.getBytes(), 0, mbc.getBytes().length);
    }

    public void addClass(String name, MemoryByteCode mbc) {
        m.put(name, mbc);
    }

    @Override
    public URL getResource(String name) {
        System.out.println("asked for " + name);
        return super.getResource(name);
    }

    @Override
    public Enumeration<URL> getResources(String name) throws IOException {
        System.out.println("Asked for all of " + name);
        return super.getResources(name);
    }

    @Override
    public InputStream getResourceAsStream(String name) {
        System.out.println("Asked for stream of " + name);
        return super.getResourceAsStream(name);
    }


}
