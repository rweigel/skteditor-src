/*
 * NOSA HEADER START
 *
 * The contents of this file are subject to the terms of the NASA Open 
 * Source Agreement (NOSA), Version 1.3 only (the "Agreement").  You may 
 * not use this file except in compliance with the Agreement.
 *
 * You can obtain a copy of the agreement at
 *   docs/NASA_Open_Source_Agreement_1.3.txt
 * or 
 *   https://spdf.gsfc.nasa.gov/skteditor/NASA_Open_Source_Agreement_1.3.txt
 *
 * See the Agreement for the specific language governing permissions
 * and limitations under the Agreement.
 *
 * When distributing Covered Code, include this NOSA HEADER in each
 * file and include the Agreement file at 
 * docs/NASA_Open_Source_Agreement_1.3.txt.  If applicable, add the 
 * following below this NOSA HEADER, with the fields enclosed by 
 * brackets "[]" replaced with your own identifying information: 
 * Portions Copyright [yyyy] [name of copyright owner]
 *
 * NOSA HEADER END
 *
 * Copyright (c) 2011-2022 United States Government as represented by 
 * the National Aeronautics and Space Administration. No copyright is 
 * claimed in the United States under Title 17, U.S.Code. All Other 
 * Rights Reserved.
 *
 * $Id: History.java,v 1.3 2022/03/24 10:38:44 btharris Exp $
 */
package gsfc.spdf.util;


import java.util.ArrayList;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;


/**
 * This class provides a mechanism to maintain a persistent history
 * of String values.  Its original purpose was to maintain a fixed
 * length history of filenames.  Once the history is full, subsequent
 * add operations cause the oldest element to be discarded.  Adding 
 * a duplicate element causes that element to move to the front of 
 * the history.  The persistence is implemented using the
 * <code>java.util.prefs.Preferences</code> class.
 *
 * @author B. Harris
 */
public class History {

    /**
     * History size limit.
     */
    private int limit = 10;

    /**
     * Identifier for this history.
     */
    private String id = null;

    /**
     * User Preference where history is stored.
     */
    private Preferences values = 
        Preferences.userNodeForPackage(History.class);


    /**
     * Creates a History of String values.
     *
     * @param id identifies this particular history.
     * @param limit size limit of this history.
     */
    public History(String id, int limit) {

        this.limit = limit;
        this.id = id;
    }


    /**
     * Removes all values from this history.
     *
     * @throws BackingStoreException if this operation cannot be 
     *     completed due to a failure in the backing store, or 
     *     inability to communicate with it.
     */
    public void clear() 
        throws BackingStoreException {

        values.clear();
    }


    /**
     * Returns the number of elements in this history.
     *
     * @return number of elements in this history.
     * @throws BackingStoreException if this operation cannot be 
     *     completed due to a failure in the backing store, or 
     *     inability to communicate with it.
     */
    public int size() 
        throws BackingStoreException {

        return values.keys().length;
    }


    /**
     * Gets the current list of values.
     *
     * @return current list of values.
     * @throws BackingStoreException if this operation cannot be 
     *     completed due to a failure in the backing store, or 
     *     inability to communicate with it.
     */
    public ArrayList/*<String>*/ get() 
        throws BackingStoreException {

        return getValues(getKeys());
    }


    /**
     * Add a value to the history.  If the value already exists in
     * this history, it is moved to the front of the history.  If the
     * value is not already in the history it is added to the front
     * and existing values are moved one position lower with any
     * excess value being discarded.
     *
     * @param value the value that is to be added.
     * @throws BackingStoreException if this operation cannot be 
     *     completed due to a failure in the backing store, or 
     *     inability to communicate with it.
     */
    public synchronized void add(String value) 
        throws BackingStoreException {

        ArrayList/*<String>*/ keys = getKeys();
                                       // current key values
        ArrayList/*<String>*/ valueList = getValues(keys);
                                       // current values
        int i = valueList.indexOf(value);
                                       // index of new value within
                                       // existing values
        switch (i) {

        case -1: {  // does not already exist in list

                int size = size();     // current size of history

                if (size < limit) {

                    moveValuesDown(0, size);
                }
                else {

                    moveValuesDown(0, size - 1);
                }
                // add new value at position 0

                values.put(id + "/" + 0, value);
            }
            break;

        case 0: // value already at index 0

            return;

        default: // value exists.  Move it to top.

            moveValuesDown(0, i);

            values.put((String)keys.get(0), value);

            break;
        }

        values.sync();
    }


