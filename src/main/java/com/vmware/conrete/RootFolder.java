package com.vmware.conrete;

public class RootFolder extends Folder<Object>
{
    public DataCenter getDataCenter() {
        return (DataCenter)this.children.get(0);
    }
}
