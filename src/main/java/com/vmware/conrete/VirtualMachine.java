package com.vmware.conrete;

import com.vmware.vim25.*;

public class VirtualMachine extends SimplifiedManagedObject
{

    public VirtualMachinePowerState powerState;

    public VirtualMachine(ObjectContent objectContent) {
        this.inflateFromReference(objectContent);
    }

    @Override
    public void inflateFromReference(ObjectContent objectContent)
    {
        super.inflateFromReference(objectContent, (name, property) -> {
            if(name.equals("summary.runtime.powerState"))
                this.powerState = (VirtualMachinePowerState)property.getVal();
        });
    }

    public String acquireTicket(VimPortType vimPort) throws RuntimeFaultFaultMsg, InvalidStateFaultMsg
    {
        return vimPort.acquireTicket(managedObjectReference, "webmks").getTicket();
    }
}
