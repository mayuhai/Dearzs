package com.dearzs.upload.uploadimage.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Environment;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;

/**
 * 图片压缩管理类（单例）
 * 包含图片copy（位置移动)
 * 等比例压缩,若超过最大值再进行质量压缩
 */
public class BitmapCompressManager {

    /** 单例对象*/
    private static BitmapCompressManager mSingleton;
    private static MessageDigest digest;
    /** 默认800k图片大小*/
    private static final int MAX_FILE_SIZE = 800 * 1024;
    /**用户上传图片缓存路径**/
    private String uploadFileDir = Environment.getExternalStorageDirectory() + File.separator + "Temp";

    private BitmapCompressManager(String uploadFileDir) {
        this.uploadFileDir = uploadFileDir;
    }

    /**
     * 初始化自身管理器
     * @return
     */
    synchronized public static boolean init(String uploadFileDir) {
        if (mSingleton != null) return false;

        mSingleton = new BitmapCompressManager(uploadFileDir);
        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
        }
        return true;
    }

    /**
     * 获取上传管理单例
     * @return
     */
    synchronized public static BitmapCompressManager getInstance() {
        return mSingleton;
    }

    /**
     * 使用Bitmap加Matrix来缩放
     */
    public Bitmap resizeImage(Bitmap bitmap) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        // 缩放比例
        float be = 1;
        if (w > 600f) {
            be = 600f / w;
        }

        if (be >= 1) return bitmap;

        Matrix matrix = new Matrix();
        matrix.postScale(be, be);
        // if you want to rotate the Bitmap
        // matrix.postRotate(45);
        return Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
    }

    /**
     * 先压缩大小在压缩质量
     *
     * @param originalPath
     */
    public void compressImage(String originalPath) throws Exception, Error {
        // 先验证文件的大小,若不需要压缩,则直接返回
        long curFileSize = getFileSize(new File(originalPath));
        if (curFileSize <= MAX_FILE_SIZE) {
            return;
        }
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap originalBitmap = BitmapFactory.decodeFile(originalPath, newOpts);
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        //现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        float hh = 800f;//这里设置高度为800f
        float ww = 600f;//这里设置宽度为600f
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0) {
            be = 1;
        }
        newOpts.inSampleSize = be;// 设置缩放比例
        originalBitmap = BitmapFactory.decodeFile(originalPath, newOpts);
        // 得到的originalBitmap是小比例的图片了,,再次验证大小是否超过最大大小
        compressImage(originalBitmap, originalPath, MAX_FILE_SIZE);
        if (originalBitmap!= null && originalBitmap.isRecycled() == false) {
            originalBitmap.recycle();
            originalBitmap = null;
        }
    }

    /**
     * 通过管道的方式复制图片
     * @param originalPath
     */
    public boolean copyFileUsingFileChannels(String originalPath, String targetPath) {
        boolean copyResult = false;
        if (TextUtils.isEmpty(originalPath) || TextUtils.isEmpty(targetPath)) {
            return copyResult;
        }
        // 若地址相同,直接返回
        if (originalPath.equals(targetPath)) {
            return copyResult;
        }
        FileChannel inputChannel = null;
        FileChannel outputChannel = null;
        try {
            inputChannel = new FileInputStream(originalPath).getChannel();
            outputChannel = new FileOutputStream(targetPath).getChannel();
            outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
            copyResult = true;
        } catch (Exception e){
        } finally {
            try {
                inputChannel.close();
                outputChannel.close();
            } catch (Exception e) {
            } catch (Error error) {
            }
        }
        return copyResult;
    }

    /**
     * 通过管道的方式复制图片
     * @param originalPath
     */
    public String copyFileUsingFileChannels(String originalPath) {
        String targetPath = buildNewFilePath(originalPath);
        if (TextUtils.isEmpty(originalPath) || TextUtils.isEmpty(targetPath)) {
            return null;
        }
        // 若地址相同,直接返回
        if (originalPath.equals(targetPath)) {
            return originalPath;
        }
        FileChannel inputChannel = null;
        FileChannel outputChannel = null;
        try {
            try {
                inputChannel = new FileInputStream(originalPath).getChannel();
                outputChannel = new FileOutputStream(targetPath).getChannel();
                outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
            } catch (Exception e) {}
            catch (Error error) {}
        } finally {
            try {
                inputChannel.close();
                outputChannel.close();
            } catch (Exception e) {}
            catch (Error error) {}
        }
        return targetPath;
    }

    /**
     * 读取流中具体数据
     *
     * @param inputStream
     * @return
     */
    public String readByteInputStream(InputStream inputStream) {
        InputStreamReader inputReader = null;
        BufferedReader bufReader = null;
        String result = "";
        try {
            inputReader = new InputStreamReader(inputStream);
            bufReader = new BufferedReader(inputReader);
            String line = "";
            while ((line = bufReader.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
        } finally {
            try {
                if (bufReader != null) {
                    bufReader.close();
                    bufReader = null;
                }
                if (inputReader != null) {
                    inputReader.close();
                    inputReader = null;
                }
            } catch (Exception e) {
            }
        }
        return result;
    }
    /**
     * 根据View(主要是ImageView)的宽和高来获取图片的缩略图
     *
     * @param path
     * @param viewWidth
     * @param viewHeight
     * @return
     */
    public Bitmap decodeThumbBitmapForFile (String path, int viewWidth, int viewHeight)  throws Exception{
        BitmapFactory.Options options = new BitmapFactory.Options();
        //设置为true,表示解析Bitmap对象，该对象不占内存
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        //设置缩放比例
        options.inSampleSize = computeScale(options, viewWidth, viewHeight);

        //设置为false,解析Bitmap对象加入到内存中
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(path, options);
    }

    /**
     * 生成照片的保存路径
     *
     * @return
     */
    public String getTakePhotoFilePath() {
        File carDir = new File(uploadFileDir);
        if (!carDir.exists()) {
            carDir.mkdir();
        }
        return carDir + File.separator + System.nanoTime() + ".jpg";
    }

    /**
     * 根据View(主要是ImageView)的宽和高来计算Bitmap缩放比例。默认不缩放
     *
     * @param options
     * @param viewWidth
     * @param viewHeight
     */
    private int computeScale(BitmapFactory.Options options, int viewWidth, int viewHeight) {
        int inSampleSize = 1;
        if (viewWidth == 0 || viewWidth == 0) {
            return inSampleSize;
        }
        int bitmapWidth = options.outWidth;
        int bitmapHeight = options.outHeight;

        //假如Bitmap的宽度或高度大于我们设定图片的View的宽高，则计算缩放比例
        if (bitmapWidth > viewWidth || bitmapHeight > viewWidth) {
            int widthScale = Math.round((float) bitmapWidth / (float) viewWidth);
            int heightScale = Math.round((float) bitmapHeight / (float) viewWidth);

            //为了保证图片不缩放变形，我们取宽高比例最小的那个
            inSampleSize = widthScale < heightScale ? widthScale : heightScale;
        }
        return inSampleSize;
    }

    /**
     * 质量压缩
     *
     * @param bitmap
     * @param outPath
     * @param maxSize
     */
    private void compressImage(Bitmap bitmap, String outPath, final int maxSize) {
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            // scale
            int options = 100;
            // Store the bitmap into output stream(no compress)
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, os);
            // Compress by loop
            while (os.toByteArray().length > maxSize) {
                // Clean up os
                os.reset();
                // interval 10
                options -= 5;
                bitmap.compress(Bitmap.CompressFormat.JPEG, options, os);

                if (options <= 0) {
                    break;
                }
            }

            //删除源文件
            File tempFile = new File(outPath);
            if (tempFile.exists()) {
                tempFile.delete();
            }
            tempFile = null;

            // 生成压缩后的文件
            FileOutputStream fos = new FileOutputStream(outPath);
            fos.write(os.toByteArray());
            fos.flush();
            fos.close();
        } catch (Exception e) {
        } catch (Error error) {
        }
    }

    /**
     * 获取指定文件大小
     *
     * @param file
     * @return
     * @throws Exception
     */
    private long getFileSize(File file) throws Exception {
        long size = 0;
        if (file.exists()) {
            FileInputStream fis = new FileInputStream(file);
            size = fis.available();
            fis.close();
        }
        return size;
    }

    /**
     * 生成新的临时目录存储复制压缩的图片
     *
     * @param picPath
     * @return
     */
    private String buildNewFilePath(String picPath) {
        String newPath = null;
        if (TextUtils.isEmpty(picPath)) {
            return newPath;
        }
        try {
            File carDir = new File(uploadFileDir);
            File picFile = new File(picPath);

            if (!carDir.getAbsolutePath().equals(picFile.getParentFile().getAbsolutePath())) {
                newPath = getTakePhotoFilePath();
            } else {
                newPath = picPath;
            }
            carDir = null;
            picFile = null;
        } catch (Exception io) {
            io.printStackTrace();
        }
        return newPath;
    }
}
