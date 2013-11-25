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

import junit.framework.TestCase;

import org.ihtsdo.ttk.queue.QueueAddress;
import org.ihtsdo.ttk.queue.QueueList;
import org.ihtsdo.ttk.queue.QueuePreferences;
import org.ihtsdo.ttk.queue.QueueType;

import org.junit.Ignore;

import static org.ihtsdo.ttk.properties.appinfo.AppInfoProperties.ARTIFACT_ID;
import static org.ihtsdo.ttk.properties.appinfo.AppInfoProperties.GROUP_ID;
import static org.ihtsdo.ttk.properties.appinfo.AppInfoProperties.VERSION;

//~--- JDK imports ------------------------------------------------------------

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.prefs.BackingStoreException;

/**
 * Tests of {@link EnumBasedPreferences} class.
 *
 * @author ocarlsen
 */
public class EnumBasedPreferencesTest extends TestCase {

   /** Field description */
   private static final int STRING_LENGTH = 8;

   /** Field description */
   private EnumBasedPreferences testPrefs;

   /** Field description */
   private String appPrefix;

   /**
    * Method description
    *
    *
    * @throws BackingStoreException
    */
   @Override
   public void tearDown() throws BackingStoreException {

      // Remove from backing store.
      // testPrefs.removeNode();
      testPrefs.flush();
   }

   /**
    * Method description
    *
    */
   @Ignore
   public void testGetDefaultAppPrefix_FromProperties() {

      // Set them on a Properties object.
      Properties appInfoProperties = new Properties();

      appInfoProperties.setProperty(GROUP_ID, System.getProperty("groupId"));
      appInfoProperties.setProperty(ARTIFACT_ID, System.getProperty("artifactId"));
      appInfoProperties.setProperty(VERSION, System.getProperty("version"));

      // Confirm app prefix is calculated as expected.
      String expectedAppPrefix = System.getProperty("groupId") + ";" + System.getProperty("artifactId") + ";"
                                 + System.getProperty("version") + ";" + System.getProperty("userName");

      assertEquals(expectedAppPrefix,
                   EnumBasedPreferences.getDefaultAppPrefix(appInfoProperties,
                      System.getProperty("userName")));
   }

   /**
    * Method description
    *
    */
   @Ignore
   public void testGetDefaultAppPrefix_FromStrings() {

      // Create random arguments.
      String groupId    = System.getProperty("groupId");
      String artifactId = System.getProperty("artifactId");
      String version    = System.getProperty("version");
      String userName   = System.getProperty("userName");

      // Confirm app prefix is calculated as expected.
      String expectedAppPrefix = groupId + ";" + artifactId + ";" + version + ";" + userName;

      assertEquals(expectedAppPrefix,
                   EnumBasedPreferences.getDefaultAppPrefix(groupId, artifactId, version, userName));
   }

   /**
    * Method description
    *
    */
   @Ignore
   public void testPutGet() {
      PreferenceWithDefaultEnumBI<String> key   = new DummyPreferenceWithDefaultEnumBI<>("");
      String                              value = "George";

      testPrefs.put(key, value);
      assertEquals(value, testPrefs.get(key));
   }

   /**
    * Method description
    *
    */
   @Ignore
   public void testPutGet_Boolean() {
      PreferenceWithDefaultEnumBI<Boolean> key   = new DummyPreferenceWithDefaultEnumBI<>(false);
      boolean                              value = true;

      testPrefs.putBoolean(key, value);
      assertEquals(value, testPrefs.getBoolean(key));
   }

   /**
    * Method description
    *
    */
   @Ignore
   public void testPutGet_ByteArray() {
      byte[]                              byteArray = new byte[] { 0xa, 0xb, 0xc };
      PreferenceWithDefaultEnumBI<byte[]> key       = new DummyPreferenceWithDefaultEnumBI<>(byteArray);
      byte[]                              value     = new byte[] { 0x1, 0x2, 0x3 };

      testPrefs.putByteArray(key, value);
      assertTrue(Arrays.equals(value, testPrefs.getByteArray(key)));
   }

   /**
    * Method description
    *
    */
   @Ignore
   public void testPutGet_Double() {
      PreferenceWithDefaultEnumBI<Double> key   = new DummyPreferenceWithDefaultEnumBI<>(1.0);
      double                              value = 2.0;

      testPrefs.putDouble(key, value);
      assertEquals(value, testPrefs.getDouble(key));
   }

   /**
    * Method description
    *
    */
   @Ignore
   public void testPutGet_Enum() {
      PreferenceWithDefaultEnumBI key   = new DummyPreferenceWithDefaultEnumBI<>(DummyEnum.JUNK1);
      Enum                        value = DummyEnum.JUNK2;

      testPrefs.putEnum(key, value);
      assertEquals(value, testPrefs.getEnum(key));
   }

   /**
    * Method description
    *
    */
   @Ignore
   public void testPutGet_Float() {
      PreferenceWithDefaultEnumBI<Float> key   = new DummyPreferenceWithDefaultEnumBI<>(1.0f);
      float                              value = 2.0f;

      testPrefs.putFloat(key, value);
      assertEquals(value, testPrefs.getFloat(key));
   }

