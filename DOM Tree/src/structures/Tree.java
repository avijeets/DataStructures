package structures;

import java.util.*;

/**
 * This class implements an HTML DOM Tree. Each node of the tree is a TagNode, with fields for
 * tag/text, first child and sibling.
 * 
 */
public class Tree {
	
	/**
	 * Root node
	 */
	TagNode root=null;
	
	/**
	 * Scanner used to read input HTML file when building the tree
	 */
	Scanner sc;
	
	/**
	 * Initializes this tree object with scanner for input HTML file
	 * 
	 * @param sc Scanner for input HTML file
	 */
	public Tree(Scanner sc) {
		this.sc = sc;
		root = null;
	}
	
	/**
	 * Builds the DOM tree from input HTML file. The root of the 
	 * tree is stored in the root field.
	 */
	
	public void build() {
		Stack<String> ephemStack = new Stack<String>();
		while(sc.hasNextLine()){
			ephemStack.push((String) sc.nextLine());
		}
		Stack<String> finalHTML = new Stack<String>();
		while(!ephemStack.isEmpty()){
			finalHTML.push(ephemStack.pop());
		}
		this.root = bringTogether(finalHTML);
	}
	
	private static TagNode bringTogether(Stack<String> str){
		if(str.isEmpty())
			return null;
		else if(str.peek().charAt(0) != '<'){
			String nextToPop = str.pop();
			return new TagNode(nextToPop, null, bringTogether(str));
		}
		else if(str.peek().charAt(0) == '<'){
			int oneinanother;
			Stack<String> shortStackLikePancakes = new Stack<String>();
			String firstLabel = str.pop();
			String nameOfLabel = firstLabel.substring(1, firstLabel.length() - 1);
			Stack<String> goneSoonStack = new Stack<String>();
			oneinanother = 0;
			
			while(true){
				if(str.peek().indexOf("</" + nameOfLabel) != -1){
					if(oneinanother > 0){
						oneinanother--;
						goneSoonStack.push(str.pop());
					}
					else{
						str.pop();
						break;
					}
				}
				else if(str.peek().indexOf('<' + nameOfLabel) != -1){
					oneinanother++;
					goneSoonStack.push(str.pop());
				}
				else
					goneSoonStack.push(str.pop());
			}
			
			while(!goneSoonStack.isEmpty()){
				shortStackLikePancakes.push(goneSoonStack.pop());
			}
			
			//bringing the labels together now
			TagNode secondLabel = new TagNode(nameOfLabel, null,null);
			secondLabel.firstChild = bringTogether(shortStackLikePancakes);
			secondLabel.sibling = bringTogether(str);
			return secondLabel;
		}
		else if(str.peek().length() == 0){
			String nextToPop = str.pop();
			return new TagNode(nextToPop, null, bringTogether(str));
		}
		else
			return null;
	}
	public void replaceTag(String oldTag, String newTag) {
		if(oldTag.equals(newTag))
			return;
		else if((oldTag.matches("ol|ul") && (!newTag.matches("ol|ul"))) || oldTag.equals("li") && (!newTag.equals("p"))){
			System.out.println("Improper replacement");
			return;
		}
		else if((oldTag.matches("html|body|table|tr|td")) || oldTag.matches("p|em|b") && (!newTag.matches("p|em|b"))){
			System.out.println("Improper replacement");
			return;
		}
		changeSymbol(this.root, oldTag, newTag);
	}
	
	private static void changeSymbol(TagNode trueVal, String oldTag, String newTag){
		if(trueVal == null)
			return;
		else if(trueVal.tag.equals(oldTag) && trueVal.firstChild != null)
			trueVal.tag = newTag;
		
		changeSymbol(trueVal.firstChild, oldTag, newTag);
		changeSymbol(trueVal.sibling, oldTag, newTag);
	}
	
