import java.io.*;

public class Oving8AlgDat {
    public static void main(String[] args) throws IOException {
        try {
            compressing();
            deCompressing();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void compressing() throws IOException {
        HuffmanGraph graph = new HuffmanGraph();
        String filePathForReadingLZ = "src/Øving8/Forelesning.txt";
        String filePathForCompressedLZ = "src/Øving8/compressedFileLZ.txt";
        String filepathForCompressedFileHuffman = "src/Øving8/compressedFileHuffman.txt";

        System.out.println("Compressing text with Lempelziv ...");
        byte[] input = FileHandler.readFileToByteArray(filePathForReadingLZ);
        byte[] compressed = LempelZiv.compress(input);
        compressed = FileHandler.removeZerosAtTheEnd(compressed);
        FileHandler.writeFromByteArrayToFile(filePathForCompressedLZ, compressed);
        System.out.println("Finished compressing Lempelziv\n");

        System.out.println("Compressing text with Huffman ...");
        graph.compressText(filePathForCompressedLZ, filepathForCompressedFileHuffman);
        System.out.println("Finished compressing Huffman\n");
    }

    private static void deCompressing() throws IOException {
        String filepathDeCompressedLZ = "src/Øving8/deCompressedFileLZ.txt";
        String filepathForDecompressedFileHuffman = "src/Øving8/deCompressedFileHuffman.txt";
        String filepathForCompressedFileHuffman = "src/Øving8/compressedFileHuffman.txt";

        HuffmanGraph graph = new HuffmanGraph();

        System.out.println("Decompressing text with Huffman...");
        graph.getDeCompressedText(filepathForCompressedFileHuffman, filepathForDecompressedFileHuffman);
        System.out.println("Finished decompressing Huffman\n");

        System.out.println("Decompressing text with Lempelziv...");
        byte[] compressed = FileHandler.readFileToByteArray(filepathForDecompressedFileHuffman);
        byte[] deCompressed = LempelZiv.decompress(compressed);
        deCompressed = FileHandler.removeZerosAtTheEnd(deCompressed);
        FileHandler.writeFromByteArrayToFile(filepathDeCompressedLZ, deCompressed);
        System.out.println("Finished decompressing Lempelziv");
    }
}

class LempelZiv {
    public static byte[] compress(byte[] data) {

        byte[] searchBuffer = new byte[data.length]; // Hvis vi passer på at søkebufferen ikke er lenger enn en/to bytes, vil vi unngå eventuelle problemer rundt det muligens...
        int searchBufferLength = 0;

        byte[] compressed = new byte[data.length];

        byte[] bytesSearchingFor = new byte[data.length];
        int bytesSearchingForLength = 0;

        int compressedIndex = 1;

        int nonRepeatedCounter = 0;

        boolean bufferIsFull = false;
        int bufferOverflow = 0;

        for (int i = 0; i < data.length; i++) {


            bytesSearchingFor[bytesSearchingForLength] = data[i];
            bytesSearchingForLength++;

            int index = textSearch(bytesSearchingFor, searchBuffer, bytesSearchingForLength, searchBufferLength);

            if (index == -1 || i == data.length-1 ) {

                if (bytesSearchingForLength > 1) {
                    index = textSearch(removeTheLastElement(bytesSearchingFor), searchBuffer, bytesSearchingForLength-1, searchBufferLength);

                    if (bufferIsFull) {
                        index = index + bufferOverflow;
                    }

                    int offset = i - index - bytesSearchingForLength + 1;
                    int length = bytesSearchingForLength;

                    if (length > 3) {
                        compressed[compressedIndex++] = (byte) (offset*-1);
                        compressed[compressedIndex++] = (byte) (length-1);
                        compressed[compressedIndex++] = 1;
                        compressed[compressedIndex++] = data[i];

                        nonRepeatedCounter = 1;
                    } else {

                        for (int j = 0; j < bytesSearchingForLength; j++) {

                            if (nonRepeatedCounter < 127) {

                                nonRepeatedCounter++;
                                compressed[compressedIndex++] = bytesSearchingFor[j];
                                compressed[compressedIndex - nonRepeatedCounter-1] = (byte) nonRepeatedCounter;
                            } else {
                                nonRepeatedCounter = 1;
                                compressed[compressedIndex++] = (byte) nonRepeatedCounter;
                                compressed[compressedIndex++] = bytesSearchingFor[j];
                            }
                        }
                    }

                    for (int j = 0; j < bytesSearchingForLength; j++) {
                        searchBuffer[searchBufferLength] = bytesSearchingFor[j];
                        searchBufferLength++;
                    }

                } else {

                    for (int j = 0; j < bytesSearchingForLength; j++) {
                        if (nonRepeatedCounter < 127) {

                            nonRepeatedCounter++;
                            compressed[compressedIndex++] = bytesSearchingFor[j];
                            compressed[compressedIndex - nonRepeatedCounter-1] = (byte) nonRepeatedCounter;

                        } else {
                            nonRepeatedCounter = 1;
                            compressed[compressedIndex - nonRepeatedCounter-1] = (byte) nonRepeatedCounter;
                        }
                    }


                    for (int j = 0; j < bytesSearchingForLength; j++) {
                        searchBuffer[searchBufferLength] = bytesSearchingFor[j];
                        searchBufferLength++;
                    }

                }

                bytesSearchingFor = new byte[data.length];
                bytesSearchingForLength = 0;
            }

            if (searchBufferLength > 127) {
                for (int j = 0; j < searchBufferLength-127; j++) {
                    searchBuffer = removeTheFirstElement(searchBuffer);
                    bufferOverflow++;
                }
                searchBufferLength = 127;
                bufferIsFull = true;
            }

        }

        return compressed;
    }

    private static byte[] removeTheLastElement(byte[] array) {
        byte[] newArray = new byte[array.length-1];
        System.arraycopy(array, 0, newArray, 0, array.length - 1);
        return newArray;
    }

    private static byte[] removeTheFirstElement(byte[] array) {
        byte[] newArray = new byte[array.length];
        System.arraycopy(array, 1, newArray, 0, array.length - 1);
        return newArray;
    }


    public static int textSearch(byte[] searchTerm, byte[] text, int searchTermLenght, int textLenght) {

        int i = 0;
        while ((i + searchTermLenght) <= text.length) {
            int j = 0;

            while (text[i+j] == searchTerm[j]) {
                j++;

                if (j >= searchTermLenght) {
                    return i;
                }
            }
            i += 1;
        }
        return -1;
    }


    public static byte[] decompress(byte[] text) {

        int size = 0;
        for (byte b : text) {
            if (b != 0) size++;
            else break;
        }

        ByteArray output = new ByteArray();

        int i = 0;
        while (i < size) {

            if (text[i] >= 0) {

                int length = text[i];
                for (int j = 0; j < length; j++) {
                    output.add(text[++i]);
                }
                i++;

            } else {

                int forwardLength = text[i+1];
                int backReference = text[i]*-1;

                for (int j = 0; j < forwardLength; j++) {
                    output.add(output.bytes[output.size - backReference]);
                }

                i+=2;
            }
        }

        output.trim();
        return output.bytes;
    }
}

class ByteArray {
    byte[] bytes;
    int size;

    public ByteArray() {
        this.bytes = new byte[10];
        this.size = 0;
    }

    public void add(byte b) {

        if (size+1 > bytes.length){
            byte[] tempArray = new byte[bytes.length*2];
            System.arraycopy(bytes, 0, tempArray, 0, bytes.length);
            bytes = new byte[bytes.length*2];
            System.arraycopy(tempArray, 0, bytes, 0, tempArray.length);

        }
        bytes[size] = b;
        size++;
    }
    public void trim() {
        byte[] tempArray = new byte[size];

        System.arraycopy(bytes, 0, tempArray, 0, size);

        bytes = tempArray;

    }

    public void replace(int i, byte b) {
        bytes[i] = b;
    }
}

class HuffmanGraph {
    private final int ASCII = 256;
    final int BYTE_LENGTH = 8;
    private boolean foundNode;
    private HuffmanNode root;
    private int[] bitArray;

    public void compressText(String readFilename, String writeFilename) throws IOException {
        byte[] data;
        try {
            data = FileHandler.readFileToByteArray(readFilename);
        } catch (IOException e) {
            throw new IOException("The compression failed due to filereading from: " + readFilename);
        }
        int[] frequencyTbl = makeFrequencyTbl(data);
        makeHuffmanTree(frequencyTbl);

        int counter = 0;
        bitArray = new int[data.length * 3];
        for (byte dataByte : data) {
            if (dataByte < 0)
                counter = compressChar((char) (dataByte + ASCII), counter);
            else
                counter = compressChar((char) dataByte, counter);
        }

        byte[] byteArray = makeByteArray(bitArray);
        int lastByteLength = bitArray.length % BYTE_LENGTH;
        if (lastByteLength == 0)
            lastByteLength = BYTE_LENGTH;

        byteArray = expandByteArray(byteArray);
        byteArray[byteArray.length - 1] = (byte)lastByteLength;
        FileHandler.writeCompressedToFile(writeFilename, byteArray, frequencyTbl);
    }

    private int compressChar(char c, int counter) {
        HuffmanNode at = root;
        int i = 0;

        while(i++ < bitArray.length) {
            foundNode = false;
            traverseTree(at.getLeft(), c);
            if (foundNode) {
                at = at.getLeft();
                if (counter == bitArray.length)
                    bitArray = expandBitArray(bitArray);
                bitArray[counter++] = 0;
                continue;
            }
            traverseTree(at.getRight(), c);
            if (foundNode){
                at = at.getRight();
                if (counter == bitArray.length)
                    bitArray = expandBitArray(bitArray);
                bitArray[counter++] = 1;
            }
        }
        return counter;
    }

    private void traverseTree(HuffmanNode at, char lookedFor) {
        if (at == null)
            return;
        if (at.getData() == lookedFor)
            foundNode = true;
        traverseTree(at.getLeft(), lookedFor);
        traverseTree(at.getRight(), lookedFor);
    }

    public void getDeCompressedText(String readFilename, String writeFilename) throws IOException {
        int[] frequencyTbl = new int[ASCII];
        byte[] byteArray;
        int lastByteLength;
        try {
            Object[] data = FileHandler.readCompressedFileFully(readFilename);
            for (int i = 0; i < frequencyTbl.length; i++)
                frequencyTbl[i] = (int)data[i];

            byteArray = new byte[data.length - ASCII - 1];
            for (int i = 0; i < data.length - ASCII - 1; i++)
                byteArray[i] = (byte)data[i + ASCII];

            lastByteLength = Integer.parseInt(String.valueOf(data[data.length - 1]));
            bitArray = makeBitArray(byteArray, lastByteLength);
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException("The compression failed due to filereading");
        }

        makeHuffmanTree(frequencyTbl);

        int counter = 0;
        HuffmanNode at = root;
        final int BYTE_LIMIT = 127;
        byteArray = new byte[byteArray.length];

        for (int b : bitArray) {
            if (b == 0)
                at = at.getLeft();
            else
                at = at.getRight();

            if (at.getData() != (char) 0) {
                if (counter == byteArray.length)
                    byteArray = expandByteArray(byteArray);

                if ((int)at.getData() > BYTE_LIMIT)
                    byteArray[counter++] = (byte) ((char)(at.getData() - ASCII));
                else
                    byteArray[counter++] = (byte)at.getData();
                at = root;
            }
        }
        FileHandler.writeFromByteArrayToFile(writeFilename, byteArray);
    }

    private void makeHuffmanTree(int[] frequencyTbl) {
        MinHeap minHeap = makeHeap(frequencyTbl);

        HuffmanNode min1;
        HuffmanNode min2 = null;
        while (minHeap.getLength() > 0) {
            min1 = minHeap.getMin();
            if (minHeap.getLength() > 0)
                min2 = minHeap.getMin();

            assert min2 != null;
            HuffmanNode nextTree = new HuffmanNode(new Node(min1.getFrequency() + min2.getFrequency()));
            if (minHeap.getLength() > 0)
                minHeap.insert(nextTree);
            nextTree.setLeft(min1);
            nextTree.setRight(min2);
            root = nextTree;
        }
    }

    private int[] makeFrequencyTbl(byte[] text) {
        int[] frequency = new int[ASCII];
        for (byte c : text) {
            if (c < 0)
                frequency[(char) (c + ASCII)] += 1;
            else
                frequency[(char) c] += 1;
        }
        return frequency;
    }

    private MinHeap makeHeap(int[] frequencyTbl) {
        MinHeap heap = new MinHeap();
        for (int i = 0; i < frequencyTbl.length; i++)
            if (frequencyTbl[i] != 0)
                heap.insert(new HuffmanNode((char) i, frequencyTbl[i]));
        return heap;
    }

    private byte[] expandByteArray(byte[] array) {
        byte[] newArray = new byte[array.length + 1];
        for (int i = 0; i < array.length; i++)
            newArray[i] = array[i];
        return newArray;
    }

    private int[] expandBitArray(int[] bitArray) {
        int[] newArray = new int[bitArray.length + 1];
        for (int i = 0; i < bitArray.length; i++) {
            newArray[i] = bitArray[i];
        }
        return newArray;
    }

    private byte[] makeByteArray(int[] bitArray) {
        byte[] byteArray = new byte[(bitArray.length + 7) / 8];
        int index;
        int byteValue = 0;
        for (index = 0; index < bitArray.length; index++) {

            byteValue = (byteValue << 1) | bitArray[index];

            if (index % 8 == 7) {
                byteArray[index / 8] = (byte) byteValue;
            }
        }
        if (index % 8 != 0)
            byteArray[index / 8] = (byte) (byteValue << (8 - (index % 8)));

        return byteArray;
    }

    private int[] makeBitArray(byte[] byteArray, int lastByteLength) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < byteArray.length - 1; i++)
            sb.append(byteToBit(byteArray[i]));
        sb.append(lastByteToBit(byteArray[byteArray.length - 1], lastByteLength));

        int[] bitArray = new int[sb.length()];
        for (int i = 0; i < sb.length(); i++)
            bitArray[i] = Integer.parseInt(String.valueOf(sb.toString().toCharArray()[i]));

        return bitArray;
    }

