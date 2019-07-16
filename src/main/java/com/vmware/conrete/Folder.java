package com.vmware.conrete;

import com.vmware.vim25.ObjectContent;

import java.util.ArrayList;

public class Folder<T> extends  SimplifiedManagedObject
{
    public ArrayList<T> children = new ArrayList<>();

    public Folder() {

    }
    public Folder(ObjectContent objectContent) {
        this.inflateFromReference(objectContent);
    }
}
