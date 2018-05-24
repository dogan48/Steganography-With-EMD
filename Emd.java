
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * @author Hadi Dogan KOCABIYIK
 *
 */

public class Emd {
	private BufferedImage 	coverImage;
	int 					coverMatrix[][];
	private BufferedImage 	secretImage;
	private String 			stegoImagePath;
	int 					stegoImageMatrix[][]; 		
	private final int 		BASE5BLOCKSIZE	= 4;
	private final int 		BASE7BLOCKSIZE	= 3;
	private final int 		COLORCHANNEL 	= 3;

	public Emd() {
		 coverImage 		= null;
		 secretImage 		= null;
		 stegoImagePath 	= null;
	}
	
	public Emd( String coverImagePath, String secretImagePath, String stegoImagePath) {
		 this.stegoImagePath 	= stegoImagePath;
		 this.coverImage 		= readImage(coverImagePath);
		 coverMatrix			= bufferedImageToMatrix(coverImage);
		 stegoImageMatrix		= coverMatrix;
		 
		 
		 if(secretImagePath != null) { this.secretImage	= readImage(secretImagePath); }
		 
	}
	
	public BufferedImage readImage(String path) {
		BufferedImage img = null;
		try {
			img = ImageIO.read(new File(path));
		} catch (IOException e) {
			System.out.println("Image can not read from file!");
			System.exit(1);
			//e.printStackTrace();
		}
		return img;
	}
	
	public void writeImage(BufferedImage img) {
		try {
			ImageIO.write(img, "BMP", new File(stegoImagePath));
		} catch (IOException e) {
			e.printStackTrace();
		}
		//System.out.println("PSNR : "+ calculatePSNR(bufferedImageToMatrix(coverImage),bufferedImageToMatrix(readImage(stegoImagePath)) ));
	}
		
	public int getPixel(BufferedImage img, int x, int y) {
		return img.getRGB(x, y);
	}
	
	public void setPixel(BufferedImage img, int satir, int sutun, int r, int g, int b) {
		Color c = new Color(r, g, b);
		img.setRGB(sutun, satir, c.getRGB());
	}
	
	public int getRed(BufferedImage img, int satir, int sutun) {
		Color c = new Color(img.getRGB(sutun, satir));
		return c.getRed();
	}
	
	public int getGreen(BufferedImage img, int satir, int sutun) {
		Color c = new Color(img.getRGB(sutun, satir));
		return c.getGreen();
	}
	
	public int getBlue(BufferedImage img, int satir, int sutun) {
		Color c = new Color(img.getRGB(sutun, satir));
		return c.getBlue();
	}
	
	public String decimalTo5Base(int decimal) {
		String base5 = "";
		int temp = 0;
		while(decimal > 0) {
			temp = decimal % 5;
			decimal = decimal / 5;
			base5 = temp + base5;			
		}
		for(int i = base5.length(); i < BASE5BLOCKSIZE; i++)
			base5 = "0" + base5;
		return base5;
	}
	
	public int base5ToDecimal(String base5) {
		int decimal = 0;
		String temp;
		for(int i = 0; i < base5.length(); i++) {
			temp = base5.substring(base5.length() - i - 1, base5.length() - i);
			decimal += Math.pow(5, i) * Integer.parseInt(temp);
		}
		return decimal;
	}
	
	public String decimalTo7Base(int decimal) {
		String base7 = "";
		int temp = 0;
		while(decimal > 0) {
			temp = decimal % 7;
			decimal = decimal / 7;
			base7 = temp + base7;			
		}
		for(int i = base7.length(); i < BASE7BLOCKSIZE; i++)
			base7 = "0" + base7;
		return base7;
	}
	
	public int base7ToDecimal(String base7) {
		int decimal = 0;
		String temp;
		for(int i = 0; i < base7.length(); i++) {
			temp = base7.substring(base7.length() - i - 1, base7.length() - i);
			decimal += Math.pow(7, i) * Integer.parseInt(temp);
		}
		return decimal;
	}
	