    /**
     * Remove a value from the history.  Each value later in the
     * history is moved forward and the last slot becomes empty.
     *
     * @param value the value that is to be removed.
     * @throws BackingStoreException if this operation cannot be 
     *     completed due to a failure in the backing store, or 
     *     inability to communicate with it.
     */
    public synchronized void remove(String value) 
        throws BackingStoreException {

        ArrayList/*<String>*/ keys = getKeys();
                                       // current key values
        ArrayList/*<String>*/ valueList = getValues(keys);
                                       // current values
        int i = valueList.indexOf(value);
                                       // index of new value within
                                       // existing values
        if (i == -1) {  // value does not exist

            return;
        }
        else {

            int size = size();         // current size of history

            moveValuesUp(i, size - 1);

            values.remove (id + "/" + (size - 1));
        }

        values.sync();
    }


    /**
     * Returns the relative preference node's name.
     *
     * @return relative preference node's name.
     */
    protected String getName() {

        return values.name();
    }


    /**
     * Gets the current key values.
     *
     * @return current key values.
     * @throws BackingStoreException if this operation cannot be 
     *     completed due to a failure in the backing store, or 
     *     inability to communicate with it.
     */
    private ArrayList/*<String>*/ getKeys() 
        throws BackingStoreException {

        ArrayList/*<String>*/ keyList = new ArrayList/*<String>*/();
                                       // list of key values
        String[] keys = values.keys(); // key values

        for (int i = 0; i < keys.length; i++) {

            keyList.add(keys[i]);
        }

        return keyList;
    }


    /**
     * Gets the current values.
     *
     * @param keys key values.
     * @return list of current values.
     */
    private ArrayList/*<String>*/ getValues(
        ArrayList/*<String>*/ keys) {

        ArrayList/*<String>*/ valueList = new ArrayList/*<String>*/();
                                       // list of values
/*
        for (String key : keys) {

            valueList.add(values.get(key, null));
        }
*/
        for (int i = 0; i < keys.size(); i++) {

            valueList.add(values.get((String)keys.get(i), null));
        }

        return valueList;
    }


    /**
     * Move values down one position.
     *
     * @param start start index.
     * @param end end index.
     */
    private void moveValuesDown(int start, int end) {

        for (int j = end - 1; j >= start; j--) {

            String value = values.get(id + "/" + j, null);
                                       // current value in j'th position
            values.put(id + "/" + (j + 1), value);
        }

    }


    /**
     * Move values up one position.
     *
     * @param start start index.
     * @param end end index.
     */
    private void moveValuesUp(int start, int end) {

        for (int i = start; i < end; i++) {

            String value = values.get(id + "/" + (i + 1), null);
                                       // current value in i+1'th 
                                       // position
            values.put(id + "/" + i, value);
        }

    }


    /**
     * Class tester.
     *
     * @param args command line arguments.  Currently ignored.
     * @throws Exception if an Exception is encountered.
     */
    public static void main(String[] args) 
        throws Exception {

        String[] fileNames = {
            "f0", "f1", "f2", "f3", "f4", "f5", "f6", "f7", "f8", "f9", 
            "f10"
        };

        History history = new History("cdf", 10);

        history.clear();

        System.out.println("History: " + history.getName());

/*
        for (String fileName : fileNames) {

            history.add(fileName);
        }
*/
        for (int i = 0; i < fileNames.length; i++) {

            history.add(fileNames[i]);
        }

        history.add(fileNames[1]);

        ArrayList/*<String>*/ values = history.get();

        for (int i = 0; i < values.size(); i++) {

            System.out.println(i + ": " + values.get(i));
        }

        System.out.println("Now removing " + fileNames[5]);

        history.remove(fileNames[5]);

        values = history.get();

        for (int i = 0; i < values.size(); i++) {

            System.out.println(i + ": " + values.get(i));
        }
    }
}
