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

import org.ihtsdo.otf.tcc.api.nid.IntSet;
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
public class IntSetTest {

    public IntSetTest() {
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

    /**
     * Test of size method, of class IntSet.
     */
    @Test
    public void testSize() {
        System.out.println("size");
        IntSet instance = new IntSet();
        instance.add(-2);
        assertEquals(1, instance.size());
    }

    /**
     * Test of isMember method, of class IntSet.
     */
    @Test
    public void testIsMember() {
        System.out.println("isMember");
        IntSet instance = new IntSet();
        instance.addAll(new int[]{-2, -3, -4});
        assertTrue(instance.isMember(-2));
    }

    /**
     * Test of setMember method, of class IntSet.
     */
    @Test
    public void testSetMember() {
        System.out.println("setMember");
        IntSet instance = new IntSet();
        instance.setMember(-2);
        assertEquals(1, instance.size());
    }

    /**
     * Test of and method, of class IntSet.
     */
    @Test
        @Ignore // to slow for now... KEC

    public void testAnd() {
        System.out.println("and");
       IntSet first = new IntSet(new int[]{-4, -3, -2});
        IntSet second = new IntSet(new int[]{-4, -5, -6});
        first.and(second);
        assertArrayEquals(new int[] {-4}, first.getSetValues());
    }

    /**
     * Test of or method, of class IntSet.
     */
    @Test
    public void testOr() {
        System.out.println("or");
        IntSet first = new IntSet(new int[]{-4, -3, -2});
        IntSet second = new IntSet(new int[]{-4, -5, -6});
        first.or(second);
        assertArrayEquals(new int[] {-6,-5,-4,-3,-2}, first.getSetValues());
    }

    /**
     * Test of xor method, of class IntSet.
     */
    @Test
    public void testXor() {
        System.out.println("xor");
        IntSet first = new IntSet(new int[]{-4, -3, -2});
        IntSet second = new IntSet(new int[]{-4, -5, -6});
        first.xor(second);
        assertArrayEquals(new int[] {-6,-5,-3,-2}, first.getSetValues());
    }

    /**
     * Test of contains method, of class IntSet.
     */
    @Test
    public void testContains() {
        System.out.println("contains");
        IntSet instance = new IntSet();
        instance.addAll(new int[]{-2, -3, -4});
        assertTrue(instance.contains(-3));
    }

    /**
     * Test of getSetValues method, of class IntSet.
     */
    @Test
    public void testGetSetValues() {
        System.out.println("getSetValues");
        IntSet instance = new IntSet();
        instance.addAll(new int[]{-2, -3, -4});
        assertArrayEquals(new int[]{-4, -3, -2}, instance.getSetValues());
    }

    /**
     * Test of add method, of class IntSet.
     */
    @Test
    public void testAdd() {
        System.out.println("add");
        int nid = 0;
        IntSet instance = new IntSet();
        instance.add(nid);
        assertTrue(instance.contains(nid));
    }

    /**
     * Test of addAll method, of class IntSet.
     */
    @Test
    public void testAddAll() {
        System.out.println("addAll");
        IntSet instance = new IntSet();
        instance.addAll(new int[]{-2, -3, -4});
        assertTrue(instance.contains(-3));
    }

    /**
     * Test of remove method, of class IntSet.
     */
    @Test
    public void testRemove() {
        System.out.println("remove");
        IntSet instance = new IntSet();
        instance.addAll(new int[]{-2, -3, -4});
        assertTrue(instance.contains(-3));
        instance.remove(-3);
        assertTrue(!instance.contains(-3));
    }

    /**
     * Test of removeAll method, of class IntSet.
     */
    @Test
    public void testRemoveAll() {
        System.out.println("removeAll");
        IntSet instance = new IntSet();
        instance.addAll(new int[]{-2, -3, -4, -5, -6});
        assertTrue(instance.contains(-3));
        instance.removeAll(new int[]{-2, -3, -4});
        assertArrayEquals(new int[]{-6, -5}, instance.getSetValues());
    }

    /**
     * Test of clear method, of class IntSet.
     */
    @Test
    public void testClear() {
        System.out.println("clear");
        IntSet instance = new IntSet(new int[]{-2, -3, -4});
        assertEquals(3, instance.size());
        instance.clear();
        assertEquals(0, instance.size());
    }

    /**
     * Test of getMax method, of class IntSet.
     */
    @Test
    public void testGetMax() {
        System.out.println("getMax");
        IntSet first = new IntSet(new int[]{-4, -3, -2});
        assertEquals(-2, first.getMax());
    }

    /**
     * Test of getMin method, of class IntSet.
     */
    @Test
    public void testGetMin() {
        System.out.println("getMin");
        IntSet first = new IntSet(new int[]{-4, -3, -2});
        assertEquals(-4, first.getMin());
    }

    /**
     * Test of contiguous method, of class IntSet.
     */
    @Test
    public void testContiguous() {
        System.out.println("contiguous");
        IntSet first = new IntSet(new int[]{-4, -3, -2});
        IntSet second = new IntSet(new int[]{-4, -6, -8, -9});
        assertTrue(first.contiguous());
        assertTrue(!second.contiguous());
    }

    /**
     * Test of union method, of class IntSet.
     */
    @Test
    public void testUnion() {
        System.out.println("union");
        IntSet first = new IntSet(new int[]{-4, -3, -2});
        IntSet second = new IntSet(new int[]{-4, -5, -6});
        first.union(second);
        assertArrayEquals(new int[] {-6,-5,-4,-3,-2}, first.getSetValues());
    }

    /**
     * Test of setNotMember method, of class IntSet.
     */
    @Test
    public void testSetNotMember() {
        System.out.println("setNotMember");
        IntSet first = new IntSet(new int[]{-4, -3, -2});
        first.setNotMember(-3);
        assertTrue(!first.contains(-3));
    }

    /**
     * Test of andNot method, of class IntSet.
     */
    @Test
    public void testAndNot() {
        System.out.println("andNot");
        IntSet first = new IntSet(new int[]{-4, -3, -2});
        IntSet second = new IntSet(new int[]{-4, -5, -6});
        first.andNot(second);
        assertArrayEquals(new int[] {-3, -2}, first.getSetValues());
    }
}