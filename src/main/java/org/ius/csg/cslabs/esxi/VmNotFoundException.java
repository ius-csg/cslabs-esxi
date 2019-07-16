package org.ius.csg.cslabs.esxi;

public class VmNotFoundException extends Exception
{
    public VmNotFoundException(String message) {
        super(message);
    }
}
