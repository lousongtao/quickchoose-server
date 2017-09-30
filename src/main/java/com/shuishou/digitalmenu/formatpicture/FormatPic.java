package com.shuishou.digitalmenu.formatpicture;

import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class FormatPic {
	
	public FormatPic(){
		System.out.println(this.getClass().getClassLoader().getResource("").getPath());
	}

	public static void main(String[] args){
		//original pictures
		String sourcePath = FormatPic.class.getClassLoader().getResource("dishimage").toString();
		//deal to 240*240
		String target1Path = FormatPic.class.getClassLoader().getResource("dishimage_big").toString();
		//deal to 120*120
		String target2Path = FormatPic.class.getClassLoader().getResource("dishimage_small").toString();
		
		File sourceDir = new File("G:/webspace-web/digitalmenu/target/classes/dishimage/");
		if (sourceDir.isDirectory()){
			File[] files = sourceDir.listFiles();
			for(File file : files){
				try {
					System.out.println("start file : " + file.getAbsolutePath());
					makeZoomImage(file.getAbsolutePath(), "G:/webspace-web/digitalmenu/target/classes/dishimage_big/" + file.getName(), 240, 240);
					makeZoomImage(file.getAbsolutePath(), "G:/webspace-web/digitalmenu/target/classes/dishimage_small/" + file.getName(), 120, 120);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	/*
     * 图片缩放,w，h为缩放的目标宽度和高度
     * src为源文件目录，dest为缩放后保存目录
     * 如果图片原始尺寸小于设定尺寸, 不进行缩放 
     */
    public static void makeZoomImage(String src,String dest,int w,int h) throws IOException {
        
        double wr=0,hr=0;
        File srcFile = new File(src);
        File destFile = new File(dest);

        BufferedImage bufImg = ImageIO.read(srcFile); //读取图片
        Image Itemp = bufImg.getScaledInstance(w, h, Image.SCALE_SMOOTH);//设置缩放目标图片模板
        
        wr=w*1.0 / bufImg.getWidth();     //获取缩放比例
        hr=h*1.0 / bufImg.getHeight();
        
        if(wr > 1.0){
        	wr = 1.0;
        }
        if (hr > 1.0){
        	hr = 1.0;
        }
        if (wr > hr){
        	wr = hr;
        } else {
        	hr = wr;
        }

        AffineTransformOp ato = new AffineTransformOp(AffineTransform.getScaleInstance(wr, hr), null);
        Itemp = ato.filter(bufImg, null);
        
        ImageIO.write((BufferedImage) Itemp,dest.substring(dest.lastIndexOf(".")+1), destFile); //写入缩减后的图片
        
    }
    
    /*
     * 图片按比率缩放
     * size为文件大小
     */
    public static void makeZoomImage(String src,String dest,Integer size) throws IOException {
        File srcFile = new File(src);
        File destFile = new File(dest);
        
        long fileSize = srcFile.length();
        if(fileSize < size * 1024)   //文件大于size k时，才进行缩放
            return;
        
        Double rate = (size * 1024 * 0.5) / fileSize; // 获取长宽缩放比例
        
        BufferedImage bufImg = ImageIO.read(srcFile);
        Image Itemp = bufImg.getScaledInstance(bufImg.getWidth(), bufImg.getHeight(), Image.SCALE_SMOOTH);
            
        AffineTransformOp ato = new AffineTransformOp(AffineTransform.getScaleInstance(rate, rate), null);
        Itemp = ato.filter(bufImg, null);
        
        ImageIO.write((BufferedImage) Itemp,dest.substring(dest.lastIndexOf(".")+1), destFile);
        
    }
}
