package a1_code;

import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Tianfu Yuan (Student ID: 300228072)
 *
 * This is node set to store the nodes that read from file
 *
 */

public class NodeSet {

    private int id;
    private double lat; //latitude
    private double lon; //longitude

    private Location loc;
    private List<SegmentSet> neighbourIn = new ArrayList<SegmentSet>(2);
    private List<SegmentSet> neighbourOut = new ArrayList<SegmentSet>(2);
    
    QuadTree quadTree;

    public NodeSet(int id, Location loc) {
    	this.id = id;
    	this.loc = loc;
    }

    //read the node file
    public NodeSet(String line) {
    	String[] attr;
    	attr = line.split("\t");
    	id = Integer.parseInt(attr[0]);
    	lat = Double.parseDouble(attr[1]);
    	lon = Double.parseDouble(attr[2]);

    	loc = Location.newFromLatLon(lat, lon);
    }

    public int getID() {
    	return this.id;
    }
    public Location getLoc() {
    	return this.loc;
    }

    public void addInSegment(SegmentSet seg) {
    	neighbourIn.add(seg);
    }
    public void addOutSegment(SegmentSet seg) {
    	neighbourOut.add(seg);
    }

    public List<SegmentSet> getOutNeighbours() {
    	return neighbourOut;
    }

    public List<SegmentSet> getInNeighbours() {
    	return neighbourIn;
    }

    public boolean closeTo(Location place, double dist) {
    	return loc.isClose(place, dist);
    }

    public double distanceTo(Location place) {
    	return loc.distance(place);
    }

    public void draw(Graphics g, Location origin, double scale) {
    	Point pt = loc.asPoint(origin, scale);
    	g.fillRect(pt.x, pt.y, 2, 2);
    }

    public String toString() {
    	return String.format("Intersection %d: at %s; Roads:  ", id, loc);
    }
}
