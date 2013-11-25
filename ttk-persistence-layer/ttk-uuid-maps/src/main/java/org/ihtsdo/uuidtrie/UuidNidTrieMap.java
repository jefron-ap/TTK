/*
 * Copyright 2012 International Health Terminology Standards Development Organisation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ihtsdo.uuidtrie;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * 
 * Consider some alternate structures...
 * 
 * 4 bit words = 32 "letters" in the alphabet
 * 
 * [parent id][32 bit bitset to give keys to sequences magnitude][key, key, key...]
 * 
 * 
 * @author kec
 */
public class UuidNidTrieMap {

    private static final int BLOCK_SIZE = 102400;
    int[][] trieBlocks = new int[BLOCK_SIZE][];
    AtomicInteger nextTrieNode = new AtomicInteger(0);
    AtomicInteger nextTrieBlock = new AtomicInteger(1);

    {
        trieBlocks[0] = new int[BLOCK_SIZE];
    }

    public boolean add(UUID uuid, int nid) throws IOException {
        byte[] positionBytes = uuidToPositionBytes(uuid);
        int nodePosition;
        int block = 0;
        boolean add = false;
        int currentIndex = 0;
        int nextIndex = 0;
        
        for (int i = 0; i < 32; i++) {
            currentIndex = currentIndex + positionBytes[i];
            nextIndex = trieBlocks[block][currentIndex];
            if (nextIndex == 0) {
                nodePosition = nextTrieNode.incrementAndGet() * 16;
                nextIndex = nodePosition + positionBytes[i];
                trieBlocks[block][currentIndex] = nextIndex;
                add = true;
            }
            currentIndex = nextIndex;
        }
        trieBlocks[block][currentIndex] = nid;
        return add;
    }
    
    public int get(UUID uuid) throws IOException {
        byte[] positionBytes = uuidToPositionBytes(uuid);
        int block = 0;
        int currentIndex = 0;
        int nextIndex = 0;
        for (int i = 0; i < 32; i++) {
            currentIndex = currentIndex + positionBytes[i];
            nextIndex = trieBlocks[block][currentIndex];
            if (nextIndex == 0) {
                return Integer.MAX_VALUE;
            }
            currentIndex = nextIndex;
        }
        return trieBlocks[block][currentIndex];
    }

    private static byte[] uuidToPositionBytes(UUID uuid) throws IOException {
        // TODO provide a more efficient way to convert to uuidBytes;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeLong(uuid.getMostSignificantBits());
        dos.writeLong(uuid.getLeastSignificantBits());
        byte[] uuidBytes = baos.toByteArray();
        assert uuidBytes.length == 16;
        byte[] positionBytes = new byte[32];
        for (int i = 0; i < 16; i++) {
            positionBytes[i * 2] = (byte) ((uuidBytes[i] >> 4) & 0x0F);
            positionBytes[i * 2 + 1] = (byte) (uuidBytes[i] & 0x0F);
        }
        return positionBytes;
    }
}
