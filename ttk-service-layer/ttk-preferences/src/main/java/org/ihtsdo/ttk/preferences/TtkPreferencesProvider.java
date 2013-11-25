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



package org.ihtsdo.ttk.preferences;

//~--- non-JDK imports --------------------------------------------------------


//~--- JDK imports ------------------------------------------------------------

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;

import java.util.Properties;
import java.util.prefs.AbstractPreferences;
import java.util.prefs.BackingStoreException;
import org.ihtsdo.otf.tcc.api.io.FileIO;

/**
 *
 * @author kec
 */
public class TtkPreferencesProvider extends AbstractPreferences {

   /** Field description */
   private static final String propsFileName = "prefs.xml";

   /** Field description */
   private File propsFile;

   /** Field description */
   private Properties props;

   /**
    * Constructs ...
    *
    *
    * @param parent
    * @param name
    */
   public TtkPreferencesProvider(TtkPreferencesProvider parent, String name) {
      super(parent, name);

      File propsFolder = new File(parent.propsFile.getParent(), name);
      

      propsFolder.mkdirs();
      propsFile = new File(propsFolder, propsFileName);
      props     = new Properties();
   }
   public TtkPreferencesProvider(File prefDir) {
      super(null, "");


      prefDir.mkdirs();
      propsFile = new File(prefDir, propsFileName);
      props     = new Properties();
   }

   /**
    * Method description
    *
    *
    * @param name
    *
    * @return
    */
   @Override
   protected TtkPreferencesProvider childSpi(String name) {
      return new TtkPreferencesProvider(this, name);
   }

   /**
    * Method description
    *
    *
    * @return
    *
    * @throws BackingStoreException
    */
   @Override
   protected String[] childrenNamesSpi() throws BackingStoreException {
      return propsFile.getParentFile().list(new FilenameFilter() {
         @Override
         public boolean accept(File dir, String name) {
            File childDir  = new File(dir, name);
            File childPref = new File(childDir, propsFileName);

            return childPref.exists();
         }
      });
   }

   /**
    * Method description
    *
    *
    * @throws BackingStoreException
    */
   @Override
   protected void flushSpi() throws BackingStoreException {
      try {
         propsFile.getParentFile().mkdirs();
         props.storeToXML(new FileOutputStream(propsFile), "");
      } catch (IOException ex) {
         throw new BackingStoreException(ex);
      }
   }

   /**
    * Method description
    *
    *
    * @return
    *
    * @throws BackingStoreException
    */
   @Override
   protected String[] keysSpi() throws BackingStoreException {
       Object[] keys = props.keySet().toArray();
       String[] keyArray = new String[keys.length];
       for (int i = 0; i < keyArray.length; i++) {
           keyArray[i] = keys[i].toString();
       }
      return keyArray;
   }

   /**
    * Method description
    *
    *
    * @param key
    * @param value
    */
   @Override
   protected void putSpi(String key, String value) {
      props.put(key, value);
   }

   /**
    * Method description
    *
    *
    * @throws BackingStoreException
    */
   @Override
   protected void removeNodeSpi() throws BackingStoreException {
      try {
         FileIO.recursiveDelete(propsFile.getParentFile());
      } catch (IOException ex) {
         throw new BackingStoreException(ex);
      }
   }

   /**
    * Method description
    *
    *
    * @param key
    */
   @Override
   protected void removeSpi(String key) {
      props.remove(key);
   }

   /**
    * Method description
    *
    *
    * @throws BackingStoreException
    */
   @Override
   protected void syncSpi() throws BackingStoreException {
      if (propsFile.exists()) {
         try {
            props.loadFromXML(new FileInputStream(propsFile));
         } catch (IOException ex) {
            throw new BackingStoreException(ex);
         }
      }
   }

   /**
    * Method description
    *
    *
    * @param key
    *
    * @return
    */
   @Override
   protected String getSpi(String key) {
      return props.getProperty(key);
   }
}
