package a1_code;

import java.awt.Graphics;
import java.awt.Point;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileFilter;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Tianfu Yuan (Student ID: 300228072)
 *
 * This is the main Java program to display the Auckland map
 *
 */

public class AKLMap {

    private JFrame frame;
    private JComponent drawing;
    private JTextArea textOutputArea;
    private JTextField search;

    //constant value
    private int WINDOW_SIZE = 900;
    private double ZOOM_FACTOR = 1.2;
    private double PAN_FRACTION = 0.2;

    private ReadFile readFile;

    private NodeSet currentNode; //selected node
    private List<SegmentSet> currentSegment; //selected segment

    private Point moveStartPt;
    private Location moveOrigin;

    //boundary
    double[] boundary;
    double westBoundary ;
    double eastBoundary ;
    double southBoundary;
    double northBoundary;

    Location origin;
    double scale;

    public AKLMap(String file) {
    	GUI();
    	readFile = new ReadFile();

    	while (file == null) {
    		file = getPath();
    	}
    	
    	textOutputArea.append("Using keyboard to implement following functions: \n");
    	textOutputArea.append("1) UP, DOWN, LEFT, RIGHT to moving the map. \n");
    	textOutputArea.append("2) MINUS, EQUAL or use mouse wheel to zooming the map . \n\n");
    	textOutputArea.append("Loading from " + file + "\n");
    	textOutputArea.append(readFile.loadFile(file));
    	textOutputArea.append("Loading completed. \n");
    	setBoundaries();
    	drawing.repaint();
    }

    /*Following code is get the idea from http://www.java2s.com/Code/Java/Swing-JFC/SelectadirectorywithaJFileChooser.htm */
	private class DirectoryFileFilter extends FileFilter {
    	public boolean accept(File file) {
    		return file.isDirectory();
    	}

		public String getDescription() {
			return null;
		}
    }

    private String getPath() {
    	JFileChooser fc = new JFileChooser();
    	fc.setCurrentDirectory(new java.io.File("."));
        fc.setDialogTitle("Choose File Directory: ");
    	fc.setFileFilter(new DirectoryFileFilter());
    	fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    	if (fc.showOpenDialog(frame) != JFileChooser.APPROVE_OPTION) return null;

    	return fc.getSelectedFile().getAbsolutePath() + File.separator;
    }
    /*END*/

    //set boundaries
	private void setBoundaries() {
		boundary = readFile.getBoundaries();
	    westBoundary = boundary[0];
	    eastBoundary = boundary[1];
	    southBoundary = boundary[2];
	    northBoundary = boundary[3];
	    originReset();
	}

