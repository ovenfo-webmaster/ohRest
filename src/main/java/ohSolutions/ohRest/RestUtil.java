package ohSolutions.ohRest;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.Base64;

import javax.imageio.ImageIO;

import sun.misc.BASE64Decoder;

public class RestUtil {
	
	public String imagenToString(BufferedImage image) throws IOException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		ImageIO.write(image, "jpg", os);
		return Base64.getEncoder().encodeToString(os.toByteArray());
	}
	
	public String fileToString(String fileURL) throws IOException {
        File file = new File(fileURL);
        byte[] b = new byte[(int) file.length()];
        try {
              FileInputStream fileInputStream = new FileInputStream(file);
              fileInputStream.read(b);
              fileInputStream.close();
         } catch (FileNotFoundException e) {
        	 e.printStackTrace();
        	 return null;
         }
         catch (IOException e1) {
        	 e1.printStackTrace();
         }
        return Base64.getEncoder().encodeToString(b);
	}
	
	public BufferedImage getImage(Object source) throws IOException {
		if(source != null) {
			return getImage((String) source);
		}
		return null;
	}
	
	public BufferedImage getImage(String source) throws IOException {
		BufferedImage image = null;
		byte[] imageByte;
		
		BASE64Decoder decoder = new BASE64Decoder();
		imageByte = decoder.decodeBuffer(source);
		ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
		image = ImageIO.read(bis);
		bis.close();
		
		return image;
	}
	
	public BufferedImage resizeImage(BufferedImage originalImage,  int scaledWidth, int scaledHeight){
        return resizeImage(originalImage, scaledWidth, scaledHeight, true, true);
    }
	
	public BufferedImage resizeImage(BufferedImage originalImage,  int scaledWidth, int scaledHeight, boolean preserveAlpha, boolean scale){
		
		if(scale) {
			int originalWidth = originalImage.getWidth();
			int originalHeight = originalImage.getHeight();
			if(originalWidth > originalHeight) { // use scaledWidth as mayor
				scaledHeight = (scaledWidth * originalHeight) / originalWidth;
			} else if (originalWidth == originalHeight) {
				scaledHeight = scaledWidth;
			} else {
				scaledWidth = (scaledHeight*originalWidth) / originalHeight;
			}
		}
		
        int imageType = preserveAlpha ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
        BufferedImage scaledBI = new BufferedImage(scaledWidth, scaledHeight, imageType);
        Graphics2D g = scaledBI.createGraphics();
        if (preserveAlpha) {
            g.setComposite(AlphaComposite.Src);
        }
        g.drawImage(originalImage, 0, 0, scaledWidth, scaledHeight, null); 
        g.dispose();
        
        return scaledBI;
        
    }
	
	public String GetHashMD5(String password) { // Transform to HASH separate with lines like : A1-2B-....
		try {
		    MessageDigest md = MessageDigest.getInstance("MD5");
		    byte[] array = md.digest(password.getBytes("UTF-16LE"));
		    StringBuilder sb = new StringBuilder();
		    for (byte b : array) {
		        sb.append(String.format("%02X", b));
		    }
		    String hexad = sb.toString();
		    int total = hexad.length() / 2;
		    String[] parts = new String[total];
		    for(int i = 0; i < total; i++) {
		    	parts[i] = hexad.substring(i*2, i*2+2);
		    }
		    return String.join("-", parts);
		} catch (Exception e) {
		    e.printStackTrace();
		}
		return "";
	}
	
}