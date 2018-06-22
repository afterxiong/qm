package com.shareshow.airpc.ftpclient;

public abstract interface FTPDataTransferListener
{
  public abstract void started();

  public abstract void transferred(int paramInt);

  public abstract void completed();

  public abstract void aborted();

  public abstract void failed();
}

/* Location:           C:\Users\tanwei\Desktop\ftp4j-1.7.2.jar
 * Qualified Name:     it.sauronsoftware.ftp4j.FTPDataTransferListener
 * JD-Core Version:    0.6.2
 */