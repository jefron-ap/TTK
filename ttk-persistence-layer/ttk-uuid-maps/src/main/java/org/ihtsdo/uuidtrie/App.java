package org.ihtsdo.uuidtrie;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Hello world!
 *
 */
public class App {

    public static void main(String[] args) {
        //tryRandomUuids();
        tryTestFile();
        
    }

    private static void tryTestFile() {
              int records = 0;
      
        
        try {
            UuidNidTrieMap uuidNidTrieMap = new UuidNidTrieMap();
            FileInputStream fis = new FileInputStream("uuidNid1.binary");
            DataInputStream dis = new DataInputStream(fis);
            try {
                while (true) {
                    records++;
                    UUID uuid = new UUID(dis.readLong(), dis.readLong());
                    int nid = dis.readInt();
                    uuidNidTrieMap.add(uuid, nid);
                    
                }
            } catch (EOFException eOFException) {
                System.out.println("Read " + records + " records.");
            }
        } catch (Throwable ex) {
           System.out.println("Read " + records + " records before exception.");
           Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
            
        }
    }

        
    private static void tryRandomUuids() {
        try {
            for (int testNid = -1; testNid > -1000; testNid--) {
                UUID test = UUID.randomUUID();
                UuidNidTrieMap uuidNidTrieMap = new UuidNidTrieMap();

                uuidNidTrieMap.add(test, testNid);
                int resultNid = uuidNidTrieMap.get(test);
                if (resultNid == testNid) {
                    System.out.println("Found nid for: " + test + " nid: " + resultNid);
                } else {
                    System.out.println("! Failed to find nid for: " + test + " nid: " + resultNid);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