	/**
	 * Boldfaces every column of the given row of the table in the DOM tree. The boldface (b)
	 * tag appears directly under the td tag of every column of this row.
	 * 
	 * @param row Row to bold, first row is numbered 1 (not 0).
	 */
	public void boldRow(int row) {
		TagNode tacobelle = findNearestTacobelle(this.root);
		if(tacobelle == null)
			return;
		TagNode ptrTag = tacobelle.firstChild;
		for(int i = 1; i < row && ptrTag != null; i++){
			if(ptrTag.firstChild != null && ptrTag.tag.equals("tr"))
				ptrTag = ptrTag.sibling;
			else{
				ptrTag = ptrTag.sibling;
				i--;
			}
		}
		if(ptrTag == null)
			return;
		else if(ptrTag.firstChild != null && !ptrTag.tag.equals("tr")){
			while(ptrTag != null){
				if(ptrTag.firstChild != null && ptrTag.tag.equals("tr"))
					break;
				else
					ptrTag = ptrTag.sibling;
			}
		}
		if(ptrTag == null) // need second check after else if
			return;
		
		ptrTag = ptrTag.firstChild;
		while(ptrTag != null){
			if(ptrTag.firstChild != null && ptrTag.tag.equals("td")){
				TagNode ephemNode = ptrTag.firstChild;
				ptrTag.firstChild = new TagNode("b", ephemNode, null);
				ptrTag = ptrTag.sibling;
			}
			else
				ptrTag = ptrTag.sibling;
		}
	}
	
	private static TagNode findNearestTacobelle(TagNode renegadeTag){
		if(renegadeTag == null)
			return null;
		else if(renegadeTag.tag.equals("table") && renegadeTag.firstChild != null)
			return renegadeTag;
		
		TagNode leftChild = findNearestTacobelle(renegadeTag.firstChild);
		TagNode adjNode = findNearestTacobelle(renegadeTag.sibling);
		
		if(adjNode != null)
			return adjNode;
		else if(leftChild != null)
			return leftChild;
		else
			return null;
	}
	
	/**
	 * Remove all occurrences of a tag from the DOM tree. If the tag is p, em, or b, all occurrences of the tag
	 * are removed. If the tag is ol or ul, then All occurrences of such a tag are removed from the tree, and, 
	 * in addition, all the li tags immediately under the removed tag are converted to p tags. 
	 * 
	 * @param tag Tag to be removed, can be p, em, b, ol, or ul
	 */
	public void removeTag(String tag) {
		if(tag.matches("p|b|em"))
			firstPossibility(root.firstChild, tag); //first possibility, goes through children
		else if(tag.matches("ol|ul"))
			secondPossibility(root, tag); // second possibility, goes through root
	}
	
	private static void firstPossibility(TagNode root, String tag){
		if(root == null)
			return;
		TagNode rootNode = root;
		
		if(rootNode.tag.equals(tag) && rootNode.firstChild != null){
			TagNode adjNode = rootNode.sibling;
			rootNode.tag = rootNode.firstChild.tag;
			rootNode.sibling = rootNode.firstChild.sibling;
			rootNode.firstChild = rootNode.firstChild.firstChild;
			
			while(rootNode.sibling != null){
				if(rootNode.tag.equals(tag) && rootNode.firstChild != null)
					firstPossibility(rootNode, tag);
				rootNode = rootNode.sibling;
			}
			if(rootNode.tag.equals(tag) && rootNode.firstChild != null)
				firstPossibility(rootNode, tag);
			rootNode.sibling = adjNode;
		}
		
		firstPossibility(root.firstChild, tag);
		firstPossibility(root.sibling, tag);
	}
	
	private static void secondPossibility(TagNode root, String tag){
		if(root == null)
			return;
		if(root.tag.equals(tag) && root.firstChild != null){
			TagNode adjNode = root.sibling;
			root.tag = root.firstChild.tag;
			root.sibling = root.firstChild.sibling;
			root.firstChild = root.firstChild.firstChild;
			changeLiToNewLine(root, tag);
			TagNode rootNode = root;
			while(rootNode.sibling != null){
				if(rootNode.firstChild != null && rootNode.tag.equals(tag))
					secondPossibility(rootNode, tag);
				rootNode = rootNode.sibling;
			}
			if(rootNode.firstChild != null && rootNode.tag.equals(tag))
				secondPossibility(rootNode, tag);
			
			rootNode.sibling = adjNode;
		}
		secondPossibility(root.firstChild, tag);
		secondPossibility(root.sibling, tag);
	}
	
	private static void changeLiToNewLine(TagNode root, String tag){
		if(root == null)
			return;
		else if(root.firstChild != null && root.tag.equals("li")){
			root.tag = "p";
			changeLiToNewLine(root.firstChild, tag);
			changeLiToNewLine(root.sibling, tag);
		}
		else if((!root.tag.equals(tag)) && root.tag.matches("ul|ol"))
			changeLiToNewLine(root.sibling, tag);
		else{
			changeLiToNewLine(root.firstChild, tag);
			changeLiToNewLine(root.sibling, tag);
		}
	}
	
