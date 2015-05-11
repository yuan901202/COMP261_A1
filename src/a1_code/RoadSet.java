package a1_code;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
*
* @author Tianfu Yuan (Student ID: 300228072)
*
* This is road set to store the roads that read from file
*
*/

public class RoadSet{

    private int id;
    private int type; //??? not defined in README.txt
    private String label;
    private String city;
    private boolean oneway;
    private int speed;
    private int roadClass;
    private boolean notForCar;
    private boolean notForPede;
    private boolean notForBicy;

    private Trie trie;

    private List<SegmentSet> segmentSets = new ArrayList<SegmentSet>();
    @SuppressWarnings("unused")
	private HashMap<Integer, RoadSet> roads;

    public RoadSet(int id, int type, String label, String city, boolean oneway, int speed, int roadClass, boolean notForCar, boolean notForPede, boolean notForBicy) {
    	this.id = id;
    	this.type = type;
    	this.label = label;
    	this.city = city;
    	this.oneway = oneway;
    	this.speed = speed;
    	this.roadClass = roadClass;
    	this.notForCar = notForCar;
    	this.notForPede = notForPede;
    	this.notForBicy = notForBicy;
    }

    //read the road file
    public RoadSet(String line){
    	roads = new HashMap<Integer, RoadSet>();
        trie = new Trie();

    	String[] attr;
    	attr = line.split("\t");

        id = Integer.parseInt(attr[0]);
        type = Integer.parseInt(attr[1]); //???
        label = attr[2];
        city = attr[3];
        oneway = Boolean.parseBoolean(attr[4]);
        speed = Integer.parseInt(attr[5]);
        roadClass = Integer.parseInt(attr[6]);
        notForCar = Boolean.parseBoolean(attr[7]);
        notForPede = Boolean.parseBoolean(attr[8]);
        notForBicy = Boolean.parseBoolean(attr[9]);

        trie.addChild(label,id);
    }


    //public RoadSet getRoadById(int id){
    //    return roads.get(id);
    //}

    /**public Set<RoadSet> getRoadByName(String name) {
        TrieNode node = trie.getChild(name);
        Set<RoadSet> returnRoads = Collections.<RoadSet>emptySet();

        if(node != null && node.getMarked() == true) {
            returnRoads = new HashSet<RoadSet>();

            for(int i : node.getIDs()) {
                returnRoads.add(this.getRoadById(i));
            }
        }
        return returnRoads;
    }
    
    public List<String> getSuggestions(String query) {
        if(query.isEmpty()) {
        	return Collections.<String>emptyList();
        }
        return trie.getNames(query);
    }
    */

    public int getID() {
    	return this.id;
    }

    //???
    public int getType() {
    	return this.type;
    }

    public String getLabel() {
    	return this.label + this.city;
    }

    public String getCity() {
    	return this.city;
    }

    public int getRoadclass() {
    	return this.roadClass;
    }

    public int getSpeed() {
    	return this.speed;
    }

    public boolean isOneWay() {
    	return this.oneway;
    }

    public boolean isNotForCars() {
    	return this.notForCar;
    }

    public boolean isNotForPedestrians() {
    	return this.notForPede;
    }

    public boolean isNotForBicycles() {
    	return this.notForBicy;
    }

    public void addSegment(SegmentSet seg) {
    	this.segmentSets.add(seg);
    }

    public List<SegmentSet> getSegments() {
    	return this.segmentSets;
    }

    public String toString() {
    	return "Road: " + getLabel();
    }
}
