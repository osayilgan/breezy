package org.scribe.services;

import org.apache.commons.codec.binary.Base64;

public class DatatypeConverterEncoder extends Base64Encoder
{
  @Override
  public String encode(byte[] bytes)
  {
    return new String(Base64.encodeBase64(bytes));
  }

  @Override
  public String getType()
  {
    return "DatatypeConverter";
  }
}
