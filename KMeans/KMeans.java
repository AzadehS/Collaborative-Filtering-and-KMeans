
/**
 *
 * @author azadehsamadian
 */
 
import java.awt.Graphics2D;
import java.awt.image.BufferedImage; 
import java.io.File; 
import java.util.Arrays; 
import javax.imageio.ImageIO; 
public class KMeans { 
    BufferedImage original; 
    BufferedImage result; 
    Cluster[] clusters;     
     
    public static void main(String[] args) { 
        
        String src = args[0];
        
        String dst = args[2];
        int k = Integer.parseInt(args[1]);


        KMeans kmeans = new KMeans(); 
        BufferedImage dstImage = kmeans.kmeans_helper(loadImage(src),k); 
        saveImage(dst, dstImage);

    }      
    public KMeans() {  
    	
    } 
    public BufferedImage kmeans_helper(BufferedImage originalImage, int k)
    {
		int w=originalImage.getWidth();
		int h=originalImage.getHeight();
		BufferedImage kmeansImage = new BufferedImage(w,h,originalImage.getType());
		Graphics2D g = kmeansImage.createGraphics();
		g.drawImage(originalImage, 0, 0, w,h , null);
		int[] rgb=new int[w*h];
		int count=0;
		for(int i=0;i<w;i++){
		    for(int j=0;j<h;j++){
			rgb[count++]=kmeansImage.getRGB(i,j);
		    }
		}
		kmeansImage = kmeans(originalImage,k);
		return kmeansImage;
    }
     
    public BufferedImage kmeans(BufferedImage image, int k) { 
        long start = System.currentTimeMillis(); 
        int w = image.getWidth(); 
        System.out.println(w);
        
        int h = image.getHeight(); 
        clusters = createClusters(image,k); 
        int[] rgb1 = new int[w*h]; 
        Arrays.fill(rgb1, -1); 
        boolean pixelChangedCluster = true; 
        int loops = 0; 
        while (pixelChangedCluster) { 
            pixelChangedCluster = false; 
            loops++; 
            for (int y=0;y<h;y++) { 
                for (int x=0;x<w;x++) { 
                    int pixel = image.getRGB(x, y); 
                    Cluster cluster = findMinimalCluster(pixel); 
                    if (rgb1[w*y+x]!=cluster.getId()) { 
                            if (rgb1[w*y+x]!=-1) { 
                                clusters[rgb1[w*y+x]].removePixel( 
                                                            pixel); 
                            } 
                            cluster.addPixel(pixel);
                        pixelChangedCluster = true; 
                        rgb1[w*y+x] = cluster.getId(); 
                    } 
                } 
            } 
        } 
        BufferedImage result = new BufferedImage(w, h,  
                                    BufferedImage.TYPE_INT_RGB); 
        for (int y=0;y<h;y++) { 
            for (int x=0;x<w;x++) { 
                int clusterId = rgb1[w*y+x]; 
                result.setRGB(x, y, clusters[clusterId].getRGB()); 
            } 
        } 
        long end = System.currentTimeMillis(); 
        System.out.println("Clustered to "+k); 
        return result; 
    } 
     
    public Cluster[] createClusters(BufferedImage image, int k) { 
        Cluster[] result = new Cluster[k]; 
        int x = 0; int y = 0; 
        int dx = image.getWidth()/k; 
        int dy = image.getHeight()/k; 
        for (int i=0;i<k;i++) { 
            result[i] = new Cluster(i,image.getRGB(x, y)); 
            x+=dx; y+=dy; 
        } 
        return result; 
    } 
     
    public Cluster findMinimalCluster(int rgb) { 
        Cluster cluster = null; 
        int min = Integer.MAX_VALUE; 
        for (int i=0;i<clusters.length;i++) { 
            int distance = clusters[i].distance(rgb); 
            if (distance<min) { 
                min = distance; 
                cluster = clusters[i]; 
            } 
        } 
        return cluster; 
    } 
     
    public static void saveImage(String filename,  
            BufferedImage image) { 
        File file = new File(filename); 
        try { 
            ImageIO.write(image, "png", file); 
        } catch (Exception e) { 
            System.out.println(e.toString()+" Image '"+filename 
                                +"' saving failed."); 
        } 
    } 
     
    public static BufferedImage loadImage(String filename) { 
        BufferedImage result = null; 
        try { 
            result = ImageIO.read(new File(filename)); 
        } catch (Exception e) { 
            System.out.println(e.toString()+" Image '" 
                                +filename+"' not found."); 
        } 
        return result; 
    } 
     
    class Cluster { 
        int id; 
        int pixelCount; 
        int red; 
        int green; 
        int blue; 
        int reds; 
        int greens; 
        int blues; 
         
        public Cluster(int id, int rgb) 
        {         	
            int r = rgb >>16&0x000000FF;  
            
            int g = rgb >> 8&0x000000FF;
        
            int b = rgb >> 0&0x000000FF;  
        
            red = r; 
            green = g; 
            blue = b; 
            this.id = id; 
            addPixel(rgb); 
        } 
         
        public void clear() { 
            red = 0; 
            green = 0; 
            blue = 0; 
            reds = 0; 
            greens = 0; 
            blues = 0; 
            pixelCount = 0; 
        } 
         
        int getId() { 
            return id; 
        } 
         
        int getRGB() { 
            int r = reds / pixelCount; 
            int g = greens / pixelCount; 
            int b = blues / pixelCount; 
            return 0xff000000|r<<16|g<<8|b; 
        } 
        void addPixel(int color) { 
            int r = color>>16&0x000000FF;  
            int g = color>> 8&0x000000FF;  
            int b = color>> 0&0x000000FF;  
            reds+=r; 
            greens+=g; 
            blues+=b; 
            pixelCount++; 
            red   = reds/pixelCount; 
            green = greens/pixelCount; 
            blue  = blues/pixelCount; 
        } 
         
        void removePixel(int color) { 
            int r = color>>16&0x000000FF;  
            int g = color>> 8&0x000000FF;  
            int b = color>> 0&0x000000FF;  
            reds-=r; 
            greens-=g; 
            blues-=b; 
            pixelCount--; 
            red   = reds/pixelCount; 
            green = greens/pixelCount; 
            blue  = blues/pixelCount; 
        } 
         
        int distance(int color) { 
            int r = color>>16&0x000000FF;  
            int g = color>> 8&0x000000FF;  
            int b = color>> 0&0x000000FF;  
            int rx = Math.abs(red-r); 
            int gx = Math.abs(green-g); 
            int bx = Math.abs(blue-b); 
            int d = (rx+gx+bx) / 3; 
            return d; 
        } 
    } 
     
} 

