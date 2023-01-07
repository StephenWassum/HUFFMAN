package huffman;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;

/**
 * This class contains methods which are used by to driver to create Huffman Encodings.
 */

public class HuffmanCoding {
    private String fileName;
    private ArrayList<CharFreq> sortedCharFreqList;
    private TreeNode huffmanRoot;
    private String[] encodings;


    /**
     * Constructor used by the driver, sets filename
     */
    public HuffmanCoding(String f) { 
        fileName = f; 
    }


    /**
     *Creates a an array list of CharFreq obkects that appear > 0 times in input file.
     *Sorts them by appearance.
     */
    public void makeSortedList() {
        StdIn.setFile(fileName);

        double CharCount = 0;
        ArrayList<Character> Charlist = new ArrayList<Character>();
        ArrayList<Character> uniqueChars = new ArrayList<Character>();

        while (StdIn.hasNextChar()){
            CharCount++;
            Charlist.add(StdIn.readChar());
        }


        ArrayList<CharFreq> Char = new ArrayList<CharFreq>(128);
        for (int i = 0; i < Charlist.size(); i++) {
            if(!uniqueChars.contains(Charlist.get(i))){
                uniqueChars.add(Charlist.get(i));
                double ThisCharcount = 0;
                for (int j = 0; j < Charlist.size(); j++) {
                    if(Charlist.get(j) == Charlist.get(i)){
                        ThisCharcount++;
                    }
                }
                CharFreq AcharFreq = new CharFreq(Charlist.get(i), (ThisCharcount/CharCount));
                Char.add(AcharFreq);
            }          
        }

        if(Char.size() < 1){
            int ASCIIvalue = (int) Char.get(0).getCharacter();
            if(ASCIIvalue == 127){
                CharFreq AcharFreq = new CharFreq((char) 0, 0);
                Char.add(AcharFreq);
            }
            else{
                ASCIIvalue++;
                CharFreq AcharFreq = new CharFreq((char) ASCIIvalue, 0);
                Char.add(AcharFreq);
            }
        }

        sortedCharFreqList = Char;
        Collections.sort(sortedCharFreqList);  
    }
    