	/**
	 * Adds a tag around all occurrences of a word in the DOM tree.
	 * 
	 * @param word Word around which tag is to be added
	 * @param tag Tag to be added
	 */
	public void addTag(String word, String tag) {
			tagAddition(this.root, word, tag); // simply calls helper method
	}
	private static void tagAddition(TagNode root, String word, String tag){
		if(root == null)
			return;
		TagNode rootNode = root;
		boolean isItFirst = true;
		
		if(root.firstChild == null){
			StringTokenizer gimmeTokenz = new StringTokenizer(root.tag, " ");
			while(gimmeTokenz.hasMoreTokens()){
				String ephemVal = gimmeTokenz.nextToken();
				if(ephemVal.equalsIgnoreCase(word) || ephemVal.equalsIgnoreCase(word + ':') || ephemVal.equalsIgnoreCase(word + '!') ||  ephemVal.equalsIgnoreCase(word + '.') || ephemVal.equalsIgnoreCase(word + ';') || ephemVal.equalsIgnoreCase(word + '?') || ephemVal.equalsIgnoreCase(word + ',')) {
					if(isItFirst){
						if(rootNode.tag.charAt(0) == ' '){
							rootNode.tag = " ";
							TagNode adjNode = rootNode.sibling;
							rootNode.sibling = new TagNode(tag, new TagNode(ephemVal, null, null), adjNode);
							rootNode = rootNode.sibling;
							isItFirst = false;
						}
						else{
							rootNode.tag = tag;
							rootNode.firstChild = new TagNode(ephemVal, null, null);
							isItFirst = false;
						}
					}
					else{
						if(rootNode.firstChild.tag.charAt(rootNode.firstChild.tag.length() - 1) != ' ' && rootNode.firstChild != null){
							TagNode adjNode = rootNode.sibling;
							rootNode.sibling = new TagNode(" ", null, new TagNode(tag, new TagNode(ephemVal, null, null), adjNode));
							rootNode = rootNode.sibling.sibling;
						}
						else if(rootNode.tag.charAt(rootNode.tag.length() - 1) != ' ' && rootNode.firstChild == null){
							rootNode.tag = rootNode.tag + ' ';
							TagNode adjNode = rootNode.sibling;
							rootNode.sibling = new TagNode(tag, new TagNode(ephemVal, null, null), adjNode);
							rootNode = rootNode.sibling;
						}
						else{
							TagNode adjNode = rootNode.sibling;
							rootNode.sibling = new TagNode(tag, new TagNode(ephemVal, null, null), adjNode);
							rootNode = rootNode.sibling;
						}
					}
				}
				else{
					if(isItFirst){
						if(rootNode.tag.charAt(0) == ' ')
							ephemVal = ' ' + ephemVal;
						rootNode.tag = ephemVal;
						isItFirst = false;
					}
					else{
						if(rootNode.firstChild == null)
							rootNode.tag = rootNode.tag + ' ' + ephemVal;
						else{
							TagNode adjNode = rootNode.sibling;
							rootNode.sibling = new TagNode(" " + ephemVal, null, adjNode);
							rootNode = rootNode.sibling;
						}
					}
				}
			}
			tagAddition(rootNode.sibling, word, tag);
		}
		else{
			tagAddition(root.firstChild, word, tag);
			tagAddition(root.sibling, word, tag);
		}
	}
	
	/**
	 * Gets the HTML represented by this DOM tree. The returned string includes
	 * new lines, so that when it is printed, it will be identical to the
	 * input file from which the DOM tree was built.
	 * 
	 * @return HTML string, including new lines. 
	 */
	public String getHTML() {
		StringBuilder sb = new StringBuilder();
		getHTML(root, sb);
		return sb.toString();
	}
	
	private void getHTML(TagNode root, StringBuilder sb) {
		for (TagNode ptr=root; ptr != null;ptr=ptr.sibling) {
			if (ptr.firstChild == null) {
				sb.append(ptr.tag);
				sb.append("\n");
			} else {
				sb.append("<");
				sb.append(ptr.tag);
				sb.append(">\n");
				getHTML(ptr.firstChild, sb);
				sb.append("</");
				sb.append(ptr.tag);
				sb.append(">\n");	
			}
		}
	}
	
}