	public BufferedImage matrixToBufferedImage(int[][]a) {
		BufferedImage img = new BufferedImage(a[0].length / COLORCHANNEL, a.length, BufferedImage.TYPE_INT_RGB);
		for(int i = 0; i < a.length; i++)
			for(int j = 0; j < a[0].length; j += 3) {
				setPixel(img, i, j/COLORCHANNEL , a[i][j], a[i][j+1], a[i][j+2]);//Red:(i,j) Green:(i,j+1) Blue:(i,j+2)
			}
		return img;
	}
	
	public int[][] bufferedImageToMatrix(BufferedImage img) {
		int width	= img.getWidth();
		int height	= img.getHeight();
		int a[][] 	= new int[height][(width * COLORCHANNEL)];
		int k 		= 0;

		for(int i = 0; i < height; i++) {
			for(int j = 0; j < width * COLORCHANNEL; j++) {
				switch (j % COLORCHANNEL)
				{
					case 0:
						a[i][j] = getRed(img, i, k);
						break;
					case 1:
						a[i][j] = getGreen(img, i, k);
						break;
					case 2:
						a[i][j] = getBlue(img, i, k);
						k++;
						break;			
				}
			}
			k=0;
		}			
		return a;
	}
	
	public double calculatePSNR(int [][]a, int [][]b ) {
		double MSE 	= 0, temp = 0, PSNR = 0;
		int m 		= a.length;
		int n 		= a[0].length;
		
		for (int i = 0; i < m-1; i++) {
			for(int j = 0; j < n-1; j++) {
				temp = a[i][j] - b[i][j];
				MSE += temp * temp;
			}
		}
		MSE = MSE / (m * n * 3); // Because of RGB
		PSNR = (20 * Math.log10(255)) - ( 10 * Math.log10(MSE));
		return PSNR;
	}
	
	public int[] encodePixel(int x, int y, int z, int secretValue) {
		
		int b[] 		= new int[3];
		int diffrence 	= (((x + (2 * y) + (3 * z)) % 7) - secretValue);

		switch (diffrence)
		{
			case 0:
				break;
			case -1: case 6: //increase 1 or 6 decrease
				if(x < 255) { x++; }  else if(y < 255)  { x--; y++; } else if(z < 255) { z++; y--; } else { x--; y--; z--; }
				break;
			case -2: case 5: // increase 2 or decrease 5
				if(y < 255) { y++; }  else if(z < 255 && x > 0) { z++; x--; } else if (z > 0) { y--; z--; }else {x += 2; }
				break;
			case -3: case 4://increase 3 or decrease 4
				if(z < 255) { z++; }  else if(x < 255 && y < 255) { x++; y++; } else if(x > 0) { x--; z--; } else { y -= 2; }
				break;
			case -4: case 3: //decrease 3 or increase 4 
				if(z > 0) { z--; } else if(y > 0 && x > 0) { x--; y--; }  else if(x < 255){ x++; z++; } else { y += 2; }
				break;
			case -5: case 2: //decrease 2 or increase 5
				if(y > 0) { y--; } else if(z > 0 && x < 255) { z--; x++; } else if (z < 255){ y++; z++; } else { x -=2; }
				break;
			case -6: case 1: //decrease 1 or increase 6
				if(x > 0) { x--; } else if(y > 0) { x++; y--; } else if(z > 0) { z--; y++; } else { x++; y++; z++; }
				break;
		}

		b[0] = x;
		b[1] = y;
		b[2] = z;
		return b;
	}
	
	public int[] encodePixel(int x, int y, int secretValue) {
		int b[]	 		= new int[2];
		int diffrence 	= (((x + 2 * y) % 5) - secretValue);

		switch (diffrence)
		{
			case 0:
				break;
			case 1: case -4://decrease 1 or increase 4
				if(x > 0) { x--; } else if(y > 0) { x++; y--; } else { y += 2; }
				break;
			case 2: case -3://decrease 2 or increase 3
				if(y > 0) { y--; } else if(x < 255) { x++; y++; } else { x -=2; }
				break;
			case 3: case -2://increase 2 or decrease 3
				if(y < 255) { y++; } else if(x > 0) { x--; y--; }  else { x += 2; }
				break;	
			case 4: case -1://increase 1 or decrease 4
				if(x < 255) { x++; }  else if(y < 255) { y++; x--; } else { y -= 2; }
				break;
		}
		b[0] = x;
		b[1] = y;
		return b;
	}
	
