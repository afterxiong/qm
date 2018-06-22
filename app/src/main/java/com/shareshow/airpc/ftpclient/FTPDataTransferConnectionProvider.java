package com.shareshow.airpc.ftpclient;

import java.net.Socket;

public abstract interface FTPDataTransferConnectionProvider
{
  public abstract Socket openDataTransferConnection()
    throws FTPDataTransferException;

  public abstract void dispose();
}

/* Location:           C:\Users\tanwei\Desktop\ftp4j-1.7.2.jar
 * Qualified Name:     it.sauronsoftware.ftp4j.FTPDataTransferConnectionProvider
 * JD-Core Version:    0.6.2
 */