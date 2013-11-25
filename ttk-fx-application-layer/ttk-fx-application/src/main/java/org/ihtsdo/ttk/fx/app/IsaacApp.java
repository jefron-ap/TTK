/*
 * Copyright 2013 VA Office of Informatics and Analytics.
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



package org.ihtsdo.ttk.fx.app;

//~--- non-JDK imports --------------------------------------------------------

import javafx.application.Application;

import javafx.fxml.FXMLLoader;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;

import javafx.stage.Stage;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.Factory;

import org.ihtsdo.otf.tcc.lookup.Looker;
import org.ihtsdo.otf.tcc.lookup.TtkEnvironment;
import org.ihtsdo.ttk.services.aa.SessionAttributeKeys;
//~--- JDK imports ------------------------------------------------------------

import java.io.IOException;
import org.ihtsdo.otf.tcc.api.metadata.binding.Snomed;
import org.ihtsdo.otf.tcc.api.metadata.binding.TermAux;
import org.ihtsdo.ttk.services.aa.SessionAttributes;
import org.ihtsdo.ttk.services.action.ActionService;

/**
 *
 * @author kec
 */
public class IsaacApp extends Application {

   /**
    * Method description
    *
    *
    * @param primaryStage
    *
    * @throws IOException
    */
   private void setupStage(Stage primaryStage) throws IOException {
      Pane isaacPane = (Pane) FXMLLoader.load(getClass().getResource("/fxml/Isaac.fxml"));

      // ScenicView.show(isaacPane);
      primaryStage.setScene(new Scene(isaacPane));
   }

   /**
    * Method description
    *
    *
    * @param args
    */
   public static void main(String[] args) {

      Factory<SecurityManager> factory = new IniSecurityManagerFactory("classpath:shiro.ini");

      SecurityUtils.setSecurityManager(factory.getInstance());

      Subject currentUser = SecurityUtils.getSubject();
      Session session     = currentUser.getSession();

      

      if (!currentUser.isAuthenticated()) {

         // collect user principals and credentials in a gui specific manner
         // such as username/password html form, X509 certificate, OpenID, etc.
         // We'll use the username/password example here since it is the most common.
         UsernamePasswordToken token = new UsernamePasswordToken("root", "secret");

         // this is all you have to do to support 'remember me' (no config - built in!):
         token.setRememberMe(true);
         currentUser.login(token);
      }

      if (currentUser.isAuthenticated()) {
         // TODO somehow associate the user UUID with the subject
          SessionAttributes.get().put(SessionAttributeKeys.USER_UUID_ARRAY, TermAux.USER.getUuids());
          SessionAttributes.get().put(SessionAttributeKeys.EDIT_MODULE_UUID_ARRAY, Snomed.CORE_MODULE.getUuids());
 
      } else {
          System.out.println("User is not authenticated");
          System.exit(0);
      }
      launch(args);
   }

   @Override
    public void init() throws Exception {
        ActionService.start();
        Looker.lookup(TtkEnvironment.class).setUseFxWorkers(true);
        
    }

   /**
    * Method description
    *
    *
    * @param primaryStage
    *
    * @throws Exception
    */
   @Override
   public void start(Stage primaryStage) throws Exception {
      setupStage(primaryStage);
      primaryStage.show();
   }

   /**
    * Method description
    *
    *
    * @throws Exception
    */
   @Override
   public void stop() throws Exception {
      System.exit(0);
   }
}
