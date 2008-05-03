package org.intellij.stripes.util;

import com.intellij.psi.xml.XmlTag;

public abstract class XmlTagContainer<T> {
    protected T container;

    public XmlTagContainer(T container) {
        this.container = container;
    }

    public T getContainer() {
        return container;
    }

    public abstract void add(XmlTag tag);
}
