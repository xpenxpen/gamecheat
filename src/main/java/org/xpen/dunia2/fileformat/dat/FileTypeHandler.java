package org.xpen.dunia2.fileformat.dat;

import java.io.File;

public interface FileTypeHandler {

    void handle(byte[] b, String newFileName, boolean isUnknown) throws Exception;
}
