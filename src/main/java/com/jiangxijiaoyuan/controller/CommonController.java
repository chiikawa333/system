package com.jiangxijiaoyuan.controller;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.jiangxijiaoyuan.exception.BussinessException;
import com.jiangxijiaoyuan.responce.R;
import com.jiangxijiaoyuan.responce.ResponseCode;
import com.jiangxijiaoyuan.util.CaptchCache;
import com.jiangxijiaoyuan.util.Captcha;
import com.jiangxijiaoyuan.util.UploadFile;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@CrossOrigin
@RestController
public class CommonController {
    @Resource
    private DefaultKaptcha defaultKaptcha;

    @Value("${file.server.dir}")
    private String fileDir = "";

    @Value("${file.server.path}")
    private String path = "";

    @Resource
    private CaptchCache captchCache;
// ... existing code ...


// ... existing code ...

    @GetMapping("/common/getCaptcha")
        public R<Captcha> getCaptcha(){
            String captchText = defaultKaptcha.createText();
            BufferedImage image = defaultKaptcha.createImage(captchText);
            String base64Code = "";
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            try {
                ImageIO.write(image,"jpg",os);
                base64Code = Base64.getEncoder().encodeToString(os.toByteArray());
            } catch (IOException e) {
                throw new BussinessException(ResponseCode.CREATE_CAPTCHA_ERROR);
            }
            Captcha captcha = new Captcha();
            captcha.setCaptchaImage("data: image/png;base64," + base64Code);
            String captchid = UUID.randomUUID().toString().replace("-","");
            captcha.setCaptchaId(captchid);
            captchCache.storeCaptcha(captchid,captchText);

            return R.data(captcha);
        };




    /**
     * 上传文件
     *
     * @param req
     * @param res
     * @throws ServletException
     * @throws IOException
     */
    @RequestMapping("/common/uploadFile")
    @CrossOrigin
    public R<UploadFile> upload(HttpServletRequest req, HttpServletResponse res, @RequestParam("file") MultipartFile file) throws ServletException, IOException {
        String fileName = file.getOriginalFilename();
        fileName = fileName.substring(fileName.lastIndexOf("."));
        String fname = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + fileName;
        String basePath = fileDir;
        File dir = new File(basePath);
        if (!dir.exists()) {
            dir.mkdir();
        }
        String furl = path + File.separator + fname;
        String url = req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort() + "/" + furl;
        File dest = new File(basePath, fname);
        try {
            file.transferTo(dest);
            UploadFile uploadFile = new UploadFile();
            uploadFile.setName(furl);
            uploadFile.setUrl(url);
            return R.data(uploadFile);
        } catch (IOException e) {
            return R.fail();
        }
    }
}
