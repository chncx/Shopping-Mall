package com.jt.manage.controller;

import java.io.File;
import java.io.IOException;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.jt.common.vo.PicUploadResult;
import com.jt.manage.service.FileService;

@Controller
public class FileController{
	
	@Autowired
	private FileService fileService;	
	/**
	 * 要求文件上传完成后,再次跳转到文件长传页面
	 * 参数一定要与提交参数保持一致
	 * @return
	 * @throws IOException 
	 * @throws IllegalStateException 
	 */
	@RequestMapping("/file")
	private String file(MultipartFile file) throws IllegalStateException, IOException{
		
		//准备文件上传的路径
		String path = "E:/jt-upload";
		
		//判断文件夹是否存在
		File filePath = new File(path);
		
		if(!filePath.exists()){
			//如果文件夹不存在,需要创建一个文件夹
			filePath.mkdirs();
		}
		//获取文件名称  abc.jpg
		String fileName = file.getOriginalFilename();
		  
		//实现文件上传   E:/jt-upload/
		file.transferTo(new File(path+"/"+fileName));
		
		return "redirect:/file.jsp";
	}
	
	
	//实现商品的文件上传
	@RequestMapping("/pic/upload")
	@ResponseBody    //{}  get方法
	public PicUploadResult uploadFile(MultipartFile uploadFile){
		
		return fileService.upload(uploadFile);
	}
	
	
	
	
	
	
}