	public void decideMethod(){
		int coverImagePixelValues		= coverImage.getHeight() * coverImage.getWidth() * COLORCHANNEL;
		int secretImagePixelValues		= secretImage.getHeight() * secretImage.getWidth() * COLORCHANNEL;
		int secretImageNeedAreaForBase5 = (secretImagePixelValues * 8) + 2 + 32;	//2 for BlokSize, 32 for width height  
		int secretImageNeedAreaForBase7 = (secretImagePixelValues * 9) + 2 + 36;	//2 for BlokSize, 36 for width height  
		
		if(coverImage.getHeight() < 50)
			System.out.println("Cover image is so small.");
		else if(secretImageNeedAreaForBase7 < coverImagePixelValues)
			encodeImageBase7();
		else if (secretImageNeedAreaForBase5 < coverImagePixelValues)
			encodeImageBase5();
		else
			System.out.println("Secret image does not fit in cover image");

	}
	
	public void encodeImage() {
		 decideMethod();		
	}

	public void encodeImageBase5() {
		int orgImgPixX				= 0;
		int orgImgPixY				= 0;
		int emdX 					= 0;
		int emdY					= 0;
		String temp 				= "";
		String colorValue			= null;
		int b[]						= null;
		boolean isLastPixel			= false;
	
		emdX = coverMatrix[orgImgPixY][orgImgPixX];
		emdY = coverMatrix[orgImgPixY][orgImgPixX+1];
		b = encodePixel(emdX,emdY,BASE5BLOCKSIZE);
		stegoImageMatrix[orgImgPixY][orgImgPixX] = b[0];
		stegoImageMatrix[orgImgPixY][orgImgPixX + 1] = b[1];
		orgImgPixX += 2;
		
		temp = decimalTo5Base(secretImage.getWidth());
		for(int i = temp.length() ; i < BASE5BLOCKSIZE * 2; i++)
			temp = "0" + temp;
		
		for(int i = 0; i < temp.length(); i++) 
		{
			emdX = coverMatrix[orgImgPixY][orgImgPixX];
			emdY = coverMatrix[orgImgPixY][orgImgPixX+1];
			b = encodePixel(emdX,emdY,Integer.parseInt(temp.substring(i, i+1)));
			stegoImageMatrix[orgImgPixY][orgImgPixX] = b[0];
			stegoImageMatrix[orgImgPixY][orgImgPixX + 1] = b[1];
			orgImgPixX += 2;
		}
		
		temp = decimalTo5Base(secretImage.getHeight());
		for(int i = temp.length() ; i < BASE5BLOCKSIZE * 2; i++)
			temp = "0" + temp;
		
		for(int i = 0; i < temp.length(); i++) 
		{
			emdX = coverMatrix[orgImgPixY][orgImgPixX];
			emdY = coverMatrix[orgImgPixY][orgImgPixX+1];
			b = encodePixel(emdX,emdY,Integer.parseInt(temp.substring(i, i+1)));
			stegoImageMatrix[orgImgPixY][orgImgPixX] = b[0];
			stegoImageMatrix[orgImgPixY][orgImgPixX + 1] = b[1];
			orgImgPixX += 2;
		}
		for(int i = 0; i < secretImage.getHeight(); i++) 
		{
			for(int j = 0; j < secretImage.getWidth(); j++)
			{
				for(int k = 0; k < COLORCHANNEL; k++) 
				{
					switch (k)
					{
						case 0:
							colorValue = decimalTo5Base(getRed(secretImage, i, j));
							break;
						case 1:
							colorValue = decimalTo5Base(getGreen(secretImage, i, j));
							break;
						case 2:
							colorValue = decimalTo5Base(getBlue(secretImage, i, j));
							break;
					}
					for(int l = 0; l < BASE5BLOCKSIZE; l++)
					{
						temp = colorValue.substring(l, l + 1);
						if(!isLastPixel) 
						{
							emdX = coverMatrix[orgImgPixY][orgImgPixX];
							emdY = coverMatrix[orgImgPixY][orgImgPixX+1];
							b = encodePixel(emdX,emdY,Integer.parseInt(temp));
							stegoImageMatrix[orgImgPixY][orgImgPixX] = b[0];
							stegoImageMatrix[orgImgPixY][orgImgPixX + 1] = b[1];
							orgImgPixX += 2;
							if(orgImgPixX == coverMatrix[0].length) 
							{
								orgImgPixX = 0;
								orgImgPixY++;
							} 
							else if(orgImgPixX == coverMatrix[0].length-1) 
							{
								isLastPixel = true;
							}
						}
						else
						{
							isLastPixel = false;
							emdX = coverMatrix[orgImgPixY][orgImgPixX];
							emdY = coverMatrix[++orgImgPixY][0];
							b = encodePixel(emdX,emdY,Integer.parseInt(temp));
							stegoImageMatrix[orgImgPixY-1][orgImgPixX] = b[0];
							stegoImageMatrix[orgImgPixY][0] = b[1];
							orgImgPixX=1;
						}
					}
							
				}
			}
		}
		writeImage(matrixToBufferedImage(stegoImageMatrix));
		System.out.println("Embedded with base 5.");
		System.out.printf("PSNR: %.4f db \n" , calculatePSNR(bufferedImageToMatrix(coverImage),stegoImageMatrix) );
	}
	 