    private String byteToBit(byte b) {
        int byteTobit = b;
        if (byteTobit < 0)
            byteTobit += ASCII;

        StringBuilder sb = new StringBuilder();
        while (sb.length() < BYTE_LENGTH) {
            sb.append(byteTobit % 2);
            byteTobit = byteTobit / 2;
        }
        return sb.reverse().toString();
    }

    private String lastByteToBit(byte b, int length) {
        StringBuilder bitString = new StringBuilder(byteToBit(b));
        return bitString.delete(length, bitString.length()).toString();
    }
}

class FileHandler {

    public static byte[] readFileToByteArray(String fileName) throws IOException {
        try (DataInputStream infile = new DataInputStream(new BufferedInputStream(new FileInputStream(fileName)))) {
            return infile.readAllBytes();
        } catch (IOException e) {
            throw new IOException("Something went wrong when trying to read file: " + fileName);
        }
    }

    public static Object[] readCompressedFileFully(String fileName) throws IOException{
        try (DataInputStream infile = new DataInputStream(new FileInputStream(fileName))) {
            int[] ints = readCompressedFileFrequencyTable(infile);
            byte[] bytes = readCompressedFileToByteArray(infile);
            Object[] objects = new Object[ints.length + bytes.length];

            for (int i = 0; i < ints.length; i++)
                objects[i]=ints[i];

            int index =256; // ASCII
            for (byte b : bytes)
                objects[index++] = b;

            return objects;
        } catch (IOException e) {
            throw new IOException("Something went wrong when trying to read file: " + fileName);
        }
    }

