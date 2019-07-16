package com.vmware.conrete;

import com.vmware.vim25.InvalidStateFaultMsg;
import com.vmware.vim25.ObjectContent;
import com.vmware.vim25.RuntimeFaultFaultMsg;
import com.vmware.vim25.VimPortType;

public class VirtualMachine extends SimplifiedManagedObject
{

    public VirtualMachine(ObjectContent objectContent) {
        this.inflateFromReference(objectContent);
    }

    public String acquireTicket(VimPortType vimPort) throws RuntimeFaultFaultMsg, InvalidStateFaultMsg
    {
        return vimPort.acquireTicket(managedObjectReference, "webmks").getTicket();
    }
}
