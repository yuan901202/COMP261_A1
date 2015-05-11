package a1_code;

import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
*
* @author Tianfu Yuan (Student ID: 300228072)
*
* This is segment set to store the segments that read from file
*
*/

public class SegmentSet {

    private RoadSet road;
    private double length;
    private NodeSet startNode; //nodeID1
    private NodeSet endNode; //nodeID2

    private List<Location> coords = new ArrayList<Location>();

    public SegmentSet(RoadSet road, double length, NodeSet startNode, NodeSet endNode) {
    	this.road = road;
    	this.length = length;
    	this.startNode = startNode;
    	this.endNode = endNode;
    }

    //read the node file
    public SegmentSet(String line, Map<Integer, RoadSet> roadSets, Map<Integer, NodeSet> nodeSets) {
    	String[] attr;
    	attr = line.split("\t");
    	road = roadSets.get(Integer.parseInt(attr[0]));
    	length = Double.parseDouble(attr[1]);
    	startNode = nodeSets.get(Integer.parseInt(attr[2]));
    	endNode = nodeSets.get(Integer.parseInt(attr[3]));

    	for (int i = 4; i < attr.length; i += 2){
    		coords.add(Location.newFromLatLon(Double.parseDouble(attr[i]), Double.parseDouble(attr[i+1])));
    	}
    }

    public RoadSet getRoad() {
    	return this.road;
    }

    public double getLength() {
    	return this.length;
    }

    public NodeSet getStartNode() {
    	return this.startNode;
    }

    public NodeSet getEndNode() {
    	return this.endNode;
    }

    public void addCoord(Location loc) {
    	coords.add(loc);
    }

    public List<Location> getCoords() {
    	return coords;
    }

    public SegmentSet oneway() {
    	SegmentSet ow =  new SegmentSet(road, length, endNode, startNode);
    	ow.coords = this.coords;
    	return ow;
    }

    public void draw(Graphics g, Location origin, double scale) {
    	if (!coords.isEmpty()) {
    		Point pt1 = coords.get(0).asPoint(origin, scale);
    		for (int i = 1; i < coords.size(); i++) {
    			Point pt2 = coords.get(i).asPoint(origin, scale);
    			g.drawLine(pt1.x, pt1.y, pt2.x, pt2.y);
    			pt1 = pt2;
    		}
    	}
    }

    public String toString() {
    	return String.format("%d: %4.2fkm from %d to %d", road.getID(), length, startNode.getID(), endNode.getID());
    }
}