    private static int[] readCompressedFileFrequencyTable(DataInputStream infile) throws IOException {
        try{
            int[] frequency = new int[256];
            for(int i = 0; i<256; i++){
                int freq = infile.readInt();
                frequency[i] = freq;
            }
            return frequency;
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException("Something went wrong when trying to read file");
        }
    }

    private static byte[] readCompressedFileToByteArray(DataInputStream infile) throws IOException {
        try{
            byte[] bytes = new byte[infile.available()];
            for (int i = 0; i < bytes.length; i++) {
                bytes[i] = infile.readByte();
            }
            return bytes;
        } catch (IOException e) {
            throw new IOException("Something went wrong when trying to read file");
        }
    }

    public static void writeFromByteArrayToFile(String fileName, byte[] data) throws IOException {
        try (DataOutputStream outfile = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(fileName)))){
            outfile.write(data);
            outfile.flush(); // Is needed for .txt files for some reason
        } catch (IOException e) {
            throw new IOException("Something went wrong when trying to write to file: " + fileName);
        }
    }

    public static void writeCompressedToFile(String filename, byte[] byteArray, int[] frequency)throws IOException{
        File output = new File(filename);
        try(DataOutputStream dos = new DataOutputStream(new FileOutputStream(output))){
            for (int j : frequency)
                dos.writeInt(j);

            for (byte b : byteArray)
                dos.write(b);

        }catch (IOException e){
            throw new IOException("Couldn't write this to file");
        }
    }

    public static byte[] removeZerosAtTheEnd(byte[] array) {
        byte[] tempArray = array;

        int trueArraySize = 0;
        for (byte b : tempArray) {
            if (b != 0) {
                trueArraySize++;
            }
        }

        array = new byte[trueArraySize];

        System.arraycopy(tempArray, 0, array, 0, trueArraySize);

        return array;
    }
}

