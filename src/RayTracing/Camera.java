package RayTracing;

public class Camera {
	
	private Vector position;
	private Vector look_at_point;
	private Vector up_vector;
	
	private double screen_distance;
	private double screen_width;
	private double screen_height;
	
	private int numOfRows;
	private int numOfCols;
	
	public Camera(Vector position, Vector look_at, Vector up_vector,
			double scrn_width, double scrn_distance, int pw, int ph)
	{
		this.numOfCols = pw;
		this.numOfRows = ph;
		this.position = position;
		this.look_at_point = look_at;
		this.up_vector = up_vector;
		this.screen_distance = scrn_distance;
		this.screen_width = scrn_width;

		this.screen_height = this.numOfRows * (this.screen_width / this.numOfCols) ;
	}
	
	public static Camera parseCamera(String[] params, int pw, int ph) 
	{
		double px, py, pz, lx, ly, lz, ux, uy, uz, sc_d, sc_w;
		px = Double.parseDouble(params[0]);
		py = Double.parseDouble(params[1]);
		pz = Double.parseDouble(params[2]);
		lx = Double.parseDouble(params[3]);
		ly = Double.parseDouble(params[4]);
		lz = Double.parseDouble(params[5]);
		ux = Double.parseDouble(params[6]);
		uy = Double.parseDouble(params[7]);
		uz = Double.parseDouble(params[8]);
		sc_d = Double.parseDouble(params[9]);
		sc_w = Double.parseDouble(params[10]);
		
		Vector p, l, u;
		p = new Vector(px, py, pz);
		l = new Vector(lx, ly, lz);
		u = new Vector(ux, uy ,uz);
		return new Camera(p,l,u, sc_d, sc_w, pw, ph);
	}
	
	@Override
	public String toString() {
		return "Cam(pos=" + position + "lookAt=" + look_at_point +
				"upvect=" + up_vector + ")";
	}

	public Ray constructRayThroughPixel(int i, int j) 
	{
		// http://web.cse.ohio-state.edu/~shen.94/681/Site/Slides_files/basic_algo.pdf
		// slide 17:
		Vector n = position.substract(look_at_point).toUnit();
		Vector u = up_vector.crossProduct(n).toUnit();
		Vector v = n.crossProduct(u);

		// slide 19:
		double d = screen_distance;
		double W = screen_width;
		double H = screen_height;

		Vector C = position.substract(n.timesScalar(d));

		Vector L = C.substract(
				u.timesScalar(W / 2)).substract(
				v.timesScalar(H / 2));

		//Note: different i,j notations from presentation at link above (slide 19)
		Vector P = L.add(u.timesScalar(
				j * W / numOfCols)).add(
						v.timesScalar(i * H / numOfRows));

		//Basic sampling - ofsset the ray to the center of the pixel
		P = P.add(u.timesScalar(0.5 * screen_width / numOfCols)).add(
				v.timesScalar(0.5 * screen_height / numOfRows));

		//TODO: add super sampling here

		Vector vOfRay = P.substract(position);
		Ray ray = new Ray(this.position, vOfRay); //TODO: check maybe ray's origin is P.
		return ray;
	}

	public int getRows()
	{
		return numOfRows;
	}
	
	public int getCols()
	{
		return numOfCols;
	}

	//DEBUG ONLY. Broken - need to be updated.
	public void printViewingPlane()
	{
		// http://web.cse.ohio-state.edu/~shen.94/681/Site/Slides_files/basic_algo.pdf
		// slide 17:
		Vector n = position.substract(look_at_point).toUnit();
		Vector u = n.crossProduct(up_vector).toUnit();
		Vector v = u.crossProduct(n);
		System.out.println(String.format("ViewingPlane: eye coords are (u,v,n)=(%s,%s,%s)", u, v, n));
		// slide 19:
		// NOTE: doing a little different because we organize
		// pixels differently
		
		double d = screen_distance;
		double aspectRatio = numOfRows / numOfCols; // h / w
		double screen_height = aspectRatio * screen_width;
		Vector C = position.substract(n.timesScalar(d));
		
		//-- here L is top left
		Vector L = C.substract(
				u.timesScalar(screen_width / 2)).add(
						v.timesScalar(screen_height / 2));
		
		Vector L2 = L.add(u.timesScalar(screen_width));
		Vector L3 = L2.substract(v.timesScalar(screen_height));
		Vector L4 = L.substract(v.timesScalar(screen_height));
		
		System.out.println(String.format("ViewingPlane: corners are (L,L2,L3,L4) = (%s,%s,%s,%s)",
				L,L2,L3,L4));
	}
}
