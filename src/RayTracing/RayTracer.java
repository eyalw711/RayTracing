package RayTracing;

import java.awt.Transparency;
import java.awt.color.*;
import java.awt.image.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import java.util.concurrent.TimeUnit;

/**
 *  Main class for ray tracing exercise.
 */
public class RayTracer 
{

	public int imageWidth;
	public int imageHeight;

	public static void main(String[] args)
	{
		System.out.println("Working Directory = " + System.getProperty("user.dir"));
		//primitiveTest();
		main_finalMain(args); 
	}
	
	public static void primitiveTest()
	{
		Sphere s = new Sphere(null, new Vector(5,5,5), 1);
		Ray r = new Ray(new Vector(0,0,0), new Vector(1,1,1));
		Triangle tr = new Triangle(null, new Vector(-2,0,0), new Vector(0,-2,0), new Vector(1,1,10));
		try
		{
			Vector intersection = tr.closerIntersectionPoint(r);
			System.out.println("intersention of " + tr + " and " + r + " is " + intersection);			
		}
		catch (RayTracerException e)
		{
			System.out.println("Failed");
		}
	}
	
	public static void vectorTest(String[] args)
	{
		Vector u = new Vector(3, 4, 6);
		Vector v = new Vector(7, 0, -4);
		System.out.println( "u is " + u + " and v is " + v);
		System.out.println( "u magn is " + u.magnitude() + " and v magn is " + v.magnitude());
		
		Vector n = u.crossProduct(v);
		System.out.println("Cross product is " + n);
		
		double prd = u.dotProduct(v);
		System.out.println("Dot product is " + prd);
		
		u.normalize();
		v.normalize();
		System.out.println( "u is " + u + " and v is " + v);
		System.out.println( "u magn is " + u.magnitude() + " and v magn is " + v.magnitude());
	}
	
