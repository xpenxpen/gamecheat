package org.xpen.ubisoft.dunia2.fileformat.dat;

import java.io.File;

public interface FileTypeHandler {

    void handle(byte[] b, String datFileName, String newFileName, boolean isUnknown) throws Exception;
}
