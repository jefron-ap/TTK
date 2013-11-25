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



package org.ihtsdo.ttk.services.sync;

//~--- non-JDK imports --------------------------------------------------------

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

//~--- JDK imports ------------------------------------------------------------

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.storage.file.FileRepository;

/**
 *
 * @author kec
 */
public class Example {

   /**
    * Method description
    *
    *
    * @param args
    */
   public static void main(String[] args) {
       try {
           
           // jGit provides the ability to manage the git repositories from 
           // your code in a very easy manner. For example, to create the new 
           // repository, you just have to indicate the target directory:
           File targetDir = new File("target/newRepository.git");
            Repository repo = new FileRepository(targetDir);
            repo.create(true);

            //Cloning the existing git repository is also very easy and nice:
            Git.cloneRepository().setURI(targetDir.toURI().toString()).
               setDirectory(new File("target/working-copy")).
               setBranch("master").setBare(false).setRemote("origin").
               setNoCheckout(false).call();

           // And here is the way how you can add the changes, 
           // commit and push them to the origin:
                Git git = new Git(new FileRepository(new File("target/working-copy")));
                git.add().addFilepattern(".").call();
                git.commit().setMessage("some comment").call();
                git.push().setPushAll().setRemote("origin").call();
            
                // http://jzelenkov.com/post/35653947951/diving-into-jgit
                
                // http://download.eclipse.org/jgit/docs/jgit-2.0.0.201206130900-r/apidocs/org/eclipse/jgit/api/package-summary.html
                
                // http://blogs.atlassian.com/2013/04/git-flow-comes-to-java/
       } catch (IOException ex) {
           Logger.getLogger(Example.class.getName()).log(Level.SEVERE, null, ex);
       } catch (GitAPIException ex) {
           Logger.getLogger(Example.class.getName()).log(Level.SEVERE, null, ex);
       } 
   }
}