	//GUI
	@SuppressWarnings("serial")
	private void GUI() {
	    frame = new JFrame("AKL_MAP_SYSTEM developed by yuantian");
	    frame.setSize(WINDOW_SIZE, WINDOW_SIZE);
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	    //map display area
	    drawing = new JComponent() {
		    protected void paintComponent(Graphics g) {
		    	redraw(g);
		    }
		};
	    frame.add(drawing, BorderLayout.CENTER);

	    //text output area
	    textOutputArea = new JTextArea(10, 100);
	    textOutputArea.setEditable(false);
	    JScrollPane textSP = new JScrollPane(textOutputArea);
	    frame.add(textSP, BorderLayout.SOUTH);

	    //display buttons
	    JPanel panel = new JPanel();
	    frame.add(panel, BorderLayout.NORTH);

	    //zoom in button
	  	JButton zoomInButton = new JButton("+");
	  	panel.add(zoomInButton);
	  	zoomInButton.addActionListener(new ActionListener() {
	  		public void actionPerformed(ActionEvent e) {
	  			zoom(ZOOM_FACTOR);
	  		}
	  	});

	  	//zoom out button
	  	JButton zoomOutButton = new JButton("-");
	  	panel.add(zoomOutButton);
	  	zoomOutButton.addActionListener(new ActionListener() {
	  		public void actionPerformed(ActionEvent e) {
	  			zoom(1 / ZOOM_FACTOR);
	  		}
	  	});

	  	//move left button
	  	JButton leftButton = new JButton("\u2190");
	  	panel.add(leftButton);
	  	zoomOutButton.addActionListener(new ActionListener() {
	  		public void actionPerformed(ActionEvent e) {
	  			panLeft();
	  		}
	  	});

	  	//moving right button
	  	JButton rightButton = new JButton("\u2192");
	  	panel.add(rightButton);
	  	zoomOutButton.addActionListener(new ActionListener() {
	  		public void actionPerformed(ActionEvent e) {
	  			panRight();
	  		}
	  	});

	  	//move up button
	  	JButton upButton = new JButton("\u2191");
	  	panel.add(upButton);
	  	zoomOutButton.addActionListener(new ActionListener() {
	  		public void actionPerformed(ActionEvent e) {
	  			panUp();
	  		}
	  	});

	  	//move down button
	  	JButton downButton = new JButton("\u2193");
	  	panel.add(downButton);
	  	zoomOutButton.addActionListener(new ActionListener() {
	  		public void actionPerformed(ActionEvent e) {
	  			panDown();
	  		}
	  	});

	  	//reset button
	  	JButton resetButton = new JButton("Reset");
	  	panel.add(resetButton);
	  	zoomOutButton.addActionListener(new ActionListener() {
	  		public void actionPerformed(ActionEvent e) {
	  			reset();
	  		}
	  	});

	  	//quit button
	  	JButton quitButton = new JButton("Quit");
	  	panel.add(quitButton);
	  	zoomOutButton.addActionListener(new ActionListener() {
	  		public void actionPerformed(ActionEvent e) {
	  			System.exit(0);
	  		}
	  	});

	  	//search bar
	  	panel.add(new JLabel("Search: "));
	    search = new JTextField(10);
	    panel.add(search);
	    search.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		    	searchName(search.getText());
		    	drawing.repaint();
		    }
		});

	    //show current node when click mouse
	    drawing.addMouseListener(new MouseAdapter() {
		    public void mouseReleased(MouseEvent e) {
		    	currentNode = findNode(e.getPoint());
		    	if (currentNode != null) {
		    		textOutputArea.setText(currentNode.toString());
		    	}
		    	drawing.repaint();
			}
		});

	    //mouse wheel doing zoomIn and zoomOut functions
	    /*The following code is get idea from http://www.java2s.com/Code/JavaAPI/javax.swing/JFrameaddMouseWheelListenerMouseWheelListenerl.htm*/
	    drawing.addMouseWheelListener(new MouseWheelListener() {
			public void mouseWheelMoved(MouseWheelEvent e) {
				Point pt = e.getPoint();
				double clicks = e.getWheelRotation();
					if(clicks > 0) {
						zoom(1 / ZOOM_FACTOR / clicks, pt.x, pt.y);
					} else if(clicks < 0) {
						zoom((-1 * clicks) * ZOOM_FACTOR, pt.x, pt.y);
					}
			}
		});
	    /*END*/

	    //implement mouse drag function
	    drawing.addMouseMotionListener(new MouseMotionListener() {
			public void mouseDragged(MouseEvent e) {
				Point pt1 = e.getPoint();
				Point pt2 = new Point(moveStartPt.x - pt1.x, moveStartPt.y - pt1.y);
				origin = Location.newFromPoint(pt2, moveOrigin, scale);
				drawing.repaint();
			}

			public void mouseMoved(MouseEvent e) {
			}
		});
	    
	    /**
		 * Following code to respond to map when user press the key on keyboard
		 * Like:
		 * 1) UP -> moving up
		 * 2) DOWN -> moving down
		 * 3) LEFT -> moving left
		 * 4) RIGHT -> moving right
		 * 5) MINUS -> zooming out
		 * 6) EQUALS -> zooming in
		 * 7) SPACE -> reset
		 *
		 * Link: http://www.java2s.com/Code/JavaAPI/javax.swing/InputMapputKeyStrokekeyStrokeObjectactionMapKey.htm
		 */

		InputMap inputMap = drawing.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		ActionMap actionMap = drawing.getActionMap();

		inputMap.put(KeyStroke.getKeyStroke("UP"), "panUp");
		inputMap.put(KeyStroke.getKeyStroke("DOWN"), "panDown");
		inputMap.put(KeyStroke.getKeyStroke("LEFT"), "panLeft");
		inputMap.put(KeyStroke.getKeyStroke("RIGHT"), "panRight");
		inputMap.put(KeyStroke.getKeyStroke("MINUS"), "zoomOut");
		inputMap.put(KeyStroke.getKeyStroke("EQUALS"), "zoomIn");
		inputMap.put(KeyStroke.getKeyStroke("SPACE"), "reset");

		actionMap.put("panUp", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				panUp();
			}
		});

		actionMap.put("panDown", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				panDown();
			}
		});

		actionMap.put("panLeft", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				panLeft();
			}
		});

		actionMap.put("panRight", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				panRight();
			}
		});

		actionMap.put("zoomIn", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				zoom(ZOOM_FACTOR);
			}
		});

		actionMap.put("zoomOut", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				zoom(1 / ZOOM_FACTOR);
			}
		});

		actionMap.put("reset", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				reset();
			}
		});

	    frame.setVisible(true);
	}

	//reset origin
	private void originReset() {
	    origin = new Location(westBoundary, northBoundary);

	    int width = drawing.getWidth(); //width = eastBoundary - westBoundary
		int height = drawing.getHeight(); //height = northBoundary - southBoundary

	    scale = Math.min(width / (eastBoundary - westBoundary), height / (northBoundary - southBoundary));
	}

	//zoom function
	private void zoom(double zoomFactor, int x, int y) {
		Location loc = Location.newFromPoint(new Point(x, y), origin, scale);
		Location newOrigin = new Location((loc.x * (1 - 1 / zoomFactor) + origin.x / zoomFactor), (loc.y * (1 - 1 / zoomFactor) + origin.y / zoomFactor));

		origin = newOrigin;
		scale = scale * zoomFactor;
		drawing.repaint();
	}

	private void zoom(double zoomFactor) {
		int x = drawing.getWidth() / 2;
		int y = drawing.getHeight() / 2;
		zoom(zoomFactor, x, y);
	}

    //move left method
    private void panLeft() {
    	double newOrigin = WINDOW_SIZE * PAN_FRACTION / scale;
    	origin = new Location(origin.x + newOrigin, origin.y);
    	drawing.repaint();
    }

    //move right method
    private void panRight() {
    	double newOrigin = WINDOW_SIZE * PAN_FRACTION / scale;
    	origin = new Location(origin.x - newOrigin, origin.y);
    	drawing.repaint();
    }

    //move up method
    private void panUp() {
    	double newOrigin = WINDOW_SIZE * PAN_FRACTION / scale;
    	origin = new Location(origin.x, origin.y - newOrigin);
    	drawing.repaint();
    }

    //move down method
    private void panDown() {
    	double newOrigin = WINDOW_SIZE * PAN_FRACTION / scale;
    	origin = new Location(origin.x, origin.y + newOrigin);
    	drawing.repaint();
    }

    //reset method
    private void reset() {
    	setBoundaries();
    	search.removeAll();
    	textOutputArea.setText(null);
    	drawing.repaint();
    }

	//Find the place that the mouse was clicked on (if any)
	private NodeSet findNode(Point nodePt) {
	    return readFile.findNode(nodePt, origin, scale);
	}

	//search name in name list
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void searchName(String query) {
		List<String> names = new ArrayList(readFile.searchName(query));
	    if(names.isEmpty()) {
	    	currentSegment = null;
	    	textOutputArea.setText("Not found! Retype...");
	    }
	    else if (names.size() == 1) {
			String fullName = names.get(0);
			search.setText(fullName);
			textOutputArea.setText("Great! Name found...");
			currentSegment = readFile.getRoadSegments(fullName);
	    }
	    else{
	    	currentSegment = null;
	    	String prefix = prefix(query, names);
	    	search.setText(prefix);
	    	//display first ten names
	    	for(int i = 0; i < 10; i++) {
	    		textOutputArea.append("===========================\n");
	    		textOutputArea.append("Results " + (i+1) + ": "  + names.get(i) + "\n");
	    	}
	    }
	}

	//prefix
    private String prefix(String query, List<String>names) {
    	String nm = query;
    	for(int i = query.length(); ; i++) {
    		if(names.get(0).length() < i) {
    			return nm;
    		}
    		String getName = names.get(0).substring(0,i);
    		for(String name : names) {
    			if(name.length() < i) {
    				return nm;
    			}
    		}
    		nm = getName;
    	}
    }

    //redraw
	public void redraw(Graphics g) {
	    if(readFile != null) {
	    	readFile.redraw(g, origin, scale);
	    	if(currentNode != null) {
	    		g.setColor(Color.red);
	    		currentNode.draw(g, origin, scale);
	    	}
	    	if(currentSegment != null) {
	    		g.setColor(Color.red);
	    		for (SegmentSet seg : currentSegment) {
	    			seg.draw(g, origin, scale);
	    		}
	    	}
	    }
	}

	//main
	public static void main(String[] arguments) {
	    if (arguments.length > 0) {
	    	@SuppressWarnings("unused")
			AKLMap akl = new AKLMap(arguments[0]);
	    } else {
	    	@SuppressWarnings("unused")
			AKLMap akl = new AKLMap(null);
	    }
	}
}