	public static void threadTest(String[] args)
	{
		long startTime = System.nanoTime();
		
		int numRows = 20;
		int numCols = 20;
		int numOfWorkers = 10;
		int[][] pixels = new int[numRows][numCols];
		ThreadPool rayTracingJobs = new ThreadPool(numOfWorkers, pixels);
		rayTracingJobs.render(numRows, numCols);
		
		System.out.printf("Made %d calculations, ~0.5 sec each. Elapsed Time = %,d msecs",
				numRows * numCols, TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime)); 
	}
	
	/**
	 * Runs the ray tracer. Takes scene file, output image file and image size as input.
	 */
	public static void main_finalMain(String[] args) 
	{
		try 
		{
			RayTracer tracer = new RayTracer();

            // Default values:
			tracer.imageWidth = 500;
			tracer.imageHeight = 500;

			if (args.length < 2)
				throw new RayTracerException("Not enough arguments provided. Please specify an input scene file and an output image file for rendering.");

			String sceneFileName = args[0];
			String outputFileName = args[1];

			if (args.length > 3)
			{
				tracer.imageWidth = Integer.parseInt(args[2]);
				tracer.imageHeight = Integer.parseInt(args[3]);
			}

			// Parse scene file:
			tracer.parseScene(sceneFileName);

			// Render scene:
			tracer.renderScene(outputFileName);

//		} catch (IOException e) {
//			System.out.println(e.getMessage());
		} 
		catch (RayTracerException e) 
		{
			System.out.println(e.getMessage());
		} 
		catch (Exception e) 
		{
			System.out.println(e.toString());
		}
	}

	/**
	 * Parses the scene file and creates the scene. Change this function so it generates the required objects.
	 */
	public void parseScene(String sceneFileName) throws IOException, RayTracerException
	{
		FileReader fr = new FileReader(sceneFileName);

		BufferedReader r = new BufferedReader(fr);
		String line = null;
		int lineNum = 0;
		System.out.println("Started parsing scene file " + sceneFileName);

		Set set;
		Camera cam;
		while ((line = r.readLine()) != null)
		{
			line = line.trim();
			++lineNum;

			if (line.isEmpty() || (line.charAt(0) == '#'))
			{  // This line in the scene file is a comment
				continue;
			}
			else
			{
				String code = line.substring(0, 3).toLowerCase();
				// Split according to white space characters:
				String[] params = line.substring(3).trim().toLowerCase().split("\\s+");

				if (code.equals("cam"))
				{
                    // Add code here to parse camera parameters
					cam = Camera.parseCamera(params);
					System.out.println(String.format("Parsed camera parameters (line %d) - " + cam, lineNum));
				}
				else if (code.equals("set"))
				{
                    // Add code here to parse general settings parameters
					set = Set.parseSet(params);
					System.out.println(String.format("Parsed general settings (line %d) - " + set, lineNum));
				}
				else if (code.equals("mtl"))
				{
                    // Add code here to parse material parameters

					System.out.println(String.format("Parsed material (line %d)", lineNum));
				}
				else if (code.equals("sph"))
				{
                    // Add code here to parse sphere parameters

                    // Example (you can implement this in many different ways!):
                    // Sphere sphere = new Sphere();
                    // sphere.setCenter(params[0], params[1], params[2]);
                    // sphere.setRadius(params[3]);
                    // sphere.setMaterial(params[4]);

					System.out.println(String.format("Parsed sphere (line %d)", lineNum));
				}
				else if (code.equals("pln"))
				{
                    // Add code here to parse plane parameters

					System.out.println(String.format("Parsed plane (line %d)", lineNum));
				}
				else if (code.equals("trg"))
				{
                    // Add code here to parse cylinder parameters

					System.out.println(String.format("Parsed cylinder (line %d)", lineNum));
				}
				else if (code.equals("lgt"))
				{
                    // Add code here to parse light parameters

					System.out.println(String.format("Parsed light (line %d)", lineNum));
				}
				else
				{
					System.out.println(String.format("ERROR: Did not recognize object: %s (line %d)", code, lineNum));
				}
			}
		}

        // It is recommended that you check here that the scene is valid,
        // for example camera settings and all necessary materials were defined.

		System.out.println("Finished parsing scene file " + sceneFileName);
	}

	/**
	 * Renders the loaded scene and saves it to the specified file location.
	 */
	public void renderScene(String outputFileName)
	{
		long startTime = System.currentTimeMillis();

		// Create a byte array to hold the pixel data:
		byte[] rgbData = new byte[this.imageWidth * this.imageHeight * 3];


        // Put your ray tracing code here!
        //
        // Write pixel color values in RGB format to rgbData:
        // Pixel [x, y] red component is in rgbData[(y * this.imageWidth + x) * 3]
        //            green component is in rgbData[(y * this.imageWidth + x) * 3 + 1]
        //             blue component is in rgbData[(y * this.imageWidth + x) * 3 + 2]
        //
        // Each of the red, green and blue components should be a byte, i.e. 0-255


		long endTime = System.currentTimeMillis();
		Long renderTime = endTime - startTime;

	    // The time is measured for your own conveniece, rendering speed will not affect your score
		// unless it is exceptionally slow (more than a couple of minutes)
		System.out.println("Finished rendering scene in " + renderTime.toString() + " milliseconds.");

        // This is already implemented, and should work without adding any code.
		saveImage(this.imageWidth, rgbData, outputFileName);

		System.out.println("Saved file " + outputFileName);
	}

	//////////////////////// FUNCTIONS TO SAVE IMAGES IN PNG FORMAT //////////////////////////////////////////

	/*
	 * Saves RGB data as an image in png format to the specified location.
	 */
	public static void saveImage(int width, byte[] rgbData, String fileName)
	{
		try {

			BufferedImage image = bytes2RGB(width, rgbData);
			ImageIO.write(image, "png", new File(fileName));

		} catch (IOException e) {
			System.out.println("ERROR SAVING FILE: " + e.getMessage());
		}

	}

	/*
	 * Producing a BufferedImage that can be saved as png from a byte array of RGB values.
	 */
	public static BufferedImage bytes2RGB(int width, byte[] buffer) {
	    int height = buffer.length / width / 3;
	    ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_sRGB);
	    ColorModel cm = new ComponentColorModel(cs, false, false,
	            Transparency.OPAQUE, DataBuffer.TYPE_BYTE);
	    SampleModel sm = cm.createCompatibleSampleModel(width, height);
	    DataBufferByte db = new DataBufferByte(buffer, width * height);
	    WritableRaster raster = Raster.createWritableRaster(sm, db, null);
	    BufferedImage result = new BufferedImage(cm, raster, false, null);

	    return result;
	}

	public static class RayTracerException extends Exception {
		public RayTracerException(String msg) {  super(msg); }
	}
}