class MinHeap {
    private int length;
    private HuffmanNode[] nodes;

    public MinHeap() {
        this.length = 0;
        this.nodes = new HuffmanNode[8];
    }

    public void insert(HuffmanNode x) {
        int i = length++;
        if (i == nodes.length)
            expand();
        nodes[i] = x;

        int f;
        while (i > 0 && nodes[i].getFrequency() < nodes[f = over(i)].getFrequency()) {
            swap(nodes, i, f);
            i = f;
        }
    }

    private void expand() {
        HuffmanNode[] newList = new HuffmanNode[length<<1];
        for (int i = 0; i < nodes.length; i++) {
            if (nodes[i] == null) break;
            newList[i] = nodes[i];
        }
        nodes = newList;
    }

    public HuffmanNode getMin() {
        HuffmanNode min = nodes[0];
        nodes[0] = nodes[(length--) - 1];
        nodes[length] = null;
        fixHeap(0);
        return min;
    }

    public void fixHeap(int i) {
        int m = left(i);
        if (m < length) {
            int h = m + 1;
            if (h < length && nodes[h].getFrequency() < nodes[m].getFrequency())
                m = h;
            if (nodes[m].getFrequency() < nodes[i].getFrequency()){
                swap(nodes, i, m);
                fixHeap(m);
            }
        }
    }

