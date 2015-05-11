package a1_code;

import java.awt.Color;
import java.awt.Point;
import java.awt.Graphics;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
*
* @author Tianfu Yuan (Student ID: 300228072)
*
* This is read file to read the files from selected directory
*
*/

public class ReadFile {

	private static final String NODES_FILENAME = "nodeID-lat-lon.tab";
	private static final String ROADS_FILENAME = "roadID-roadInfo.tab";
	private static final String SEGS_FILENAME = "roadSeg-roadID-length-nodeID-nodeID-coords.tab";
	private static final String POLYS_FILENAME = "polygon-shapes.mp";
	
    double westBoundary = Double.POSITIVE_INFINITY;
    double eastBoundary = Double.NEGATIVE_INFINITY;
    double southBoundary = Double.POSITIVE_INFINITY;
    double northBoundary = Double.NEGATIVE_INFINITY;
    
    private double west;
    private double east;
    private double north;
    private double south;

    Map<Integer, NodeSet> nodeSets = new HashMap<Integer, NodeSet>();

    Map<Integer, RoadSet> roadSets = new HashMap<Integer, RoadSet>();

    Map<String, Set<RoadSet>> roadsByName = new HashMap<String, Set<RoadSet>>();

    Set<String> roadNames = new HashSet<String>();

    QuadTree quadTree; //store intersections
    
    public ReadFile() {

    }

    public String loadFile(String file) {
    	loadNodes(file);
    	loadRoads(file);
    	loadSegments(file);
		return null;
    }

    //load node file
    public void loadNodes(String file) {
    	File nodeFile = new File(file + NODES_FILENAME);

    	BufferedReader bf;
    	try {
    	    bf = new BufferedReader(new FileReader(nodeFile));
    	    while(true) {
    	    	String line = bf.readLine();
    	    	if(line == null) break;
    	    	NodeSet nodeSet = new NodeSet(line);
    	    	nodeSets.put(nodeSet.getID(), nodeSet);
    	    }
    	} catch(IOException e) {
    		System.out.println("Cannot open roadID-roadInfo.tab " + e);
    	}
    }

    //load road file
    public void loadRoads(String file) {
    	File roadFile = new File(file + ROADS_FILENAME);
    	inputToQuadTree();
    	BufferedReader bf;
    	try {
    		bf = new BufferedReader(new FileReader(roadFile));
    		bf.readLine(); //skip header line

    		while(true) {
    			String line = bf.readLine();
    			if(line == null) break;
    			RoadSet roadSet = new RoadSet(line);
    			roadSets.put(roadSet.getID(), roadSet);
    			String name = roadSet.getLabel();
    			roadNames.add(name);
    			Set<RoadSet> rds = roadsByName.get(name);
    			if(rds == null) {
    				rds = new HashSet<RoadSet>(4);
    				roadsByName.put(name, rds);
    			}
    			rds.add(roadSet);
    		}
    	} catch(IOException e){
    		System.out.println("Cannot open roadID-roadInfo.tab " + e);
    	}
    }

    //load segment file
    public void loadSegments(String file) {
    	File segFile = new File(file + SEGS_FILENAME);

    	BufferedReader bf;
    	try{
    		bf = new BufferedReader(new  FileReader(segFile));
    		bf.readLine(); //skip header line
    		while (true) {
    			String line = bf.readLine();
    			if(line == null) break;
    			SegmentSet seg = new SegmentSet(line, roadSets, nodeSets);
    			NodeSet nodeID1 = seg.getStartNode();
    			NodeSet nodeID2 = seg.getEndNode();
    			nodeID1.addOutSegment(seg);
    			nodeID2.addInSegment(seg);
    			RoadSet roadSet = seg.getRoad();
    			roadSet.addSegment(seg);
    			if(!roadSet.isOneWay()) {
    				SegmentSet revSeg = seg.oneway();
    				nodeID2.addOutSegment(revSeg);
    				nodeID1.addInSegment(revSeg);
    			}
    		}
    	} catch(IOException e){
    		System.out.println("Cannot open roadID-roadInfo.tab " + e);
    	}
    }

    //load polygon file
    public void loadPolygon(String file) {
    	File polFile = new File(file + POLYS_FILENAME);

    	BufferedReader bf;
    	try{
    		bf = new BufferedReader(new  FileReader(polFile));
    		String line = bf.readLine();
    		bf.readLine();
    		
    	} catch(IOException e){
    		System.out.println("Cannot open polygon-shapes.mp " + e);
    	}
    }
    
    public double[] getBoundaries() {
    	for(NodeSet nodeSet : nodeSets.values()) {
    		Location loc = nodeSet.getLoc();
    		if(loc.x < westBoundary) {
    			westBoundary = loc.x;
    		}
    		if(loc.x > eastBoundary) {
    			eastBoundary = loc.x;
    		}
    		if(loc.y < southBoundary) {
    			southBoundary = loc.y;
    		}
    		if(loc.y > northBoundary) {
    			northBoundary = loc.y;
    		}
    	}
    	return new double[]{westBoundary, eastBoundary, southBoundary, northBoundary};
    }

    public void redraw(Graphics g, Location origin, double scale) {
    	g.setColor(Color.black);
    	for (NodeSet nodeSet : nodeSets.values()) {
    		for (SegmentSet seg : nodeSet.getOutNeighbours()) {
    			seg.draw(g, origin, scale);
    		}
    	}

    	g.setColor(Color.blue);
    	for(NodeSet nodeSet : nodeSets.values()) {
    		nodeSet.draw(g, origin, scale);
    	}
    }

    //find node when click mouse
    public NodeSet findNode(Point point, Location origin, double scale) {
    	Location mouse = Location.newFromPoint(point, origin, scale);
    	NodeSet node = null;
    	double mind = Double.POSITIVE_INFINITY;
    	for (NodeSet nodeSet : nodeSets.values()) {
    		double dist = nodeSet.distanceTo(mouse);
    		if (dist < mind) {
    			mind = dist;
    			node = nodeSet;
    		}
    	}
    	return node;
    }

    //search name when enter the prefix
    public Set<String> searchName(String query) {
    	Set<String> result = new HashSet<String>(10);
    	if (query == null) {
    		return null;
    	}
    	query = query.toLowerCase();

    	for (String name : roadNames){
    		if (name.equals(query)){
    			result.clear();
    			result.add(name);
    			return result;
    		}
    	}
    	return result;
    }

    public Set<RoadSet> getRoadsByName(String name){
    	return roadsByName.get(name);
    }

    public List<SegmentSet> getRoadSegments(String name){
    	Set<RoadSet> road = roadsByName.get(name);
    	if (road == null) {
    		return null; 
    	}
    	System.out.println("Found " + road.size() + " road objects: " + road.iterator().next());
    	List<SegmentSet> result = new ArrayList<SegmentSet>();
    	for (RoadSet roadSet : road) {
    		result.addAll(roadSet.getSegments());
    	}
    	return result;
    }
    
    //put data from hash-map to quad-tree
    private void inputToQuadTree() {
    	double [] bounds = getQuadBoundaries();

    	//index the intersections
    	quadTree = new QuadTree(bounds[0], bounds[2], bounds[1], bounds[3]);

    	for(Map.Entry<Integer, NodeSet> e : nodeSets.entrySet()) {
    		quadTree.addNode(e.getValue());
    	}
    }
    
    public double[] getQuadBoundaries() {
		final double margin = 10; //km
		return new double[]{(west - margin), (east + margin), (south - margin), (north + margin)};
    }
}