	public void encodeImageBase7() {
		int orgImgPixX				= 0;
		int orgImgPixY				= 0;
		int emdX 					= 0;
		int emdY					= 0;
		int emdZ					= 0;
		String temp 				= "";
		String colorValue			= null;
		int b[]						= null;
		
		emdX = coverMatrix[orgImgPixY][orgImgPixX];
		emdY = coverMatrix[orgImgPixY][orgImgPixX+1];
		b = encodePixel(emdX,emdY,BASE7BLOCKSIZE);
		stegoImageMatrix[orgImgPixY][orgImgPixX] = b[0];
		stegoImageMatrix[orgImgPixY][orgImgPixX + 1] = b[1];
		orgImgPixX += 2;
		
		temp = decimalTo7Base(secretImage.getWidth());
		for(int i = temp.length() ; i < BASE7BLOCKSIZE * 2; i++)
			temp = "0" + temp;
		
		for(int i = 0; i < temp.length(); i++) {
			emdX = coverMatrix[orgImgPixY][orgImgPixX];
			emdY = coverMatrix[orgImgPixY][orgImgPixX+1];
			emdZ = coverMatrix[orgImgPixY][orgImgPixX+2];
			b = encodePixel(emdX, emdY,emdZ, Integer.parseInt(temp.substring(i, i+1)));
			stegoImageMatrix[orgImgPixY][orgImgPixX] = b[0];
			stegoImageMatrix[orgImgPixY][orgImgPixX + 1] = b[1];
			stegoImageMatrix[orgImgPixY][orgImgPixX + 2] = b[2];
			orgImgPixX += 3;
		}
		
		temp = decimalTo7Base(secretImage.getHeight());
		for(int i = temp.length() ; i < BASE7BLOCKSIZE * 2; i++)
			temp = "0" + temp;
		
		for(int i = 0; i < temp.length(); i++) {
			emdX = coverMatrix[orgImgPixY][orgImgPixX];
			emdY = coverMatrix[orgImgPixY][orgImgPixX+1];
			emdZ = coverMatrix[orgImgPixY][orgImgPixX+2];
			b = encodePixel(emdX, emdY, emdZ, Integer.parseInt(temp.substring(i, i+1)));
			stegoImageMatrix[orgImgPixY][orgImgPixX] = b[0];
			stegoImageMatrix[orgImgPixY][orgImgPixX + 1] = b[1];
			stegoImageMatrix[orgImgPixY][orgImgPixX + 2] = b[2];
			orgImgPixX += 3;
		}
		
		for(int i = 0; i < secretImage.getHeight(); i++) {
			for(int j = 0; j < secretImage.getWidth(); j++) {
				for(int k = 0; k < COLORCHANNEL; k++) {
					switch (k){
						case 0:
							colorValue = decimalTo7Base(getRed(secretImage, i, j));
							break;
						case 1:
							colorValue = decimalTo7Base(getGreen(secretImage, i, j));
							break;
						case 2:
							colorValue = decimalTo7Base(getBlue(secretImage, i, j));
							break;
					}
					for(int l = 0; l < BASE7BLOCKSIZE; l++) {
						temp = colorValue.substring(l, l + 1);
						
						if(orgImgPixX < coverMatrix[0].length - 2) {
							emdX = coverMatrix[orgImgPixY][orgImgPixX];
							emdY = coverMatrix[orgImgPixY][orgImgPixX+1];
							emdZ = coverMatrix[orgImgPixY][orgImgPixX+2];
							b = encodePixel(emdX,emdY,emdZ,Integer.parseInt(temp));
							stegoImageMatrix[orgImgPixY][orgImgPixX] = b[0];
							stegoImageMatrix[orgImgPixY][orgImgPixX + 1] = b[1];
							stegoImageMatrix[orgImgPixY][orgImgPixX + 2] = b[2];
							orgImgPixX += 3;
							if(orgImgPixX == coverMatrix[0].length) { orgImgPixX = 0; orgImgPixY++; }
							
						}
						else if(orgImgPixX == coverMatrix[0].length - 2) {
							emdX = coverMatrix[orgImgPixY][orgImgPixX];
							emdY = coverMatrix[orgImgPixY][orgImgPixX+1];
							emdZ = coverMatrix[orgImgPixY+1][0];
							b = encodePixel(emdX,emdY,emdZ,Integer.parseInt(temp));
							stegoImageMatrix[orgImgPixY][orgImgPixX] = b[0];
							stegoImageMatrix[orgImgPixY][orgImgPixX + 1] = b[1];
							stegoImageMatrix[orgImgPixY+1][0] = b[2];
							orgImgPixX = 1;
							orgImgPixY++;
							
						}
						else if(orgImgPixX == coverMatrix[0].length - 1) {
							emdX = coverMatrix[orgImgPixY][orgImgPixX];
							emdY = coverMatrix[orgImgPixY+1][0];
							emdZ = coverMatrix[orgImgPixY+1][1];
							b = encodePixel(emdX,emdY,emdZ,Integer.parseInt(temp));
							stegoImageMatrix[orgImgPixY][orgImgPixX] = b[0];
							stegoImageMatrix[orgImgPixY+1][0] = b[1];
							stegoImageMatrix[orgImgPixY+1][1] = b[2];
							orgImgPixX = 2;
							orgImgPixY++;
						}
					}
				}
			}
		}
			
		writeImage(matrixToBufferedImage(stegoImageMatrix));
		System.out.println("Embedded with base 7.");
		System.out.printf("PSNR: %.4f db \n" , calculatePSNR(bufferedImageToMatrix(coverImage),stegoImageMatrix) );
	}
	
