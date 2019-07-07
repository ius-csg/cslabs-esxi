package com.vmware.common.ssl;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

public class TrustAll
{
  public static void trust() throws NoSuchAlgorithmException, KeyManagementException
  {
      (new TrustAllTrustManager()).register();
      (new TrustAllHostNameVerifier()).register();
  }
}
