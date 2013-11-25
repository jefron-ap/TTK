/*
 * Copyright 2013 International Health Terminology Standards Development Organisation.
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
package org.ihtsdo.otf.query;

import org.ihtsdo.otf.tcc.api.nid.HybridNidSet;
import org.ihtsdo.otf.tcc.api.nid.IntSet;
import java.io.IOException;
import org.ihtsdo.otf.tcc.api.nid.ConcurrentBitSet;
import org.ihtsdo.otf.tcc.api.nid.NativeIdSetItrBI;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;

/**
 *
 * @author dylangrald
 */
public class HybridNidSetTest {
    
    public HybridNidSetTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }
    public HybridNidSet getLargeHybrid() {
        HybridNidSet instance = new HybridNidSet();
        int[] nids = new int[instance.getThreshold() - 10];
        for (int i = 0; i < instance.getThreshold() - 10; i++) {
            nids[i] = -(i + 2);
        }
        instance.addAll(nids);

        return instance;
    }

    public HybridNidSet getLargeId() {
        HybridNidSet instance = new HybridNidSet();
        ConcurrentBitSet idSet = new ConcurrentBitSet();
        for (int i = 0; i < instance.getThreshold() + 2; i++) {
            idSet.add(-(i + 2));
        }
        instance = new HybridNidSet(idSet);
        return instance;
    }

    /**
     * Test of and method, of class HybridNidSet.
     */
    @Test
    @Ignore // to slow for now... KEC
    public void testAnd() {
        System.out.println("and");
        //HybridNidSet first = new HybridNidSet(getLargeId());
        HybridNidSet first = getLargeId();
        //HybridNidSet second = getLargeHybrid();
        HybridNidSet second = new HybridNidSet(getLargeHybrid());
        HybridNidSet third = new HybridNidSet(new int[]{-2, -3, -4});
        second.and(third);
        assertArrayEquals(new int[] {-4,-3,-2}, second.getSetValues());
        assertTrue(second.nidSet.getClass().isAssignableFrom(IntSet.class));


    }

    /**
     * Test of andNot method, of class HybridNidSet.
     */
    @Test
    @Ignore // to slow for now... KEC
    public void testAndNot() {
        System.out.println("andNot");
        HybridNidSet first = new HybridNidSet(getLargeId());
        HybridNidSet second = new HybridNidSet(getLargeHybrid());
        first.andNot(second);
        assertEquals(12, first.size());
    }

    /**
     * Test of contains method, of class HybridNidSet.
     */
    @Test
    public void testContains() {
        System.out.println("contains");
        int nid = -3;
        HybridNidSet instance = new HybridNidSet(nid);
        assertTrue(instance.contains(nid));
    }

    /**
     * Test of getSetValues method, of class HybridNidSet.
     */
    @Test
    public void testGetSetValues() {
        System.out.println("getSetValues");
        HybridNidSet instance = new HybridNidSet(new int[]{-4, -3, -2});
        assertArrayEquals(new int[]{-4, -3, -2}, instance.getSetValues());

    }

    /**
     * Test of add method, of class HybridNidSet.
     */
    @Test
    public void testAdd() {
        System.out.println("add");
        int nid = 0;
        HybridNidSet instance = new HybridNidSet();
        instance.add(nid);
        assertTrue(instance.size() == 1);
    }

    /**
     * Test of remove method, of class HybridNidSet.
     */
    @Test
    public void testRemove() {
        System.out.println("remove");
        HybridNidSet instance = new HybridNidSet(new int[]{-2, -3});
        instance.remove(-3);
        assertEquals(1, instance.size());
    }

    /**
     * Test of addAll method, of class HybridNidSet.
     */
    @Test
    public void testAddAll() {
        System.out.println("addAll");
        HybridNidSet instance = new HybridNidSet();
        instance.addAll(new int[] {-2,-3,-4});
        assertArrayEquals(new int[] {-4,-3,-2}, instance.getSetValues());
        assertTrue(instance.nidSet.getClass().isAssignableFrom(IntSet.class));
    }

    /**
     * Test of removeAll method, of class HybridNidSet.
     */
    @Test
    @Ignore // to slow for now... KEC
    public void testRemoveAll() {
        System.out.println("removeAll");
        HybridNidSet instance = new HybridNidSet();
        int[] nids = new int[4];
        for (int i = 0; i < 4; i++) {
            nids[i] = -(i + 1);
        }
        instance.addAll(nids);
        instance.removeAll(nids);
        assertTrue(instance.isEmpty());


    }

    /**
     * Test of clear method, of class HybridNidSet.
     */
    @Test
    @Ignore // to slow for now... KEC
    public void testClear() {
        System.out.println("clear");
        HybridNidSet instance = new HybridNidSet();
        instance.add(-2);
        instance.clear();
        assertTrue(instance.isEmpty());
    }

    /**
     * Test of size method, of class HybridNidSet.
     */
    @Test
    public void testSize() {
        System.out.println("size");
        HybridNidSet instance = new HybridNidSet();
        instance.add(-1);
        assertTrue(instance.size() == 1);
    }

    /**
     * Test of getMax method, of class HybridNidSet.
     */
    @Test
    @Ignore // to slow for now... KEC
    public void testGetMax() {
        System.out.println("getMax");
        HybridNidSet instance = new HybridNidSet();
        int[] nids = new int[156255];
        for (int i = 0; i < 156255; i++) {
            nids[i] = -(i + 2);
        }
        instance.addAll(nids);
        assertEquals(instance.getMax(), -2);

    }

    /**
     * Test of getMin method, of class HybridNidSet.
     */
    @Test
    @Ignore // to slow for now... KEC
    public void testGetMin() {
        System.out.println("getMin");
        HybridNidSet instance = new HybridNidSet();
        assertEquals(-(instance.getThreshold() + 3), getLargeId().getMin());
    }

    /**
     * Test of contiguous method, of class HybridNidSet.
     */
    @Test
    public void testContiguous() {
        System.out.println("contiguous");
        assertTrue(getLargeHybrid().contiguous());
    }

    /**
     * Test of union method, of class HybridNidSet.
     */
    @Test
    @Ignore // to slow for now... KEC
    public void testUnion() {
        System.out.println("union");
        HybridNidSet first = getLargeId();
        HybridNidSet second = getLargeHybrid();
        second.union(first);
        assertEquals(first.size(), second.size());



    }

    /**
     * Test of xor method, of class HybridNidSet.
     */
    @Test
    @Ignore // to slow for now... KEC
    public void testXor() {
        System.out.println("xor");
        HybridNidSet first = getLargeId();
        HybridNidSet second = getLargeHybrid();
        second.xor(first);
        assertEquals(12, second.size());
        assertTrue(second.nidSet.getClass().isAssignableFrom(IntSet.class));
        /*
         HybridNidSet first = new HybridNidSet(new int[] {-4,-3,-2});
         HybridNidSet second = new HybridNidSet(new int[] {-5, -4, -3});
         first.xor(second);
         assertEquals(1, first.size());
         */
    }

    /**
     * Test of isMember method, of class HybridNidSet.
     */
    @Test
    @Ignore // to slow for now... KEC
    public void testIsMember() {
        System.out.println("isMember");
        int nid = -3;
        HybridNidSet instance = getLargeHybrid();
        assertTrue(instance.isMember(nid));
    }

    /**
     * Test of setMember method, of class HybridNidSet.
     */
    @Test
    public void testSetMember() {
        System.out.println("setMember");
        int nid = -2;
        HybridNidSet instance = new HybridNidSet();
        instance.setMember(nid);
        assertTrue(instance.isMember(nid));
    }

    /**
     * Test of setNotMember method, of class HybridNidSet.
     */
    @Test
    public void testSetNotMember() {
        System.out.println("setNotMember");
        int nid = -2;
        HybridNidSet instance = new HybridNidSet();
        instance.addAll(new int[]{-2, -3, -4});
        instance.setNotMember(nid);
        assertTrue(!instance.isMember(-2));

    }
    
    @Test
    public void testIterator() throws IOException{
        System.out.println("iterator");
        HybridNidSet instance = new HybridNidSet(new int[] {-4,-3,-2});
        NativeIdSetItrBI iter = instance.getIterator();
        int[] setValues = new int[] {-2,-3,-4};
        int count = 0;
        while(iter.next()){
            assertEquals(setValues[count], iter.nid());
            count++;
        }
    }
}