	public void decodeImage() {
		int blockSize = (coverMatrix[0][0] + (2 * coverMatrix[0][1])) % 5;
		if( blockSize == BASE5BLOCKSIZE )			decodeImageBase5();
		else if ( blockSize == BASE7BLOCKSIZE )		decodeImageBase7();
		else System.out.println("Unknown stegonography method.");
		}
	
	public void decodeImageBase5() {
		int secretMatrix[][]	= null;
		int coverImageX			= 2; // start after block size. First 2 value for block size
		int coverImageY			= 0;
		int secretImageWidth 	= 0;
		int secretImageHeight	= 0;
		String temp 			= "";

		for(int i = 0; i < BASE5BLOCKSIZE * 2; i++,coverImageX+=2)  //secret image 8bit width value for base 5 
			temp = temp + (coverMatrix[0][coverImageX]+ 2 * coverMatrix[0][coverImageX+1]) % 5 ;	//X+2Y %5
		secretImageWidth = base5ToDecimal(temp);
		temp = "";
		
		for(int i = 0; i < BASE5BLOCKSIZE * 2; i++,coverImageX+=2)  //secret image 8bit height value for base 5 
			temp = temp + (coverMatrix[0][coverImageX]+ 2 * coverMatrix[0][coverImageX+1]) % 5 ;
		secretImageHeight = base5ToDecimal(temp);
		temp="";
		
		secretMatrix = new int[secretImageHeight][secretImageWidth * COLORCHANNEL];
		
		for(int i = 0; i < secretImageHeight; i++) 
		{
			for(int j = 0; j < secretImageWidth * COLORCHANNEL; j++) 
			{
				for(int k = 0; k < BASE5BLOCKSIZE; k++ ) 
				{
					if(coverImageX < coverMatrix[0].length - 1 ) {
						temp = temp + ( coverMatrix[coverImageY][coverImageX] + 2 * coverMatrix[coverImageY][coverImageX+1]) % 5 ;
						coverImageX += 2;
						if(coverImageX == coverMatrix[0].length) { coverImageX = 0; coverImageY++; }
					}
					else if(coverImageX == coverMatrix[0].length -1 ) {
						temp = temp + ( coverMatrix[coverImageY][coverImageX] + 2 * coverMatrix[++coverImageY][0]) % 5 ;
						coverImageX = 1;
					}
					
				}
				secretMatrix[i][j] = base5ToDecimal(temp);
				temp = "";
			}
		}
		
		writeImage(matrixToBufferedImage(secretMatrix));
		System.err.println("Extracted with base 5.");
	}

