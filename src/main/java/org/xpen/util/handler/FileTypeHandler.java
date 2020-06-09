package org.xpen.util.handler;


public interface FileTypeHandler {

    void handle(byte[] b, String datFileName, String newFileName, boolean isUnknown) throws Exception;
}
