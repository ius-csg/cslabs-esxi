package com.vmware.conrete;

import com.vmware.vim25.ObjectContent;

public class DataCenter extends SimplifiedManagedObject
{
    public Folder<VirtualMachine> VMs;

    public DataCenter(ObjectContent objectContent) {
        this.inflateFromReference(objectContent);
    }
}
