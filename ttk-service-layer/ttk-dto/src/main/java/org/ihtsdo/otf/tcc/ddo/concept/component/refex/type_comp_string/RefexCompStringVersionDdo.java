package org.ihtsdo.otf.tcc.ddo.concept.component.refex.type_comp_string;

//~--- non-JDK imports --------------------------------------------------------

import javafx.beans.property.SimpleStringProperty;

import org.ihtsdo.otf.tcc.ddo.concept.component.refex.RefexChronicleDdo;
import org.ihtsdo.otf.tcc.ddo.concept.component.refex.type_comp.RefexCompVersionDdo;
import org.ihtsdo.otf.tcc.api.contradiction.ContradictionException;
import org.ihtsdo.otf.tcc.api.store.TerminologySnapshotDI;
import org.ihtsdo.otf.tcc.api.refex.type_nid_string.RefexNidStringVersionBI;

//~--- JDK imports ------------------------------------------------------------

import java.io.IOException;
import javax.xml.bind.annotation.XmlSeeAlso;
import org.ihtsdo.otf.tcc.ddo.concept.component.refex.type_comp_comp_comp_string.RefexCompCompCompStringVersionDdo;


@XmlSeeAlso( {
    RefexCompCompCompStringVersionDdo.class, 
})
public class RefexCompStringVersionDdo<T extends RefexChronicleDdo, V extends RefexCompStringVersionDdo>
        extends RefexCompVersionDdo<T, V> {
   public static final long serialVersionUID = 1;

   //~--- fields --------------------------------------------------------------

   public SimpleStringProperty string1Property = new SimpleStringProperty(this, "string1");

   //~--- constructors --------------------------------------------------------

   public RefexCompStringVersionDdo() {
      super();
   }

   public RefexCompStringVersionDdo(T chronicle, TerminologySnapshotDI ss, RefexNidStringVersionBI another)
           throws IOException, ContradictionException {
      super(chronicle, ss, another);
      this.string1Property.set(another.getString1());
   }

   //~--- methods -------------------------------------------------------------

   /**
    * Compares this object to the specified object. The result is <tt>true</tt>
    * if and only if the argument is not <tt>null</tt>, is a
    * <tt>ERefsetCidStrVersion</tt> object, and contains the same values, field by field,
    * as this <tt>ERefsetCidStrVersion</tt>.
    *
    * @param obj the object to compare with.
    * @return <code>true</code> if the objects are the same;
    *         <code>false</code> otherwise.
    */
   @Override
   public boolean equals(Object obj) {
      if (obj == null) {
         return false;
      }

      if (RefexCompStringVersionDdo.class.isAssignableFrom(obj.getClass())) {
         RefexCompStringVersionDdo another = (RefexCompStringVersionDdo) obj;

         // =========================================================
         // Compare properties of 'this' class to the 'another' class
         // =========================================================
         // Compare strValue
         if (!this.string1Property.equals(another.string1Property)) {
            return false;
         }

         // Compare their parents
         return super.equals(obj);
      }

      return false;
   }

   public SimpleStringProperty string1Property() {
      return string1Property;
   }

   /**
    * Returns a string representation of the object.
    */
   @Override
   public String toString() {
      StringBuilder buff = new StringBuilder();

      buff.append(this.getClass().getSimpleName()).append(": ");
      buff.append(" str: ");
      buff.append("'").append(this.string1Property.get()).append("'");
      buff.append(" ");
      buff.append(super.toString());

      return buff.toString();
   }

   //~--- get methods ---------------------------------------------------------

   public String getString1() {
      return string1Property.get();
   }

   //~--- set methods ---------------------------------------------------------

   public void setString1(String string1) {
      this.string1Property.set(string1);
   }
}