    /**
     * Uses sortedCharFreqList to build a huffman coding tree. The root is stored as a private TreeNode
     */
    public void makeTree() {

        // makes two new queues for HuffMan Algorithm
        Queue<TreeNode> Target = new Queue<TreeNode>();
        Queue<TreeNode> Source = new Queue<TreeNode>();

        //fills the Source Queue with the data gathered makesourtedlist, char proboccurence from input file
        for (int i = 0; i < sortedCharFreqList.size(); i++) {
            TreeNode ANode = new TreeNode(sortedCharFreqList.get(i), null, null);
            Source.enqueue(ANode);
        }

        // creates to empty nodes that will be filled with the two smallest nodes from target and source each iteration
        TreeNode smallest = new TreeNode();
        TreeNode smallest2nd = new TreeNode();


        while(!Source.isEmpty()){  
            // From here till next // gets the smallest Node in both queues
            TreeNode smallestSource = new TreeNode();  if(!Source.isEmpty()){ smallestSource = Source.peek();}
            TreeNode smallestTarget = new TreeNode(); if(!Target.isEmpty()){ smallestTarget = Target.peek();}

            if(smallestSource.getData() != null && smallestTarget.getData() != null){
                if(smallestSource.getData().getProbOcc() <= smallestTarget.getData().getProbOcc()){
                    smallest = Source.dequeue();
                }
                else{
                    smallest = Target.dequeue();
                }
            }
            else if(smallestSource.getData() == null && smallestTarget.getData() == null){
                smallest = null;
            }
            else if(smallestTarget.getData() == null){
                smallest = Source.dequeue();
            }
            else {
                smallest = Target.dequeue();
            }

            // from here until next // gets the 2nd smallest node in both queues.
            TreeNode smallestSource2 = new TreeNode(); if(!Source.isEmpty()){ smallestSource2 = Source.peek();}
            TreeNode smallestTarget2 = new TreeNode(); if(!Target.isEmpty()){ smallestTarget2 = Target.peek();}

            if(smallestSource2.getData() != null && smallestTarget2.getData() != null){
                if(smallestSource2.getData().getProbOcc() <= smallestTarget2.getData().getProbOcc()){
                    smallest2nd = Source.dequeue();
                }
                else{
                    smallest2nd = Target.dequeue();
                }
            }
            else if(smallestSource2.getData() == null && smallestTarget2.getData() == null){
                smallest2nd = null;
            }
            else if(smallestTarget2.getData() == null){
                smallest2nd = Source.dequeue();
            }
            else {
                smallest2nd = Target.dequeue();
            }

            if(smallest != null && smallest2nd != null){
                CharFreq dataForTreeNode = new CharFreq(null, smallest.getData().getProbOcc() + smallest2nd.getData().getProbOcc());
                TreeNode ATreeNode = new TreeNode(dataForTreeNode, smallest, smallest2nd);
                Target.enqueue(ATreeNode);
            }
            else if(smallest2nd == null){
                Target.enqueue(smallest);
            }
            else{
                Target.enqueue(smallest2nd);
            }
        }

        while(Target.size() > 1){
            smallest = Target.dequeue();
            smallest2nd = Target.dequeue();
            CharFreq dataForTreeNode = new CharFreq(null, smallest.getData().getProbOcc() + smallest2nd.getData().getProbOcc());
            TreeNode ATreeNode = new TreeNode(dataForTreeNode, smallest, smallest2nd);
            Target.enqueue(ATreeNode);
        }

        huffmanRoot = Target.peek();


    }



     /**
     * creates bitstring encodings for an array size 128. In this array each index is populated
     * by a ASCII character's bitsting encoding. If a character is not present that index will
     * be left null.
     */

    private ArrayList<Encoder> makeEncodingslist(String str, TreeNode Tree){
        ArrayList<Encoder> ReturnList = new ArrayList<Encoder>(128);

        if(Tree.getLeft() == null && Tree.getRight() == null && Tree.getData().getCharacter() != null){
            Encoder Encoding = new Encoder();
            Encoding.setCharacter(Tree.getData().getCharacter());
            Encoding.setString(str);
            ReturnList.add(Encoding);
            return ReturnList;
        }

        if(Tree.getLeft() != null){ReturnList.addAll(makeEncodingslist(str + "0", Tree.getLeft()));}
        if(Tree.getRight()!= null){ReturnList.addAll(makeEncodingslist(str + "1", Tree.getRight()));}

        return ReturnList;
    }


    public void makeEncodings() {

        String[] EncodedChars = new String[128];
        String Bit = "";
        ArrayList<Encoder> Encoders = makeEncodingslist(Bit, huffmanRoot);

        for (int i = 0; i < Encoders.size(); i++) {
            if(Encoders.get(i)==null){
                break;
            }
            Encoder ThisEncoder = Encoders.get(i);
            Character CharVal = ThisEncoder.getCharacter();
            String coding = ThisEncoder.getCode();
            int ASCIIvalue = (int) CharVal;
            EncodedChars[ASCIIvalue] = coding;
            
        }

        encodings = EncodedChars;

        }


    /**
     * Using encodings, this method writes final encoding to 1 and 0's.
     */
    public void encode(String encodedFile) {
        StdIn.setFile(fileName);
        String zeroone = "";
        while(StdIn.hasNextChar()){
            Character encoded = StdIn.readChar();
            int encodedint = (int) encoded;
            zeroone = zeroone + encodings[encodedint];
        }
        
        writeBitString(encodedFile, zeroone);
    }
    
