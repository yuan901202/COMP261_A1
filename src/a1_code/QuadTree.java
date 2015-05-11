package a1_code;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
*
* @author Tianfu Yuan (Student ID: 300228072)
*
* This is quad-tree to store intersections
*
*/

public class QuadTree {
	List<NodeSet> intersection = new ArrayList<NodeSet>();
	QuadTree NorthEast, SouthEast, SouthWest, NorthWest;
	
	private double minX, minY, maxX, maxY;
	private final int capacity = 4;
	
	public QuadTree(double minX, double minY, double maxX, double maxY){
		this.minX = minX;
		this.minY = minY;
		this.maxX = maxX;
		this.maxY = maxY;
		
		NorthEast = null;  
		SouthEast = null;
		SouthWest = null;
		NorthWest = null;
	}
	
	public boolean addNode(NodeSet intersections) {
		if(NorthEast == null) {
			if(intersection.size() < capacity) {
				intersection.add(intersections);
				return true;
			}
			else{
				center();
			}
		}
		
		if(NorthEast.addNode(intersections)) {
			return true;
		}
		if(SouthEast.addNode(intersections)) {
			return true;
		}
		if(SouthWest.addNode(intersections)) {
			return true;
		}
		if(NorthWest.addNode(intersections)) {
			return true;
		}
		
		return false;
	}
	
	private void center() {
		if(NorthEast != null) {
			return;
		}
		
		double centerX = (minX + maxX) / 2;
		double centerY = (minY + maxY) / 2;
		
		NorthEast = new QuadTree(centerX, centerY, maxX, maxY);
		SouthEast = new QuadTree(centerX, minY, maxX, centerY);
		SouthWest = new QuadTree(minX, minY, centerX, centerY);
		NorthWest = new QuadTree(minX, centerY, centerX, maxY);
		
		intersection = null;
	}
	
	private boolean isOverlapped(double left, double right, double bottom, double top) {
		if(left >= this.maxX || right < this.minX || bottom >= this.maxY || top < this.minY) {
			return false;
		}
		else{
			return true;
		}
	}
	
	//return all intersections
	public List<NodeSet> returnIntersections() {
		List<NodeSet> List = new ArrayList<NodeSet>();
		
		if(this.NorthWest == null) {
			if(this.intersection.size() != 0) {
				for(NodeSet i : intersection) {
					List.add(i);
				}
			}
		}
		else{
			List.addAll(NorthWest.returnIntersections());
			List.addAll(NorthEast.returnIntersections());
			List.addAll(SouthEast.returnIntersections());
			List.addAll(SouthWest.returnIntersections());
		}
		return List;
	}
	
	//find intersection node
	public List<NodeSet> findIntersections(double left, double right, double bottom, double top) {
		if(!isOverlapped(left, right, bottom, top)) {
			return Collections.<NodeSet>emptyList();
		}
		
		List<NodeSet> List = new ArrayList<NodeSet>();
		
		if(NorthEast != null) {
			List.addAll(NorthEast.findIntersections(left, right, bottom, top));
			List.addAll(SouthEast.findIntersections(left, right, bottom, top));
			List.addAll(SouthWest.findIntersections(left, right, bottom, top));
			List.addAll(NorthWest.findIntersections(left, right, bottom, top));
		}
		else{
			return Collections.<NodeSet>emptyList();
		}
		
		return List;
	}
}
