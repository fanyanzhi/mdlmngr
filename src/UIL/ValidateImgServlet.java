package UIL;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import BLL.Logger;

/**
 * Servlet implementation class ValidateImgServlet
 */
@WebServlet("/ValidateImg")
public class ValidateImgServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private Random random = new Random();
	private int width = 108;// 图片宽
	private int height = 31;// 图片高
	private int lineSize = 2;// 干扰线数量
	private int stringNum = 4;// 随机产生字符数量

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ValidateImgServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//HttpSession session = request.getSession();
//		if (session.getAttribute("sessionid") == null || !session.getId().equals(session.getAttribute("sessionid").toString())) {
//			return;
//		}
		response.setContentType("image/JPEG");
		response.setHeader("Pragma", "No-cache");// 设置响应头信息，告诉浏览器不要缓存此内容
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expire", 0);
		getRandcode(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * * 生成随机图片
	 * */
	public void getRandcode(HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession();
		// BufferedImage类是具有缓冲区的Image类,Image类是用于描述图像信息的类
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);
		Graphics g = image.getGraphics();// 产生Image对象的Graphics对象,改对象可以在图像上进行各种绘制操作
		//g.setColor(new Color(Integer.valueOf("da", 16), Integer.valueOf("e4", 16), Integer.valueOf("ee", 16)));
		//g.setColor(Color.black);
		g.drawRect(0, 0, width - 1, height - 1);
		//g.setColor(new Color(Integer.valueOf("f0", 16), Integer.valueOf("f3", 16), Integer.valueOf("f8", 16)));
		g.setColor(Color.black);
		g.fillRect(1, 1, width - 2, height - 2);
		// 绘制干扰线
		for (int i = 0; i <= lineSize; i++) {
			drowLine(g);
		}
		// 绘制随机字符
		String randomString = "";
		for (int i = 0; i < stringNum; i++) {
			char cValidate;
			cValidate = (char)(48 + random.nextInt(10));
			drawString(g, Character.toString(cValidate), i);
			randomString = randomString.concat(Character.toString(cValidate));
		}
		session.removeAttribute("ValidateCode");
		session.setAttribute("ValidateCode", randomString);
		g.dispose();
		try {
			response.setContentType("image/png");
			ImageIO.write(image, "png", response.getOutputStream());// 将内存中的图片通过流动形式输出到客户端
			response.getOutputStream().close();
		} catch (Exception e) {
			Logger.WriteException(e);
		}
	}

	private void drawString(Graphics g, String randomString, int i) {
		g.setFont(getFont());
		//g.setColor(new Color(160,160 ,160 ));
		g.setColor(new Color(Integer.valueOf("c9", 16),Integer.valueOf("c9", 16) ,Integer.valueOf("c9", 16) ));
		g.translate(random.nextInt(3), random.nextInt(3));
		g.drawString(randomString, i * 21 + 14 + random.nextInt(3), 17 + random.nextInt(3));
	}

	private void drowLine(Graphics g) {
		g.setColor(new Color(Integer.valueOf("b7", 16), Integer.valueOf("bd", 16), Integer.valueOf("c7", 16)));
		g.drawLine(random.nextInt(40), random.nextInt(height), 60+random.nextInt(35), random.nextInt(height) + random.nextInt(15));
	}

	/* *
	 * 获得字体
	 */
	private Font getFont() {
		String[] strFonts = new String[] {"Helvetica,","Geneva", "sans-serif", "Arial"};
		return new Font(strFonts[random.nextInt(strFonts.length - 1)], Font.PLAIN, 20);

	}
}