	public void decodeImageBase7() {
		int secretMatrix[][]	= null;
		int coverImageX			= 2; // start after block size. First 2 value for block size
		int coverImageY			= 0;
		int secretImageWidth 	= 0;
		int secretImageHeight	= 0;
		String temp 			= "";
		
		for(int i = 0; i < BASE7BLOCKSIZE * 2; i++, coverImageX += 3)  //secret image 8bit width value for base 5 
			temp = temp + (coverMatrix[0][coverImageX] + 2 * coverMatrix[0][coverImageX+1] + 3 * coverMatrix[0][coverImageX + 2] ) % 7 ;	//X+2Y %5
		secretImageWidth = base7ToDecimal(temp);
		temp = "";
		
		for(int i = 0; i < BASE7BLOCKSIZE * 2; i++, coverImageX += 3)  //secret image 8bit height value for base 5 
			temp = temp + (coverMatrix[0][coverImageX]+ 2 * coverMatrix[0][coverImageX+1] + 3 * coverMatrix[0][coverImageX + 2] ) % 7 ;
		secretImageHeight = base7ToDecimal(temp);
		temp="";
		
		secretMatrix = new int[secretImageHeight][secretImageWidth * COLORCHANNEL];
		
		for(int i = 0; i < secretImageHeight; i++) 
		{
			for(int j = 0; j < secretImageWidth * COLORCHANNEL; j++) 
			{
				for(int k = 0; k < BASE7BLOCKSIZE; k++ ) 
				{
					if(coverImageX < coverMatrix[0].length - 2 ) {
						temp = temp + ( coverMatrix[coverImageY][coverImageX] + 2 * coverMatrix[coverImageY][coverImageX+1] + 3 * coverMatrix[coverImageY][coverImageX+2]) % 7 ;
						coverImageX += 3;
						if(coverImageX == coverMatrix[0].length) { coverImageX = 0; coverImageY++; }
					}
					else if(coverImageX == coverMatrix[0].length -2 ) {
						temp = temp + ( coverMatrix[coverImageY][coverImageX] + 2 * coverMatrix[coverImageY][coverImageX+1]  + 3 * coverMatrix[++coverImageY][0] ) % 7 ;
						coverImageX = 1;
					}
					else if(coverImageX == coverMatrix[0].length -1 ) {
						temp = temp + ( coverMatrix[coverImageY][coverImageX] + 2 * coverMatrix[++coverImageY][0]  + 3 * coverMatrix[coverImageY][1] ) % 7 ;
						coverImageX = 2;
					}
				}
				secretMatrix[i][j] = base7ToDecimal(temp);
				temp = "";
			}
		}
		writeImage(matrixToBufferedImage(secretMatrix));
		System.err.println("Extracted with base 7.");
	}
}