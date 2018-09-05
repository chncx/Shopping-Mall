package com.jt.manage.service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.jt.common.vo.PicUploadResult;

@Service
public class FileServiceImpl implements FileService {
	
	//要求参数应该动态获取???
	//定义文件存储的根目录
	@Value("${image.localPath}") //通过注解的方式 为属性动态赋值
	private String localPath;  //= "E:/jt-upload/";
	
	//定义虚拟路径的根目录
	@Value("${image.urlPath}")
	private String urlPath;    //="http://image.jt.com/";
	
	/**
	 * 问题:
	 * 	1.是否为正确的图片??? jpg|png|gif
	 * 	2.是否为恶意程序???   
	 *	3.不能将图片保存到同一个文件夹下
	 *  4.图片的重名问题
	 *   解决策略:
	 *  1.正则表达式实现图片的判断.
	 *  2.使用BufferedImage转化图片 height/weight
	 *  3.使用分文件夹存储 yyyy/MM/dd 并不是唯一的
	 *  4.UUID+三位随机数区分图片
	 */
	@Override
	public PicUploadResult upload(MultipartFile uploadFile) {
		PicUploadResult result = new PicUploadResult();
		
		//1.获取图片的名称   abc.jpg
		String fileName = uploadFile.getOriginalFilename();
		fileName = fileName.toLowerCase(); //.PNG
		
		//2.判断是否为图片的类型
		if(!fileName.matches("^.*(jpg|png|gif)$")){
			
			result.setError(1); //表示不是图片
			//说明:如果传递的图片不是指定的格式,直接程序结束
			return result;
		}
		
		//3.判断是否为恶意程序
		try {
			BufferedImage bufferedImage = 
					ImageIO.read(uploadFile.getInputStream());
			
			int height = bufferedImage.getHeight();
			int width = bufferedImage.getWidth();
			if(height == 0 || width == 0){
				
				result.setError(1);
				return result;
			}
			
			//4.将图片分文件存储 yyyy/MM/dd
			String DatePath = 
					new SimpleDateFormat("yyyy/MM/dd").format(new Date());
			
			//判断是否有该文件夹  E:/jt-upload/2018/11/11
			String picDir = localPath + DatePath;
			File picFile = new File(picDir);
			
			if(!picFile.exists()){
				
				picFile.mkdirs();
			}
			
			//防止文件重名   
			String uuid = UUID.randomUUID().toString().replace("-", "");
			
			int randomNum = new Random().nextInt(1000);
			//.jpg
			String fileType = fileName.substring(fileName.lastIndexOf("."));
			
			//拼接文件的名称
			String fileNowName = uuid + randomNum + fileType;
			
			//实现文件上传            e:jt-upload/yyyy/MM/dd/1231231231231231231.jpg
			String realFilePath = picDir + "/" +fileNowName;
			uploadFile.transferTo(new File(realFilePath));

			
			//将真实数据回显
			result.setHeight(height+"");
			result.setWidth(width+"");
			
			/**
			 * 实现虚拟路径的拼接
			 * E:/jt-upload/2018/07/23/e4d5c2667a174477b2ab59158670bbbe816.jpg
			 * image.jt.com
			 */
			String realUrl = urlPath + DatePath + "/" + fileNowName;
			result.setUrl(realUrl);
			
		} catch (Exception e) {
			e.printStackTrace();
			result.setError(1); //文件长传有误
		}
		return result;
	}

}
