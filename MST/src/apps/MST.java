package apps;

import structures.*;
import java.util.ArrayList;

public class MST {
	
	/**
	 * Initializes the algorithm by building single-vertex partial trees
	 * 
	 * @param graph Graph for which the MST is to be found
	 * @return The initial partial tree list
	 */
	public static PartialTreeList initialize(Graph graph) {
		PartialTreeList PTL1 = new PartialTreeList();
    	for (int i = 0; i < graph.vertices.length; i++) {
    		PartialTree PT = new PartialTree(graph.vertices[i]);
    		
			for (Vertex.Neighbor verts = graph.vertices[i].neighbors; verts != null; verts = verts.next) {
				PartialTree.Arc PTLArc = new PartialTree.Arc(PT.getRoot(), verts.vertex, verts.weight);
				PT.getArcs().insert(PTLArc);
			}
			PTL1.append(PT);
		}
    	return PTL1;
	}

	/**
	 * Executes the algorithm on a graph, starting with the initial partial tree list
	 * 
	 * @param ptlist Initial partial tree list
	 * @return Array list of all arcs that are in the MST - sequence of arcs is irrelevant
	 */
	public static ArrayList<PartialTree.Arc> execute(PartialTreeList ptlist) {
		ArrayList<PartialTree.Arc> ptArcMST = new ArrayList<PartialTree.Arc>();
		while (ptlist.size() > 1) {
			PartialTree ptRemove = ptlist.remove();
			PartialTree.Arc ptArcTwo = ptRemove.getArcs().deleteMin();
			while (ptRemove.getRoot() == ptArcTwo.v2) {
				ptArcTwo = ptRemove.getArcs().deleteMin();
			}
			
			PartialTree ptContaining = ptlist.removeTreeContaining(ptArcTwo.v2);
			
			if (ptContaining != null) {
				while (ptContaining.getRoot() == ptArcTwo.v1) {
					ptArcTwo = ptRemove.getArcs().deleteMin();
				}
				ptRemove.merge(ptContaining);
				ptArcMST.add(ptArcTwo);
			}
			System.out.println(ptRemove);
			ptlist.append(ptRemove);
		}System.out.println(ptArcMST);
		return ptArcMST;
	}
}
