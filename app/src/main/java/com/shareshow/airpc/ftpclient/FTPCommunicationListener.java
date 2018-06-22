package com.shareshow.airpc.ftpclient;

public abstract interface FTPCommunicationListener
{
  public abstract void sent(String paramString);

  public abstract void received(String paramString);
}

/* Location:           C:\Users\tanwei\Desktop\ftp4j-1.7.2.jar
 * Qualified Name:     it.sauronsoftware.ftp4j.FTPCommunicationListener
 * JD-Core Version:    0.6.2
 */