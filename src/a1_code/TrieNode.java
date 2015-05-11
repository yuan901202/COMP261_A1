package a1_code;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 *
 * @author Tianfu Yuan (Student ID: 300228072)
 *
 * This is for trie structure to store the node
 *
 */

public class TrieNode {
    private boolean isMarked;
    private Set<Integer> ids;
    TreeMap<Character, TrieNode> nodeChildren;

    public TrieNode() {
        isMarked = false;
        ids = new HashSet<Integer>();
        nodeChildren = new TreeMap<Character, TrieNode>();
    }

    public void setMarked(boolean isMarked) {
        this.isMarked = isMarked;
    }

    public boolean getMarked() {
        return isMarked;
    }

    public void addID(int id) {
        ids.add(id);
    }

    public Set<Integer> getIDs() {
        return ids;
    }

    public TrieNode getChild(char childNode) {
        return nodeChildren.get((Character)childNode);
    }

    public TrieNode addChild(char child) {
        TrieNode newChild = new TrieNode();
        nodeChildren.put((Character)child, newChild);
        return newChild;
    }

    public List<String> getNames(String prefix) {
        List<String> returnStrings = new ArrayList<String>();

        if(this.isMarked) {
            returnStrings.add(prefix);
        }

        for(Map.Entry<Character, TrieNode> nd : nodeChildren.entrySet()) {

            if(nd != null) {
            	TrieNode node = nd.getValue();
            	if(node != null) {
                    returnStrings.addAll(node.getNames(prefix + nd.getKey().toString()));
                }
            }
        }
        return returnStrings;
    }
}
