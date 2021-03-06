
package me.pjq.photo;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import me.pjq.base.BaseHttpServlet;
import me.pjq.base.CommonParamString;
import me.pjq.base.UploadPhoto;
import me.pjq.util.Utils;

public class UploadPhotoImpl extends UploadPhoto {
    public static final String mUploadPath = "./upload/upload/";
    public static final String mUploadTmpPath = "./upload/tmp/";

    private String mPhotoName;

    /**
     * 
     */
    private static final long serialVersionUID = -7769869574976977418L;

    // private String uploadPath = "./upload/upload/";
    // private String tempPath = "./upload/tmp";
    File tempPathFile;

    public void init() throws ServletException {
        File uploadFile = new File(mUploadPath);
        if (!uploadFile.exists()) {
            uploadFile.mkdirs();
        }
        File tempPathFile = new File(mUploadTmpPath);
        if (!tempPathFile.exists()) {
            tempPathFile.mkdirs();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // TODO Auto-generated method stub
        super.doPost(req, resp);

        init();
        // String contentType = req.getContentType();
        int contentLength = req.getContentLength();
        mPhotoName = Utils.createPhotoName();
        Utils.storeImageFromInputStream(req.getInputStream(), contentLength,
                mUploadPath, mPhotoName);     

        // out.println("UploadPhoto done");
        // userTheDefaultAccount();
        String url = uploadPhoto();
        out.println(url);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // TODO Auto-generated method stub
        super.doGet(req, resp);
    }

    /**
     * Delete the photo when upload success.
     */
    private void deletePhoto() {
        File file = new File(mUploadPath, mPhotoName);

        if (file.exists()) {
            file.delete();
        }
    }

    /**
     * Upload the Url with the default YFrog.
     * 
     * @return the url of the uploaded image.
     */
    private String uploadPhoto() {
        return "";
    }

    private String createPhotoPath() {
        return mUploadPath + "/" + mPhotoName;
    }

}