    /**
     * Writes a given string of 1's and 0's to the given file byte by byte
     * and NOT as characters of 1 and 0 which take up 8 bits each
     */
    public static void writeBitString(String filename, String bitString) {
        byte[] bytes = new byte[bitString.length() / 8 + 1];
        int bytesIndex = 0, byteIndex = 0, currentByte = 0;

        // Pad the string with initial zeroes and then a one in order to bring
        // its length to a multiple of 8. When reading, the 1 signifies the
        // end of padding.
        int padding = 8 - (bitString.length() % 8);
        String pad = "";
        for (int i = 0; i < padding-1; i++) pad = pad + "0";
        pad = pad + "1";
        bitString = pad + bitString;

        // For every bit, add it to the right spot in the corresponding byte,
        // and store bytes in the array when finished
        for (char c : bitString.toCharArray()) {
            if (c != '1' && c != '0') {
                System.out.println("Invalid characters in bitstring");
                return;
            }

            if (c == '1') currentByte += 1 << (7-byteIndex);
            byteIndex++;
            
            if (byteIndex == 8) {
                bytes[bytesIndex] = (byte) currentByte;
                bytesIndex++;
                currentByte = 0;
                byteIndex = 0;
            }
        }
        
        try {
            FileOutputStream out = new FileOutputStream(filename);
            out.write(bytes);
            out.close();
        }
        catch(Exception e) {
            System.err.println("Error when writing to file!");
        }
    }

    /**
     * converts the encoded file back to text.
     */
    public void decode(String encodedFile, String decodedFile) {
      
        StdOut.setFile(decodedFile);
        String encodedtxt = readBitString(encodedFile);
        decodedFile = "";
        for (int i= 0; i < encodedtxt.length();) {
            TreeNode decoder = huffmanRoot;
            while (decoder.getData().getCharacter() == null){
                if(encodedtxt.charAt(i) == '0'){
                    decoder = decoder.getLeft();
                }
                else{
                    decoder = decoder.getRight();
                }
                i++; 
            }
            Character encodedChar = decoder.getData().getCharacter();
            decodedFile += encodedChar;
        }
        StdOut.print(decodedFile);
    }

    /**
     * Reads a given file byte by byte, and returns a string of 1's and 0's
     * representing the bits in the file
     */
    public static String readBitString(String filename) {
        String bitString = "";
        
        try {
            FileInputStream in = new FileInputStream(filename);
            File file = new File(filename);

            byte bytes[] = new byte[(int) file.length()];
            in.read(bytes);
            in.close();
            
            // For each byte read, convert it to a binary string of length 8 and add it
            // to the bit string
            for (byte b : bytes) {
                bitString = bitString + 
                String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
            }

            // Detect the first 1 signifying the end of padding, then remove the first few
            // characters, including the 1
            for (int i = 0; i < 8; i++) {
                if (bitString.charAt(i) == '1') return bitString.substring(i+1);
            }
            
            return bitString.substring(8);
        }
        catch(Exception e) {
            System.out.println("Error while reading file!");
            return "";
        }
    }

    /*
     * Getters used by the driver. 
     */

    public String getFileName() { 
        return fileName; 
    }

    public ArrayList<CharFreq> getSortedCharFreqList() { 
        return sortedCharFreqList; 
    }

    public TreeNode getHuffmanRoot() { 
        return huffmanRoot; 
    }

    public String[] getEncodings() { 
        return encodings; 
    }

    /*
     * class used to make encodings in HuffmanRootEncodings
     */

    private class Encoder {
        private String Code;
        private Character Character;

        public Encoder() {
            Character = null;
            Code = null;
        }

        public Encoder(String C, Character CH){
            Code = C;
            Character = CH;
        }

        public Character getCharacter () {
            return Character;
        }

        public void setCharacter(Character Character){
            this.Character = Character;
        }
    
        public String getCode() {
            return Code;
        }
    
        public void setString (String Code) {
            this.Code = Code;
        }

    }
}