   /**
    * Method description
    *
    */
   @Ignore
   public void testPutGet_Int() {
      PreferenceWithDefaultEnumBI<Integer> key   = new DummyPreferenceWithDefaultEnumBI<>(1);
      int                                  value = 2;

      testPrefs.putInt(key, value);
      assertEquals(value, testPrefs.getInt(key));
   }

   /**
    * Method description
    *
    */
   @Ignore
   public void testPutGet_Long() {
      PreferenceWithDefaultEnumBI<Long> key   = new DummyPreferenceWithDefaultEnumBI<>(3l);
      long                              value = System.currentTimeMillis();

      testPrefs.putLong(key, value);
      assertEquals(value, testPrefs.getLong(key));
   }

   /**
    * Method description
    *
    *
    * @throws BackingStoreException
    * @throws IOException
    */
   @Ignore
   public void testQueueListSettingsPreserved() throws BackingStoreException, IOException {

      // Construct a default QueuePreferences object.
      QueuePreferences qp0 = new QueuePreferences();

      // Construct a custom QueuePreferences object with some service item properties.
      final String     queueDisplayName  = "some other display";
      final String     queueId           = "big bad queue";
      final File       queueDirectory    = new File("queues/some other location");
      final Boolean    readInsteadOfTake = Boolean.TRUE;
      final QueueType  queueType         = new QueueType(QueueType.Types.OUTBOX);
      QueuePreferences qp1               = new QueuePreferences(queueDisplayName, queueId, queueDirectory,
                                              readInsteadOfTake, queueType);
      final QueueAddress queueAddress = new QueueAddress("qp2 address");

      qp1.getServiceItemProperties().add(queueAddress);

      // Add QueuePreferences to a QueueList.
      QueueList queueList = new QueueList();

      queueList.getQueuePreferences().add(qp0);
      queueList.getQueuePreferences().add(qp1);

      // Export to preferences.
      queueList.exportFields(testPrefs);

      // DEBUG ONLY!
      testPrefs.exportSubtree(new FileOutputStream("Prefs.xml"));
      // Bring preferences up-to-date.
      testPrefs.sync();

      // Now look up the QueueList and confirm its QueuePreferences were saved.
      QueueList              resultQueueList        = new QueueList(testPrefs);
      List<QueuePreferences> resultQueuePreferences = resultQueueList.getQueuePreferences();

      assertEquals(2, resultQueuePreferences.size());

      // Confirm default settings for QueuePreferences #0 were preserved.
      QueuePreferences resultQP0 = resultQueuePreferences.get(0);

      assertEquals(QueuePreferences.Fields.DISPLAY_NAME.getDefaultValue(), resultQP0.getDisplayName());
      assertEquals(new File((String) QueuePreferences.Fields.QUEUE_DIRECTORY.getDefaultValue()),
                   resultQP0.getQueueDirectory());
      assertEquals(QueuePreferences.Fields.READ_INSTEAD_OF_TAKE.getDefaultValue(),
                   resultQP0.getReadInsteadOfTake());
      assertEquals(QueuePreferences.Fields.QUEUE_INSTANCE_PROPERTIES_LIST.getDefaultValue(),
                   resultQP0.getServiceItemProperties().size());

      // Confirm custom settings for QueuePreferences #1 were preserved.
      QueuePreferences resultQP1 = resultQueuePreferences.get(1);

      assertEquals(queueDisplayName, resultQP1.getDisplayName());
      assertEquals(queueId, resultQP1.getId());
      assertEquals(queueDirectory, resultQP1.getQueueDirectory());
      assertEquals(readInsteadOfTake, resultQP1.getReadInsteadOfTake());

      // Confirm custom service item properties were preserved.
      List<PreferenceObject> serviceItemProperties = resultQP1.getServiceItemProperties();

      assertEquals(2, serviceItemProperties.size());

      QueueType resultQueueType = (QueueType) serviceItemProperties.get(0);

      assertEquals(queueType, resultQueueType);

      QueueAddress resultQueueAddress = (QueueAddress) serviceItemProperties.get(1);

      assertEquals(queueAddress, resultQueueAddress);
   }

   /**
    * Method description
    *
    *
    * @throws BackingStoreException
    */
   @Override
   public void setUp() throws BackingStoreException {
      System.setProperty("java.util.prefs.userRoot", System.getProperty("buildDirectory") + "/preferences/user");
      System.setProperty("java.util.prefs.systemRoot", System.getProperty("buildDirectory") + "/preferences/root");
      System.out.println("java.util.prefs.userRoot: " + System.getProperty("java.util.prefs.userRoot"));
      System.out.println("java.util.prefs.systemRoot: " + System.getProperty("java.util.prefs.systemRoot"));
      System.setProperty("java.util.prefs.PreferencesFactory",
                         "org.ihtsdo.ttk.preferences.TtkPreferencesFactory");

      // Create random appPrefix.
      // (EnumBasedPreferences.getDefaultAppPrefix is itself tested below.)
      String groupId    = System.getProperty("groupId");
      String artifactId = System.getProperty("artifactId");
      String version    = System.getProperty("version");
      String userName   = System.getProperty("userName");

      this.appPrefix = EnumBasedPreferences.getDefaultAppPrefix(groupId, artifactId, version, userName);

      // Create test EnumBasedPreferences.
      this.testPrefs = new EnumBasedPreferences(appPrefix);
   }
}
