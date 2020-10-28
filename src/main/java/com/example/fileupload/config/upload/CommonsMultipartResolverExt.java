package com.example.fileupload.config.upload;

import org.apache.commons.fileupload.*;
import org.apache.commons.fileupload.servlet.ServletRequestContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @Author: liulang
 * @Date: 2020/10/27 15:47
 */
@Configuration
public class CommonsMultipartResolverExt extends CommonsMultipartResolver {


    @Override
    protected MultipartParsingResult parseRequest(HttpServletRequest request) throws MultipartException {
        MyProgressListener listener = new MyProgressListener(request);
        String encoding = determineEncoding(request);
        FileUpload fileUpload = prepareFileUpload(encoding);
        fileUpload.setProgressListener(listener);
        ServletRequestContext context = new ServletRequestContext(request);
        try {
            List<FileItem> fileItems = fileUpload.parseRequest(context);
            return parseFileItems(fileItems,encoding);


        }   catch (FileUploadBase.SizeLimitExceededException ex) {
            throw new MaxUploadSizeExceededException(fileUpload.getSizeMax(), ex);
        }
        catch (FileUploadBase.FileSizeLimitExceededException ex) {
            throw new MaxUploadSizeExceededException(fileUpload.getFileSizeMax(), ex);
        }
        catch (FileUploadException ex) {
            throw new MultipartException("Failed to parse multipart servlet request", ex);
        }
    }
}
