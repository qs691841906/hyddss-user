//package com.sinosoft.ddss.controller;
//
//import java.awt.image.BufferedImage;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.OutputStream;
//
//import javax.imageio.ImageIO;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.sinosoft.ddss.common.entity.User;
//import com.sinosoft.ddss.jedis.JedisClient;
//import com.sinosoft.ddss.utils.VerifyCode;
//@RestController
//public class VerificationCode {
//	
//	@Autowired
//	private JedisClient jedisClient;
//	
//	 
//	/**
//	 * 获取验证码图片
//	 * @param response
//	 * @param request
//	 * @param user
//	 */
//	@RequestMapping(method = { RequestMethod.POST}, value = "/oauth/user/getVerificationCode")
//	 
//	         public void getVerificationCode(HttpServletResponse response,HttpServletRequest request,User user) {
//	 
//	                   try {
//	 
//	                            int width=200;
//	 
//	                            int height=69;
//	 
//	         BufferedImage verifyImg=new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
//	 
//	//生成对应宽高的初始图片
//	 
//	                            String randomText = VerifyCode.drawRandomText(width,height,verifyImg);
//	 
//	//单独的一个类方法，出于代码复用考虑，进行了封装。
//	 
//	//功能是生成验证码字符并加上噪点，干扰线，返回值为验证码字符                   
//	 
////	                            “用户名_code”组成验证码在redis中的key
//	                            String userName = user.getUserName();
//	                            String key = userName+"_code";
//	                            
//	                            randomText =  randomText.toUpperCase();
//	                            
//	                            jedisClient.set(key, randomText);
//	                            
//	                   response.setContentType("image/png");//必须设置响应内容类型为图片，否则前台不识别
//	 
//	                 OutputStream os = response.getOutputStream(); //获取文件输出流    
//	                 
//	 
//	                 ImageIO.write(verifyImg,"png",os);//输出图片流
//	 
//	                 os.flush();
//	 
//	                 os.close();//关闭流
//	 
//	                   } catch (IOException e) {
//	 
////	                            this.logger.error(e.getMessage());
//	 
//	                            e.printStackTrace();
//	 
//	                   }
//	 
//	         }
////	         public static void main(String[] args) {
////	        	 int width=200;
////	        	 
////                 int height=69;
////
////                 BufferedImage verifyImg=new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
////                 String randomText = VerifyCode.drawRandomText(width,height,verifyImg);
////                 System.out.println(randomText);
////                 
////                 try{
////                 OutputStream os = new FileOutputStream("D://test/test.png");
////            	 
////                 ImageIO.write(verifyImg,"png",os);//输出图片流
//// 
////                 os.flush();
//// 
////                 os.close();//关闭流
//// 
////                   } catch (IOException e) {
//// 
//////                            this.logger.error(e.getMessage());
//// 
////                            e.printStackTrace();
//// 
////                   }
////                 
////                 
////			}
//	
//}
//
