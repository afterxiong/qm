package com.shareshow.airpc.ftpclient;

public abstract interface FTPListParser
{
  public abstract FTPFile[] parse(String[] paramArrayOfString, int type)
    throws FTPListParseException;
}

/* Location:           C:\Users\tanwei\Desktop\ftp4j-1.7.2.jar
 * Qualified Name:     it.sauronsoftware.ftp4j.FTPListParser
 * JD-Core Version:    0.6.2
 */