    private void swap(Node[] nodes, int i, int m) {
        Node k = nodes[m];
        nodes[m] = nodes[i];
        nodes[i] = k;
    }

    private int over(int i) {
        return (i - 1) >> 1;
    }

    private int left(int i) {
        return (i << 1) + 1;
    }

    private int right(int i) {
        return (i + 1) << 1;
    }

    public int getLength() {
        return length;
    }
}

class HuffmanNode extends Node {
    private HuffmanNode left;
    private HuffmanNode right;

    public HuffmanNode(Node n) {
        super(n.getData(), n.getFrequency());
        left = null;
        right = null;
    }

    public HuffmanNode(char data, int frequency) {
        super(data, frequency);
        this.left = null;
        this.right = null;
    }

    public HuffmanNode getLeft() {
        return left;
    }

    public void setLeft(HuffmanNode left) {
        this.left = left;
    }

    public HuffmanNode getRight() {
        return right;
    }

    public void setRight(HuffmanNode right) {
        this.right = right;
    }

    @Override
    public String toString() {
        if (left == null || right == null)
            return getData() +
                    ", " +
                    "Frequency: " + getFrequency();
        return getData() +
                ", " +
                "Frequency: " + getFrequency() +
                " l: " + left.getFrequency() +
                ", r: " + right.getFrequency();
    }
}

class Node {
    private final int frequency;
    private final char data;

    public Node(int frequency) {
        this.data = (char) 0;
        this.frequency = frequency;
    }

    public Node(char data, int frequency) {
        this.data = data;
        this.frequency = frequency;
    }

    public int getFrequency() {
        return frequency;
    }

    public char getData() {
        return data;
    }
}