package de.consistec.syncframework.impl.adapter;

import static org.junit.Assert.assertEquals;

import de.consistec.syncframework.common.data.schema.ISQLConverter;
import de.consistec.syncframework.common.exception.SerializationException;

import java.io.InputStream;
import java.util.Scanner;
import org.junit.Test;

/**
 * Tests of SQL converter.
 *
 * @author Markus Backes
 * @company Consistec Engineering and Consulting GmbH
 * @date 09.08.12 09:27
 * @since 0.0.1-SNAPSHOT
 */
public class ChangeLogToSQLConverterTest extends TestBase {

    @Test
    public void testDeleteFromXml() throws SerializationException {
        ISQLConverter converter = new ChangeLogToSQLConverter();
        String sql = converter.fromChangelog(getStringFromXMLFile("category6_a_delete.xml"));
        assertEquals("delete from categories where categoryid=6;\n", sql);
    }

    @Test
    public void testUpdateFromXml() throws SerializationException {
        ISQLConverter converter = new ChangeLogToSQLConverter();
        String sql = converter.fromChangelog(getStringFromXMLFile("category6_b_update.xml"));
        assertEquals(
            "update categories SET categoryid=6,categoryname='Cat6b',description='uhhhhhhh 6b' where categoryid=6;\n",
            sql);
    }

    @Test
    public void testInsertFromXml() throws SerializationException {
        ISQLConverter converter = new ChangeLogToSQLConverter();
        String sql = converter.fromChangelog(getStringFromXMLFile("category7_a_insert.xml"));
        assertEquals("insert into categories (categoryid,categoryname,description) VALUES (7,'Cat7a','uhhhhhhh 7a');\n",
            sql);
    }

    @Test
    public void testMultipleInsertFromXml() throws SerializationException {
        String expected = "insert into categories (categoryid,categoryname,description) "
            + "VALUES (1,'Beverages','Soft drinks, coffees, teas, beers, and ales');\n"
            + "insert into categories (categoryid,categoryname,description) VALUES "
            + "(2,'Condiments','Sweet and savory sauces, relishes, spreads, and seasonings');\n"
            + "insert into categories (categoryid,categoryname,description) "
            + "VALUES (3,'Confections','Desserts, candies, and sweet breads');\n"
            + "insert into categories (categoryid,categoryname,description) "
            + "VALUES (4,'Dairy Products','Cheeses');\n"
            + "insert into categories (categoryid,categoryname,description) "
            + "VALUES (5,'Grains','Breads, crackers, pasta, and cereal');\n"
            + "insert into categories (categoryid,categoryname,description) VALUES (6,'Cat6a','uhhhhhhh 6a');\n"
            + "insert into categories_md (rev,mdv,pk,f) VALUES (1,'B4F135B634EDA2894254E5205F401E90',1,0);\n"
            + "insert into categories_md (rev,mdv,pk,f) VALUES (1,'B9CBF2C3AA1964E3A752F4C34E07369D',2,0);\n"
            + "insert into categories_md (rev,mdv,pk,f) VALUES (1,'A359E8A01ED93D37C0BF6DB13CC74488',3,0);\n"
            + "insert into categories_md (rev,mdv,pk,f) VALUES (1,'0DA3D1A2A2539E864D2D1B6636898395',4,0);\n"
            + "insert into categories_md (rev,mdv,pk,f) VALUES (1,'A52D87B86798B317A7C1C01837290D2F',5,0);\n"
            + "insert into categories_md (rev,mdv,pk,f) VALUES (1,'0D9F6F55D5BF5190D2B8C1105AE21325',6,0);\n";
        ISQLConverter converter = new ChangeLogToSQLConverter();
        String sql = converter.fromChangelog(getStringFromXMLFile("client_data.xml"));
        assertEquals(expected, sql);
    }

    /**
     * Returns content of the xml file as simple string.
     *
     * @param filename XML file
     * @return Content of the file
     */
    private static String getStringFromXMLFile(String filename) {
        InputStream is = TestUtil.class.getClassLoader().getResourceAsStream(filename);
        return new Scanner(is, "UTF-8").useDelimiter("\\A").next();
    }
}
